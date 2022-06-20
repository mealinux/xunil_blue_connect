import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:xunil_blue_connect/xunil_blue_connect.dart';
import 'package:xunil_blue_connect_example/device.dart';
import 'package:xunil_blue_connect/utils/status.dart';
import 'package:xunil_blue_connect_example/uuid.dart';

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
    bottomsheetForUUIDS(List<UUIDS>? uuids) {
      return showModalBottomSheet(
        context: context,
        isDismissible: true,
        backgroundColor: Colors.white,
        builder: (data) {
          return SizedBox(
            height: MediaQuery.of(context).size.height * 0.6,
            child: ListView.builder(
              itemCount: uuids?.length,
              shrinkWrap: true,
              itemBuilder: (context, index) {
                return ListTile(
                  visualDensity: VisualDensity.adaptivePlatformDensity,
                  style: ListTileStyle.list,
                  title: Text(
                    uuids![index].name!.toString(),
                    style: const TextStyle(
                      fontSize: 12.0,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  subtitle: Text(
                    uuids[index].shortDescription!.toString(),
                    style: const TextStyle(
                      fontSize: 12.0,
                    ),
                  ),
                  trailing: Text(
                    uuids[index].uuid!.toString(),
                    style: const TextStyle(
                      fontSize: 12.0,
                    ),
                  ),
                );
              },
            ),
          );
        },
      );
    }

    return SizedBox(
      height: MediaQuery.of(context).size.height * 0.85,
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
                style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.all(
                      _isBluetoothAvailable ? Colors.lightGreen : Colors.blue),
                ),
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
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async {
                  var devices = await blueConnect.getPairedDevices();

                  print(devices);
                },
                child: const Text('Get paired devices'),
              )
            ],
          ),
          if (isLoading)
            const LinearProgressIndicator(color: Colors.orangeAccent),
          StreamBuilder(
            stream: blueConnect.listenStatus,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var STATUS = jsonDecode(snapshot.data as String);

                print(STATUS['MAC_ADDRESS']);

                switch (STATUS['STATUS_PAIRING']) {
                  case PairedStatus.PAIRED:
                    print(PairedStatus.PAIRED);
                    break;
                  case PairedStatus.PAIRING:
                    print(PairedStatus.PAIRING);
                    break;
                  case PairedStatus.PAIRED_NONE:
                    print(PairedStatus.PAIRED_NONE);
                    break;
                  case PairedStatus.UNKNOWN_PAIRED:
                    print(PairedStatus.UNKNOWN_PAIRED);
                    break;
                }

                switch (STATUS['STATUS_CONNECTING']) {
                  case ConnectingStatus.STATE_CONNECTED:
                    print(ConnectingStatus.STATE_CONNECTED);
                    break;
                  case ConnectingStatus.STATE_DISCONNECTED:
                    print(ConnectingStatus.STATE_DISCONNECTED);
                    break;
                }
              }
              return const SizedBox();
            },
          ),
          StreamBuilder(
            stream: blueConnect.listenDeviceResults,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var device = BluetoothDevice.fromJson(
                    jsonDecode(snapshot.data as String));

                bool isEmpty = devices!
                    .where(
                      (localAddress) => localAddress.address == device.address,
                    )
                    .isEmpty;

                if (isEmpty) {
                  devices?.add(device);
                }

                return devices!.isNotEmpty
                    ? Expanded(
                        child: ListView.builder(
                          shrinkWrap: true,
                          itemCount: devices?.length,
                          padding: const EdgeInsets.all(10.0),
                          itemBuilder: (context, index) {
                            return ListTile(
                              onLongPress: () {
                                bottomsheetForUUIDS(devices![index].uuids);
                              },
                              onTap: () async {
                                await blueConnect.connect(
                                  macAddress: devices![index].address!,
                                );
                              },
                              title: Text(devices![index].name! +
                                  " (${devices![index].aliasName!})"),
                              subtitle: Text(devices![index].address!),
                              trailing: Row(
                                mainAxisSize: MainAxisSize.min,
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment: MainAxisAlignment.end,
                                children: [
                                  SizedBox(
                                    width: 30.0,
                                    child: ElevatedButton(
                                      style: ButtonStyle(
                                        padding: MaterialStateProperty.all(
                                            EdgeInsets.zero),
                                        backgroundColor:
                                            MaterialStateProperty.all(
                                          devices![index].isPaired! == "PAIRED"
                                              ? Colors.lightGreen
                                              : Colors.blue,
                                        ),
                                      ),
                                      onPressed: () async {
                                        await blueConnect.pair(
                                          macAddress: devices![index].address!,
                                        );
                                      },
                                      child: Text(
                                        devices![index].isPaired! == "PAIRED"
                                            ? "C"
                                            : "P",
                                      ),
                                    ),
                                  ),
                                  const SizedBox(
                                    width: 5.0,
                                  ),
                                  if (devices![index].isPaired! == "PAIRED")
                                    SizedBox(
                                      width: 30.0,
                                      child: ElevatedButton(
                                        style: ButtonStyle(
                                          padding: MaterialStateProperty.all(
                                              EdgeInsets.zero),
                                          backgroundColor:
                                              MaterialStateProperty.all(
                                                  Colors.redAccent[400]),
                                        ),
                                        onPressed: () async {
                                          await blueConnect.disconnect();
                                        },
                                        child: const Text("D"),
                                      ),
                                    ),
                                  if (devices![index].isPaired! == "PAIRED")
                                    const SizedBox(
                                      width: 5.0,
                                    ),
                                  if (devices![index].isPaired! == "PAIRED")
                                    SizedBox(
                                      width: 30.0,
                                      child: ElevatedButton(
                                        style: ButtonStyle(
                                          padding: MaterialStateProperty.all(
                                              EdgeInsets.zero),
                                          backgroundColor:
                                              MaterialStateProperty.all(
                                                  Colors.blueGrey),
                                        ),
                                        onPressed: () async {
                                          await blueConnect.write(
                                            data:
                                                "World is something, like something, yeah i know",
                                            autoConnect: true,
                                          );
                                        },
                                        child: const Text("W"),
                                      ),
                                    ),
                                ],
                              ),
                            );
                          },
                        ),
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
