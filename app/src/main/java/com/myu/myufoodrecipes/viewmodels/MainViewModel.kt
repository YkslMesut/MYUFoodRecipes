package com.myu.myufoodrecipes.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.myu.myufoodrecipes.data.Repository
import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.data.database.entities.FoodJokeEntity
import com.myu.myufoodrecipes.data.database.entities.RecipesEntity
import com.myu.myufoodrecipes.models.FoodJoke
import com.myu.myufoodrecipes.models.FoodRecipe
import com.myu.myufoodrecipes.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application : Application) : AndroidViewModel(application) {

    // ROOM DATABASE */

    val readRecipes : LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes : LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()
    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecipes(recipesEntity)
    }

    fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertFoodJoke(foodJokeEntity)
    }

    fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertFavoriteRecipes(favoritesEntity)
    }

    fun deleteFavoriteRecipes(favoritesEntity: FavoritesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteFavoriteRecipe(favoritesEntity)
    }

    fun deleteAllFavoriteRecipes() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllFavoriteRecipes()
    }


    /**  RETROTFIT  */
    var recipesResponse : MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchRecipesResponse : MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var foodJokeResponse : MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()

    fun getFoodJoke(apiKey : String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }

    fun searchRecipes(searchQuery : Map<String,String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getRecipes(queries : Map<String,String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e : java.lang.Exception) {
                recipesResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodJokeResponse(response)

                val foodJoke = foodJokeResponse.value!!.data
                if (foodJoke != null)
                    offlineCacheFoodJoke(foodJoke)
            } catch (e : java.lang.Exception) {
                foodJokeResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection.")
        }

    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e : java.lang.Exception) {
                searchRecipesResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke>? {
        return when{
            response.message().toString().contains("timeout ") -> {
                NetworkResult.Error("Timeout")
            }
            response.isSuccessful ->{
                val foodRecipes = response.body()
                NetworkResult.Success(foodRecipes)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        return when{
            response.message().toString().contains("timeout ") -> {
                NetworkResult.Error("Timeout")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                NetworkResult.Error("Recipes not found")
            }
            response.isSuccessful ->{
                val foodRecipes = response.body()
                NetworkResult.Success(foodRecipes)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection() : Boolean {
            val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }

}