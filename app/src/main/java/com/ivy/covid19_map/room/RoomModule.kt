package com.ivy.covid19_map.room

import android.content.Context
import com.ivy.covid19_map.repository.CenterRepository
import com.ivy.covid19_map.room.CenterDAO
import com.ivy.covid19_map.room.CenterDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/* hilt를 이용하여 room 관련 객체 의존성 주입 */
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