package com.serhohuk.powerchat.api

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.serhohuk.powerchat.data.Dialog
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
        msgCollection.document("${fromUser}_${toUser}").get()
            .addOnSuccessListener { documentSnapshot->
                if (documentSnapshot.exists()){
                    Log.d(TAG,"Case 1")
                    send("${fromUser}_${toUser}", message)
                }
                else{
                    msgCollection.document("${toUser}_${fromUser}").get()
                            .addOnSuccessListener { documentSnapshot2->
                                Log.d(TAG,"Case 2")
                                send("${toUser}_${fromUser}", message)
                            }
                }
            }
    }

    private fun send(doc: String, message: Message){
        msgCollection.document(doc).set(Dialog(true))
        try {
            chatUser.id=doc
            message.status=1
            msgCollection.document(doc).collection("messages").document(message.createdAt.toString()).set(
                message,
                SetOptions.merge()
            ).addOnSuccessListener {
                Log.d(TAG,"Message sender Success ${message.createdAt}")
                listener.onSuccess(message)
            }.addOnFailureListener {
                message.status=4
                Log.d(TAG,"Message sender Failed ${it.message}")
                listener.onFailed(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}