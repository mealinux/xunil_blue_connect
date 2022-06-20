# xunil_blue_connect

This package is bluetooth management as basic. The package supports just ANDROID 
but will get IOS support.

Also the package supports  classic bluetooth now but will get Bluetooth Low Energy Technology soon 

Only For Android now

- [x] Check location setting
- [x] Apply location permission
- [x] Check bluetooth available
- [x] Bluetooth set enable/disable
- [x] Establishing/Close a connection
- [x] Pair a device
- [x] Write
- [x] Status reconnect when disconnect for Write
- [x] Start/Stop discovery
- [x] Discover devices
- [x] Get paired device's service uuids
- [x] Get name and short description with uuid
- [x] Listen connection and pairing status
- [x] Support Android 12
- [ ] iOS Support
- [ ] BLE support
- [ ] Read/Reset
- [ ] Multi connection
- [ ] Auto connect
- [ ] Reconnect
- [ ] Remove pair
- [ ] Listen Start/Stop discovery status

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

> Android 11 and lower versions can be enable directly but first ask bluetooth use permission on Android 12

    await blueConnect.bluetoothSetEnable();

**Bluetooth set disable**

> Android 11 and lower versions can be disable directly but first ask bluetooth use permission on Android 12

    await blueConnect.bluetoothSetDisable();

## ***Scan for devices***

Android scans devices just for 12 seconds, look for detail: [Official link](https://developer.android.com/guide/topics/connectivity/bluetooth/find-bluetooth-devices#discover-devices)

> Use 12 seconds with timer on this plugin's example app, discovery listens feature in TODO

**first start discovery**

    await blueConnect.startDiscovery();

**then listen results**

    blueConnect.listenDeviceResults.listen((device){
	    print(device);
    });

Or use with **StreamBuilder** for listen

>BluetoothDevice is a model from example app

    StreamBuilder(
	    stream: blueConnect.listenDeviceResults,
	    builder: (context, snapshot) {
		    if (snapshot.hasData) {
			    var device = BluetoothDevice.fromJson(
				    jsonDecode(snapshot.data as String)
			    );
				print(device)
		    }
	    },
    ),

**Scan returns these parameters;**

`name` -> device's name, given by hardware (default)
for example: `alikilic`

`aliasName` -> device's name, given by user
for example: `Ali Kılıç's Laptop`

`address` -> device's mac address (only android)
for example: `94:E9:68:EA:8B:8R`

`type` -> device's type
for example: `PLEASE READ NOTE`

`isPaired` -> device's pair status
for example: `PLEASE READ NOTE 2`

`uuids` -> device's uuid or uuids (only for paired devices)
for example: `0000110a-0000-1000-8000-00805f9b34fb`

>NOTE: Device types return as DEVICE_TYPE_UNKNOWN, DEVICE_TYPE_CLASSIC, DEVICE_TYPE_LE, DEVICE_TYPE_DUAL, ERROR.
if returns else any case than these is UNKNOWN_TYPE

>NOTE 2: Device pair returns as PAIRED, PAIRED_NONE.
if returns else any case than these is UNKNOWN_PAIRED

>NOTE 3: UUIDS return only paired devices

**For UUIDS** 

uuid returns with its `name` and `short_description`

like this

    {name: "Message Access Profile", short_description: "Allows exchange of messages between devices", uuid: "00001134-0000-1000-8000-00805F9B34FB"}

> also you can get uuid list as json file from this link: [UUID List](https://github.com/mealinux/xunil_blue_connect/blob/main/android/src/main/assets/uuids_list.json) 
> this list is uuids for use as general means includes some services and profiles
> if you want to all list (but not json) just pdf format from this link:
> [Official All UUID List](https://btprodspecificationrefs.blob.core.windows.net/assigned-values/16-bit%20UUID%20Numbers%20Document.pdf)

**if you stop listening**

    await blueConnect.stopDiscovery();



## *Connection and Status Listening*

>Connection returns `STATUS_CONNECTING` as key with `MAC_ADDRESS` as well
>and
>Status returns `STATUS_PAIRING` as key

     blueConnect.listenStatus.listen((status){
	   	    print(status);
     });
     
or you can use **StreamBuilder**

`import 'package:xunil_blue_connect/utils/status.dart';`



    StreamBuilder(
	    stream: blueConnect.listenStatus,
	    builder: (context, snapshot) {
		    if (snapshot.hasData) {
		    
			    var  STATUS = jsonDecode(snapshot.data  as  String);
    
			    //STATUS_PAIRING
			    
			    switch (STATUS['STATUS_PAIRING']) {
				    case  PairedStatus.PAIRED:
					    //do something
				    break;
				    case  PairedStatus.PAIRING:
					    //do something
				    break;
				    case  PairedStatus.PAIRED_NONE:
					    //do something
				    break;
				    case  PairedStatus.UNKNOWN_PAIRED:
					    //do something
				    break;
			    }
			    
			    //STATUS_CONNECTING
			    
			    // also connection status returns with STATUS['MAC_ADDRESS'] 		
			  					
			    switch (STATUS['STATUS_CONNECTING']) {
				    case  ConnectingStatus.STATE_CONNECTED:
					    //do something
				    break
				    case  ConnectingStatus.STATE_DISCONNECTED:
					    //do something
				    break;
			    }
		    }
	    },
    ),

## ***Establishing a connection***

> this method takes two parameters
> one of them is `macAddress` type `String`
> another of them is `UUIDString` type `String`

 if you don't give `UUIDString` parameter, it gives `0x1101`  serial port uuid from itself means it is default

	// takes it as default 00001101-0000-1000-8000-00805F9B34FB
	// means serial port uuid
    await blueConnect.connect(macAddress: device.macAddress);
  
  
    // or you can give any uuid
    await blueConnect.connect(macAddress: device.macAddress, UUIDString: "00001200-0000-1000-8000-00805F9B34FB");

**if you want to disconnect from a connected device**

>this method takes a parameter ``macAddress`` type ``String``

   	// if you want to don't give a parameter so last connection will be disconnect
    await blueConnect.disconnect();
  
  
    // or you can give any connected device's macAddress
    await blueConnect.connect(macAddress: device.macAddress);

## ***Pair a device***

>this method takes a parameter `macAddress` type `String`

    await blueConnect.pair(macAddress: device.macAddress);

**if you want to just get all paired devices**

    await blueConnect.getPairedDevices();

## *Write, Read and Reset*

>NOTE 4: `Read` and `Reset` is in TODO List

**Write**

> Write takes two parameters,
> One of them is `data`, type `String` for now, other stream features (e.g. audio stream) in TODO
> 
> Another parameter is `autoConnect`, type `bool`, device will disconnect when write job is done, if you want to reconnect `autoConnect` set `true` if you don't want so set `false` or null

>Tested virtual printer

    await blueConnect.write(data:"Lorem ipsum dolor sit amet.", autoConnect: true);

## ***For Bluetooth and location permission***

add in `/android/app/src/main/AndroidManifest.xml`

**For Bluetooth**

    <uses-permission  android:name="android.permission.BLUETOOTH"  android:maxSdkVersion="30"  />
    <uses-permission  android:name="android.permission.BLUETOOTH_ADMIN"  android:maxSdkVersion="30"  />
    
    // for Android 12
    <uses-permission  android:name="android.permission.BLUETOOTH_SCAN"  /> 
    <uses-permission  android:name="android.permission.BLUETOOTH_ADVERTISE"  />  
    <uses-permission  android:name="android.permission.BLUETOOTH_CONNECT"  />

**For Location**

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />