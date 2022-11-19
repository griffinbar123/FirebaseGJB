package com.example.firebasegjb

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private var login: Button? = null
    private var signup: Button? = null
    private var logoff: Button? = null
    private var activity_main: RelativeLayout? = null
    private var adapter: FirebaseListAdapter<ChatMessage>? = null
    var currentUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        login = findViewById(R.id.login)
        signup = findViewById(R.id.signup)
        logoff = findViewById(R.id.logoff)
        activity_main = findViewById(R.id.activity_main)
        val mAuth = FirebaseAuth.getInstance()

// Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.currentUser
        if (currentUser == null) {
            // nobody is logged in... we should probably show some
            //  buttons for "Login" and "Register" that will call our cool new Activities
            logoff?.setVisibility(View.INVISIBLE)
            activity_main?.setVisibility(View.INVISIBLE)
            login?.setVisibility(View.VISIBLE)
            signup?.setVisibility(View.VISIBLE)
        } else {
            // we have a user already logged in... cool.  What do we do with that?
            login?.setVisibility(View.INVISIBLE)
            signup?.setVisibility(View.INVISIBLE)
            logoff?.setVisibility(View.VISIBLE)
            activity_main?.setVisibility(View.VISIBLE)
            displayChatMessages()
        }
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val input = findViewById<View>(R.id.input) as EditText

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            if (input.text.toString() == "" || input.text.toString() == " ") {
                Toast.makeText(
                    applicationContext,
                    "Message Needs Content",
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                FirebaseDatabase.getInstance()
                    .reference
                    .push()
                    .setValue(
                        ChatMessage(
                            input.text.toString(),
                            FirebaseAuth.getInstance()
                                .currentUser
                                ?.getEmail()
                        )
                    )
                Log.d(
                    "user", FirebaseAuth.getInstance()
                        .currentUser
                        ?.getEmail()!!
                )
                // Clear the input
                input.setText("")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (currentUser != null) {
            adapter!!.stopListening()
        }
    }

    fun displayChatMessages() {
        val options = FirebaseListOptions.Builder<ChatMessage>()
            .setQuery(FirebaseDatabase.getInstance().reference, ChatMessage::class.java)
            .setLayout(R.layout.message).build()
        val listOfMessages = findViewById<View>(R.id.list_of_messages) as ListView
        adapter = object : FirebaseListAdapter<ChatMessage>(options) {
            override fun populateView(v: View, model: ChatMessage, position: Int) {
                // Get references to the views of message.xml
                val messageText = v.findViewById<View>(R.id.message_text) as TextView
                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                messageText.text = model.messageText
                messageUser.text = model.messageUser

                // Format the date before showing it
                messageTime.text = DateFormat.format(
                    "dd-MM-yyyy (HH:mm:ss)",
                    model.messageTime
                )
            }
        }
        listOfMessages.adapter = adapter
    }

    fun signUp(view: View?) {
        val intent = Intent(
            this@MainActivity,
            RegistrationActivity::class.java
        )
        startActivity(intent)
    }

    fun login(view: View?) {
        val intent = Intent(
            this@MainActivity,
            LoginActivity::class.java
        )
        startActivity(intent)
    }

    fun logOff(view: View?) {
        FirebaseAuth.getInstance().signOut()
        logoff!!.visibility = View.INVISIBLE
        activity_main!!.visibility = View.INVISIBLE
        login!!.visibility = View.VISIBLE
        signup!!.visibility = View.VISIBLE
    }
}