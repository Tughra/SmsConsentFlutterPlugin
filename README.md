# sms_consent

Flutter plugin to provide SMS code autofill support.

If you want to avoid including any 11-character hash strings in your SMS messages, this plugin can help.
While iOS offers SMS autofill functionality natively, Android requires this package to achieve the same behavior.
SmsConsentAPI automatically requests permission to read the otp code.
<p align="center">
  <img src="https://github.com/Tughra/SmsConsentPlugin/blob/master/example/Screenshot_20240819_164610.png" width="400"/>
</p>

## How to install

### Android


## Getting Started




## Usage
```dart
class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? _receivedSms ;
  final _smsConsentPlugin = SmsConsent();
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
                await _smsConsentPlugin.initializeSmsConsent("OtpSMS");
                _receivedSms = await _smsConsentPlugin.getOtpSms();
                setState(() {

                });
              }, child: const Text("Listen Otp")),
              TextButton(onPressed: (){
                _smsConsentPlugin.disposeSmsConsent();
              }, child: const Text("Dispose ListenOtp")),
              if(_receivedSms!=null)Text(_receivedSms!)
            ],
          ),
        ),
      ),
    );
  }
}
```
When you initialized the "initializeSmsConsent", the method's String parameter (_smsConsentPlugin.initializeSmsConsent("OtpSMS")) has to be same with received Sms Message's title.
