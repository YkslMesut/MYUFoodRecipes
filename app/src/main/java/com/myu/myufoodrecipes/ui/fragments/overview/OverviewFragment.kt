package com.myu.myufoodrecipes.ui.fragments.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.myu.myufoodrecipes.databinding.FragmentOverviewBinding
import com.myu.myufoodrecipes.util.Constants.Companion.RECIPE_RESULT_KEY
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: com.myu.myufoodrecipes.models.Result = args!!.getParcelable<com.myu.myufoodrecipes.models.Result>(RECIPE_RESULT_KEY)
                as com.myu.myufoodrecipes.models.Result

        binding.mainImageView.load(myBundle.image)
        binding.titleTextView.text = myBundle.title
        binding.likesTextView.text = myBundle.aggregateLikes.toString()
        binding.timeTextView.text = myBundle.readyInMinutes.toString()
        myBundle.summary?.let {
            val summary = Jsoup.parse(it).text()
            binding.summaryTextView.text = summary
        }

        return binding.root
    }
}