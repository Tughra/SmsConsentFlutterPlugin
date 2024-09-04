package com.tughra.get_sms_otp_consent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.tughra.get_sms_otp_consent.receiver.SmsReceiver
import io.flutter.plugin.common.MethodChannel


class SmsConsentManager (private val activity: Activity) {

    fun initialize(smsTitle: String) {
        val client: SmsRetrieverClient = SmsRetriever.getClient(activity)
        //print("SmsConsentManager initialize $smsTitle")
        client.startSmsUserConsent(smsTitle)
    }

    private val smsReceiver = SmsReceiver()

    // Interface to handle success and failure callbacks
    interface SmsListener {
        fun onSmsReceived(message: String)
        //fun onPhoneReceived(message: String)
        fun onSmsRetrievalFailed()
    }

    private var smsListener: SmsListener? = null


    /*
    fun setSmsListener(listener: SmsListener) {
        this.smsListener = listener
    }
     */

    fun requestHint(result:MethodChannel.Result) {

        /*
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
         */
        val hintRequest =
            GetPhoneNumberHintIntentRequest.builder().build()
        Identity.getSignInClient(activity)
            .getPhoneNumberHintIntent(hintRequest)
            .addOnSuccessListener { pendingIntent ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(
                        pendingIntent!!
                    ).build()
                    activity.startIntentSenderForResult(
                        intentSenderRequest.intentSender,
                        CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    result.error("ERROR", e.message, e)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                result.error("ERROR", e.message, e)
            }
    }



    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun startListeningForSms() {
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        //intentFilter.addAction("com.google.android.gms.auth.api.phone.SMS_RETRIEVED") // Add specific action for SmsRetriever

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(smsReceiver, intentFilter,
                SmsRetriever.SEND_PERMISSION,null,Context.RECEIVER_EXPORTED)
        } else {
            activity.registerReceiver(smsReceiver, intentFilter)
        }
        smsReceiver.smsBroadcastReceiverListener =
            object: SmsReceiver.SmsBroadcastReceiverListener{
                override fun onSuccess(intent: Intent?) {
                    /*
                    val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT,Intent::class.java)
                     */
                    val senderPackageName = intent?.`package`
                    if (intent != null && senderPackageName == "com.google.android.gms") {
                        // Check intent origin
                        //println("SmsConsentManager senderPackageName => ${senderPackageName}")
                        // Extract message and validate
                        //intent.`package`="com.google.android.gms"
                        //intent.setAction(SmsRetriever.SMS_RETRIEVED_ACTION)
                        //intent.setFlags(90)
                        //val message = extractSmsFromIntent(intent)
                        // Process valid OTP
                        //println("SmsConsentManager message => $message")
                        //smsListener?.onSmsReceived(message)
                        //activity.startActivity(intent);
                        activity.startActivityForResult(intent, SMS_CONSENT_REQUEST)
                    } else {
                        smsListener?.onSmsRetrievalFailed()
                    }
                }

                override fun onFailure() {
                    smsListener?.onSmsRetrievalFailed()
                }
            }
    }

    fun stopListeningForSms() {
        activity.unregisterReceiver(smsReceiver)
    }
    companion object {
        const val SMS_CONSENT_REQUEST = 2
        const val CREDENTIAL_PICKER_REQUEST = 1
    }
    private fun extractSmsFromIntent(intent: Intent): String {
        val message = intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE) ?: return ""
        return message
    }
}
