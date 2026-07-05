package com.phishguard.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BedrockPhishingDetector {
    
    data class PhishingResult(
        val isSafe: Boolean,
        val explanation: String,
        val confidence: Float = 0.0f
    )
    
    suspend fun analyzeUrl(url: String, pageContent: String): PhishingResult = withContext(Dispatchers.IO) {
        try {
            // Simplified phishing detection logic
            val isSuspicious = checkSuspiciousPatterns(url, pageContent)
            
            PhishingResult(
                isSafe = !isSuspicious,
                explanation = if (isSuspicious) "Suspicious URL pattern detected" else "URL appears safe",
                confidence = 0.8f
            )
        } catch (e: Exception) {
            PhishingResult(true, "Analysis failed: ${e.message}")
        }
    }
    
    private fun checkSuspiciousPatterns(url: String, content: String): Boolean {
        return checkDomainSpoofing(url) ||
               checkSuspiciousKeywords(url, content) ||
               checkURLStructure(url) ||
               checkPhishingIndicators(content)
    }
    
    private fun checkDomainSpoofing(url: String): Boolean {
        val legitimateDomains = mapOf(
            "google" to listOf("g00gle", "g0ogle", "googIe", "gooogle"),
            "facebook" to listOf("faceb00k", "facebok", "facebook", "facebk"),
            "instagram" to listOf("instagrem", "instgram", "instagrm", "instagramm"),
            "paypal" to listOf("paypaI", "payp4l", "paypaII", "paipal"),
            "amazon" to listOf("amaz0n", "amazom", "amazone", "ammazon"),
            "microsoft" to listOf("micr0soft", "microsooft", "mircosoft"),
            "apple" to listOf("appIe", "appl3", "applle", "aple"),
            "twitter" to listOf("twiter", "twittr", "twittter", "tw1tter"),
            "youtube" to listOf("youtub", "y0utube", "youtubee", "yotube"),
            "gmail" to listOf("gmial", "gmai1", "gmaiI", "gmal")
        )
        
        return legitimateDomains.values.flatten().any { spoofed ->
            url.contains(spoofed, ignoreCase = true)
        }
    }
    
    private fun checkSuspiciousKeywords(url: String, content: String): Boolean {
        val phishingKeywords = listOf(
            "verify", "suspend", "urgent", "immediate", "expire", "update",
            "confirm", "secure", "account", "login", "password", "click",
            "winner", "congratulations", "prize", "free", "limited", "offer"
        )
        
        val securityKeywords = listOf(
            "security-alert", "account-locked", "verify-now", "update-payment",
            "confirm-identity", "suspicious-activity", "temporary-hold"
        )
        
        return (phishingKeywords + securityKeywords).any { keyword ->
            url.contains(keyword, ignoreCase = true) || 
            content.contains(keyword, ignoreCase = true)
        }
    }
    
    private fun checkURLStructure(url: String): Boolean {
        // Check for suspicious URL patterns
        val suspiciousPatterns = listOf(
            Regex("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"), // IP addresses
            Regex("[a-z]+-[a-z]+\\.(tk|ml|ga|cf)"), // Suspicious TLDs
            Regex("[a-z]+[0-9]+[a-z]+\\."), // Mixed numbers in domain
            Regex("https?://[^/]*-[^/]*\\."), // Hyphens in subdomain
            Regex("[a-z]{20,}\\.") // Very long domain names
        )
        
        return suspiciousPatterns.any { it.containsMatchIn(url.lowercase()) } ||
               url.count { it == '.' } > 4 || // Too many subdomains
               url.contains("bit.ly") || url.contains("tinyurl") || // URL shorteners
               url.length > 100 // Extremely long URLs
    }
    
    private fun checkPhishingIndicators(content: String): Boolean {
        val phishingIndicators = listOf(
            "enter your password", "confirm your account", "verify identity",
            "account will be closed", "suspended account", "click here now",
            "act immediately", "limited time", "expires today",
            "social security number", "credit card", "bank account"
        )
        
        return phishingIndicators.any { 
            content.contains(it, ignoreCase = true) 
        }
    }
}