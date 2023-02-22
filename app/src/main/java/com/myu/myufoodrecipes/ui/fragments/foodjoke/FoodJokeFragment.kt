package com.myu.myufoodrecipes.ui.fragments.foodjoke

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.myu.myufoodrecipes.R
import com.myu.myufoodrecipes.databinding.FragmentFoodJokeBinding
import com.myu.myufoodrecipes.util.Constants.Companion.API_KEY
import com.myu.myufoodrecipes.util.NetworkResult
import com.myu.myufoodrecipes.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {
    private var _binding : FragmentFoodJokeBinding? = null
    private val binding get () = _binding!!
    private val mainViewModel by viewModels <MainViewModel>()
    private var foodJoke = "No Food Joke"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFoodJokeBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = mainViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.food_joke_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.share_food_joke_menu) {
                    val shareIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                        this.type = "text/plain"
                    }
                    startActivity(shareIntent)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.foodJokeTextView.text = response.data?.text
                    if (response.data != null) {
                        foodJoke = response.data.text
                    }

                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {

                }
            }
        }


        return binding.root
    }

    private fun loadDataFromCache() {
        mainViewModel.readFoodJoke.observe(viewLifecycleOwner) { database ->
            if (!database.isNullOrEmpty()) {
                binding.foodJokeTextView.text = database[0].foodJoke.text
                foodJoke = database[0].foodJoke.text
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}