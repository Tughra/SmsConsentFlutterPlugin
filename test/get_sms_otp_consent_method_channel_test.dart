import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_sms_otp_consent/get_sms_otp_consent_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelGetSmsOtpConsent platform = MethodChannelGetSmsOtpConsent();
  const MethodChannel channel = MethodChannel('get_sms_otp_consent');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
