package com.myu.myufoodrecipes.data.database

import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao : RecipesDao
) {
    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    fun readDatabase() : kotlinx.coroutines.flow.Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }
}