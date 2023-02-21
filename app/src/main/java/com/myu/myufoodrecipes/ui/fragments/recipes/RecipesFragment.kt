package com.myu.myufoodrecipes.ui.fragments.recipes

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.myu.myufoodrecipes.R
import com.myu.myufoodrecipes.adapter.RecipesAdapter
import com.myu.myufoodrecipes.databinding.FragmentRecipesBinding
import com.myu.myufoodrecipes.util.NetworkListener
import com.myu.myufoodrecipes.util.NetworkResult
import com.myu.myufoodrecipes.util.observeOnce
import com.myu.myufoodrecipes.viewmodels.MainViewModel
import com.myu.myufoodrecipes.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(),SearchView.OnQueryTextListener {
    private val args by navArgs<RecipesFragmentArgs>()
    private lateinit var mainViewModel : MainViewModel
    private lateinit var recipesViewModel : RecipesViewModel
    private lateinit var networkListener: NetworkListener
    private val mAdapter by lazy { RecipesAdapter() }
    private var _binding : FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private val TAG = "RecipesFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupRecyclerView()
        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        lifecycleScope.launch{
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect{ status ->
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        return binding.root
    }

    private fun setupRecyclerView(){
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readDatabase() {
       lifecycleScope.launch {
           mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
               if (database.isNotEmpty() && !args.backFromBottomSheet) {
                   mAdapter.setData(database.get(0).foodRecipe)
                   hideShimmerEffect()
               } else {
                   requestApiData()
               }
           }
       }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun  requestApiData(){
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
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
                    loadDataFromCache()
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

    private fun searchApiData(searchQuery : String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchRepicesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
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

    private  fun loadDataFromCache(){
        lifecycleScope.launch{
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].foodRecipe)
                }
            }
        }
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            searchApiData(it)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}