package com.myu.myufoodrecipes.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myu.myufoodrecipes.models.FoodRecipe
import com.myu.myufoodrecipes.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe : FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id : Int = 0
}