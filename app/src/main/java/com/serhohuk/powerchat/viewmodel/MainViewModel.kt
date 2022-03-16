package com.serhohuk.powerchat.viewmodel

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.serhohuk.powerchat.data.Message
import com.serhohuk.powerchat.data.PowerAccount
import com.serhohuk.powerchat.repository.AppRepository
import com.serhohuk.powerchat.utils.MessageAdded
import java.util.*

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val TAG = "viewModel_TAG"

    private val db = FirebaseFirestore.getInstance()

    private val _users : MutableLiveData<List<Map<String, Any>>> = MutableLiveData()
    val users : LiveData<List<Map<String, Any>>> = _users


    fun getIsLoggedIn(key: String) : Boolean{
        return repository.getIsLoggedIn(key)
    }

    fun saveBoolean(key: String, value: Boolean) : Boolean{
        return repository.setBoolean(key,value)
    }

    fun saveString(key: String, value: String) : Boolean {
        return repository.setString(key,value)
    }

    fun getAccountId() : String{
        return repository.getAccountId()
    }

    fun getIsFirstLogin() : Boolean {
        return repository.getIsFirstLogin()
    }

    fun getPowerAccount() : PowerAccount {
        return repository.getPowerAccount()
    }

    fun <T>saveClass(key : String, value : T) {
        repository.setClass(key,value)
    }

    fun saveAccountInFirebase(account : PowerAccount){
        db.collection("Users")
            .document(account.id!!)
            .set(account, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun getAccountByIdInFirebase(id : String){
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun getAccountsFromFirebase(query : String, id: String) {
        val resultMap = mutableListOf<Map<String, Any>>()
        if (query.isEmpty()) _users.value = resultMap
        db.collection("Users").get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")
                val name = document.data["displayName"].toString()
                    .lowercase()
                val mQuery = query.lowercase().trim()
                if (name.contains(mQuery)
                    && document.data["id"].toString() != id
                ){
                    resultMap.add(document.data)
                }
            }
            _users.value = resultMap
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)
        }
    }

    fun getDbCollection(collectionName : String) : CollectionReference{
        return db.collection(collectionName)
    }


}