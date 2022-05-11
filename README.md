# xunil_blue_connect

This package is bluetooth management.

import

    import 'package:xunil_blue_connect/xunil_blue_connect.dart';

Call

    XunilBlueConnect blueConnect = XunilBlueConnect();

  

**And use like these as async**

Bluetooth check available

    await blueConnect.isBluetoothAvailable();

Check location setting

    await blueConnect.checkSettingLocation();

Apply location permission

    await blueConnect.applyPermissionLocation();


For Bluetooth and location permission

add in `/android/app/src/main/AndroidManifest.xml`

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission  android:name="android.permission.ACCESS_COARSE_LOCATION"  />
    <uses-permission  android:name="android.permission.ACCESS_FINE_LOCATION"  />
    <uses-permission  android:name="android.permission.ACCESS_BACKGROUND_LOCATION"  />


  

TODO For Android

- [x] Check location setting
- [x] Apply location permission
- [x] Check bluetooth available
- [ ] Establishing a connection
- [ ] Discover devices
- [ ] Discover services
- [ ] Read / write a characteristic
- [ ] Connection of multiple devices
- [ ] Implement BLE
- [ ] Clear GATT cache
- [ ] Negotiate MTU size

TODO For IOS

- [ ] Support IOS
- [ ] Check location setting
- [ ] Apply location permission
- [ ] Check bluetooth available
- [ ] Establishing a connection
- [ ] Discover devices
- [ ] Discover services
- [ ] Read / write a characteristic
- [ ] Connection of multiple devices
- [ ] Implement BLE
- [ ] Clear GATT cache
- [ ] Negotiate MTU size