package com.phishguard.ai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupUI()
    }
    
    private fun setupUI() {
        findViewById<Button>(R.id.testBrowserButton).setOnClickListener {
            val intent = Intent(this, VirtualBrowserActivity::class.java)
            intent.putExtra("url", "https://www.google.com")
            startActivity(intent)
        }
    }
}