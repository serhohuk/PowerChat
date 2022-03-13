package com.serhohuk.powerchat.data

import android.content.Context
import com.google.gson.Gson

class SharedPrefsStorage(context: Context) {

    companion object{
        const val SHARED_PREFS_NAME = "chat_app_prefs"
        const val IS_LOGGED_IN_BOOL = "is_logged_in"
        const val ACCOUNT_ID_STRING = "account_id"
        const val IS_FIRST_LOGIN = "is_first_login"
        const val POWER_ACCOUNT_CLASS = "power_account_data"
    }

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun saveBoolean(key : String, value : Boolean) : Boolean {
        sharedPreferences.edit().putBoolean(key,value).apply()
        return true
    }

    fun saveInteger(key: String, value : Int) : Boolean {
        sharedPreferences.edit().putInt(key,value).apply()
        return true
    }

    fun saveString(key: String, value : String) : Boolean {
        sharedPreferences.edit().putString(key,value).apply()
        return true
    }

    fun <T>saveClass(key: String, value : T) : Boolean {
        val json = Gson().toJson(value)
        return saveString(key, json)
    }

    fun getPowerAccount(): PowerAccount {
        val string = sharedPreferences.getString(POWER_ACCOUNT_CLASS, "")
        return Gson().fromJson(string, PowerAccount::class.java) ?: PowerAccount.getEmptyAccount()
    }

    fun getIsLoggedIn(key : String) : Boolean{
        return sharedPreferences.getBoolean(key, false)
    }

    fun getAccountId() : String {
        return sharedPreferences.getString(ACCOUNT_ID_STRING, "empty")!!
    }

    fun getIsFirstLogin() : Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LOGIN, true)
    }


}