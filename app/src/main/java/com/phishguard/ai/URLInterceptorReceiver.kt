package com.phishguard.ai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import java.util.regex.Pattern

class URLInterceptorReceiver : BroadcastReceiver() {
    
    companion object {
        private val URL_PATTERN = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" +
            "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" +
            "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
        )
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                handleSmsReceived(context, intent)
            }
        }
    }
    
    private fun handleSmsReceived(context: Context, intent: Intent) {
        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as Array<*>? ?: return
        
        for (pdu in pdus) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
            val messageBody = smsMessage.messageBody
            
            extractAndInterceptUrls(context, messageBody)
        }
    }
    
    private fun extractAndInterceptUrls(context: Context, text: String) {
        val matcher = URL_PATTERN.matcher(text)
        
        while (matcher.find()) {
            var url = matcher.group()
            if (!url.startsWith("http")) {
                url = "https://$url"
            }
            
            // Launch virtual browser instead of system browser
            val browserIntent = Intent(context, VirtualBrowserActivity::class.java).apply {
                putExtra("url", url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        }
    }
}