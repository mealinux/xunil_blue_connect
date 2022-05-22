class BluetoothDevice {
  final String? name;
  final String? aliasName;
  final String? address;
  final String? isPaired;
  final String? type;
  final String? uuids;

  BluetoothDevice(
      {this.name,
      this.aliasName,
      this.address,
      this.isPaired,
      this.type,
      this.uuids});

  factory BluetoothDevice.fromJson(Map<Object?, Object?> json) =>
      BluetoothDevice(
        name: json["name"].toString(),
        aliasName: json["aliasName"].toString(),
        address: json["address"].toString(),
        type: json["type"].toString(),
        isPaired: json["isPaired"].toString(),
        uuids: json["uuids"].toString(),
      );
}
