package com.example.workstation.moneypal.entities

import android.net.Uri

/**
 * The content of an invitation, with optional fields to accommodate all presenters.
 * This type could be modified to also include an image, for sending invites over email.
 */
data class InviteContent(
    val subject: String?,
    val body: String?,
    val link: Uri
)