package com.serhohuk.powerchat.utils

import com.serhohuk.powerchat.data.Message

interface MessageAdded {
    fun onMessageAdded(message: Message)
    fun onWriteByMe(value : Boolean)
}