import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'get_sms_otp_consent_method_channel.dart';

abstract class GetSmsOtpConsentPlatform extends PlatformInterface {
  /// Constructs a GetSmsOtpConsentPlatform.
  GetSmsOtpConsentPlatform() : super(token: _token);

  static final Object _token = Object();

  static GetSmsOtpConsentPlatform _instance = MethodChannelGetSmsOtpConsent();

  /// The default instance of [GetSmsOtpConsentPlatform] to use.
  ///
  /// Defaults to [MethodChannelGetSmsOtpConsent].
  static GetSmsOtpConsentPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GetSmsOtpConsentPlatform] when
  /// they register themselves.
  static set instance(GetSmsOtpConsentPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> initializeSmsConsent(String smsTitle) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<void> disposeSmsConsent() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<String?> getOtpSmsMessage() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<String?> getPhoneNumber() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> controlSimCard() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
