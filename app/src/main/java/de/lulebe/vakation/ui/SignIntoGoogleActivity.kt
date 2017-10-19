package de.lulebe.vakation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import de.lulebe.vakation.R

class SignIntoGoogleActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 12643

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_into_google)
        startSigninFlow()
    }

    private fun startSigninFlow () {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val gClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        findViewById<View>(R.id.btn_signin).setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(gClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult (result: GoogleSignInResult) {
        Log.d("Google Signin", result.isSuccess.toString())
        val spe = getSharedPreferences("internal", Context.MODE_PRIVATE).edit()
        spe.putBoolean("googleSignedIn", true)
        spe.commit()
        finish()
    }

    override fun onConnectionFailed (p0: ConnectionResult) {

    }

}
