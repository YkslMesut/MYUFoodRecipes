package com.myu.myufoodrecipes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.myu.myufoodrecipes.R
import com.myu.myufoodrecipes.adapter.PagerAdapter
import com.myu.myufoodrecipes.databinding.ActivityDetailsBinding
import com.myu.myufoodrecipes.ui.fragments.ingredients.IngredientsFragment
import com.myu.myufoodrecipes.ui.fragments.instructions.InstructionsFragment
import com.myu.myufoodrecipes.ui.fragments.overview.OverviewFragment
import com.myu.myufoodrecipes.util.Constants.Companion.RECIPE_RESULT_KEY
import com.myu.myufoodrecipes.viewmodels.MainViewModel

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )
        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.save_to_favorites_menu)
       // checkSavedRecipes(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }/* else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
            removeFromFavorites(item)
        }*/
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        //changeMenuItemColor(menuItem, R.color.white)
    }

}