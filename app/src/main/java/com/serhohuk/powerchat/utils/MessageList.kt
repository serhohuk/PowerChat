package com.serhohuk.powerchat.utils

import com.serhohuk.powerchat.data.Message

interface MessageList {
    fun onSuccess(list : List<Message>, fromYou : Boolean)
}