package com.r42914lg.smsretriever.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class MySMSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            when ((intent.extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as Status).statusCode) {
                CommonStatusCodes.SUCCESS -> {

                }
                CommonStatusCodes.TIMEOUT -> {}
            }
        }
    }
}