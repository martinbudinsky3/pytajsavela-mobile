package com.example.mtaafe.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.mtaafe.R

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.user_info_shared_prefs), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "api_token"
        const val USER_ID = "id"
    }

    fun saveApiToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun saveUserId(id: Long) {
        val editor = prefs.edit()
        editor.putLong(USER_ID, id)
        editor.apply()
    }

    fun fetchApiToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchUserId(): Long? {
        return prefs.getLong(USER_ID, 0L)
    }
}