package com.xthan.asteroidsandroid

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
            setSupportZoom(false)
        }

        WebView.setWebContentsDebuggingEnabled(true)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Send initial theme state on load
                updateThemeInWebView()
            }
        }

        // Disables long-press vibration and context menu popups completely
        webView.isLongClickable = false
        webView.setOnLongClickListener { true }
        webView.isHapticFeedbackEnabled = false

        if (savedInstanceState == null) {
            webView.loadUrl("file:///android_asset/index.html")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Instantly notify JS when system theme changes
        updateThemeInWebView()
    }

    override fun onResume() {
        super.onResume()
        updateThemeInWebView()
    }

    private fun updateThemeInWebView() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        // Call JavaScript function in index.html to switch themes instantly
        webView.evaluateJavascript("if (window.setAppTheme) { window.setAppTheme($isDarkMode); }", null)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
