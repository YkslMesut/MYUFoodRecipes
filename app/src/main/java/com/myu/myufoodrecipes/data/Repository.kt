package com.myu.myufoodrecipes.data

import com.myu.myufoodrecipes.data.database.LocalDataSource
import com.myu.myufoodrecipes.data.network.RemoteDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    val remote = remoteDataSource
    val local = localDataSource
}