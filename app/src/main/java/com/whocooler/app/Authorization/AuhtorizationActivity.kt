package com.whocooler.app.Authorization

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.whocooler.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auhtorization.*

class AuhtorizationActivity : AppCompatActivity(), AuthorizationContracts.PresenterViewContract {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "?!?!GoogleActivity"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var interactor: AuthorizationContracts.ViewInteractorContract

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auhtorization)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        setupModule()
        setupViews()
    }

    private fun setupModule() {
        var activity = this
        var interactor = AuthorizationInteractor()
        var presenter = AuthorizationPresenter()

        activity.interactor = interactor
        interactor.output = presenter
        presenter.output = activity
    }

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Google SIGN IN SUCCEEDED:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google SIGN IN FAILED ", e)
            }
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    task.result?.user?.getIdToken(true)
                        ?.addOnCompleteListener {tokenResult ->
                            val tokenResult = tokenResult.result?.token
                            if (tokenResult != null) {
                                interactor.authorize(tokenResult)
                            }

                            Log.d(TAG, "Google SIGN IN FIREBASE:success, idTOKEN: ${tokenResult}")
                        }
                    // Sign in success, update UI with the signed-in user's information
                    val token = user?.getIdToken(true)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "Google SIGN IN FIREBASE:failure", task.exception)
//                    Snackbar.make(, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }

                // [START_EXCLUDE]
//                hideProgressBar()
                // [END_EXCLUDE]
            }
    }

    private fun setupViews() {
        authInfoText.text = getString(R.string.auth_info)
        authInfoText.setTextColor(Color.BLACK)
        auth_google_button.setOnClickListener {
            signInGoogle()
        }
        auth_google_button.setBackgroundResource(R.drawable.custom_background_border)
        auth_google_button.setTextColor(Color.GRAY)
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun dismissActivity() {
        super.onBackPressed()
    }

}
