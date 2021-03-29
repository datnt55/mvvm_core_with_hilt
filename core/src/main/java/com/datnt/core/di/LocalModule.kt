package com.datnt.core.di

import android.content.Context
import android.content.SharedPreferences
import com.datnt.core.di.qualifier.PreferenceInfo
import com.datnt.core.model.local.preference.PreferenceHelper
import com.datnt.core.model.local.preference.PreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    fun providePreference(@ApplicationContext context: Context, @PreferenceInfo name : String) : SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    @Provides
    fun providePrefRepository(preferenceRepository: PreferenceRepository): PreferenceHelper = preferenceRepository
}