package com.serhohuk.powerchat.repository


import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.data.SharedPrefsStorage

class AppRepository(val prefs : SharedPrefsStorage) {


    fun getIsLoggedIn(key : String) : Boolean{
        return prefs.getIsLoggedIn(key)
    }

    fun setBoolean(key: String, value : Boolean) : Boolean{
        return prefs.saveBoolean(key,value)
    }

    fun setInt(key: String, value: Int) : Boolean {
        return prefs.saveInteger(key,value)
    }

    fun setString(key: String, value: String) : Boolean{
        return prefs.saveString(key,value)
    }

    fun <T>setClass(key: String, value: T){
        prefs.saveClass(key,value)
    }

    fun getAccountId() : String {
        return prefs.getAccountId()
    }

    fun getIsFirstLogin() : Boolean {
        return prefs.getIsFirstLogin()
    }

    fun getPowerAccount() : PowerAccount {
        return prefs.getPowerAccount()
    }



}