package com.example.respuestaautomatica_broadcastr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (TelephonyManager.EXTRA_STATE_RINGING == state) {
                Log.d("CallReceiver", "📞 Llamada entrante detectada.")
                val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d("CallReceiver", "📞 Número de teléfono: $phoneNumber")
                val sharedPreferences =
                    context.getSharedPreferences("AutoReplyPrefs", Context.MODE_PRIVATE)
                val phoneNumberSaved = sharedPreferences.getString("phoneNumber", "")
                if (phoneNumber != null && phoneNumberSaved != null && phoneNumber == phoneNumberSaved) {
                    val message = sharedPreferences.getString("message", "")
                    if (message != null) {
                        val smsManager = android.telephony.SmsManager.getDefault()
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                        Log.d("CallReceiver", "📞 Mensaje enviado: $message")
                    }
                }
            }
        }
    }
}