package com.serhohuk.powerchat.screen

sealed class Screen(val route : String){
    object LoginScreen : Screen("login_screen")
    object DialogsScreen : Screen("dialogs_screen")

    fun withArgs(vararg args : String) : String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}
