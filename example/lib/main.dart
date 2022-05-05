import 'package:flutter/material.dart';
import 'package:xunil_blue_connect/xunil_blue_connect.dart';

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
            title: const Text('Plugin bluetooth check app'),
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

  //call the class
  XunilBlueConnect blueConnect = XunilBlueConnect();

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
                //call the function but as async
                //but if function return null means the device doesn't support bluetooth
                var isBlue = await blueConnect.isBluetoothAvailable();
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
