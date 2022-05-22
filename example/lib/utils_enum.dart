enum DevicePaired {
  PAIRED,
  PAIRED_NONE,
  PAIRING,
  UNKNOWN_PAIRED,
}

enum DeviceType {
  DEVICE_TYPE_UNKNOWN,
  DEVICE_TYPE_CLASSIC,
  DEVICE_TYPE_LE,
  DEVICE_TYPE_DUAL,
  ERROR,
  UNKNOWN_TYPE,
}

extension DeviceTypeExtension on DeviceType {
  String get deviceType {
    switch (this) {
      case DeviceType.DEVICE_TYPE_UNKNOWN:
        return "DEVICE_TYPE_UNKNOWN";
      case DeviceType.DEVICE_TYPE_CLASSIC:
        return "DEVICE_TYPE_CLASSIC";
      case DeviceType.DEVICE_TYPE_LE:
        return "DEVICE_TYPE_LE";
      case DeviceType.DEVICE_TYPE_DUAL:
        return "DEVICE_TYPE_DUAL";
      case DeviceType.ERROR:
        return "ERROR";
      case DeviceType.UNKNOWN_TYPE:
        return "UNKNOWN_TYPE";
      default:
        return "ERROR";
    }
  }
}

extension DevicePairedExtension on DevicePaired {
  String get devicePaired {
    switch (this) {
      case DevicePaired.PAIRED:
        return "PAIRED";
      case DevicePaired.PAIRED_NONE:
        return "PAIRED_NONE";
      case DevicePaired.PAIRING:
        return "PAIRING";
      case DevicePaired.UNKNOWN_PAIRED:
        return "UNKNOWN_PAIRED";
      default:
        return "ERROR";
    }
  }
}
