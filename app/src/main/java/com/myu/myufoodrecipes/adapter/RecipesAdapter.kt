package com.myu.myufoodrecipes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myu.myufoodrecipes.databinding.RecipesRowLayoutBinding
import com.myu.myufoodrecipes.models.FoodRecipe
import com.myu.myufoodrecipes.util.RecipesDiffUtil

class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private var recipeList = emptyList<com.myu.myufoodrecipes.models.Result>()



    class RecipesViewHolder(private val binding : RecipesRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result : com.myu.myufoodrecipes.models.Result) {
            binding.result = result
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup) : RecipesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater,parent,false)
                return RecipesViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentRecipe = recipeList[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun setData(newData : FoodRecipe) {
        val recipesDiffUtil = RecipesDiffUtil(recipeList,newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipeList = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}