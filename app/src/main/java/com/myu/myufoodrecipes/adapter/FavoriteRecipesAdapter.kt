package com.myu.myufoodrecipes.adapter

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myu.myufoodrecipes.R
import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.databinding.FavoriteRecipesRowLayoutBinding
import com.myu.myufoodrecipes.ui.fragments.favorites.FavoriteRecipesFragmentDirections
import com.myu.myufoodrecipes.util.RecipesDiffUtil

class FavoriteRecipesAdapter(
    private val requiredActivity : FragmentActivity
) : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(),ActionMode.Callback {
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
        val selectedRecipe = favoriteRecipes[position]
        holder.bind(selectedRecipe)


        /**
         * Single Click Listener
         * */

        holder.binding.favroiteRecipesRowLayout.setOnClickListener {
            val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(selectedRecipe.result)
            holder.itemView.findNavController().navigate(action)
        }

        /**
         * Long Click Listener
         * */

        holder.binding.favroiteRecipesRowLayout.setOnLongClickListener {
            requiredActivity.startActionMode(this)
            true
        }

    }

    override fun getItemCount(): Int {
       return favoriteRecipes.size
    }


    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu,menu)
        applyStatusBarColor(R.color.contextualStatusBarColor)

        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true

    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        return true

    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
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

}