package com.udacity.project4.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private var _binding: ActivityAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil
            .setContentView(this, R.layout.activity_authentication)
        binding.lifecycleOwner = this

        //check if user logged in before or not
        //if logged in go to RemindersActivity
        val isLoggedIn = loadData(this, "isLoggedIn", false)
        if (isLoggedIn == true) {
            startActivity(Intent(this, RemindersActivity::class.java))
            finish()
        }
        //Init FirebaseUI sign in launcher
        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { res ->
            this.onSignInResult(res)
        }

        binding.loginBtn.setOnClickListener {
            signIn(signInLauncher)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            saveData(this, "isLoggedIn", true)
            startActivity(Intent(this, RemindersActivity::class.java))
            finish()
        }
    }
}