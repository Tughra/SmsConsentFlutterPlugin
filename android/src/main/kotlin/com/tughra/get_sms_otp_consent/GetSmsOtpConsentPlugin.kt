package com.tughra.get_sms_otp_consent

import android.app.Activity
import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import io.flutter.embedding.android.FlutterActivity.RESULT_OK
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** SmsConsentPlugin */
class GetSmsOtpConsentPlugin: FlutterPlugin, ActivityAware,MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private var activity: Activity? = null
  private val  channelName: String = "get_sms_otp_consent"
  private lateinit var channel : MethodChannel
  private lateinit var methodResault : MethodChannel.Result
  private var smsManager: SmsConsentManager? = null
  private val activityResultListener: PluginRegistry.ActivityResultListener =
    PluginRegistry.ActivityResultListener { requestCode, resultCode, data ->
      try {
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
          val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
          // println("onActivityResult message $message")
          channel.invokeMethod("SmsReceived", message)

        }
        if (requestCode == smsManager!!.CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
          val phoneNumber =
            Identity.getSignInClient(activity!!).getPhoneNumberFromIntent(data)
          methodResault.success(phoneNumber)
        }
      } catch (e: Exception) {
        Log.e("Exception", e.toString())
      }
      false
    }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    // Activity'ye bağlandığında yapılacak işlemler
    activity = binding.getActivity()
    binding.addActivityResultListener(activityResultListener)

    // println("SmsConsentPlugin onAttachedToActivity")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    disposeSmsManager()
    //println("SmsConsentPlugin onDetachedFromActivityForConfigChanges")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.getActivity()
    binding.addActivityResultListener(activityResultListener)
    //println("SmsConsentPlugin onReattachedToActivityForConfigChanges")
  }

  override fun onDetachedFromActivity() {
    disposeSmsManager()
    //println("SmsConsentPlugin onDetachedFromActivity")
  }
  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    setupChannel(flutterPluginBinding.binaryMessenger)
    //println("SmsConsentPlugin onAttachedToEngine")
  }
  private fun setupChannel(messenger: BinaryMessenger) {
    channel = MethodChannel(messenger, channelName)
    channel.setMethodCallHandler(this)
  }
  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "startListenOtp") {
      if(!isSimSupport())result.error("ERROR_START_SMS_CONSENT", "Can't start sms consent","No SIM")
      else{
        val title:String =  call.arguments as String
        initializeSmsManager(title,result)
        result.success(null)
        //  println("SmsConsentPlugin startListenOtp $title")
      }

    }
    else if (call.method == "isSimSupport") {
      result.success(isSimSupport())
    }
    else if (call.method == "requestHint") {
      methodResault=result
      if(!isSimSupport())result.error("ERROR_START_SMS_CONSENT", "Can't start sms consent","No SIM")
      else{
        if(smsManager==null)smsManager=SmsConsentManager(this.activity!!)
        smsManager!!.requestHint(result)
      }
    }
    else {
      if (call.method == "stopListenOtp"){
        disposeSmsManager()
        result.success(null)
      }

      else {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    disposeSmsManager()

    //println("SmsConsentPlugin onDetachedFromEngine")
  }
  private fun initializeSmsManager(smsTitle: String,result:Result){
    smsManager = SmsConsentManager(this.activity!!)
    smsManager!!.initialize(smsTitle)
    smsManager!!.startListeningForSms()
    /*
     smsManager!!.setSmsListener(object : SmsConsentManager.SmsListener {
         override fun onSmsReceived(message: String) {
              result.success(null)
         }
         override fun onSmsRetrievalFailed() {
             result.error("ERROR_START_SMS_RETRIEVER", "Can't start sms retriever",null);

         }
     })
     */
  }


  fun isSimSupport(): Boolean {
    val telephonyManager =
      activity!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.simState != TelephonyManager.SIM_STATE_ABSENT
  }

  private fun disposeSmsManager(){
    smsManager?.stopListeningForSms()
    smsManager=null
    //  println("dispose message listening")
  }

}
