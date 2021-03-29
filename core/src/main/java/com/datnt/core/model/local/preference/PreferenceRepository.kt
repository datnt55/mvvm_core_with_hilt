package com.datnt.core.model.local.preference

import android.content.SharedPreferences
import com.datnt.core.utils.Constants
import javax.inject.Inject

class PreferenceRepository
@Inject constructor(private val preference: SharedPreferences) :
    PreferenceHelper {

    override var token by preference.String(Constants.TOKEN, "")

}