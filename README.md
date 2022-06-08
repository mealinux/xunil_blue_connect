# xunil_blue_connect

This package is bluetooth management as basic. The package supports just ANDROID 
but will get IOS support.

Also the package supports  classic bluetooth now but will get BLE technology soon 

Only For Android now

- [x] Check location setting
- [x] Apply location permission
- [x] Check bluetooth available
- [x] Bluetooth set enable/disable
- [x] Establishing/Close a connection
- [x] Pair a device
- [x] Start/Stop discovery
- [x] Discover devices
- [x] Get paired device's service uuids
- [ ] iOS Support
- [ ] BLE support


import

    import 'package:xunil_blue_connect/xunil_blue_connect.dart';

Call

    XunilBlueConnect blueConnect = XunilBlueConnect();

## **And use like these as async**

**Bluetooth check available**

    await blueConnect.isBluetoothAvailable();

**Check location setting**

    await blueConnect.checkSettingLocation();

**Apply location permission**

    await blueConnect.applyPermissionLocation();

**Bluetooth set enable**

    await blueConnect.bluetoothSetEnable();

**Bluetooth set disable**

    await blueConnect.bluetoothSetDisable();

## ***Scan for devices***

**first start discovery**

    await blueConnect.startDiscovery();

**then listen results**

    blueConnect.listenResults.listen((device){
	    print(device);
    });

**Scan returns these parameters;**

`name` -> device's name, given by hardware (default)
for example: `alikilic`

`aliasName` -> device's name, given by user
for example: `Ali Kılıç's Laptop`

`address` -> device's mac address (only android)
for example: `94:E9:79:AA:8B:8E`

`type` -> device's type
for example: `PLEASE READ NOTE`

`isPaired` -> device's pair status
for example: `PLEASE READ NOTE 2`

`uuids` -> device's uuid or uuids (only for paired devices)
for example: `0000110a-0000-1000-8000-00805f9b34fb`

>NOTE: Device types return as DEVICE_TYPE_UNKNOWN, DEVICE_TYPE_CLASSIC, DEVICE_TYPE_LE, DEVICE_TYPE_DUAL, ERROR.
if returns any case expect those is UNKNOWN_TYPE

>NOTE 2: Device pair returns as PAIRED, PAIRED_NONE, PAIRING.
if returns any case expect those is UNKNOWN_PAIRED

> NOTE 3: UUIDS return only paired devices

**if you stop listening**

    await blueConnect.stopDiscovery();

## ***Establishing a connection***

> this method takes a parameter ``macAddress`` type ``String``

    await blueConnect.connect(macAddress: device.macAddress);

**if you want to disconnect a connected device**

> this method takes a parameter ``macAddress`` type ``String``

    await blueConnect.disconnect(macAddress: device.macAddress);

## ***Pair a device***

> this method takes a parameter ``macAddress`` type ``String``

    await blueConnect.pair(macAddress: device.macAddress);

**if you want to just get all paired devices**

    await blueConnect.getPairedDevices();

## ***For Bluetooth and location permission***

add in `/android/app/src/main/AndroidManifest.xml`

**For Bluetooth**

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

**For Location**

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />