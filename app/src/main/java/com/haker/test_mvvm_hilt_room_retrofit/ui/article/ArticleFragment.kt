package com.haker.test_mvvm_hilt_room_retrofit.ui.article

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.haker.test_mvvm_hilt_room_retrofit.R
import com.haker.test_mvvm_hilt_room_retrofit.databinding.FragmentArticleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment: Fragment(R.layout.fragment_article) {

    private val viewModel: ArticleViewModel by viewModels()
    private val args by navArgs<ArticleFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentArticleBinding.bind(view)
        binding.apply {
            val article = args.article
            webView.apply {
                webViewClient = WebViewClient()
                article.url?.let {
                    loadUrl(article.url.toString())
                }
            }

            fab.setOnClickListener {
                viewModel.saveArticle(article)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.articleEvent.collect { event ->
                when (event) {
                    is ArticleViewModel.ArticleEvent.ShowArticleSavedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}