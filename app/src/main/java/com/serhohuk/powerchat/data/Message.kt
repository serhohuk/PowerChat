package com.serhohuk.powerchat.data

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class Message(
    val id: String,
    var from: String,
    var to : String,
    @ServerTimestamp
    var createdAt: Date?=Date(),
    var type: String="text",//0=text,1=audio,2=image,3=video,4=file
    var status: Int=0,//0=sending,1=sent,2=delivered,3=seen,4=failed
    var textMessage: TextMessage=TextMessage("hello chat"),
    @ServerTimestamp
    var deliveryTime: Date?=null,
    @ServerTimestamp
    var seenTime: Date?=null,
    @set:Exclude @get:Exclude
    var chatUserId: String?=null)

data class TextMessage(val text: String)
