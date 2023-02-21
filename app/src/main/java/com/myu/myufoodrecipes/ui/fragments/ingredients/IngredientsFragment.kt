package com.myu.myufoodrecipes.ui.fragments.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.myu.myufoodrecipes.adapter.IngredientsAdapter
import com.myu.myufoodrecipes.databinding.FragmentIngredientsBinding
import com.myu.myufoodrecipes.util.Constants.Companion.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!
    private val mAdapter : IngredientsAdapter by lazy { IngredientsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)


        val args = arguments
        val myBundle: com.myu.myufoodrecipes.models.Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setupRecyclerView(view)
        myBundle?.extendedIngredients?.let { mAdapter.setData(it) }

        return binding.root
    }

    private fun setupRecyclerView(view: View?) {
        binding.ingredientsRecyclerview.adapter = mAdapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}