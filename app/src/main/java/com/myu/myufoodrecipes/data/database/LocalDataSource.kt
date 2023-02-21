package com.myu.myufoodrecipes.data.database

import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao : RecipesDao
) {
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    fun readFavoriteRecipes() : Flow<List<FavoritesEntity>>{
        return recipesDao.readFavoriteRecipe()
    }

    fun readRecipes() : kotlinx.coroutines.flow.Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes () {
        recipesDao.deleteAllFavoriteRecipes()
    }
}