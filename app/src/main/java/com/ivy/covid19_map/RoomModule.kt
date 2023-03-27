package com.ivy.covid19_map

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext appContext: Context)
    = CenterDB.getDatabase(appContext)

    @Provides
    @Singleton
    fun providesCenterDAO(centerDB: CenterDB) = centerDB.getCenterDAO()

    @Provides
    @Singleton
    fun providesRepository(centerDAO: CenterDAO) = CenterRepository(centerDAO)
}