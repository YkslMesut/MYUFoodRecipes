package com.myu.myufoodrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.myu.myufoodrecipes.MainViewModel
import com.myu.myufoodrecipes.adapter.RecipesAdapter
import com.myu.myufoodrecipes.databinding.FragmentRecipesBinding
import com.myu.myufoodrecipes.util.Constants.Companion.API_KEY
import com.myu.myufoodrecipes.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private lateinit var mainViewModel : MainViewModel
    private val mAdapter by lazy { RecipesAdapter() }
    private var _binding : FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupRecyclerView()
        requestApiData()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestApiData(){
        mainViewModel.getRecipes(applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun applyQueries() : HashMap<String,String> {

        val queries : HashMap<String,String> = HashMap()
        queries["number"] = "50"
        queries["apiKey"] = API_KEY
        queries["type"] = "snack"
        queries["diet"] = "vegan"
        queries["addRecipeInformation"] = "true"
        queries["fillIngredients"] = "true"

        return queries
    }

    private fun setupRecyclerView(){
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = VISIBLE
        binding.recyclerView.visibility = GONE
    }

    private fun hideShimmerEffect(){
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = GONE
        binding.recyclerView.visibility = VISIBLE
    }
}