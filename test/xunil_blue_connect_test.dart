import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:xunil_blue_connect/xunil_blue_connect.dart';

void main() {
  const MethodChannel channel = MethodChannel('xunil_blue_connect');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await XunilBlueConnect.platformVersion, '42');
  });
}
