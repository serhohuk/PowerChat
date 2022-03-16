package com.serhohuk.powerchat.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PowerAccount(
    var displayName : String?,
    var familyName: String?,
    var givenName : String?,
    var email: String?,
    var idToken : String?,
    var id : String?,
    var photoUri : String?,
    var authCode : String?,
    var isExpired : Boolean?
) : Parcelable {

    companion object{
        fun getEmptyAccount() : PowerAccount{
            return PowerAccount(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null)
        }

        fun convertToAccount(map : Map<String, Any>) : PowerAccount{
            return PowerAccount(
                displayName = map["displayName"].toString(),
                familyName = map["familyName"].toString(),
                givenName = map["givenName"].toString(),
                email = map["email"].toString(),
                idToken = map["idToken"].toString(),
                id = map["id"].toString(),
                photoUri = map["photoUri"].toString(),
                authCode = map["authCode"].toString(),
                isExpired = map["isExpired"] as Boolean?
            )
        }

    }

}