package com.tughra.get_sms_otp_consent.receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsReceiver : BroadcastReceiver() {

    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null


    override fun onReceive(context: Context?, intent: Intent?) {
        //println("SmsReceiver onReceive => ${intent?.`package`}")
        //intent.scheme?.startsWith("com.google.android.gms") == true
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION ) { // Kaynak paket kontrolü

            val extras = intent.extras
            val smsRetrieverStatus: Status? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras?.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
            } else {
                @Suppress("DEPRECATION")
                extras?.getParcelable(SmsRetriever.EXTRA_STATUS) as? Status
            }
            if(extras!=null && smsRetrieverStatus!=null){
                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT,Intent::class.java)
                            smsBroadcastReceiverListener?.onSuccess(messageIntent)
                            // Tiramisu (Android 13) ve üzeri için kod burada çalışır.
                        } else {
                            @Suppress("DEPRECATION")
                            val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                            smsBroadcastReceiverListener?.onSuccess(messageIntent)
                        }

                    }
                    CommonStatusCodes.TIMEOUT -> {
                        smsBroadcastReceiverListener?.onFailure()
                    }
                }
            }
        }
    }
    /**
     * Sms Broadcast Receiver Listener
     */
    interface SmsBroadcastReceiverListener {
        /**
         * if common status success
         */
        fun onSuccess(intent: Intent?)
        /**
         * if common status timeout
         */
        fun onFailure()
    }
}

