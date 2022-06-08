//Euzübillâhimineşşeytânirracîm bismillâhirrahmânirrahîm
package com.alinux.xunil_blue_connect

import android.provider.Settings
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable
import android.os.ParcelUuid

import java.util.UUID
import android.util.Log

import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGatt

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import androidx.annotation.NonNull

import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

/* import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity */

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

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
class XunilBlueConnectPlugin: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {

  protected lateinit var channel: MethodChannel
  protected lateinit var eventStartStreamChannel: EventChannel
  protected lateinit var context: Context
  protected var activity: Activity? = null
  protected var result: Result? = null
  protected lateinit var locationManager: LocationManager
  protected var locationPermission: Boolean = false
  protected val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter();
  protected var eventSink: EventChannel.EventSink? = null
  protected var filter: IntentFilter? = null

  //00001101-0000-1000-8000-00805F9B34FB
  //0000112f-0000-1000-8000-00805f9b34fb
  protected val forBluetoothConnectionUUID = UUID.fromString("0000112f-0000-1000-8000-00805f9b34fb")
  protected var mmSocket: BluetoothSocket? = null

  override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
    this.eventSink = eventSink

    Log.d("onListen", "called onListen")
  }

  override fun onCancel(arguments: Any?) {
    this.eventSink = null
    Log.d("onCancel", "called onCancel")
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bluetooth")
    channel.setMethodCallHandler(this)

    eventStartStreamChannel = EventChannel(flutterPluginBinding.binaryMessenger, "bluetoothStream")
    eventStartStreamChannel.setStreamHandler(this)

    context = flutterPluginBinding.applicationContext
  }

  override fun onDetachedFromActivity() {}

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
      activity = binding.activity;
  }

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
      "PAIR_TO_DEVICE" -> startPairing(result, call.argument("macAddress"))
      "GET_PAIRED_DEVICES" -> getJustPairedDevices(result)
      "CONNECT_TO_DEVICE" -> createConnectionToDevice(result, call.argument("macAddress"))
      "CLOSE_TO_DEVICE" -> closeConnectionFromDevice(result, call.argument("macAddress"))


      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

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

  fun applyPermissionLocation(result: Result){

    var locationPermission: Boolean = false

    if (ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.ACCESS_FINE_LOCATION) !==
        PackageManager.PERMISSION_GRANTED) {

       ActivityCompat.requestPermissions(activity!!, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                1)
    }
    else{
        locationPermission = true

        Log.d("applyPermissionLocation", "Location permission granted")
    }

    result.success(locationPermission)
  }

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

  fun goLocationForEnable(result: Result){
    var goLocationForEnable: Boolean = false

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if(! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        activity!!.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1)

        goLocationForEnable = true
    }
    else{
     
      Log.d("goLocationForEnable", "Already still enabled")
      goLocationForEnable = true
    }

    result.success(goLocationForEnable)
  }

  fun bluetoohSetEnable(){
    if(! bluetoothAdapter!!.isEnabled())
    {
      bluetoothAdapter!!.enable()
      Log.d("bluetoohSetEnable", "Enable")
    }
  }

  fun bluetoohSetDisable(){

    if(bluetoothAdapter!!.isEnabled())
    {
      bluetoothAdapter!!.disable()
      Log.d("bluetoohSetDisable", "Disable")
    }
  }

  fun getIsPaired(isPaired: Int): String{
    return when(isPaired) {
      10 -> "PAIRED_NONE"
      11 -> "PAIRING"
      12 -> "PAIRED"

      else -> "UNKNOWN_PAIRED"
    }
  }

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

  protected val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val action: String? = intent.action
      when(action!!) {
        BluetoothDevice.ACTION_FOUND -> {
          val device: BluetoothDevice? =
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

          getDeviceInfo(device!!)
        }
        BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
          val device: BluetoothDevice? =
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

          getPairingStatus(device!!, getIsPaired(intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)))
        }
      }
    }
  }

  fun getPairingStatus(device: BluetoothDevice, caseValue: Any?){
    eventSink!!.success(mapOf("pairingStatus" to mapOf(device!!.toString() to caseValue!!.toString())))
  }

  fun getJustPairedDevices(result: Result){
    val resultForPairedDevices: MutableList<Map<String, String?>> = mutableListOf()
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val uuids: MutableMap<String, String> = mutableMapOf()

    if(! pairedDevices.isNullOrEmpty()){
      pairedDevices?.forEach { device ->

        device!!.getUuids().forEachIndexed{index: Int, uuid: ParcelUuid? -> 
          uuids.put("uuid$index", uuid!!.getUuid().toString())
        }

        resultForPairedDevices.add(
          mapOf(
            "name" to device!!.name?.toString(),
            "aliasName" to device!!.getAlias()?.toString(),
            "address" to device!!.address?.toString(),
            "type" to getType(device!!.getType()?.toInt()),
            "uuids" to uuids.entries?.joinToString()
          ) 
        )
      }
    }

    result!!.success(resultForPairedDevices)
  }

  fun getDeviceInfo(device: BluetoothDevice?){
    val uuids = mutableMapOf<String, String>()

    try {
      //only get paired device's uuids
      if(device!!.getUuids() != null)
      {
        device!!.getUuids().forEachIndexed{index: Int, uuid: ParcelUuid? -> 
          uuids.put("uuid$index", uuid!!.getUuid().toString())
        }
      }

      eventSink!!.success(
        mapOf(
            "name" to device!!.name?.toString(),
            "aliasName" to device!!.getAlias()?.toString(),
            "address" to device!!.address?.toString(),
            "type" to getType(device!!.getType()?.toInt()),
            "isPaired" to getIsPaired(device!!.getBondState()?.toInt()),
            "uuids" to uuids.entries?.joinToString()
          )
      )
    }
    catch(e: Throwable) {
      Log.d("SomeException", e.toString())
    }
  }

  fun startDiscovery (result: Result){
    var valueBS = bluetoothAdapter?.startDiscovery()

    filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(receiver, filter)
    
    Log.d("startDiscovery", "discovery started")

    result!!.success(valueBS)
  }  
  
  fun stopDiscovery (result: Result){
    bluetoothAdapter?.cancelDiscovery()
    Log.d("stopDiscovery", "discovery stopped")

    result!!.success(true)
  }

 fun startPairing(result: Result, macAddress: String?){
  var bluetoothDevice: BluetoothDevice = bluetoothAdapter!!.getRemoteDevice(macAddress!!)

  var isPaired = getIsPaired(bluetoothDevice.getBondState())?.toString()

  if(isPaired == "PAIRED"){
    createConnectionToDevice(result, bluetoothDevice.address)
  }else{
    bluetoothDevice.createBond()

    filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    context.registerReceiver(receiver, filter)
  }
}

