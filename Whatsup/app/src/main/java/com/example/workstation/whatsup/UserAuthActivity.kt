package com.example.workstation.whatsup


import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.workstation.whatsup.util.FirestoreUtil
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_user_auth.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog
import java.util.concurrent.TimeUnit

class UserAuthActivity : AppCompatActivity() {

    // [START declare_auth]
    private var auth=FirebaseAuth.getInstance()
    // [END declare_auth]


    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_auth)

        updateUI(STATE_INITIALIZED)
        verify_phone_number2.setOnClickListener {

            if(code_verification2.visibility==View.INVISIBLE){
                // envoit du message a lutilisateur
                sendMessageToUser()
            }else{
                // verification du code de confirmation
                verifyCode(storedVerificationId)
            }
        }
    }


    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)

        // [START_EXCLUDE]
        if (verificationInProgress) {
            sendMessageToUser()
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        return
    }*/

    private fun sendMessageToUser(){

        // callback
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            val progressDialog=indeterminateProgressDialog("Veillez patienter")
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressDialog.dismiss()
                Log.d(TAG, "onVerificationCompleted:$credential")
                verificationInProgress = false
                signInWithPhoneAuthCredential(credential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Log.w(TAG, "onVerificationFailed", e)
                verificationInProgress = false
                if (e is FirebaseAuthInvalidCredentialsException) {
                    progressDialog.dismiss()
                    info2.text="Numéro de téléphone invalide"
                } else if (e is FirebaseTooManyRequestsException) {
                    info2.text="Temps de vérification expiré"
                }
                updateUI(STATE_VERIFY_FAILED)
            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialog.dismiss()
                Log.d(TAG, "onCodeSent:" + verificationId!!)
                storedVerificationId = verificationId
                resendToken = token
                updateUI(STATE_CODE_SENT)

            }
        }

        var phoneNumber=phone_number2.text.toString()
        var phoneCode=phone_code_number.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "$phoneCode$phoneNumber",      // Phone number to verify
            1,               // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this,             // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacksPhoneAuthActivity.kt
        verificationInProgress = true
    }

    private fun verifyCode(verificationId: String?){
        var code=code_verification2.text.toString()
        // [START verify_with_code]
        var credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val progressDialog=indeterminateProgressDialog("Veillez patienter")
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user

                    FirestoreUtil.initCurrentUserIfFirstTime {
                        val intent = Intent(this, MyAccountActivity::class.java)
                        startActivity(intent)
                    }

                } else {
                    progressDialog.dismiss()
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        info2.text="Code invalide"
                    }
                    updateUI(STATE_SIGNIN_FAILED)
                }
            }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(uiState: Int, cred: PhoneAuthCredential) {
        updateUI(uiState, null, cred)
    }

    private fun updateUI(
        uiState: Int,
        user: FirebaseUser? = auth.currentUser,
        cred: PhoneAuthCredential? = null
    ) {
        when (uiState) {
            STATE_INITIALIZED -> {
                code_verification2.visibility= View.INVISIBLE
                phone_number2.visibility=View.VISIBLE
                phone_code_number.visibility=View.VISIBLE
                info2.text="Connexion à Whatsup"
                verify_phone_number2.text="Envoyer le code par SMS"
            }
            STATE_CODE_SENT -> {
                code_verification2.visibility= View.VISIBLE
                phone_number2.visibility=View.INVISIBLE
                phone_code_number.visibility=View.INVISIBLE
                //international_code2.visibility=View.INVISIBLE
                verify_phone_number2.text="Vérifier"
                info2.text="Entrez le Code de confirmation"
            }
            STATE_VERIFY_FAILED -> {

            }
            STATE_VERIFY_SUCCESS -> {

            }
            STATE_SIGNIN_FAILED ->
                info2.text="Code invalide"
            STATE_SIGNIN_SUCCESS -> {
                if(user!=null) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }


    companion object {
        private const val TAG = "PhoneAuthActivity"
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
        private const val STATE_INITIALIZED = 1
        private const val STATE_VERIFY_FAILED = 3
        private const val STATE_VERIFY_SUCCESS = 4
        private const val STATE_CODE_SENT = 2
        private const val STATE_SIGNIN_FAILED = 5
        private const val STATE_SIGNIN_SUCCESS = 6
    }

}

