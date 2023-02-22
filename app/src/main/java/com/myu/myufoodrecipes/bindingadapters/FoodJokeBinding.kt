package com.myu.myufoodrecipes.bindingadapters

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.myu.myufoodrecipes.data.database.entities.FoodJokeEntity
import com.myu.myufoodrecipes.models.FoodJoke
import com.myu.myufoodrecipes.util.NetworkResult
import com.myu.myufoodrecipes.util.NetworkResult.Success

class FoodJokeBinding {

    companion object {

        @BindingAdapter("readApiResponse3", "readDatabase3", requireAll = false)
        @JvmStatic
        fun setCardAndProfessVisibility (
            view : View,
            apiResponse : NetworkResult<FoodJoke>,
            database : List<FoodJokeEntity>?
        ) {
            when(apiResponse) {
                is NetworkResult.Loading -> {
                    when(view) {
                        is ProgressBar -> {
                            view.visibility = View.VISIBLE
                        }
                        is MaterialCardView -> {
                            view.visibility = View.INVISIBLE
                        }
                    }
                }
                is NetworkResult.Error -> {
                    when(view) {
                        is ProgressBar -> {
                            view.visibility = View.INVISIBLE
                        }
                        is MaterialCardView -> {
                            view.visibility = View.VISIBLE
                            if (database!= null) {
                                if (database.isEmpty()){
                                    view.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
                is Success -> {
                    when(view) {
                        is ProgressBar -> {
                            view.visibility = View.INVISIBLE
                        }
                        is MaterialCardView -> {
                            view.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        @BindingAdapter("readApiResponse4" ,"readDatabase4" , requireAll = false)
        @JvmStatic
        fun setErrorViewsVisibility(
            view : View,
            apiResponse : NetworkResult<FoodJoke>?,
            database : List<FoodJokeEntity>?
        ){
            if (database != null) {
                if (database.isEmpty()) {
                    view.visibility = View.VISIBLE
                    if (view is TextView) {
                        if (apiResponse != null) {
                            view.text = apiResponse.message.toString()
                        }
                    }
                }
            }

            if (apiResponse is NetworkResult.Success) {
                view.visibility = View.INVISIBLE
            }
        }
    }
}