package com.serhohuk.powerchat.api

import com.google.firebase.firestore.CollectionReference
import com.serhohuk.powerchat.data.PowerAccount

interface DialogsGetter{
    fun onSuccess(list : List<PowerAccount>)
    fun onError(msg : String)
}

class DialogsList(
    private val usersCollection: CollectionReference,
    private val msgCollection: CollectionReference,
    private val myAccountId : String,
    private val listener : DialogsGetter
) {

    fun getCompanionsId(){
        val listOfIds = mutableListOf<String>()
        msgCollection.get().addOnSuccessListener { snapshotDialogs->
            for (dialog in snapshotDialogs.documents){
                if (dialog.id.contains(myAccountId)){
                    listOfIds.add(getUserIdFromDialogId(dialog.id))
                }
            }
            getAccounts(listOfIds)
        }
        .addOnFailureListener{
            listener.onError(it.message.toString())
        }
    }

    private fun getAccounts(list : List<String>){
        val listOfUsers = mutableListOf<PowerAccount>()
        usersCollection.get()
            .addOnSuccessListener { usersSnapshots->
                for (user in usersSnapshots.documents){
                    if (list.contains(user.id)){
                        listOfUsers.add(PowerAccount.convertToAccount(user.data!!))
                    }
                }
                listener.onSuccess(listOfUsers)
            }
            .addOnFailureListener{
                listener.onError(it.message.toString())
            }
    }

    private fun getUserIdFromDialogId(dialogId : String) : String{
        val splitIds = dialogId.split("_")
        var result = ""
        for (item in splitIds){
            if (item!=myAccountId){
                result = item
            }
        }
        return result
    }

}