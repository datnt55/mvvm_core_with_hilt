/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datnt.exmaple.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datnt.core.model.local.preference.PreferenceHelper
import com.qbicles.core.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(private val preferenceHelper: PreferenceHelper): BaseViewModel() {

    val loginState = MutableLiveData<String>()

    fun login(username: String) {
        preferenceHelper.token = username
        loginState.value = preferenceHelper.token
    }


}
