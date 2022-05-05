import 'dart:async';

import 'package:flutter/services.dart';

class XunilBlueConnect {
  static const MethodChannel _channel = MethodChannel('bluetooth');

  Future<bool> isBluetoothAvailable() async {
    final bool isBluetoothAvailable =
        await _channel.invokeMethod('is_bluetooth_available');

    print('Bluetooth ${isBluetoothAvailable ? 'ON' : 'OFF'}');

    return isBluetoothAvailable;
  }
}
