package com.serhohuk.powerchat.screen


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.R
import com.serhohuk.powerchat.api.DialogsGetter
import com.serhohuk.powerchat.api.DialogsList
import com.serhohuk.powerchat.api.FirebaseUtils
import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.ACCOUNT_ID_STRING
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.IS_FIRST_LOGIN
import com.serhohuk.powerchat.data.SharedPrefsStorage.Companion.POWER_ACCOUNT_CLASS
import com.serhohuk.powerchat.screen.destinations.DialogScreenDestination
import com.serhohuk.powerchat.screen.destinations.MessageScreenDestination
import com.serhohuk.powerchat.screen.destinations.SearchUserScreenDestination
import com.serhohuk.powerchat.viewmodel.MainViewModel
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel


@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Destination
@Composable
fun DialogScreen(navigator : DestinationsNavigator, account: GoogleSignInAccount){
    //navigator.clearBackStack(LoginScreenDestination.route)

    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val interactionSource = remember { MutableInteractionSource() }
    
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

    val dialogs = remember{
        mutableStateOf(listOf<PowerAccount>())
    }

    val dialogsList = DialogsList(
        viewModel.getDbCollection("Users"),
        viewModel.getDbCollection("Dialogs"),
        viewModel.getAccountId(),
        object : DialogsGetter{
            override fun onSuccess(list: List<PowerAccount>) {
                dialogs.value = list
            }

            override fun onError(msg: String) {
                Log.e("dialog_screen", msg)
            }

        }
    )

    dialogsList.getCompanionsId()

    if (viewModel.getIsFirstLogin()){
        viewModel.saveString(ACCOUNT_ID_STRING, powerAccount.id?: "empty")
        viewModel.saveAccountInFirebase(powerAccount)
        viewModel.saveClass(POWER_ACCOUNT_CLASS, powerAccount)
        viewModel.saveBoolean(IS_FIRST_LOGIN,false)
        //viewModel.getAccountByIdInFirebase(viewModel.getAccountId())
    } else {
        powerAccount = viewModel.getPowerAccount()
    }

    //viewModel.getAccountByIdInFirebase(viewModel.getAccountId())
    
    Scaffold(modifier = Modifier.fillMaxSize(),scaffoldState = state,
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
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        with(state.drawerState){
                            if(isClosed) open() else close()
                        }
                    }
                }) {
                    Icon(
                        Icons.Filled.Menu, contentDescription = "Menu",
                        tint = Color.White)
                }
            }
        )
    },
    backgroundColor = colorResource(id = R.color.main_color),
    floatingActionButton = {
        FloatingActionButton(onClick = {
            navigator.navigate(SearchUserScreenDestination)
        }, backgroundColor = Color.Black) {
            Icon(Icons.Filled.Edit,"",tint= colorResource(id = R.color.white))
        }
    },
        drawerContent = {
            Text("Log out", modifier = Modifier.fillMaxWidth().height(50.dp)
                .padding(16.dp)
                .clickable(interactionSource = interactionSource, indication = null){
                    Toast.makeText(context, "WORK", Toast.LENGTH_SHORT)
                        .show()
                })
            Divider()
        }
    ) {
        LazyColumn(){
            items(dialogs.value){ dialog->
                DialogMessages(
                    navigator, dialog
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun MyBottomBar(navigator: DestinationsNavigator,index : Int){
    val selectedIndex = remember{
        mutableStateOf(index)
    }
    BottomNavigation(backgroundColor = Color.Black) {
        BottomNavigationItem(
            selected = selectedIndex.value==0,
            onClick = {

                selectedIndex.value=0
                navigator.navigate(DialogScreenDestination(GoogleSignInAccount.createDefault()))
            },
            label = {
                Text("Messages")
            },
            icon = {
                Icon(Icons.Filled.Message, contentDescription ="Message" )
            }, unselectedContentColor = Color.Gray, selectedContentColor = Color.White
        )
        BottomNavigationItem(
            selected = selectedIndex.value==1,
            onClick = {
                selectedIndex.value=1
                navigator.navigate(SearchUserScreenDestination)
            },
            label = {
                Text("Search")
            },
            icon = {
                Icon(Icons.Filled.Search, contentDescription ="Search" )
            }, unselectedContentColor = Color.Gray, selectedContentColor = Color.White
        )
        BottomNavigationItem(
            selected = selectedIndex.value==2,
            onClick = {
                selectedIndex.value=2

            },
            label = {
                Text("Settings")
            },
            icon = {
                Icon(Icons.Filled.Settings, contentDescription ="Settings" )
            }, unselectedContentColor = Color.Gray, selectedContentColor = Color.White
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun DialogMessages(navigator: DestinationsNavigator, account: PowerAccount){
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp),
        shape= RectangleShape,
        onClick = {
            navigator.navigate(MessageScreenDestination(account = account))
        },
        backgroundColor = Color.LightGray
    ) {
        Row(modifier= Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(48.dp),
                shape = CircleShape,
                elevation = 2.dp
            ) {
                CoilImage(
                    modifier= Modifier.fillMaxSize(),
                    imageModel = FirebaseUtils.BASE_GOOGLE_ACCOUNT_PHOTO_URL + account.photoUri,
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = account.givenName.toString(),
                fontSize = 25.sp, color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

private fun getGoogleLoginAuth(context : Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestId()
        .requestProfile()
        .build()
    return GoogleSignIn.getClient(context, gso)
}