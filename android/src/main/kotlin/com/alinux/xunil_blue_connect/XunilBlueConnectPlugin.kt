//Euzübillâhimineşşeytânirracîm bismillâhirrahmânirrahîm
package com.alinux.xunil_blue_connect

import android.provider.Settings
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver

import android.app.Activity;
import android.os.Parcelable
import android.os.ParcelUuid
import android.os.Handler
import android.os.Bundle
import android.os.Build

import java.util.UUID
import android.util.Log

import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException

import org.json.JSONArray
import org.json.JSONObject

import androidx.annotation.NonNull

import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener

import android.content.pm.PackageManager
import android.content.Context
import android.location.LocationManager

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.EventChannel.EventSink

/** XunilBlueConnectPlugin */
class XunilBlueConnectPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel: MethodChannel
  private lateinit var eventStartStreamChannel: EventChannel
  private lateinit var eventKeyValueStreamChannel: EventChannel
  private lateinit var context: Context
  private lateinit var locationManager: LocationManager



  private var locationPermission: Boolean = false



  private var deviceResultStreamEventSink: EventChannel.EventSink? = null
  private var eventAnyStatusSink: EventChannel.EventSink? = null



  private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter();
  private var mmSocket: BluetoothSocket? = null
  private var bluetoothDevice: BluetoothDevice? = null


  
  private var activity: Activity? = null
  private var result: Result? = null



  private var filter: IntentFilter? = null



  //for all of status broadcast receiver
  private var filterAnyStatusFilter: IntentFilter? = null
  private var uuidList: JSONArray = JSONArray()



  //for serial port connecting 0x1101, initialized as default
  private val baseUUID: String = "00001101-0000-1000-8000-00805F9B34FB"



  private val handler: Handler? = null



  //main mac address use as general in this class
  private var mainMacAddress: String? = null



  //main uuid for use as general in this class, type String and one, not array
  private var mainUuid: UUID? = null



  //auto connect for write job // true or false
  private var mainStreamAutoConnect: Boolean = false

  


  @Serializable
  data class UUIDSerializable(
    val name: String?, 
    val short_description: String?, 
    val uuid: String?
  )



  @Serializable
  data class DeviceSerializable(
    val name: String?, 
    val aliasName: String?, 
    val address: String?,
    val type: String?,
    val isPaired: String?,
    val uuids: String?,
  )



  @Serializable
  data class PairingStatusSerializable(
    val STATUS_PAIRING: String?,
  )



  @Serializable
  data class ConnectingStatusSerializable(
    val STATUS_CONNECTING: String?,
    val MAC_ADDRESS: String?,
  )
  

  @Serializable
  data class DiscoveryStatusSerializable(
    val STATUS_DISCOVERY: String?,
  )

  // Defines several constants used when transmitting messages between the
  // service and the UI.
  companion object {
    //get string from function getConnectingStatus
    private const val STATE_DISCONNECTED: Int = 0
    private const val STATE_CONNECTED: Int = 2

    //location and bluetooth permissions system numbers
    private const val BLUETOOTH_ENABLE_PERMISSION_NUMBER: Int = 17
    private const val BLUETOOTH_DISABLE_PERMISSION_NUMBER: Int = 18
    private const val LOCATION_PERMISSION_NUMBER: Int = 19
    private const val LOCATION_ENABLE_PERMISSION_NUMBER: Int = 20
    private const val PAIRING_CHANGE_NUMBER: Int = 21

    //read/write system send target (OS default)
    private const val MESSAGE_READ: Int = 0
    private const val MESSAGE_WRITE: Int = 1
    private const val MESSAGE_TOAST: Int = 2
  }





  //devices' connecting status
  fun getConnectingStatus(connectId: Int): String{
    return when(connectId) {
      0 -> "STATE_DISCONNECTED"
      1 -> "STATE_CONNECTING"
      2 -> "STATE_CONNECTED"
      3 -> "STATE_DISCONNECTING"

      else -> "ERROR"
    }
  }






  //devices' pairing status
  fun getIsPaired(isPaired: Int): String{
    return when(isPaired) {
      10 -> "PAIRED_NONE"
      11 -> "PAIRING"
      12 -> "PAIRED"

      else -> "UNKNOWN_PAIRED"
    }
  }






  //found device's type
  fun getType(type: Int): String{
    return when(type) {
      0 -> "DEVICE_TYPE_UNKNOWN"
      1 -> "DEVICE_TYPE_CLASSIC"
      2 -> "DEVICE_TYPE_LE"
      3 -> "DEVICE_TYPE_DUAL"
      -2147483648 -> "ERROR"

      else -> "UNKNOWN_TYPE"
    }
  }






  /* 

    this [onAttachedToEngine] has one [MethodChannel]
    and has two [EventChannel],
    one of [EventChannels] is found devices return to flutter
    another is connecting and pairing status return to flutter
  
  */
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    
    //MethodChannel for general jobs
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bluetooth")
    channel.setMethodCallHandler(this)

    //[deviceResultStream] is [EventChannel], found devices return to flutter
    EventChannel(flutterPluginBinding.binaryMessenger, "deviceResultStream").setStreamHandler(object : EventChannel.StreamHandler {
      
      override fun onListen(arguments: Any?, deviceResultStreamEventSinkInLine: EventChannel.EventSink?) {
        deviceResultStreamEventSink = deviceResultStreamEventSinkInLine
        Log.d("deviceResultStream", "called onListen")
      }
    
      override fun onCancel(arguments: Any?) {
        deviceResultStreamEventSink = null
        Log.d("deviceResultStream", "called onCancel")
      }

    });

    //[anyStatusResultStream] is [EventChannel], connecting and pairing return to flutter
    EventChannel(flutterPluginBinding.binaryMessenger, "anyStatusResultStream").setStreamHandler(object : EventChannel.StreamHandler {
      
      override fun onListen(arguments: Any?, eventAnyStatusSinkInLine: EventChannel.EventSink?) {
        eventAnyStatusSink = eventAnyStatusSinkInLine
        Log.d("anyStatusResultStream", "called onListen")
      }
    
      override fun onCancel(arguments: Any?) {
        eventAnyStatusSink = null
        Log.d("anyStatusResultStream", "called onCancel")
      }

    });

    //this context belongs to this class
    context = flutterPluginBinding.applicationContext

    //get uuids and their explain from a json file when trigger this plugin
    getUUIDListFromFile()

    //some intents for register
    filterAnyStatusFilter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
    context.registerReceiver(receiverAnyStatusResult, filterAnyStatusFilter)

    filterAnyStatusFilter = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
    context.registerReceiver(receiverAnyStatusResult, filterAnyStatusFilter)

    filterAnyStatusFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    context.registerReceiver(receiverAnyStatusResult, filterAnyStatusFilter)

    filterAnyStatusFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
    context.registerReceiver(receiverAnyStatusResult, filterAnyStatusFilter)

    filterAnyStatusFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    context.registerReceiver(receiverAnyStatusResult, filterAnyStatusFilter)

    filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(receiverDeviceResults, filter)
  }





  override fun onDetachedFromActivity() {
    bluetoothAdapter?.cancelDiscovery()
    context.unregisterReceiver(receiverDeviceResults)
    mainStreamAutoConnect = false
  }






  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}






  override fun onDetachedFromActivityForConfigChanges() {}






  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
      activity = binding.activity;
  }






  /* 

    this [getUUIDListFromFile] is
    get a json file in assets directory when trigger this plugin
  
  */
  fun getUUIDListFromFile() {
    var uuidListString: String = ""

    try {
      uuidListString = context.assets.open("uuids_list.json").bufferedReader().use { it.readText() }
      uuidList = JSONArray(uuidListString)

    } catch (ioException: IOException) {
        ioException.printStackTrace()
    }
  }






  /* 

    this [findFromUUIDList] is
    find uuid from return file from function [getUUIDListFromFile]
    takes [word] named parameter, the parameter is [UUID] and type is [String]
    for example: 00001101-0000-1000-8000-00805F9B34FB
  
  */
  fun findFromUUIDList(word: String?): JSONObject? {
    var returnObject: JSONObject? = null

    var countNumber: Int = 0
    while(countNumber < uuidList.length()){

      if(uuidList.getJSONObject(countNumber).getString("uuid").lowercase() == word?.lowercase()){
        returnObject = uuidList.getJSONObject(countNumber)
        break
      }

      countNumber++
    }

    return returnObject
  }






  /* 

    this is [MethodChannel]
    whatever wants from side flutter api methods
  
  */
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method){
      "IS_BLUETOOTH_AVAILABLE" -> isBluetoothAvailable(result)
      "CHECK_SETTING_LOCATION" -> isOnLocation(result)
      "GO_LOCATION_FOR_ENABLE" -> goLocationForEnable(result)
      "APPLY_PERMISSION_LOCATION" -> applyPermissionLocation(result)
      "SET_BLUETOOTH_ENABLE" -> bluetoohSetEnable()
      "SET_BLUETOOTH_DISABLE" -> bluetoohSetDisable()
      "START_DISCOVERY" -> startDiscovery(result)
      "STOP_DISCOVERY" -> stopDiscovery(result)
      "PAIR_TO_DEVICE" -> startPairing(call.argument("macAddress"))
      "GET_PAIRED_DEVICES" -> getJustPairedDevices(result)
      "CONNECT_TO_DEVICE" -> createConnectionToDevice(call.argument("macAddress"), call.argument("UUIDString"))
      "CLOSE_TO_DEVICE" -> closeConnectionFromDevice(call.argument("macAddress"))
      "WRITE_TO_DEVICE" -> writeToDevice(call.argument("data"), call.argument("autoConnect"))

      else -> result.notImplemented()
    }
  }






  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }






  /* 

    this [isBluetoothAvailable]
    check device's bluetooth available status
    if the device doesn't support bluetooth return [null] parameter
    if bluetooth already active return [true] parameter
    or not return [false] parameter
    takes [result] named parameter, type is [Result]
  
  */
  fun isBluetoothAvailable(result: Result) {

    //if bluetooth is null means the device doesn't support bluetooth
    if(bluetoothAdapter == null){

      Log.d("isBluetoothAvailable", "Bluetooth is NULL")

      result.success(null)
    }

    Log.d("isBluetoothAvailable", "Bluetooth is " + bluetoothAdapter!!.isEnabled())

    //if bluetooth is true means the device's bluetooh is active
    //if not means the device's bluetooh isn't active
    result.success(bluetoothAdapter!!.isEnabled());
  }






  /* 

    this [applyPermissionLocation]
    for ask device's location permission for use
    takes [result] named parameter, type is [Result]
  
  */
  fun applyPermissionLocation(result: Result){

    var locationPermission: Boolean = false
    val checkLocation: Int = ContextCompat.checkSelfPermission(context,
    android.Manifest.permission.ACCESS_FINE_LOCATION)

    if (checkLocation !== PackageManager.PERMISSION_GRANTED) {

       ActivityCompat.requestPermissions(activity!!, arrayOf(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
          ),
            XunilBlueConnectPlugin.LOCATION_PERMISSION_NUMBER
          )
    }
    else{
        locationPermission = true

        Log.d("applyPermissionLocation", "Location permission granted")
    }

    result.success(locationPermission)
  }






  /* 

    this [isOnLocation]
    for device's location check
    if the location is on, return parameter [true], type is [Boolean]
    if the location is off, return parameter [false], type is [Boolean]
    takes [result] named parameter, type is [Result]
  
  */
  fun isOnLocation(result: Result){
    var isOnLocation: Boolean = false

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
      Log.d("isEnableOrDisable", "Enable")

      isOnLocation = true
    }
    else{
      Log.d("isEnableOrDisable", "Disable")

      isOnLocation = false
    }

    result.success(isOnLocation)
  }






  /* 

    this [goLocationForEnable]
    for device's location permission check
    if the location is off, turn on as manuel for goes to location setting
    and return parameter [true], type is [Boolean]
    if the location is on, return parameter [true], type is [Boolean]
    takes [result] named parameter, type is [Result]
  
  */
  fun goLocationForEnable(result: Result){
    var goLocationForEnable: Boolean = false

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if(! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        activity!!.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 
          XunilBlueConnectPlugin.LOCATION_ENABLE_PERMISSION_NUMBER
        )

        goLocationForEnable = true
    }
    else{
     
      Log.d("goLocationForEnable", "Already still enabled")
      goLocationForEnable = true
    }

    result.success(goLocationForEnable)
  }






  /* 

    this [bluetoohSetEnable]
    for device's bluetooth turn on
  
  */
  fun bluetoohSetEnable() {
    val checkBluetoothState: Int = ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.BLUETOOTH_CONNECT)

    if(! bluetoothAdapter!!.isEnabled())
    {
       //android 12 and higher
       if(Build.VERSION.SDK_INT >= 31){

        if(checkBluetoothState !== PackageManager.PERMISSION_GRANTED)
        {
          ActivityCompat.requestPermissions(activity!!, arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
          ), 
            XunilBlueConnectPlugin.BLUETOOTH_ENABLE_PERMISSION_NUMBER
          )

          return
        }
      }

      bluetoothAdapter!!.enable()

      Log.d("bluetoohSetEnable", "Enable")
    }
  }






  /* 

    this [bluetoohSetDisable]
    for device's bluetooth turn off
  
  */
  fun bluetoohSetDisable() {

    val checkBluetoothState: Int = ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.BLUETOOTH_CONNECT)

    if(bluetoothAdapter!!.isEnabled())
    {
       //android 12 and higher
       if(Build.VERSION.SDK_INT >= 31){

        if(checkBluetoothState !== PackageManager.PERMISSION_GRANTED)
        {
          ActivityCompat.requestPermissions(activity!!, arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
          ),
            XunilBlueConnectPlugin.BLUETOOTH_DISABLE_PERMISSION_NUMBER
          )

          return
        }
      }

      bluetoothAdapter!!.disable()

      Log.d("bluetoohSetEnable", "Enable")
    }
  }






  /* 

    this [receiverDeviceResults]
    for this is a broadcast receiver
    its job is find a device and return to flutter
    gives parameters to function [getDeviceInfo] its found device
  
  */
  protected val receiverDeviceResults = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val action: String? = intent.action
      when(action!!) {
        BluetoothDevice.ACTION_FOUND -> {
          val device: BluetoothDevice? =
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

          getDeviceInfo(device!!)
        }
      }
    }
  }






  /* 

    this [receiverAnyStatusResult]
    for this is a broadcast receiver
    its job is listen connecting status and pairing status
    then return them to flutter
  
  */
  protected val receiverAnyStatusResult = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val action: String? = intent.action
      when(action!!) {


        //STATUS_PAIRING
        BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
          val device: BluetoothDevice? =
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

          val pairedStatus: Int = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, XunilBlueConnectPlugin.PAIRING_CHANGE_NUMBER)

          eventAnyStatusSink!!.success(Json.encodeToString(PairingStatusSerializable(getIsPaired(pairedStatus))))
        }


        //STATUS_CONNECTING
        BluetoothDevice.ACTION_ACL_CONNECTED -> {
          val device: BluetoothDevice? =
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

          //[STATE_CONNECTED] named parameter takes reference 2
          eventAnyStatusSink!!.success(Json.encodeToString(ConnectingStatusSerializable(getConnectingStatus(XunilBlueConnectPlugin.STATE_CONNECTED), device?.toString())))
        }
        BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
          val device: BluetoothDevice? =
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

          //[STATE_DISCONNECTED] named parameter takes reference 0
          eventAnyStatusSink!!.success(Json.encodeToString(ConnectingStatusSerializable(getConnectingStatus(XunilBlueConnectPlugin.STATE_DISCONNECTED), device?.toString())))



          //if give [true] parameter from flutter to api on the write job
          //reconnect when write job is done
          if(mainStreamAutoConnect)
            createConnectionToDevice(mainMacAddress, mainUuid.toString())
        }

        //DISCOVERY_STATUS
        BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {

          eventAnyStatusSink!!.success(Json.encodeToString(DiscoveryStatusSerializable("STARTED")))
        }
        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
          
          eventAnyStatusSink!!.success(Json.encodeToString(DiscoveryStatusSerializable("FINISHED")))
        }
      }
    }
  }






  //-------------------------------------------------------------------------
  /* 

    TODO listener profile 

  */
  /* protected val listenerProfile = object : BluetoothProfile.ServiceListener {
    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
      
      val profileStatus: Int = profile

      var devices: List<BluetoothDevice> = proxy.getConnectedDevices()

      Log.d("devices", devices.toString())

      //eventAnyStatusSink!!.success(Json.encodeToString(ConnectingStatusSerializable(getConnectingStatus(profileStatus), "asdasda")))
    }

    override fun onServiceDisconnected(profile: Int) {
    }
  } */

  //-------------------------------------------------------------------------






  /* 

    this [getJustPairedDevices]
    for only return device's paired devices as string for decode as json
    takes [result] named parameter, type is [Result]
  
  */
  fun getJustPairedDevices(result: Result){
    val resultForPairedDevices: MutableList<String> = mutableListOf()
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val uuids: MutableList<UUIDSerializable> = mutableListOf()

    if(! pairedDevices.isNullOrEmpty()){
      pairedDevices?.forEach { device ->

        device!!.getUuids().forEach{uuid: ParcelUuid? -> 
          val fromUuidList: JSONObject? = findFromUUIDList(word = uuid?.getUuid()?.toString())
          
          uuids.add(
            UUIDSerializable(
                if(fromUuidList != null) fromUuidList?.getString("name") else "UNKNOWN", 
                if(fromUuidList != null) fromUuidList?.getString("short_description") else "UNKNOWN",
                if(fromUuidList != null) fromUuidList?.getString("uuid") else uuid?.getUuid()?.toString()
            )
          )
        }

        resultForPairedDevices.add(
            Json.encodeToString(
              DeviceSerializable(
                device!!.name?.toString(),
                device!!.getAlias()?.toString(),
                device!!.address?.toString(),
                getType(device!!.getType()?.toInt()).toString(),
                getIsPaired(device!!.getBondState()?.toInt()).toString(),
                Json.encodeToString(uuids)
            )
          )
        )
      }
    }

    result!!.success(resultForPairedDevices)
  }






  /* 

    this [getDeviceInfo]
    belongs to [receiverDeviceResults] as teoric :)
    function [receiverDeviceResults] give found device
    returns device's some info as string for decode as json
    takes [device] named parameter, type is [BluetoothDevice]
  
  */
  fun getDeviceInfo(device: BluetoothDevice?){
    val uuids: MutableList<UUIDSerializable> = mutableListOf()

    try {
      //only get paired device's uuids
      if(device!!.getUuids() != null)
      {
        device!!.getUuids().forEach{uuid: ParcelUuid? -> 
          val fromUuidList: JSONObject? = findFromUUIDList(word = uuid?.getUuid()?.toString())
          
          uuids.add(
            UUIDSerializable(
                if(fromUuidList != null) fromUuidList?.getString("name") else "UNKNOWN", 
                if(fromUuidList != null) fromUuidList?.getString("short_description") else "UNKNOWN",
                if(fromUuidList != null) fromUuidList?.getString("uuid") else uuid?.getUuid()?.toString()
            )
          )
        }
      }

      deviceResultStreamEventSink!!.success(
        Json.encodeToString(
            DeviceSerializable(
              device!!.name?.toString(),
              device!!.getAlias()?.toString(),
              device!!.address?.toString(),
              getType(device!!.getType()?.toInt()).toString(),
              getIsPaired(device!!.getBondState()?.toInt()).toString(),
              Json.encodeToString(uuids)
          )
        )
      )
    }
    catch(e: Throwable) {
      Log.d("SomeException", e.toString())
    }
  }






  /* 

    this [startDiscovery]
    belongs to [receiverDeviceResults]
    starts discovery job
    if start returns [true] or not returns [false] parameter
    takes [result] named parameter, type is [Result]
  
  */
  fun startDiscovery (result: Result){
    val valueBS = bluetoothAdapter?.startDiscovery()
    
    Log.d("startDiscovery", "discovery started")

    result!!.success(valueBS)
  }  
  





  /* 

    this [stopDiscovery]
    belongs to [receiverDeviceResults]
    stops discovery job
    if stop returns [true] or not returns [false] parameter
    takes [result] named parameter, type is [Result]
  
  */
  fun stopDiscovery (result: Result){
    val valueBC = bluetoothAdapter?.cancelDiscovery()
    Log.d("stopDiscovery", "discovery stopped")

    result!!.success(valueBC)
  }






  /* 

    this [startPairing] as override Synchronized
    starts pair to any device
    if already paired [void] or wants accept a number between each devices (as OS default)
    takes [macAddress] named parameter, type is [String]
  
  */
  @Synchronized
  fun startPairing(macAddress: String?){

    mainMacAddress = macAddress

    val bluetoothDeviceForPair: BluetoothDevice = bluetoothAdapter!!.getRemoteDevice(mainMacAddress!!)

    var isPaired = getIsPaired(bluetoothDeviceForPair.getBondState())?.toString()

    if(isPaired != "PAIRED"){
      bluetoothDeviceForPair.createBond()

      bluetoothDevice = bluetoothDeviceForPair
    }
  }






  //-------------------------------------------------------------------------
  /* 

    TODO remove pair

  */

  /* fun removePaired(result: Result, macAddress: String?) {
    var bluetoothDevice: BluetoothDevice = bluetoothAdapter!!.getRemoteDevice(macAddress!!)

    var isPaired = getIsPaired(bluetoothDevice.getBondState())?.toString()
    
    if(isPaired != "PAIRED"){
      bluetoothDevice.removeBond()

      filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
      context.registerReceiver(receiver, filter)

      result.success("The device removed")
    }

    result.success("The device isn't already paired")
  } */

  //-------------------------------------------------------------------------






  /* 

    this [createConnectionToDevice] as override Synchronized
    belongs to function [ConnectThread]
    for goes to [ConnectThread] for connect to device 
    and gives two parameters to [ConnectThread]
    one of the parameters is [bluetoothDevice], type is [BluetoothDevice]
    another of the parameters is [mainUuid], type is [UUID]
    takes [macAddress] named parameter, type is [String]
    takes [UUIDString] named parameter, type is [String]
  
  */
  @Synchronized
  fun createConnectionToDevice(macAddress: String?, UUIDString: String? = null){

    mainUuid = UUID.fromString(UUIDString ?: baseUUID)
    mainMacAddress = macAddress

    if(mainMacAddress != null)
      bluetoothDevice = bluetoothAdapter?.getRemoteDevice(mainMacAddress!!)

    Log.d("uuid", mainUuid.toString())

    //-------------------------------------------------------------------------
    /* 

      TODO listener profile 

    */
    //bluetoothAdapter?.getProfileProxy(context, listenerProfile, BluetoothProfile.A2DP)

    //-------------------------------------------------------------------------

    ConnectThread(bluetoothDevice, mainUuid).run()
  }






  /* 

    this [closeConnectionFromDevice]
    belongs to function [ConnectThread]
    for goes to [ConnectThread] for disconnect connected device 
    and gives one parameter to [ConnectThread]
    the parameter is [bluetoothDevice], type is [BluetoothDevice]
    takes [macAddress] named parameter, type is [String]
  
  */
  fun closeConnectionFromDevice(macAddress: String? = null){
  
    mainMacAddress = macAddress

    if(mainMacAddress != null)
      bluetoothDevice = bluetoothAdapter?.getRemoteDevice(mainMacAddress!!)

    ConnectThread(bluetoothDevice).cancel()
  }






   /* 

    this [writeToDevice] as override Synchronized
    belongs to function [StreamThread]
    for goes to [StreamThread] for write to remote device
    and gives two parameters to [StreamThread]
    one of parameters is [data], [data] is any text for now, type is [String]
    another of parameters is [autoConnect],
    [autoConnect] is reconnect to device because 
    status will be disconnect when write is done (OS default), 
    type is [Boolean]
    takes [data] named parameter, type is [String], actually is text
    takes [autoConnect] named parameter, type is [Boolean]
  
  */
  @Synchronized
  fun writeToDevice(data: String? = null, autoConnect: Boolean? = null){

      StreamThread(data, autoConnect).write()
  }
 





  //---------------------------------------------------------------------

  /*

    TODO read and reset

  */
  /* @Synchronized
  fun readFromDevice(result: Result){

      StreamThread().run()
  } 
  
  @Synchronized
  fun resetReadDevice(result: Result){

      StreamThread().reset()
  } */

  //---------------------------------------------------------------------






  /* 

    this [ConnectThread]
    for this is a [Thread]
    this has two functions
    one of them is [run], use for connect a device,
    another of them is [cancel], use for disconnect already connect device
    takes two parameters, one of them is [device], type is [BluetoothDevice]
    another of them is [UUIDString], type is [String]
  
  */
  protected inner class ConnectThread(device: BluetoothDevice? = null, UUIDString: UUID? = null) : Thread() {
    
    private var mmDevice: BluetoothDevice? = device    

    private var UUIDForConnect: UUID = UUIDString ?: UUID.fromString(baseUUID)
  
    public override fun run() {
      mainUuid = UUIDForConnect

      mmSocket = mmDevice!!.createRfcommSocketToServiceRecord(mainUuid)
      bluetoothAdapter?.cancelDiscovery()

        try {
            mmSocket?.let { socket ->
              socket.connect()
            }

            Log.d("STATUS_CONNECT", true.toString())
        }
        catch(e: Throwable) {
            Log.d("STATUS_CONNECT", e.toString())
        }    
    }
    
    fun cancel() {
        try {
          mmSocket?.let { socket ->
            socket.close()
          }

          Log.d("STATUS_DISCONNECT", true.toString())
        } catch (e: Throwable) {
            Log.d("STATUS_DISCONNECT", e.toString())
        }
    }
  }






  /* 

    this [StreamThread]
    for this is a [Thread]
    this has three functions but [read] and [reset] in TODO list
    one parameter is [write] named, write to remote device means send data to remote device
    takes two parameters, one of the parameters is [data], type is [String]
    another of the parameters is [autoConnect], type is [Boolean]
  
  */
  protected inner class StreamThread(data: String? = null, autoConnect: Boolean? = null) : Thread() {

    private val mmInStream: InputStream? = mmSocket?.inputStream
    private val mmOutStream: OutputStream? = mmSocket?.outputStream
    private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

    private var streamData: String? = data ?: ""
    private var streamAutoConnect: Boolean = autoConnect ?: false

    //-------------------------------------------------------------------------
    /* 

      TODO read and reset
      
    */
    /* override fun run() {
        var numBytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            // Read from the InputStream.
            numBytes = try {
                var readValue = mmInStream!!.read(mmBuffer)

                Log.d("read", readValue.toString())
            } catch (e: IOException) {
                Log.d("StreamThread", "Input stream was disconnected", e)
                break
            }

            // Send the obtained bytes to the UI activity.
            val readMsg = handler?.obtainMessage(
              XunilBlueConnectPlugin.MESSAGE_READ, numBytes, -1,
                    mmBuffer)
            readMsg?.sendToTarget()
        }
    }

    fun reset(){
      try {
        mmInStream!!.reset()
      }
      catch(e: Throwable) {
          Log.d("StreamThread reset Throwable", e.toString())
      }
    
    } */
    //-------------------------------------------------------------------------

    // Call this from the main activity to send data to the remote device.
    fun write() {
        try {
          //[stramData] is text but convert to [ArrayByte]
          mmOutStream!!.write(streamData!!.toByteArray())
          
          //if write method is done system auto disconnect the connect
          //if variable [mainStreamAutoConnect] set as [true] connection auto reconnect
          //or 
          //if variable [mainStreamAutoConnect] set as [false] write is done, will be disconnect
          mainStreamAutoConnect = streamAutoConnect
          
        } catch (e: IOException) {
            Log.e("StreamThread", "Error occurred when sending data", e)

            // Send a failure message back to the activity.
            val writeErrorMsg = handler?.obtainMessage(XunilBlueConnectPlugin.MESSAGE_TOAST)
            val bundle = Bundle().apply {
                putString("toast", "Couldn't send data to the other device")
            }
            writeErrorMsg?.data = bundle
            handler?.sendMessage(writeErrorMsg!!)
            return
        }

        // Share the sent message with the UI activity.
        val writtenMsg = handler?.obtainMessage(
          XunilBlueConnectPlugin.MESSAGE_WRITE, -1, -1, mmBuffer)
        writtenMsg?.sendToTarget()
    }
  }  
}
