# xunil_blue_connect

  

This package is bluetooth management.

import

    import 'package:xunil_blue_connect/xunil_blue_connect.dart';

  Call 

      XunilBlueConnect  blueConnect = XunilBlueConnect();

And use like this as  async

    await  blueConnect.isBluetoothAvailable();

For Bluetooth permission

add in `/android/app/src/main/AndroidManifest.xml`

    <uses-permission android:name="android.permission.BLUETOOTH" />


TODO For Android
- [x] Check bluetooth available
- [ ] Establishing a BLE connection
- [ ] Discover devices
- [ ] Discover services
- [ ] Read / write a characteristic
- [ ] Connection of multiple devices
- [ ] Implement BLE
- [ ] Clear GATT cache
- [ ] Negotiate MTU size
 

TODO For IOS
- [ ] Check bluetooth available
- [ ] Implement IOS
- [ ] Establishing a BLE connection
- [ ] Discover devices
- [ ] Discover services
- [ ] Read / write a characteristic
- [ ] Connection of multiple devices
- [ ] Implement BLE
- [ ] Clear GATT cache
- [ ] Negotiate MTU size