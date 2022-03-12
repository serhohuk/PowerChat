package com.serhohuk.powerchat.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.R

@Destination
@Composable
fun DialogScreen(navigator : DestinationsNavigator){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(id = R.color.main_color)),
    contentAlignment = Alignment.Center){
        Text(text = "You are logged in",
        fontSize = 34.sp)

    }

}