package com.myu.myufoodrecipes.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.myu.myufoodrecipes.data.Repository
import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.data.database.entities.RecipesEntity
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

    private fun insertRecipes(recipesEntity: RecipesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertRecipes(recipesEntity)
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
    var searchRepicesResponse : MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun searchRecipes(searchQuery : Map<String,String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRepicesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRepicesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e : java.lang.Exception) {
                searchRepicesResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            searchRepicesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
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

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
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
}