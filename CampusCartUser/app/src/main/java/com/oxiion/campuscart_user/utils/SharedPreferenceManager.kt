package com.oxiion.campuscart_user.utils

import android.annotation.SuppressLint
import android.content.Context

object SharedPreferencesManager {
    private const val PREF_NAME = "AppPreferences"
    private const val KEY_IS_LOGGED_OUT = "isLoggedOut"
    private const val KEY_UID = "uid"
    private const val KEY_COLLEGE = "college"
    private const val KEY_HOSTEL_NUMBER = "hostelNumber"

    // Save the logout state
    @SuppressLint("CommitPrefEdits")
    fun saveLogOutState(context: Context, isLoggedOut: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_OUT, isLoggedOut).apply()
    }

    // Check if the user is logged out
    fun isLoggedOut(context: Context): Boolean {
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
        return sharedPreferences.getString(KEY_UID, null)
    }

    // Save College name
    fun saveCollege(context: Context, college: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_COLLEGE, college).apply()
    }

    // Get College name
    fun getCollege(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_COLLEGE, null)
    }

    // Save Hostel Number
    fun saveHostelNumber(context: Context, hostelNumber: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_HOSTEL_NUMBER, hostelNumber).apply()
    }

    // Get Hostel Number
    fun getHostelNumber(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_HOSTEL_NUMBER, null)
    }

    // Remove UID and user details (log out)
    fun removeUid(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_UID).apply()
        sharedPreferences.edit().remove(KEY_COLLEGE).apply()
        sharedPreferences.edit().remove(KEY_HOSTEL_NUMBER).apply()
    }
}
