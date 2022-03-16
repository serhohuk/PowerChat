package com.serhohuk.powerchat.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.R
import com.serhohuk.powerchat.api.FirebaseUtils.BASE_GOOGLE_ACCOUNT_PHOTO_URL
import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.screen.destinations.MessageScreenDestination
import com.serhohuk.powerchat.viewmodel.MainViewModel
import com.skydoves.landscapist.coil.CoilImage
import org.koin.androidx.compose.viewModel


@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Destination
@Composable
fun SearchUserScreen(navigator : DestinationsNavigator){
    val viewModel : MainViewModel by viewModel<MainViewModel>()

    //val keyboardController = LocalSoftwareKeyboardController.current

    val searchTextField = remember{
        mutableStateOf("")
    }

    val list by viewModel.users.observeAsState(null)



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = colorResource(id = R.color.main_color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = searchTextField.value,
                onValueChange = {
                searchTextField.value = it
            },
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search" )

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp)
            ,
            label = {
                Text(text = "Search Users", color = Color.Black)
            },
            colors = TextFieldDefaults
                .textFieldColors(textColor=Color.Black,
                 backgroundColor = Color.LightGray,
                 focusedLabelColor = Color.Black,
                 unfocusedLabelColor = Color.Black,
                 focusedIndicatorColor = Color.Blue,
                 unfocusedIndicatorColor = Color.Blue
            ),
            shape = CutCornerShape(10.dp),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.getAccountsFromFirebase(searchTextField.value, viewModel.getAccountId())

                })
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(){
                list?.let{
                    items(it){ value->
                        UserCard(
                        navigator = navigator,
                        account = PowerAccount.convertToAccount(value)
                        )
                    }
                }
            }
        }

    }

}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun UserCard(navigator: DestinationsNavigator, account : PowerAccount){
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .padding(horizontal = 15.dp),
        onClick = {
            navigator.navigate(MessageScreenDestination(account = account))
        }
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
                    imageModel = BASE_GOOGLE_ACCOUNT_PHOTO_URL + account.photoUri,
                    contentScale = ContentScale.Crop
                )
            }
            Text(text = account.givenName.toString(), fontSize = 25.sp)
        }
    }
}

