package com.myu.myufoodrecipes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myu.myufoodrecipes.data.database.entities.FavoritesEntity
import com.myu.myufoodrecipes.data.database.entities.FoodJokeEntity
import com.myu.myufoodrecipes.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class,FavoritesEntity::class,FoodJokeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipesDao() : RecipesDao
}