//Euzübillâhimineşşeytânirracîm bismillâhirrahmânirrahîm
import 'dart:async';
import 'package:flutter/services.dart';

class XunilBlueConnect {
  static const MethodChannel _channel = MethodChannel('bluetooth');
  static const EventChannel _eventStartStreamChannel =
      EventChannel('bluetoothStream');

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

  Future<void> pair({required macAddress}) async {
    try {
      await _channel.invokeMethod('PAIR_TO_DEVICE', {'macAddress': macAddress});
    } catch (e) {
      print("Failed to pair: $e");
    }
  }

/* 

  TODO remove pair

*/
/*   Future<String> unpair({required macAddress}) async {
    var unpairAnswer = "";
    try {
      unpairAnswer = await _channel
          .invokeMethod('UNPAIR_TO_DEVICE', {'macAddress': macAddress});
    } catch (e) {
      print("Failed to pair: $e");
    }

    return unpairAnswer;
  } */

  Future getPairedDevices() async {
    return await _channel.invokeMethod('GET_PAIRED_DEVICES');
  }

  Future<void> connect({required macAddress}) async {
    try {
      await _channel
          .invokeMethod('CONNECT_TO_DEVICE', {'macAddress': macAddress});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  Future<void> disconnect({required macAddress}) async {
    try {
      await _channel
          .invokeMethod('CLOSE_TO_DEVICE', {'macAddress': macAddress});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }
}
