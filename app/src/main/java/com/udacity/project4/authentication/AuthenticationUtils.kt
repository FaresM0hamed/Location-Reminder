package com.udacity.project4.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI

/**
 * This method to sign in app with google provider $ Email provider
 */
fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {
    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    // Create and launch sign-in intent
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()
    signInLauncher.launch(signInIntent)
}

/**
 * this method to make sign out from email
 */
fun signOut(activity: Activity, method: (() -> Unit)?) {
    val signOut = AuthUI.getInstance().signOut(activity)
    if (method == null) return
    signOut.addOnSuccessListener {
        method()
    }
}

private var sharedPreference: SharedPreferences? = null

/**
 * This method used to save boolean data to sharedPreference
 */
fun saveData(context: Context, key: String, value: Boolean) {
    if (sharedPreference == null) {
        sharedPreference =
            context.getSharedPreferences("Location Reminder", Context.MODE_PRIVATE)
    }
    val editor = sharedPreference?.edit()!!
    editor.putBoolean(key, value)
    editor.apply()
}
/**
 * This method used to load boolean data to sharedPreference
 */
fun loadData(
    context: Context,
    key: String,
    defaultVal: Boolean
): Boolean? {
    if (sharedPreference == null) {
        sharedPreference =
            context.getSharedPreferences("Location Reminder", Context.MODE_PRIVATE)
    }
    return sharedPreference?.getBoolean(key, defaultVal)
}

