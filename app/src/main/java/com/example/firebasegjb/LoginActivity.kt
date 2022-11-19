package com.example.firebasegjb

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import com.example.firebasegjb.R
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import android.content.Intent
import android.view.View
import android.widget.Button
import com.example.firebasegjb.MainActivity

class LoginActivity : AppCompatActivity() {
    private var emailTextView: EditText? = null
    private var passwordTextView: EditText? = null
    private var Btn: Button? = null
    private var progressbar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email)
        passwordTextView = findViewById(R.id.password)
        Btn = findViewById(R.id.login)
        progressbar = findViewById(R.id.progressBar)

        // Set on Click Listener on Sign-in button
    }

    public fun loginUserAccount(view:View) {

        // show the visibility of progress bar to show loading
        progressbar!!.visibility = View.VISIBLE

        // Take the value of two edit texts in Strings
        val email: String
        val password: String
        email = emailTextView!!.text.toString()
        password = passwordTextView!!.text.toString()

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                applicationContext,
                "Please enter email!!",
                Toast.LENGTH_LONG
            )
                .show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                applicationContext,
                "Please enter password!!",
                Toast.LENGTH_LONG
            )
                .show()
            return
        }

        // signin existing user
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Login successful!!",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    // hide the progress bar
                    progressbar!!.visibility = View.GONE

                    // if sign-in is successful
                    // intent to home activity
                    val intent = Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                    startActivity(intent)
                } else {

                    // sign-in failed
                    Toast.makeText(
                        applicationContext,
                        "Login failed!!",
                        Toast.LENGTH_LONG
                    )
                        .show()

                    // hide the progress bar
                    progressbar!!.visibility = View.GONE
                }
            }
    }
}