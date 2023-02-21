package com.myu.myufoodrecipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myu.myufoodrecipes.util.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var result : com.myu.myufoodrecipes.models.Result
    )