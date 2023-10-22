package com.r42914lg.smsretriever.impl

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsRetrieverWrapper(
    private val ctx: ComponentActivity,
    private val senderPhone: String? = null,
    private val callback: (code: String) -> Unit = {}
) {
    private val client = SmsRetriever.getClient(ctx)

    private val resultLauncher = ctx.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val message = result.data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            message?.let {
                val code = parseSms(it)
                if (code.isNotEmpty())
                    callback(code)
            }
        }
    }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        @SuppressLint("UnsafeIntentLaunch")
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            resultLauncher.launch(consentIntent)
                        } catch (_: ActivityNotFoundException) { }
                    }
                    CommonStatusCodes.TIMEOUT -> {}
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun start() {
        client.startSmsUserConsent(senderPhone)
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION).apply {
            addAction(SmsRetriever.SEND_PERMISSION)
        }
        ctx.registerReceiver(smsVerificationReceiver, intentFilter)
    }

    private fun parseSms(text: String): String {
        return ""
    }
}