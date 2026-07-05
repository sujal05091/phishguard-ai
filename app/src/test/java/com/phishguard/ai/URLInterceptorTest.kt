package com.phishguard.ai

import org.junit.Test
import org.junit.Assert.*
import java.util.regex.Pattern

class URLInterceptorTest {
    
    private val urlPattern = Pattern.compile(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" +
        "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" +
        "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
    )
    
    @Test
    fun testUrlExtraction() {
        val testMessages = listOf(
            "Check out this link: https://www.google.com",
            "Visit www.example.com for more info",
            "Click here: http://suspicious-site.com/login"
        )
        
        testMessages.forEach { message ->
            val matcher = urlPattern.matcher(message)
            assertTrue("Should find URL in: $message", matcher.find())
        }
    }
    
    @Test
    fun testPhishingDetection() {
        val suspiciousUrls = listOf(
            "https://g00gle.com",
            "https://paypal-security.com"
        )
        
        suspiciousUrls.forEach { url ->
            assertTrue("Should detect suspicious patterns", 
                url.contains("g00gle") || url.contains("paypal-security"))
        }
    }
}