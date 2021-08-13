package com.codexo.githubclient

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.codexo.githubclient.databinding.FragmentExploreBinding
import com.codexo.githubclient.databinding.FragmentProfileBinding
import com.codexo.githubclient.home.HomeFragment

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupWebView(savedInstanceState)
        binding!!.swipeRefresh.setOnRefreshListener { refreshPage() }

        return binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(savedInstanceState: Bundle?) {
        binding!!.wvProfile.webViewClient = MyWVClient()
        binding!!.wvProfile.loadUrl(mainUrl)
        if (savedInstanceState != null) {
            binding!!.wvProfile.restoreState(savedInstanceState)
        } else {
            binding!!.wvProfile.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                cacheMode = WebSettings.LOAD_DEFAULT
                allowFileAccess = true
                textZoom = 150
            }

            binding!!.wvProfile.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            binding!!.wvProfile.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress < 80) {
                        binding!!.progressBar.visibility = View.VISIBLE
                        binding!!.wvProfile.visibility = View.GONE
                    }
                    if (newProgress >= 80) {
                        binding!!.wvProfile.visibility = View.VISIBLE
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.swipeRefresh.isRefreshing = false
                    } else {
                        binding!!.progressBar.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    private fun refreshPage() {
        binding!!.wvProfile.apply {
            webViewClient = MyWVClient()
            loadUrl(binding!!.wvProfile.url.toString())
        }
    }

    companion object {
        private const val mainUrl = "https://github.com/settings/profile"
        private val targetUrl =
            listOf("www.github.com", "www.github.com/", "github.com", "github.com/")
        private val CLASS_TAG = HomeFragment::class.java.simpleName.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    inner class MyWVClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            if (Uri.parse(url).host in targetUrl) {
                binding!!.wvProfile.loadUrl(url)
//                reloadActivity(url)
//                Toast.makeText(context, Uri.parse(url).host, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, url, Toast.LENGTH_LONG).show()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
            return true
        }
    }
}