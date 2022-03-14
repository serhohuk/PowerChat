package com.serhohuk.powerchat.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.api.FirebaseUtils
import com.serhohuk.powerchat.api.MessageSender
import com.serhohuk.powerchat.api.OnMessageResponse
import com.serhohuk.powerchat.api.TAG
import com.serhohuk.powerchat.data.Message
import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.data.TextMessage
import com.serhohuk.powerchat.utils.StringUtils.generateId
import com.serhohuk.powerchat.viewmodel.MainViewModel
import com.skydoves.landscapist.coil.CoilImage
import org.koin.androidx.compose.viewModel
import java.util.*


@Destination
@Composable
fun MessageScreen(navigator: DestinationsNavigator, account : PowerAccount){

    val viewModel : MainViewModel by viewModel<MainViewModel>()

    val textFieldValue = remember{
        mutableStateOf("")
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.LightGray,
        topBar = {
            TopAppBar(
                backgroundColor = Color.Black,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight(0.8f)
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .size(30.dp),
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
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }

                },
                actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            Icons.Filled.MoreVert, contentDescription = "More",
                        tint = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.popBackStack()
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack, contentDescription = "Back",
                            tint = Color.White)
                    }
                },
                contentColor = Color.Blue
            )
        },
        bottomBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .weight(7f)
                        .fillMaxHeight(),
                    value = textFieldValue.value ,
                    onValueChange = {
                        textFieldValue.value = it
                    },
                    placeholder = {
                        Text(text = "Send Message", color = Color.Gray)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(modifier = Modifier.weight(1f),onClick = {
                    testMsgSender(viewModel, account,Message(
                        id = generateId(),
                        from= viewModel.getAccountId(),
                        to= account.id.toString(),
                        Date(), textMessage = TextMessage(textFieldValue.value)) )

                }) {
                    Icon(
                        Icons.Filled.Send, contentDescription = "Send",
                        tint = Color.Gray)
                }
            }
        }
    ) {
        Column(modifier= Modifier.fillMaxSize()) {
            LazyColumn(modifier= Modifier.fillMaxHeight()){

            }
        }
    }

}

fun testMsgSender(viewModel: MainViewModel,
                  companionUser : PowerAccount,
                  msg: Message){
    val msgSender = MessageSender(
        viewModel.getDbCollection("Dialogs"), companionUser,
        object : OnMessageResponse{
            override fun onSuccess(message: Message) {
                Log.e(TAG,message.textMessage.text)
            }

            override fun onFailed(message: Message) {
                Log.e(TAG,message.textMessage.text)
            }

        })
    msgSender.checkAndSend(viewModel.getPowerAccount().id.toString(),
        companionUser.id.toString(),msg)

}
