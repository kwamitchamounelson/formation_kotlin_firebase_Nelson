package com.example.workstation.moneypal.util

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

object DynamicLinkUtil {
    fun generateContentLink(): Uri {
        val baseUrl = Uri.parse("https://www.youtube.com/watch?v=22uV3hV91M8&vl=fr")
        val domain = "https://workstation.page.link"

        val link = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(domain)
            .setIosParameters(DynamicLink.IosParameters.Builder("com.example.workstation.moneypal").build())
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.example.workstation.moneypal").build())
            .buildDynamicLink()

        return link.uri
    }
}