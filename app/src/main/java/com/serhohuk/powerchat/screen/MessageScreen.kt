package com.serhohuk.powerchat.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.google.firebase.firestore.remote.WatchChange
import com.google.firestore.v1.DocumentChange
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.serhohuk.powerchat.R
import com.serhohuk.powerchat.api.FirebaseUtils
import com.serhohuk.powerchat.api.MessageSender
import com.serhohuk.powerchat.api.OnMessageResponse
import com.serhohuk.powerchat.api.TAG
import com.serhohuk.powerchat.data.Message
import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.data.TextMessage
import com.serhohuk.powerchat.utils.MessageAdded
import com.serhohuk.powerchat.utils.MessageList
import com.serhohuk.powerchat.utils.StringUtils.generateId
import com.serhohuk.powerchat.viewmodel.MainViewModel
import com.skydoves.landscapist.coil.CoilImage
import org.koin.androidx.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalComposeUiApi
@Destination
@Composable
fun MessageScreen(navigator: DestinationsNavigator, account : PowerAccount){

    val viewModel : MainViewModel by viewModel<MainViewModel>()

    val textFieldValue = remember{
        mutableStateOf("")
    }
    
    val messages = remember {
        mutableStateListOf<Message>()
    }

    val from = remember {
        mutableStateOf(true)
    }

    val listenerStarted = remember{
        mutableStateOf(false)
    }


    if (!listenerStarted.value){
        listenerStarted.value = true
        setMessageListener(viewModel, account.id.toString(), object : MessageAdded {
            override fun onMessageAdded(message: Message) {
                messages.add(message)
            }

            override fun onWriteByMe(value: Boolean) {
                from.value = value
            }

        })
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
                    msgSend(viewModel, account,Message(
                        id = generateId(),
                        from= viewModel.getAccountId(),
                        to= account.id.toString(),
                        Date(), textMessage = TextMessage(textFieldValue.value)),
                        object : OnMessageResponse {
                            override fun onSuccess(message: Message) {
                                textFieldValue.value = ""
                            }

                            override fun onFailed(message: Message) {

                            }
                        })
                }) {
                    Icon(
                        Icons.Filled.Send, contentDescription = "Send",
                        tint = Color.Gray)
                }
            }
        }
    ) {
        LazyColumn(modifier= Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
            contentPadding = PaddingValues(vertical=10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ){
            items(messages.reversed()){ message->
                if(from.value){
                    if(message.from==viewModel.getAccountId()){
                        MyMessage(msg = message.textMessage.text, time =message.deliveryTime!!.dateToString("HH:mm"))
                    } else FriendMessage(msg = message.textMessage.text, time =message.deliveryTime!!.dateToString("HH:mm"))
                } else{
                    if(message.from==viewModel.getAccountId()){
                        MyMessage(msg = message.textMessage.text, time =message.deliveryTime!!.dateToString("HH:mm"))
                    } else FriendMessage(msg = message.textMessage.text, time =message.deliveryTime!!.dateToString("HH:mm"))
                }
            }
        }
    }

}

fun msgSend(viewModel: MainViewModel,
                  companionUser : PowerAccount,
                  msg: Message,
                listener: OnMessageResponse): Message? {
    val msgSender = MessageSender(
        viewModel.getDbCollection("Dialogs"), companionUser, listener)
    msgSender.checkAndSend(viewModel.getPowerAccount().id.toString(),
        companionUser.id.toString(),msg)
    return msgSender.msg
}

fun setMessageListener(viewModel: MainViewModel, userId: String, listener: MessageAdded){
    val collection = viewModel.getDbCollection("Dialogs")
    val first = "${viewModel.getAccountId()}_${userId}"
    val second = "${userId}_${viewModel.getAccountId()}"
    collection.document(first).get().addOnSuccessListener { doc1->
        if (doc1.exists()) {
            listener.onWriteByMe(true)
            collection.document(first).collection("messages").addSnapshotListener { value, error ->
                for (item in value!!.documentChanges) {
                    if (item.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val dat = item.document.data
                        listener.onMessageAdded(Message.convertToMessage(dat))
                    }
                }
            }
        }
        else{
            collection.document(second).get().addOnSuccessListener { doc2->
                listener.onWriteByMe(false)
                collection.document(second).collection("messages").addSnapshotListener { value, error ->
                for (item in value!!.documentChanges){
                            if (item.type == com.google.firebase.firestore.DocumentChange.Type.ADDED){
                                listener.onMessageAdded(Message.convertToMessage(item.document.data))
                            }
                        }
                    }
            }
        }
    }
}


@Composable
fun MyMessage(msg : String, time : String) {
    Box(modifier= Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd) {
        Card(
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp),
            backgroundColor = Color.Yellow,
            modifier = Modifier.padding(end = 10.dp, start = 30.dp)
        ) {
            val constraints = ConstraintSet {
                val msgId = createRefFor("msg")
                val timeId = createRefFor("time")

                constrain(msgId){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.wrapContent
                }

                constrain(timeId){
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            }
            ConstraintLayout(constraintSet = constraints, modifier = Modifier.padding(7.dp)){
                Text(modifier= Modifier
                    .layoutId("msg")
                    .padding(end = 40.dp),
                    text = msg, fontSize = 20.sp, color = colorResource(id = R.color.black) )
                Text(modifier = Modifier.layoutId("time"),text=time,
                    fontSize = 14.sp, color = colorResource(id = R.color.black))
            }
        }
    }
}


@Composable
fun FriendMessage(msg : String, time : String){
    Box(modifier= Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart) {
        Card(
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp),
            backgroundColor = colorResource(id = R.color.light_blue),
            modifier = Modifier.padding(start = 10.dp, end = 30.dp)
        ) {
            val constraints = ConstraintSet {
                val msgId = createRefFor("msg")
                val timeId = createRefFor("time")

                constrain(msgId){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.wrapContent
                }

                constrain(timeId){
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            }
            ConstraintLayout(constraintSet = constraints, modifier = Modifier.padding(7.dp)){
                Text(modifier= Modifier
                    .layoutId("msg")
                    .padding(end = 40.dp), overflow = TextOverflow.Visible,
                    text = msg, fontSize = 22.sp,color = colorResource(id = R.color.white))
                Text(modifier = Modifier.layoutId("time"),text=time,
                    fontSize = 14.sp, color = colorResource(id = R.color.white))
            }
        }
    }
}

private fun Date.dateToString(format: String): String {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(this)
}
