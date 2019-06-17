package com.example.workstation.whatsup.entities

import java.util.*

data class TextMessage(val tex:String,
                       override val time:Date,
                       override val senderId:String,
                       override val type:String=MessageType.TEXT)
    :Message{
    constructor():this("",Date(0),"")
}