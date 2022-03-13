package com.serhohuk.powerchat.api

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.serhohuk.powerchat.data.Message
import com.serhohuk.powerchat.data.PowerAccount

interface OnMessageResponse{
    fun onSuccess(message: Message)
    fun onFailed(message: Message)
}

const val TAG = "MESSAGE_SENDER"

class MessageSender(private val msgCollection: CollectionReference,
                    private val chatUser: PowerAccount,
                    private val listener: OnMessageResponse) {

    fun checkAndSend(fromUser: String, toUser: String, message: Message) {
        val docId = chatUser.id
        if (!docId.isNullOrEmpty()){
            Log.d(TAG,"Case 0 ${chatUser.id}")
            send(docId, message)
        } else {
            //so we don't create multiple nodes for same chat
            msgCollection.document("${fromUser}_${toUser}").get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        //this node exists send your message
                        Log.d(TAG,"Case 1")
                        send("${fromUser}_${toUser}", message)
                    } else {
                        //senderId_receiverId node doesn't exist check receiverId_senderId
                        msgCollection.document("${toUser}_${fromUser}").get()
                            .addOnSuccessListener { documentSnapshot2 ->
                                if (documentSnapshot2.exists()) {
                                    Log.d(TAG,"Case 2")
                                    send("${toUser}_${fromUser}", message)
                                } else {
                                    //no previous chat history(senderId_receiverId & receiverId_senderId both don't exist)
                                    //so we create document senderId_receiverId then messages array then add messageMap to messages
                                    //this node exists send your message
                                    //add ids of chat members
                                    Log.d(TAG,"Case 3")
                                    msgCollection.document("${fromUser}_${toUser}")
                                        .set(mapOf("chat_members" to FieldValue.arrayUnion(fromUser, toUser)),
                                            SetOptions.merge()
                                        ).addOnSuccessListener {
                                            Log.d(TAG,"chat member update successfully")
                                            send("${fromUser}_${toUser}", message)
                                        }.addOnFailureListener {
                                            Log.d(TAG,"chat member update failed ${it.message}")
                                        }
                                }
                            }
                    }
                }
        }
    }

    private fun send(doc: String, message: Message){
        try {
            chatUser.id=doc
            val chatUserId = message.chatUserId
            message.status=1
            msgCollection.document(doc).collection("messages").document(message.createdAt.toString()).set(
                message,
                SetOptions.merge()
            ).addOnSuccessListener {
                Log.d(TAG,"Message sender Sucesss ${message.createdAt}")
                message.chatUserId=chatUserId
                listener.onSuccess(message)
            }.addOnFailureListener {
                message.chatUserId=chatUserId
                message.status=4
                Log.d(TAG,"Message sender Failed ${it.message}")
                listener.onFailed(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}