package com.datnt.exmaple.di

import com.datnt.core.di.qualifier.BaseUrl
import com.datnt.core.di.qualifier.PreferenceInfo
import com.datnt.core.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class AppMicroModule {

    @Provides
    @PreferenceInfo
    internal fun providePreferenceName(): String = Constants.PREFERENCES_FILE_NAME

    @Provides
    @BaseUrl
    internal fun provideHost(): String = Constants.PREFERENCES_FILE_NAME

}
