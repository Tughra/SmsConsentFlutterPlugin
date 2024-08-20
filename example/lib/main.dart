import 'package:flutter/material.dart';
import 'package:get_sms_otp_consent/get_sms_otp_consent.dart';



//Regex("(\\d{6})")
void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? _receivedSms ;
  String? _phoneNumber ;
  final _smsConsentPlugin = GetSmsOtpConsent();
  @override
  void initState() {
    super.initState();
  }
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('SMS Consent Example'),
        ),
        body: Center(
          child: Column(
            children: [
              TextButton(onPressed: ()async{
                //_launchCustomActivity();
                _phoneNumber= await _smsConsentPlugin.getPhoneNumber();
                setState(() {

                });
              }, child: const Text("Get Phone Number")),
              TextButton(onPressed: ()async{
                //_launchCustomActivity();
                await _smsConsentPlugin.initializeSmsConsent("OtpSMS");
                _receivedSms = await _smsConsentPlugin.getOtpSms();
                setState(() {

                });
              }, child: const Text("Listen Otp")),
              TextButton(onPressed: (){
                //_disposeActivity();
                _smsConsentPlugin.disposeSmsConsent();
              }, child: const Text("Dispose ListenOtp")),
              if(_receivedSms!=null)Text(_receivedSms!),
              if(_phoneNumber!=null)Text(_phoneNumber!)
            ],
          ),
        ),
      ),
    );
  }
}
