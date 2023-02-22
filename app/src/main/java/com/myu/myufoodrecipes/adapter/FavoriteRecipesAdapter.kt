package com.myu.myufoodrecipes.adapter

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.myu.myufoodrecipes.R
import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.databinding.FavoriteRecipesRowLayoutBinding
import com.myu.myufoodrecipes.ui.fragments.favorites.FavoriteRecipesFragmentDirections
import com.myu.myufoodrecipes.util.RecipesDiffUtil
import com.myu.myufoodrecipes.viewmodels.MainViewModel

class FavoriteRecipesAdapter(
    private val requiredActivity : FragmentActivity,
    private val mainViewModel : MainViewModel
) : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(),ActionMode.Callback {

    private var multiSelection = false

    private lateinit var mActionMode : ActionMode
    private lateinit var rootView : View

    private var myViewHolder = arrayListOf<MyViewHolder>()
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var favoriteRecipes = emptyList<FavoritesEntity>()

    class MyViewHolder(
        val binding : FavoriteRecipesRowLayoutBinding
    ) : RecyclerView.ViewHolder(binding.favroiteRecipesRowLayout) {

        fun bind (favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolder.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)

        saveItemStateOnScroll(currentRecipe,holder)

        /**
         * Single Click Listener
         * */

        holder.binding.favroiteRecipesRowLayout.setOnClickListener {
            if (multiSelection) {
                applySelection(holder,currentRecipe)
            } else{
                val action =
                    FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(currentRecipe.result)
                holder.itemView.findNavController().navigate(action)
            }

        }

        /**
         * Long Click Listener
         * */

        holder.binding.favroiteRecipesRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requiredActivity.startActionMode(this)
                applySelection(holder,currentRecipe)
                true
            } else {
                applySelection(holder,currentRecipe)
                true
            }

        }

    }

    private fun saveItemStateOnScroll(currentRecipe : FavoritesEntity , holder: MyViewHolder){
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder,R.color.cardBackgroundLightColor,R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder,R.color.cardBackgroundColor,R.color.strokeColor)
        }
    }

    override fun getItemCount(): Int {
       return favoriteRecipes.size
    }

    private fun applySelection(holder : MyViewHolder , currentRecipe : FavoritesEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder,R.color.cardBackgroundColor,R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder,R.color.cardBackgroundLightColor,R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(holder: MyViewHolder , backgroundColor : Int , strokeColor : Int){
        holder.binding.apply {
            favroiteRecipesRowLayout.setBackgroundColor(ContextCompat.getColor(requiredActivity,backgroundColor))
            favoriteRowCardView.strokeColor = ContextCompat.getColor(requiredActivity,strokeColor)
        }
    }

    private fun applyActionModeTitle() {
        when(selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"

            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu,menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)

        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true

    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.delete_favorite_recipe_menu) {
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipes(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")
            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true

    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolder.forEach { holder ->
            changeRecipeStyle(holder,R.color.cardBackgroundColor,R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color : Int) {
        requiredActivity.window.statusBarColor =
            ContextCompat.getColor(requiredActivity,color)
    }

    fun setData(newFavoritesEntity: List<FavoritesEntity>) {
        val recipesDiffUtil =
            RecipesDiffUtil(favoriteRecipes,newFavoritesEntity)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favoriteRecipes = newFavoritesEntity
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun showSnackBar(message : String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}.show()
    }

    fun clearContextualActionMode(){
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }

}