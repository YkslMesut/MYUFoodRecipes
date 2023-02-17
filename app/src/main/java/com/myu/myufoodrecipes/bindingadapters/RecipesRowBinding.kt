package com.myu.myufoodrecipes.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.myu.myufoodrecipes.R
import com.myu.myufoodrecipes.ui.fragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup

class RecipesRowBinding {
    companion object {

        @BindingAdapter("setNumberOfResult")
        @JvmStatic
        fun setNumberOfLikes(textView : TextView,value : Int) {
            textView.text = value.toString()
        }

        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view : View,vegan : Boolean) {
            if (vegan) {
             when(view) {
                 is TextView -> {
                     view.setTextColor(ContextCompat.getColor(
                         view.context,
                         R.color.teal_200
                     ))
                 }
                 is ImageView -> {
                     view.setColorFilter(
                           ContextCompat.getColor(
                             view.context,
                             R.color.purple_200
                         )
                     )
                 }
             }
            }
        }

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView : ImageView,imageUrl : String) {
            imageView.load(imageUrl) {
                crossfade(600)
                    .error(R.drawable.ic_error_placeholder)

            }
        }

        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout : ConstraintLayout,result : com.myu.myufoodrecipes.models.Result) {
            recipeRowLayout.setOnClickListener {
                try {
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
                    recipeRowLayout.findNavController().navigate(action)
                } catch (e : java.lang.Exception) {
                    Toast.makeText(recipeRowLayout.context,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView,description : String?) {
            description?.let {
                val desc = Jsoup.parse(it).text()
                textView.text = desc
            }
        }

    }
}