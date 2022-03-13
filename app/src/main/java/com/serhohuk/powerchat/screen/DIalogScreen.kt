package com.serhohuk.powerchat.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
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


@Destination
@Composable
fun DialogScreen(navigator : DestinationsNavigator, account: GoogleSignInAccount){
    //navigator.clearBackStack(LoginScreenDestination.route)
    val powerAccount = PowerAccount(
        displayName = account.displayName,
        familyName = account.familyName,
        givenName = account.givenName,
        photoUri = account.photoUrl,
        id = account.id,
        idToken = account.idToken,
        isExpired = account.isExpired,
        email = account.email,
        authCode = account.serverAuthCode
    )
    
    Scaffold(modifier = Modifier.fillMaxSize(),
    topBar = {
        TopAppBar(
            backgroundColor = Color.Black,
            title = {
                Text(
                    text = account.displayName.toString(),
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(onClick = {
                }) {
                    Icon(Icons.Filled.Logout, contentDescription = "Log Out",
                    tint= Color.Gray)
                }
            }
        )
    },
    backgroundColor = colorResource(id = R.color.main_color)) {
        Box(modifier = Modifier.fillMaxSize()) {
            
        }
        
    }

}