/* 

  TODO remove pair

*/

/* fun removePaired(result: Result, macAddress: String?){
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

 fun createConnectionToDevice(result: Result, macAddress: String?){
    var bluetoothDevice: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(macAddress!!)
    
    ConnectThread(bluetoothDevice).run()
 }

 fun closeConnectionFromDevice(result: Result, macAddress: String?){
    var bluetoothDevice: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(macAddress!!)

    ConnectThread(bluetoothDevice).cancel()
 }

 /*

    Devices connection

 */

  protected inner class ConnectThread(device: BluetoothDevice?) : Thread() {
    protected var mmDevice: BluetoothDevice? = device
  
    public override fun run() {
      mmSocket = mmDevice!!.createRfcommSocketToServiceRecord(forBluetoothConnectionUUID)
      bluetoothAdapter?.cancelDiscovery()

        try {
            mmSocket?.let { socket ->
              socket.connect()
            }

            eventSink!!.success(mapOf("connect" to true))
        }
        catch(e: Throwable) {
          Log.d("ConnectThread", e.toString())

          eventSink!!.success(mapOf("connect" to false))
        }    
    }
    
    fun cancel() {
        try {
          mmSocket?.let { socket ->
            socket.close()
          }

          eventSink!!.success(mapOf("disconnect" to true))
        } catch (e: Throwable) {
            Log.d("ConnectThread", e.toString())

            eventSink!!.success(mapOf("disconnect" to false))
        }
    }
 }
}
