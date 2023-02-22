package com.myu.myufoodrecipes.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.myu.myufoodrecipes.data.DataStoreRepository
import com.myu.myufoodrecipes.data.MealAndDietType
import com.myu.myufoodrecipes.util.Constants
import com.myu.myufoodrecipes.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.myu.myufoodrecipes.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.myu.myufoodrecipes.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_API_KEY
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_DIET
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_NUMBER
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_SEARCH
import com.myu.myufoodrecipes.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(application: Application,
private val dataStoreRepository: DataStoreRepository) : AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    private lateinit var mealAndDietType: MealAndDietType

    fun saveBackOnline(backOnline : Boolean) = viewModelScope.launch {
        dataStoreRepository.saveBackOnline(backOnline)
    }

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            if (this@RecipesViewModel::mealAndDietType.isInitialized) {
                dataStoreRepository.saveMealAndDietType(
                    mealAndDietType.selectedMealType,
                    mealAndDietType.selectedMealTypeId,
                    mealAndDietType.selectedDietType,
                    mealAndDietType.selectedDietTypeId
                )
            }
        }

    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDietType = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }
    fun applyQueries() : HashMap<String,String> {

        val queries : HashMap<String,String> = HashMap()
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = Constants.API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        if (this@RecipesViewModel::mealAndDietType.isInitialized) {
            queries[QUERY_TYPE] = mealAndDietType.selectedMealType
            queries[QUERY_DIET] = mealAndDietType.selectedDietType
        } else {
            queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
            queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        }

        return queries
    }

    fun applySearchQuery(searchQuery : String) : HashMap<String,String> {

        val queries : HashMap<String,String> = HashMap()
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_API_KEY] = Constants.API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun showNetworkStatus(){
        if (!networkStatus) {
            Toast.makeText(getApplication(),"No Internet Connection",Toast.LENGTH_LONG).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(),"We're back online", Toast.LENGTH_LONG).show()
                saveBackOnline(false)
            }
        }


    }
}