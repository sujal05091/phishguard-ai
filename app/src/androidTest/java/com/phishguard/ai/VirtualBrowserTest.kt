package com.phishguard.ai

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class VirtualBrowserTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(VirtualBrowserActivity::class.java)

    @Test
    fun testWebViewDownloadBlocked() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.phishguard.ai", appContext.packageName)
        
        activityRule.scenario.onActivity { activity ->
            val webView = activity.findViewById<android.webkit.WebView>(R.id.webView)
            assertNotNull("WebView should be initialized", webView)
            assertFalse("File access should be disabled", 
                webView.settings.allowFileAccess)
            assertFalse("Content access should be disabled", 
                webView.settings.allowContentAccess)
        }
    }
}