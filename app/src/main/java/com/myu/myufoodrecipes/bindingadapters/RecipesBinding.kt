package com.myu.myufoodrecipes.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.myu.myufoodrecipes.data.database.RecipesEntity
import com.myu.myufoodrecipes.models.FoodRecipe
import com.myu.myufoodrecipes.util.NetworkResult

class RecipesBinding {
    companion object {

        @BindingAdapter("readApiResonse","readDatabase", requireAll = true)
        @JvmStatic
        fun errorImageViewVisibility(
            imageView: ImageView,
            apiResonse :NetworkResult<FoodRecipe>?,
            database : List<RecipesEntity>?
        ) {
            if (apiResonse is NetworkResult.Error &&
                    database.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE

            } else if (apiResonse is NetworkResult.Loading) {
                imageView.visibility = View.INVISIBLE
            }else if (apiResonse is NetworkResult.Success) {
                imageView.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("readApiResonse2","readDatabase2", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibility(
            textView: TextView,
            apiResonse :NetworkResult<FoodRecipe>?,
            database : List<RecipesEntity>?
        ) {
            if (apiResonse is NetworkResult.Error &&
                database.isNullOrEmpty()) {
                textView.visibility = View.VISIBLE
                textView.text = apiResonse.message.toString()
            } else if (apiResonse is NetworkResult.Loading) {
                textView.visibility = View.INVISIBLE
            }else if (apiResonse is NetworkResult.Success) {
                textView.visibility = View.INVISIBLE
            }
        }
    }
}