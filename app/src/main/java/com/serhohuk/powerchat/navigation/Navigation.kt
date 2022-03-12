package com.serhohuk.powerchat.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.DestinationsNavHost
import com.serhohuk.powerchat.screen.*
import com.serhohuk.powerchat.screen.destinations.DialogScreenDestination
import com.serhohuk.powerchat.screen.destinations.LoginScreenDestination


@Composable
fun Navigation(){

    DestinationsNavHost(navGraph = NavGraphs.root)
}
