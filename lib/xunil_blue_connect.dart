//Euzübillâhimineşşeytânirracîm bismillâhirrahmânirrahîm
import 'dart:async';
import 'package:flutter/services.dart';

class XunilBlueConnect {
  static const MethodChannel _channel = MethodChannel('bluetooth');
  static const EventChannel _eventDeviceResultStreamChannel =
      EventChannel('deviceResultStream');
  static const EventChannel _eventStatusStreamChannel =
      EventChannel('anyStatusResultStream');

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

  Stream get listenDeviceResults async* {
    yield* _eventDeviceResultStreamChannel.receiveBroadcastStream();
  }

  Stream get listenStatus async* {
    yield* _eventStatusStreamChannel.receiveBroadcastStream();
  }

  Future<void> pair({required String macAddress}) async {
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

  Future<void> connect({required String macAddress, String? UUIDString}) async {
    try {
      await _channel.invokeMethod('CONNECT_TO_DEVICE',
          {'macAddress': macAddress, "UUIDString": UUIDString});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  Future<void> disconnect({String? macAddress}) async {
    try {
      await _channel
          .invokeMethod('CLOSE_TO_DEVICE', {'macAddress': macAddress});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  Future<void> write({String? data, bool? autoConnect}) async {
    try {
      await _channel.invokeMethod(
          'WRITE_TO_DEVICE', {"data": data, "autoConnect": autoConnect});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  /*

    TODO read and reset

  */
  /* Future<void> read() async {
    try {
      await _channel.invokeMethod('READ_FROM_DEVICE');
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  Future<void> reset() async {
    try {
      await _channel.invokeMethod('RESET_READ_DEVICE');
    } catch (e) {
      print("Failed to connect: $e");
    }
  } */
}
