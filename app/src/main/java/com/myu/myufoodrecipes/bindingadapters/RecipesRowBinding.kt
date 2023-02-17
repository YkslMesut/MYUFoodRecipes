package com.myu.myufoodrecipes.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.myu.myufoodrecipes.R

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

    }
}