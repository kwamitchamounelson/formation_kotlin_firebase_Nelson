package com.example.workstation.moneypal.util

import android.content.Context
import android.net.Uri
import com.example.workstation.moneypal.AppConstants
import com.example.workstation.moneypal.entities.GroupUsers
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

object DynamicLinkUtil {
    fun generateContentLink(context: Context,group:GroupUsers,onComplete:(String)->Unit){
        FirestoreUtil.getApkurl {urlApk ->
            val baseUrl = Uri.parse("https://workstation.page.link/${AppConstants.SPLITOR_OF_LINK}${group.groupId}")
            val domain = "https://workstation.page.link"

            val progressDialog=context.indeterminateProgressDialog("Veillez patienter")
            FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("MoneyPal by Nelson")
                        .setDescription("Votre application de gestion de crédit participatif via des transactions Mobile (OrangeMoney et MobileMoney")
                        .setImageUrl(Uri.parse(AppConstants.URL_ICON_DYNAMIC_LINK))
                        .build()
                )
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)
                .setIosParameters(DynamicLink.IosParameters.Builder("com.example.workstation.moneypal").build())
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder("com.example.workstation.moneypal")
                        .setFallbackUrl(Uri.parse(urlApk))
                        .build()
                )
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnSuccessListener { result ->
                    val shortLink = result.shortLink
                    //val flowchartLink = result.previewLink
                    onComplete(shortLink.toString())
                    progressDialog.dismiss()
                }.addOnFailureListener {
                    context.toast("erreur de conversion du lien")
                    progressDialog.dismiss()
                }
        }
    }
}