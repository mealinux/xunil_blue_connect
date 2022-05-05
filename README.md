
# xunil_blue_connect

This package is bluetooth management.

don't forget import this

`import 'package:xunil_blue_connect/xunil_blue_connect.dart';`

  Actually just like that for now
`await XunilBlueConnect.isBluetoothAvailable();`





    import 'package:flutter/material.dart';
    
    import 'package:xunil_blue_connect/xunil_blue_connect.dart';
    

    class MainBody extends StatefulWidget {
    
    const MainBody({Key? key}) : super(key: key);
    
    @override
    State<MainBody> createState() => _BodyState();
    
    }
    
    class _BodyState extends State<MainBody> {
    
    bool _isBluetoothAvailable = false;
  
    @override
    void initState() {
    
        super.initState();
        
        initBlueToothAvailable();
        
    }
    
    //if bluetooth return null, it means the device doesn't support bluetooth
    
    Future<void> initBlueToothAvailable() async {
    
        bool isBluetoothAvailable;
        
        isBluetoothAvailable = await XunilBlueConnect.isBluetoothAvailable();
        
        if (!mounted) return;
        
        setState(() {
        
          _isBluetoothAvailable = isBluetoothAvailable;
        
        });
    
    }
    
      
    @override
    Widget build(BuildContext context) {
        return Center(
            child: SizedBox(
                height: MediaQuery.of(context).size.height * 0.33,
                
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                        Text('Bluetooth is ${_isBluetoothAvailable ? 'ON' : 'OFF'}'),
                        ElevatedButton(
                        onPressed: () async {
                        var isBlue = await XunilBlueConnect.isBluetoothAvailable();
                          setState(() {
                              _isBluetoothAvailable = isBlue;
                          });
                        },
                        child: const Text('Check'),
                        )
                    ],
                ),
            ),
        );
    }
    }

For Bluetooth permission
add in `/android/app/src/main/AndroidManifest.xml`

    <uses-permission  android:name="android.permission.BLUETOOTH"  />


TODO For Android
 - [ ] Establishing a BLE connection
 - [ ] Discover devices
 - [ ] Discover services
 - [ ] Read / write a characteristic
 - [ ] Connection of multiple devices
 - [ ] Implement BLE
 - [ ] Clear GATT cache
 - [ ]  Negotiate MTU size


TODO For IOS
 - [ ] Implement IOS
 - [ ] Establishing a BLE connection
 - [ ] Discover devices
 - [ ] Discover services
 - [ ] Read / write a characteristic
 - [ ] Connection of multiple devices
 - [ ] Implement BLE
 - [ ] Clear GATT cache
 - [ ] Negotiate MTU size

