package com.oxiion.campuscart.utils

import android.annotation.SuppressLint
import android.content.Context

object SharedPreferencesManager {
    private const val PREF_NAME = "AppPreferences"
    private const val KEY_IS_LOGGED_OUT = "isLoggedOut"
    private const val KEY_UID = "uid"

    @SuppressLint("CommitPrefEdits")
    fun saveLogOutState(context: Context, isLoggedOut: Boolean){
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_OUT,false).apply()
    }
    fun isLoggedOut(context: Context): Boolean{
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_OUT, true)
    }
    // Save UID
    fun saveUid(context: Context, uid: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_UID, uid).apply()
    }

    // Get UID
    fun getUid(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_UID, null) // Return null if no UID is stored
    }

    // Remove UID (logout)
    fun removeUid(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_UID).apply()
    }
}