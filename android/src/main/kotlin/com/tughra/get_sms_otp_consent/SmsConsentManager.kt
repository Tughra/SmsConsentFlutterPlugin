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
    private val otpResponseCode = 1001
    val CREDENTIAL_PICKER_REQUEST = 1

    fun initialize(smsTitle: String) {
        val client: SmsRetrieverClient = SmsRetriever.getClient(activity)
        //print("SmsConsentManager initialize $smsTitle")
        client.startSmsUserConsent(smsTitle)
    }

    private val smsReceiver = SmsReceiver()

    // Interface to handle success and failure callbacks
    interface SmsListener {
        fun onSmsReceived(message: String)
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

        smsReceiver.smsBroadcastReceiverListener =
            object : SmsReceiver.SmsBroadcastReceiverListener {

                override fun onSuccess(intent: Intent?) {
                    if (intent != null) {
                        val message: String = extractSmsFromIntent(intent)
                        smsListener?.onSmsReceived(message)
                        activity.startActivityForResult(intent,otpResponseCode)
                    } else {
                        smsListener?.onSmsRetrievalFailed()
                    }
                }

                override fun onFailure() {
                    smsListener?.onSmsRetrievalFailed()
                }
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(
                smsReceiver, intentFilter,
                Context.RECEIVER_EXPORTED
            )
        }
        else {
            activity.registerReceiver(smsReceiver, intentFilter)
        }
    }

    fun stopListeningForSms() {
        activity.unregisterReceiver(smsReceiver)
    }

    private fun extractSmsFromIntent(intent: Intent): String {
        val extras = intent.extras ?: return ""
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                ?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
        } else {
            @Suppress("DEPRECATION")
            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                ?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
        }
        return message ?: ""
    }
}