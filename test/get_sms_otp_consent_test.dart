import 'package:flutter_test/flutter_test.dart';
import 'package:get_sms_otp_consent/get_sms_otp_consent.dart';
import 'package:get_sms_otp_consent/get_sms_otp_consent_platform_interface.dart';
import 'package:get_sms_otp_consent/get_sms_otp_consent_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGetSmsOtpConsentPlatform
    with MockPlatformInterfaceMixin
    implements GetSmsOtpConsentPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final GetSmsOtpConsentPlatform initialPlatform = GetSmsOtpConsentPlatform.instance;

  test('$MethodChannelGetSmsOtpConsent is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGetSmsOtpConsent>());
  });

  test('getPlatformVersion', () async {
    GetSmsOtpConsent getSmsOtpConsentPlugin = GetSmsOtpConsent();
    MockGetSmsOtpConsentPlatform fakePlatform = MockGetSmsOtpConsentPlatform();
    GetSmsOtpConsentPlatform.instance = fakePlatform;

    expect(await getSmsOtpConsentPlugin.getPlatformVersion(), '42');
  });
}
