import 'dart:async';
import 'package:flutter/services.dart';

class XunilBlueConnect {
  static const MethodChannel _channel = MethodChannel('bluetooth');
  static const EventChannel _eventStartStreamChannel =
      EventChannel('bluetoothStartStream');

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

  Future<bool> goLocationForEnable() async {
    var goLocationForEnable =
        await _channel.invokeMethod('GO_LOCATION_FOR_ENABLE');

    return goLocationForEnable;
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

  Future<void> startDiscovery() async {
    await _channel.invokeMethod('START_DISCOVERY');
  }

  Future<void> stopDiscovery() async {
    await _channel.invokeMethod('STOP_DISCOVERY');
  }

  Stream get listenResults async* {
    yield* _eventStartStreamChannel.receiveBroadcastStream();
  }
}
