package com.phishguard.ai

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.launch

class VirtualBrowserActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var urlTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var backButton: ImageView
    private lateinit var shieldIcon: ImageView
    private lateinit var phishingDetector: BedrockPhishingDetector
    private var currentUrl: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_browser)
        
        webView = findViewById(R.id.webView)
        urlTextView = findViewById(R.id.urlTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        backButton = findViewById(R.id.backButton)
        shieldIcon = findViewById(R.id.shieldIcon)
        
        phishingDetector = BedrockPhishingDetector()
        setupSecureWebView()
        
        val url = intent.getStringExtra("url") 
            ?: intent.data?.toString() 
            ?: "https://www.google.com"
        loadUrl(url)
        
        backButton.setOnClickListener { finish() }
    }
    
    private fun setupSecureWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = false
                databaseEnabled = false
                allowFileAccess = false
                allowContentAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                setGeolocationEnabled(false)
                cacheMode = WebSettings.LOAD_NO_CACHE
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
            }
            
            webViewClient = SecureWebViewClient()
            webChromeClient = SecureWebChromeClient()
            
            setDownloadListener { _, _, _, _, _ ->
                Toast.makeText(this@VirtualBrowserActivity, "Downloads are disabled for security", Toast.LENGTH_SHORT).show()
            }
            
            setOnLongClickListener { true }
        }
    }
    
    private fun loadUrl(url: String) {
        currentUrl = url
        urlTextView.text = url
        loadingProgressBar.visibility = View.VISIBLE
        
        Toast.makeText(this, getString(R.string.analyzing_website), Toast.LENGTH_SHORT).show()
        
        // Analyze URL immediately before loading
        analyzeAndLoadUrl(url)
    }
    
    private inner class SecureWebViewClient : WebViewClient() {
        
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString() ?: return false
            analyzeAndLoadUrl(url)
            return true
        }
        
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loadingProgressBar.visibility = View.VISIBLE
        }
        
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loadingProgressBar.visibility = View.GONE
            url?.let { analyzeCurrentPage(it) }
        }
        
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            loadingProgressBar.visibility = View.GONE
        }
    }
    
    private inner class SecureWebChromeClient : WebChromeClient() {
        
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            loadingProgressBar.progress = newProgress
        }
        
        override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
            return false
        }
        
        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            result?.confirm()
            return true
        }
        
        override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
            // Block all location requests and show warning
            callback?.invoke(origin, false, false)
            runOnUiThread {
                Toast.makeText(this@VirtualBrowserActivity, "⚠️ Location access blocked for security", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun analyzeAndLoadUrl(url: String) {
        lifecycleScope.launch {
            try {
                val result = phishingDetector.analyzeUrl(url, "")
                
                if (result.isSafe) {
                    runOnUiThread {
                        webView.loadUrl(url)
                        urlTextView.text = url
                        showSafeMessage()
                    }
                } else {
                    runOnUiThread {
                        showPhishingAlert(url, result.explanation)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    webView.loadUrl(url)
                    urlTextView.text = url
                }
            }
        }
    }
    
    private fun analyzeCurrentPage(url: String) {
        webView.evaluateJavascript(
            "(function() { return document.documentElement.innerText; })();"
        ) { content ->
            val cleanContent = content?.replace("\\n", "\n")?.replace("\\\"", "\"") ?: ""
            
            lifecycleScope.launch {
                try {
                    val result = phishingDetector.analyzeUrl(url, cleanContent)
                    
                    runOnUiThread {
                        if (!result.isSafe) {
                            showPhishingAlert(url, result.explanation)
                        } else {
                            updateShieldIcon(true)
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { updateShieldIcon(true) }
                }
            }
        }
    }
    
    private fun showPhishingAlert(url: String, explanation: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.phishing_detected_title))
            .setMessage("${getString(R.string.phishing_detected_message)}\n\nReason: $explanation")
            .setPositiveButton(getString(R.string.block_navigation)) { _, _ ->
                finish()
            }
            .setNegativeButton(getString(R.string.proceed_anyway)) { _, _ ->
                webView.loadUrl(url)
                updateShieldIcon(false)
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showSafeMessage() {
        Toast.makeText(this, getString(R.string.safe_website_message), Toast.LENGTH_SHORT).show()
        updateShieldIcon(true)
    }
    
    private fun updateShieldIcon(isSafe: Boolean) {
        shieldIcon.setColorFilter(
            if (isSafe) getColor(R.color.success_color) 
            else getColor(R.color.danger_color)
        )
    }
}