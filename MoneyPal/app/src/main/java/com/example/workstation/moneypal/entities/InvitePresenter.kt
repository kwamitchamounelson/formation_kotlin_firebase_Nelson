package com.example.workstation.moneypal.entities

import android.content.Context
import android.support.annotation.DrawableRes

/**
 * Presents the invite using a specific method, such as email or social.
 */
open class InvitePresenter(
    /** The user-visible name of the invite method, like 'Email' or 'SMS'  */
    val name: String,
    /** An icon representing the invite method.  */
    @param:DrawableRes @field:DrawableRes
    val icon: Int,
    /** Whether or not the method is available on this device. For example, SMS is phone only.  */
    val isAvailable: Boolean,
    /** The Content of the invitation  */
    val content: InviteContent
) {
    /**
     * Send the invitation using the specified method.
     */
    open fun sendInvite(context: Context) {
        // ...
    }
}