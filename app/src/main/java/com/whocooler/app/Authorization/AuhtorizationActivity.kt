package com.whocooler.app.Authorization

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.whocooler.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.whocooler.app.Common.Utilities.EXTRA_SHOULD_RELOAD_DEBATE_LIST
import kotlinx.android.synthetic.main.activity_auhtorization.*

class AuhtorizationActivity : AppCompatActivity(), AuthorizationContracts.PresenterViewContract {

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "?!?!GoogleActivity"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var facebookCallbackManager: CallbackManager
    lateinit var interactor: AuthorizationContracts.ViewInteractorContract
    private var shouldReloadList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auhtorization)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        if (intent.extras != null) {
            shouldReloadList = intent.getBooleanExtra(EXTRA_SHOULD_RELOAD_DEBATE_LIST, false)
        }

        setupModule()
        setupViews()

        toggleProgressBar(false)
    }

    private fun setupModule() {
        var activity = this
        var interactor = AuthorizationInteractor()
        var presenter = AuthorizationPresenter()

        activity.interactor = interactor
        interactor.presenter = presenter
        presenter.activity = activity
    }

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google SIGN IN FAILED ", e)
            }
        } else {
            facebookCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        toggleProgressBar(true)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.getIdToken(true)
                        ?.addOnCompleteListener {tokenResult ->
                            val tokenResult = tokenResult.result?.token
                            if (tokenResult != null) {
                                interactor.authorize(tokenResult)
                            }
                        }
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }

                toggleProgressBar(false)
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        toggleProgressBar(true)

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.getIdToken(true)
                        ?.addOnCompleteListener {tokenResult ->
                            val tokenResult = tokenResult.result?.token
                            if (tokenResult != null) {
                                interactor.authorize(tokenResult)
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                toggleProgressBar(false)
            }
    }

    private fun toggleProgressBar(isVisible: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.auth_progress_bar)
        progressBar.isVisible = isVisible
    }

    private fun setupViews() {
        authInfoText.text = getString(R.string.auth_info)
        authInfoText.setTextColor(Color.BLACK)
        auth_google_button.setOnClickListener {
            signInGoogle()
        }

        val authTermsInfo = findViewById<TextView>(R.id.auth_terms_info)
        authTermsInfo.text = "By proceeding you agree with our Terms of Use. To find out how we use your data please see our Privacy Policy."

        makeTextLink(authTermsInfo, "Terms of Use", true, null, action = {
            openUrl("https://api.whocooler.com/terms", this)
        })
        makeTextLink(authTermsInfo, "Privacy Policy", true, null, action = {
            openUrl("https://www.iubenda.com/privacy-policy/66454455", this)
        })

        setupFacebook()
    }

    private fun makeTextLink(textView: TextView, str: String, underlined: Boolean, color: Int?, action: (() -> Unit)? = null) {
        val spannableString = SpannableString(textView.text)
        val textColor = color ?: textView.currentTextColor
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                action?.invoke()
            }
            override fun updateDrawState(drawState: TextPaint) {
                super.updateDrawState(drawState)
                drawState.isUnderlineText = underlined
                drawState.color = textColor
            }
        }
        val index = spannableString.indexOf(str)
        spannableString.setSpan(clickableSpan, index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

    private fun setupFacebook() {
        facebookCallbackManager = CallbackManager.Factory.create()
        val fbLoginBtn = findViewById<LoginButton>(R.id.auth_facebook_btn)
        fbLoginBtn.setPermissions("email", "public_profile")
        fbLoginBtn.registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val token = result?.accessToken
                if (token != null) {
                    handleFacebookAccessToken(token)
                }
            }
            override fun onCancel() {
                Log.d("letsSee", "Facebook onCancel.")

            }
            override fun onError(error: FacebookException) {
                Log.d("letsSee", "Facebook onError.")
            }
        })
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun dismissActivity() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun openUrl(url: String, context: Context) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}
