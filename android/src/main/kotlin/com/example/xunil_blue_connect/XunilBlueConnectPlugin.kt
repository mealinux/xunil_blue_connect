package com.example.xunil_blue_connect

import androidx.annotation.NonNull
import android.bluetooth.BluetoothAdapter

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** XunilBlueConnectPlugin */
class XunilBlueConnectPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "bluetooth")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "is_bluetooth_available") {
      val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      //if bluetooth is null means the device doesn't support bluetooth
      if(bluetoothAdapter == null){
          result.success(null)
      }

      //if bluetooth is true means the device's bluetooh is active
      //if not means the device's bluetooh isn't active
      result.success(bluetoothAdapter.isEnabled())
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
