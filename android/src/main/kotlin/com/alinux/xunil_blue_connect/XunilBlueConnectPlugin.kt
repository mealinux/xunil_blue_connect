package com.alinux.xunil_blue_connect

import android.provider.Settings
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable
import android.os.ParcelUuid
import android.util.Log

import androidx.annotation.NonNull
import androidx.activity.ComponentActivity

import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

/* import androidx.appcompat.app.AppCompatActivity; */

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
class XunilBlueConnectPlugin: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware, ComponentActivity() {

  var intent1: Intent? = null
  protected lateinit var channel: MethodChannel
  protected lateinit var eventStartStreamChannel: EventChannel
  protected lateinit var context: Context
  protected var activity: Activity? = null
  protected var result: Result? = null
  private lateinit var locationManager: LocationManager
  var locationPermission: Boolean = false
  val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  var eventSink: EventChannel.EventSink? = null
  var filter: IntentFilter? = null

/*   override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("onCreate", "called onCreate")
  }

  override fun onDestroy() {
    super.onDestroy()
    this.context.unregisterReceiver(this.receiver)
    Log.d("onDestroy", "called onDestroy")
  } */

  override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
    this.eventSink = eventSink
    this.filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    this.context.registerReceiver(this.receiver, this.filter)
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

    this.context = flutterPluginBinding.applicationContext
  }

  override fun onDetachedFromActivity() {}

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
      this.activity = binding.activity;
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


      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  fun isBluetoothAvailable(result: Result) {

    //if bluetooth is null means the device doesn't support bluetooth
    if(this.bluetoothAdapter == null){

      Log.d("isBluetoothAvailable", "Bluetooth is NULL")

      result.success(null)
      return
    }

    Log.d("isBluetoothAvailable", "Bluetooth is " + bluetoothAdapter.isEnabled())

    //if bluetooth is true means the device's bluetooh is active
    //if not means the device's bluetooh isn't active
    result.success(this.bluetoothAdapter.isEnabled());
  }

  fun applyPermissionLocation(result: Result){

    var locationPermission: Boolean = false

    if (ContextCompat.checkSelfPermission(this.context,
        android.Manifest.permission.ACCESS_FINE_LOCATION) !==
        PackageManager.PERMISSION_GRANTED) {

       ActivityCompat.requestPermissions(this.activity!!, arrayOf(
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

    val locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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

    val locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if(! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        this.activity!!.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1)

        goLocationForEnable = true
    }
    else{
     
      Log.d("goLocationForEnable", "Already still enabled")
      goLocationForEnable = true
    }

    result.success(goLocationForEnable)
  }

  fun bluetoohSetEnable(){

    if(! this.bluetoothAdapter.isEnabled())
    {
      this.bluetoothAdapter.enable()
      Log.d("bluetoohSetEnable", "Enable")
    }
  }

  fun bluetoohSetDisable(){

    if(this.bluetoothAdapter.isEnabled())
    {
      this.bluetoothAdapter.disable()
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
          }
      }
  }

  fun getDeviceInfo(device: BluetoothDevice?){
    val uuids = mutableMapOf<String, String>()

    //only get paired device's uuids
    if(device!!.getUuids() != null)
    {
      device!!.getUuids().forEachIndexed{index: Int, uuid: ParcelUuid -> 
        uuids.put("uuid$index", uuid.getUuid().toString())
      }
    }
                
    val deviceName = device!!.name
    val deviceAliasName = device!!.getAlias()
    val deviceHardwareAddress = device!!.address 
    val type = device!!.getType()
    val isPaired = device!!.getBondState()
    val allUuids = uuids

    eventSink!!.success(
      mapOf(
          "name" to deviceName?.toString(),
          "aliasName" to deviceAliasName?.toString(),
          "address" to deviceHardwareAddress?.toString(),
          "type" to getType(type)?.toString(),
          "isPaired" to getIsPaired(isPaired)?.toString(),
          "uuids" to allUuids.entries.joinToString()
        )
      )
  }

  fun startDiscovery (result: Result){
    var valueBS = this.bluetoothAdapter.startDiscovery()
    Log.d("startDiscovery", when(valueBS){true -> "true" false -> "false"})
    Log.d("startDiscovery", "discovery started")

    result!!.success(valueBS)
  }  
  
  fun stopDiscovery (result: Result){
    this.bluetoothAdapter.cancelDiscovery()
    this.context.unregisterReceiver(this.receiver)
    Log.d("stopDiscovery", "discovery stopped")

    result!!.success(true)
  }
}
