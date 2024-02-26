package com.haker.test_mvvm_hilt_room_retrofit.ui.breakingnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haker.test_mvvm_hilt_room_retrofit.R
import com.haker.test_mvvm_hilt_room_retrofit.adapter.ArticlesAdapter
import com.haker.test_mvvm_hilt_room_retrofit.data.model.Article
import com.haker.test_mvvm_hilt_room_retrofit.databinding.FragmentBreakingNewsBinding
import com.haker.test_mvvm_hilt_room_retrofit.util.COUNTRY_CODE
import com.haker.test_mvvm_hilt_room_retrofit.util.QUERY_PAGE_SIZE
import com.haker.test_mvvm_hilt_room_retrofit.util.Resource
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BreakingNewsFragment"
@AndroidEntryPoint
class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news), ArticlesAdapter.OnItemClickListener {

    private val viewModel: BreakingNewsViewModel by viewModels()
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBreakingNewsBinding.bind(view)
        val articlesAdapter = ArticlesAdapter(this)

        binding.apply {
            rvBreakingNews.apply {
                adapter = articlesAdapter
                setHasFixedSize(true)
                addOnScrollListener(this@BreakingNewsFragment.scrollListener)
            }
        }

        viewModel.breakingNews.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = false
                    it.data?.let { newsResponse ->
                        articlesAdapter.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage)
                            binding.rvBreakingNews.setPadding(0,0,0,0)
                    }
                }
                is Resource.Error -> {
                    binding.paginationProgressBar.visibility = View.INVISIBLE
                    isLoading = true
                    it.message?.let {message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error : $message")
                    }
                }
                is Resource.Loading -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                //State is scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews(COUNTRY_CODE)
                isScrolling = false
            }
        }
    }

    override fun onItemClick(article: Article) {
        val action = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}