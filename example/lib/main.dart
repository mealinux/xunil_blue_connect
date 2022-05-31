import 'dart:async';

import 'package:flutter/material.dart';
import 'package:xunil_blue_connect/xunil_blue_connect.dart';
import 'package:xunil_blue_connect_example/device.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SafeArea(
        child: Scaffold(
          appBar: AppBar(
            title: const Text('Basic bluetooth management'),
          ),
          body: const Center(
            child: MainBody(),
          ),
        ),
      ),
    );
  }
}

class MainBody extends StatefulWidget {
  const MainBody({Key? key}) : super(key: key);

  @override
  State<MainBody> createState() => _BodyState();
}

class _BodyState extends State<MainBody> {
  bool _isBluetoothAvailable = false;
  bool _isLocationAvailable = false;
  bool _isLocationOn = false;
  List<BluetoothDevice>? devices = [];
  bool isLoading = false;

  //call the class
  XunilBlueConnect blueConnect = XunilBlueConnect();

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: MediaQuery.of(context).size.height * 0.8,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text('Bluetooth is ${_isBluetoothAvailable ? 'ON' : 'OFF'}'),
              Text(
                  'Location permission is ${_isLocationAvailable ? 'ON' : 'OFF'}'),
              Text('Location setting is ${_isLocationOn ? 'ON' : 'OFF'}'),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  //call the function but as async
                  //but if function return null means the device doesn't support bluetooth
                  var isBlue = await blueConnect.isBluetoothAvailable();
                  setState(() {
                    _isBluetoothAvailable = isBlue;
                  });
                },
                child: const Text('Check Bluetooth'),
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  await blueConnect.startDiscovery();
                  setState(() {
                    isLoading = true;
                  });
                  Timer(const Duration(seconds: 13), () async {
                    await blueConnect.stopDiscovery();
                    setState(() {
                      isLoading = false;
                    });
                  });
                },
                child: const Text('Start Discovery'),
              ),
              ElevatedButton(
                onPressed: () async {
                  await blueConnect.stopDiscovery();
                  setState(() {
                    isLoading = false;
                  });
                },
                child: const Text('Stop Discovery'),
              ),
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                style: ButtonStyle(
                    backgroundColor: MaterialStateProperty.all(
                        _isBluetoothAvailable
                            ? Colors.lightGreen
                            : Colors.blue)),
                onPressed: () async {
                  //call the function but as async
                  //bluetoothenable turn on
                  await blueConnect.bluetoothSetEnable();
                },
                child: const Text('Set Bluetooth Enable'),
              ),
              ElevatedButton(
                onPressed: () async {
                  //call the function but as async
                  //bluetoothenable turn off
                  await blueConnect.bluetoothSetDisable();
                },
                child: const Text('Set Bluetooth Disable'),
              )
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  //call the function but as async
                  //but if function return null means the device's location permission is off
                  var apply = await blueConnect.applyPermissionLocation();

                  setState(() {
                    _isLocationAvailable = apply;
                  });
                },
                child: const Text('Apply Location Permission'),
              ),
              ElevatedButton(
                style: ButtonStyle(
                    backgroundColor: MaterialStateProperty.all(
                        _isLocationOn ? Colors.lightGreen : Colors.blue)),
                onPressed: () async {
                  //call the function but as async
                  //but if function return null means the device's location is off
                  var isLocation = await blueConnect.checkSettingLocation();

                  setState(() {
                    _isLocationOn = isLocation;
                  });
                },
                child: const Text('Check Location'),
              )
            ],
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  //call the function but as async
                  //this code goes to location setting for set enable
                  await blueConnect.goLocationForEnable();
                },
                child: const Text('Go Location Settting for Enable'),
              )
            ],
          ),
          if (isLoading)
            const LinearProgressIndicator(color: Colors.orangeAccent),
          StreamBuilder(
            stream: blueConnect.listenResults,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var device = BluetoothDevice.fromJson(
                    snapshot.data as Map<Object?, Object?>);

                bool isEmpty = devices!
                    .where(
                      (localAddress) => localAddress.address == device.address,
                    )
                    .isEmpty;

                if (isEmpty) {
                  devices?.add(device);
                }

                return devices!.isNotEmpty
                    ? ListView.builder(
                        shrinkWrap: true,
                        itemCount: devices?.length,
                        padding: const EdgeInsets.all(10.0),
                        itemBuilder: (context, index) {
                          return ListTile(
                            title: Text(devices![index].name! +
                                " (${devices![index].aliasName!})"),
                            subtitle: Text(devices![index].address!),
                            trailing: ElevatedButton(
                              style: ButtonStyle(
                                backgroundColor: MaterialStateProperty.all(
                                    devices![index].isPaired! == "PAIRED"
                                        ? Colors.lightGreen
                                        : Colors.blue),
                              ),
                              onPressed: () {},
                              child: Text(devices![index].isPaired! == "PAIRED"
                                  ? "Paired"
                                  : "Not Paired"),
                            ),
                          );
                        },
                      )
                    : const SizedBox();
              }

              return const SizedBox();
            },
          ),
        ],
      ),
    );
  }
}
