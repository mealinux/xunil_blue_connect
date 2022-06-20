import 'dart:convert';
import 'package:xunil_blue_connect_example/uuid.dart';

class BluetoothDevice {
  final String? name;
  final String? aliasName;
  final String? address;
  final String? isPaired;
  final String? type;
  final List<UUIDS> uuids;

  BluetoothDevice(
      {this.name,
      this.aliasName,
      this.address,
      this.isPaired,
      this.type,
      required this.uuids});

  factory BluetoothDevice.fromJson(Map<String, dynamic> json) =>
      BluetoothDevice(
        name: json["name"].toString(),
        aliasName: json["aliasName"].toString(),
        address: json["address"].toString(),
        type: json["type"].toString(),
        isPaired: json["isPaired"].toString(),
        uuids: jsonDecode(json["uuids"]).isNotEmpty
            ? List<UUIDS>.from(
                jsonDecode(json["uuids"]).map(
                  (element) => UUIDS.fromJson(element),
                ),
              )
            : List<UUIDS>.from([]),
      );
}
