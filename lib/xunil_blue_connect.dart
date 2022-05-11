import 'dart:async';
import 'package:flutter/services.dart';

class XunilBlueConnect {
  static const MethodChannel _channel = MethodChannel('bluetooth');

  Future<bool> isBluetoothAvailable() async {
    final bool isBluetoothAvailable =
        await _channel.invokeMethod('IS_BLUETOOTH_AVAILABLE');

    return isBluetoothAvailable;
  }

  Future<bool> checkSettingLocation() async {
    var checkSettingLocation =
        await _channel.invokeMethod('CHECK_SETTING_LOCATION');

    return checkSettingLocation;
  }

  Future<bool> applyPermissionLocation() async {
    var isPermissionLocation =
        await _channel.invokeMethod('APPLY_PERMISSION_LOCATION');

    return isPermissionLocation;
  }

  Future<void> bluetoothSetEnable() async {
    await _channel.invokeMethod('SET_BLUETOOTH_ENABLE');
  }

  Future<void> bluetoothSetDisable() async {
    await _channel.invokeMethod('SET_BLUETOOTH_DISABLE');
  }
}
