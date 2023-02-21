package com.myu.myufoodrecipes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.databinding.FavoriteRecipesRowLayoutBinding
import com.myu.myufoodrecipes.util.RecipesDiffUtil

class FavoriteRecipesAdapter : RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>() {
    private var favoriteRecipes = emptyList<FavoritesEntity>()

    class MyViewHolder(
        private val binding : FavoriteRecipesRowLayoutBinding
    ) : RecyclerView.ViewHolder(binding.recipesRowLayout) {

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
    }

    override fun getItemCount(): Int {
       return favoriteRecipes.size
    }

    fun setData(newFavoritesEntity: List<FavoritesEntity>) {
        val recipesDiffUtil =
            RecipesDiffUtil(favoriteRecipes,newFavoritesEntity)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favoriteRecipes = newFavoritesEntity
        diffUtilResult.dispatchUpdatesTo(this)
    }

}