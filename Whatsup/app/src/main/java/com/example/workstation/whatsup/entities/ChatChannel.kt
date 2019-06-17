package com.example.workstation.whatsup.entities

data class ChatChannel(val userIds: MutableList<String?>) {
    constructor():this(mutableListOf())
}