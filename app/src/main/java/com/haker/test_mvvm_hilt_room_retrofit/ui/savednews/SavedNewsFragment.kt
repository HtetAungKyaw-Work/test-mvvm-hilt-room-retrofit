package com.haker.test_mvvm_hilt_room_retrofit.ui.savednews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.haker.test_mvvm_hilt_room_retrofit.R
import com.haker.test_mvvm_hilt_room_retrofit.adapter.ArticlesAdapter
import com.haker.test_mvvm_hilt_room_retrofit.data.model.Article
import com.haker.test_mvvm_hilt_room_retrofit.databinding.FragmentSavedNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment: Fragment(R.layout.fragment_saved_news),
    ArticlesAdapter.OnItemClickListener {

    private val viewModel: SavedNewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSavedNewsBinding.bind(view)
        val articleAdapter = ArticlesAdapter(this)

        binding.apply {
            rvSavedNews.apply {
                adapter = articleAdapter
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            )
            {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val article = articleAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onArticleSwiped(article)
                }

            }).attachToRecyclerView(rvSavedNews)
        }

        viewModel.getAllArticles().observe(viewLifecycleOwner) {
            articleAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.savedArticleEvent.collect { event ->
                when (event) {
                    is SavedNewsViewModel.SavedArticleEvent.ShowUndoDeleteArticleMessage -> {
                        Snackbar.make(requireView(), "Article Deleted!", Snackbar.LENGTH_SHORT)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.article)
                            }
                            .show()
                    }
                }
            }
        }
    }

    override fun onItemClick(article: Article) {
        val action = SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(article)
        findNavController().navigate(action)
    }
}