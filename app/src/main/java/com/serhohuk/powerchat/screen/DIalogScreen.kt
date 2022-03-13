package com.serhohuk.powerchat.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.R
import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.ACCOUNT_ID_STRING
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.IS_FIRST_LOGIN
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.POWER_ACCOUNT_CLASS
import com.serhohuk.powerchat.viewmodel.MainViewModel
import org.koin.androidx.compose.viewModel


@Destination
@Composable
fun DialogScreen(navigator : DestinationsNavigator, account: GoogleSignInAccount){
    //navigator.clearBackStack(LoginScreenDestination.route)
    var powerAccount = PowerAccount(
        displayName = account.displayName,
        familyName = account.familyName,
        givenName = account.givenName,
        photoUri = account.photoUrl?.path,
        id = account.id,
        idToken = account.idToken,
        isExpired = account.isExpired,
        email = account.email,
        authCode = account.serverAuthCode
    )
    val viewModel : MainViewModel by viewModel<MainViewModel>()

    if (viewModel.getIsFirstLogin()){
        viewModel.saveString(ACCOUNT_ID_STRING, powerAccount.id?: "empty")
        viewModel.saveAccountInFirebase(powerAccount)
        viewModel.saveClass(POWER_ACCOUNT_CLASS, powerAccount)
        viewModel.saveBoolean(IS_FIRST_LOGIN,false)
        //viewModel.getAccountByIdInFirebase(viewModel.getAccountId())
    } else {
        powerAccount = viewModel.getPowerAccount()
    }
    
    Scaffold(modifier = Modifier.fillMaxSize(),
    topBar = {
        TopAppBar(
            backgroundColor = Color.Black,
            title = {
                Text(
                    text = powerAccount.givenName.toString(),
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(onClick = {

                }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search",
                    tint = Color.White)
                }
                IconButton(onClick = {

                }) {
                    Icon(Icons.Filled.Logout, contentDescription = "Log Out",
                    tint= Color.White)
                }
            }
        )
    },
    backgroundColor = colorResource(id = R.color.main_color)) {
        Box(modifier = Modifier.fillMaxSize()) {
            
        }
        
    }

}