
import 'get_sms_otp_consent_platform_interface.dart';

class GetSmsOtpConsent {
  Future<void> initializeSmsConsent(String smsTitle) {
    return GetSmsOtpConsentPlatform.instance.initializeSmsConsent(smsTitle);
  }
  Future<void> disposeSmsConsent() {
    return GetSmsOtpConsentPlatform.instance.disposeSmsConsent();
  }
  Future<String?> getOtpSms() {
    return GetSmsOtpConsentPlatform.instance.getOtpSmsMessage();
  }
  Future<String?> getPhoneNumber() {
    return GetSmsOtpConsentPlatform.instance.getPhoneNumber();
  }
}
