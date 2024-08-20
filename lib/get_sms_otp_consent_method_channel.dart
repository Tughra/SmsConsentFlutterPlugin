import 'dart:async';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'get_sms_otp_consent_platform_interface.dart';

/// An implementation of [GetSmsOtpConsentPlatform] that uses method channels.
class MethodChannelGetSmsOtpConsent extends GetSmsOtpConsentPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('get_sms_otp_consent');

  Completer<String?> _otpSMS = Completer();
  @override
  Future<void> initializeSmsConsent(String smsTitle) async{
    if(Platform.isIOS)return;
    methodChannel.setMethodCallHandler(_invokedMethods);
    try{
      return await methodChannel.invokeMethod<void>('startListenOtp',smsTitle);
    }on PlatformException catch (e){
      debugPrint("Failed to start sms consent $e");
    }
  }

  @override
  Future<void> disposeSmsConsent() async{
    if(Platform.isIOS)return;
    _otpSMS=Completer();
    return await methodChannel.invokeMethod<void>('stopListenOtp');
  }

  @override
  Future<String?> getOtpSmsMessage() async {
    if(Platform.isIOS)return null;
    return await _otpSMS.future;
  }
  @override
  Future<String?> getPhoneNumber() async {
    try{
      if(Platform.isIOS)return null;
      return await methodChannel.invokeMethod<String?>('requestHint');
    }on PlatformException catch (e){
      debugPrint("Failed to get phone number $e");
      return null;
    }
  }
  Future<dynamic> _invokedMethods(MethodCall methodCall) async {
    switch (methodCall.method) {
      case "SmsReceived":
        _otpSMS.complete(methodCall.arguments.toString());
      default:
        return null;
    }
  }
}
