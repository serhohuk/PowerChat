package com.serhohuk.powerchat.data

import com.google.firebase.Timestamp
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
    var seenTime: Date?=null)
{

    companion object{
        fun convertToMessage(map : Map<String,Any>) : Message{
            val deliveryTime = (map["deliveryTime"] as Timestamp?)?.seconds?.times(1000) ?:
            (map["createdAt"] as Timestamp?)!!.seconds*1000
            return Message(
                id = map["id"].toString(),
                from = map["from"].toString(),
                to= map["to"].toString(),
                createdAt = Date((map["createdAt"] as Timestamp?)!!.seconds*1000),
                type = "text",
                status = 1,
                textMessage = TextMessage(text = (map["textMessage"] as Map<*, *>)["text"].toString()),
                deliveryTime = Date(deliveryTime),
                seenTime = Date(deliveryTime)
            )
        }
    }
}

data class TextMessage(val text: String)
