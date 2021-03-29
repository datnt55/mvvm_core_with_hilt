package com.datnt.core.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthenticateURL
