package com.alinux.xunil_blue_connect

import android.app.Activity;
import android.content.Context
import android.os.Bundle;
import android.os.Build
import android.util.Log
import android.provider.Settings

import android.content.ActivityNotFoundException
import android.content.Intent

import androidx.annotation.NonNull
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager

import android.location.LocationManager

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.LocationSettingsResponse;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** XunilBlueConnectPlugin */
class XunilBlueConnectPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, ComponentActivity() {
  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private var activity: Activity? = null
  private var mLocationRequest: LocationRequest? = null
  private lateinit var locationManager: LocationManager
  var intent1: Intent? = null
  var locationPermission: Boolean = false

  override fun onCreate(savedInstanceState : Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("onCreate", "onCreate çalıştı")
  }
  
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bluetooth")
    channel.setMethodCallHandler(this)

    this.context = flutterPluginBinding.applicationContext
  }

  override fun onDetachedFromActivity() {
      TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
      TODO("Not yet implemented")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
      this.activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {
      TODO("Not yet implemented")
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method){
      "IS_BLUETOOTH_AVAILABLE" -> isBluetoothAvailable(result)
      "CHECK_SETTING_LOCATION" -> isOnLocation(result)
      "APPLY_PERMISSION_LOCATION" -> applyPermissionLocation(result)
      "DISCOVER_DEVICES" -> discoverDevices(result)

      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  fun isBluetoothAvailable(result: Result) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //if bluetooth is null means the device doesn't support bluetooth
    if(bluetoothAdapter == null){

      Log.d("isBluetoothAvailable", "Bluetooth is NULL")

      result.success(null)
      return
    }

    Log.d("isBluetoothAvailable", "Bluetooth is " + bluetoothAdapter.isEnabled())

    //if bluetooth is true means the device's bluetooh is active
    //if not means the device's bluetooh isn't active
    result.success(bluetoothAdapter.isEnabled());
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
      }else{
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
    }else{
      Log.d("isEnableOrDisable", "Disable")
      isOnLocation = false
    }

    result.success(isOnLocation)
  }

  fun discoverDevices(result: Result){

  }

}
