package com.example.appscheduler

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideAppScheduleDao(database: AppScheduleDatabase): AppScheduleDao {
        return database.appScheduleDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppScheduleDatabase {
        return Room.databaseBuilder(
            context,
            AppScheduleDatabase::class.java,
            "app_schedule_database"
        ).build()
    }


}