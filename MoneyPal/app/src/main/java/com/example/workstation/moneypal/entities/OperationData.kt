package com.example.workstation.moneypal.entities

import com.example.workstation.moneypal.AppConstants

object OperationData {
    val transfereOperation=Operation(AppConstants.TRANSFERE_D_ARGENT)
    val achatCrediOperation=Operation(AppConstants.ACHAT_DE_CREDIT)
    val factureEneoOperation=Operation(AppConstants.FACTURE_ENEO)
    val retraitOperation=Operation(AppConstants.REATRAIT_D_ARGENT)
    val depotOperation=Operation(AppConstants.DEPOTS)
    val internetOperation=Operation(AppConstants.FORFAIT_INTERNET)


    //liste des operation
    val listeOfOperation= arrayListOf<Operation>(
        transfereOperation,
        achatCrediOperation,
        internetOperation,
        factureEneoOperation,
        retraitOperation,
        depotOperation
    )
}