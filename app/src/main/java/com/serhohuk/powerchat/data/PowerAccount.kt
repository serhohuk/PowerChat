package com.serhohuk.powerchat.data

import android.net.Uri

data class PowerAccount(
    var displayName : String?,
    var familyName: String?,
    var givenName : String?,
    var email: String?,
    var idToken : String?,
    var id : String?,
    var photoUri : Uri?,
    var authCode : String?,
    var isExpired : Boolean?
) {
}