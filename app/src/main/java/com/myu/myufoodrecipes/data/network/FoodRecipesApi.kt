package com.myu.myufoodrecipes.data.network

import com.myu.myufoodrecipes.models.FoodJoke
import com.myu.myufoodrecipes.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FoodRecipesApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries : Map<String,String>
    ) : Response<FoodRecipe>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery : Map<String,String>
    ) : Response<FoodRecipe>

    @GET("food/jokes/random")
    suspend fun getFoodJoke(
        @Query("apiKey") apiKey : String
    ) : Response<FoodJoke>
}