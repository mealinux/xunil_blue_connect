//Euzübillâhimineşşeytânirracîm bismillâhirrahmânirrahîm
import 'dart:async';
import 'package:flutter/services.dart';

class XunilBlueConnect {
  //for general call-end functions broadcast receiver
  static const MethodChannel _channel = MethodChannel('bluetooth');

  //for only found a device broadcast receiver
  static const EventChannel _eventDeviceResultStreamChannel =
      EventChannel('deviceResultStream');

  //for only connecting and pairing status broadcast receiver for now
  static const EventChannel _eventStatusStreamChannel =
      EventChannel('anyStatusResultStream');

  //bluetooth is active or inactive, output: [true] or [false]
  Future<bool> isBluetoothAvailable() async {
    final bool isBluetoothAvailable =
        await _channel.invokeMethod('IS_BLUETOOTH_AVAILABLE');

    return isBluetoothAvailable;
  }

  //location is active or inactive, output: [true] or [false]
  Future<bool> checkSettingLocation() async {
    var checkSettingLocation =
        await _channel.invokeMethod('CHECK_SETTING_LOCATION');

    return checkSettingLocation;
  }

  //goes to device's location setting for turn on
  Future<void> goLocationForEnable() async {
    await _channel.invokeMethod('GO_LOCATION_FOR_ENABLE');
  }

  //ask location permission for use
  //if already active return [true] or not return [false]
  Future<bool> applyPermissionLocation() async {
    var isPermissionLocation =
        await _channel.invokeMethod('APPLY_PERMISSION_LOCATION');

    return isPermissionLocation;
  }

  //for device's bluetooth turn on, if already active [void]
  //also first ask permission for use bluetooth on android 12
  Future<void> bluetoothSetEnable() async {
    await _channel.invokeMethod('SET_BLUETOOTH_ENABLE');
  }

  //for device's bluetooth turn off, if already inactive [void]
  //also first ask permission for use bluetooth on android 12
  Future<void> bluetoothSetDisable() async {
    await _channel.invokeMethod('SET_BLUETOOTH_DISABLE');
  }

  //start discovery for find a device, return [BluetoothDevice] type [String]
  //also just scan for 12 seconds (OS default)
  Future<void> startDiscovery() async {
    await _channel.invokeMethod('START_DISCOVERY');
  }

  //stop discovery for find a device
  Future<void> stopDiscovery() async {
    await _channel.invokeMethod('STOP_DISCOVERY');
  }

  //this is eventchannel foır just listen find a device
  Stream get listenDeviceResults async* {
    yield* _eventDeviceResultStreamChannel.receiveBroadcastStream();
  }

  //this is eventchannel foır just listen connecting and pairing status for now
  Stream get listenStatus async* {
    yield* _eventStatusStreamChannel.receiveBroadcastStream();
  }

  //pair with a device, takes [macAddress] type [String]
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

  //get all paired devices
  Future getPairedDevices() async {
    return await _channel.invokeMethod('GET_PAIRED_DEVICES');
  }

  //connect a device, takes two parameters [macAddress] and [uuidString], both types [String]
  // if you don't give [uuidString] parameter, it takes serial port uuid (00001101-0000-1000-8000-00805F9B34FB) as default
  Future<void> connect({required String macAddress, String? uuidString}) async {
    try {
      await _channel.invokeMethod('CONNECT_TO_DEVICE',
          {'macAddress': macAddress, "UUIDString": uuidString});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  //if you don't give [macAddress], it disconnect last connection
  //also you can give [macAddress]
  //takes [macAddress] named parameter, type is [String]
  Future<void> disconnect({String? macAddress}) async {
    try {
      await _channel
          .invokeMethod('CLOSE_TO_DEVICE', {'macAddress': macAddress});
    } catch (e) {
      print("Failed to connect: $e");
    }
  }

  //takes two parameters
  //one of parameters is [data], [data] is any text for now, type is [String]
  //another of parameters is [autoConnect],
  //[autoConnect] is reconnect to device because
  //status will be disconnect when write is done (OS default),
  //type is [Boolean]
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
