    package com.example.firebasegjb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

    public class MainActivity extends AppCompatActivity {

        private Button login;
        private Button signup;
        private Button logoff;
        private RelativeLayout activity_main;
        private FirebaseListAdapter<ChatMessage> adapter;
        FirebaseUser currentUser;


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            login = findViewById(R.id.login);
            signup = findViewById(R.id.signup);
            logoff = findViewById(R.id.logoff);
            activity_main = findViewById(R.id.activity_main);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

// Check if user is signed in (non-null) and update UI accordingly.
            currentUser = mAuth.getCurrentUser();

            if (currentUser == null)
            {
                // nobody is logged in... we should probably show some
                //  buttons for "Login" and "Register" that will call our cool new Activities
                logoff.setVisibility(View.INVISIBLE);
                activity_main.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
                signup.setVisibility(View.VISIBLE);

            }
            else
            {
                // we have a user already logged in... cool.  What do we do with that?
                login.setVisibility(View.INVISIBLE);
                signup.setVisibility(View.INVISIBLE);
                logoff.setVisibility(View.VISIBLE);
                activity_main.setVisibility(View.VISIBLE);
                displayChatMessages();
            }

            FloatingActionButton fab =
                    (FloatingActionButton)findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText input = (EditText) findViewById(R.id.input);

                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
                    if (input.getText().toString().equals("") || input.getText().toString().equals(" ")) {
                        Toast.makeText(
                                        getApplicationContext(),
                                        "Message Needs Content",
                                        Toast.LENGTH_LONG)
                                .show();
                    } else {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .push()
                                .setValue(new ChatMessage(input.getText().toString(),
                                        FirebaseAuth.getInstance()
                                                .getCurrentUser()
                                                .getEmail())
                                );
                        Log.d("user", FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getEmail());
                        // Clear the input
                        input.setText("");
                    }
                }
            });
        }

        @Override
        protected void onStart() {
            super.onStart();
            if (currentUser != null) {
                adapter.startListening();
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (currentUser != null) {
                adapter.stopListening();
            }
        }

        public void displayChatMessages(){
            FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                    .setQuery(FirebaseDatabase.getInstance().getReference(), ChatMessage.class).setLayout(R.layout.message).build();

            ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

            adapter = new FirebaseListAdapter<ChatMessage>(options) {
                @Override
                protected void populateView(@NonNull View v, @NonNull ChatMessage model, int position) {
                    // Get references to the views of message.xml
                    TextView messageText = (TextView)v.findViewById(R.id.message_text);
                    TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                    TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                    // Set their text
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                }
            };

            listOfMessages.setAdapter(adapter);
        }

        public void signUp(View view){
            Intent intent
                    = new Intent(MainActivity.this,
                    RegistrationActivity.class);
            startActivity(intent);
        }

        public void login(View view){
            Intent intent
                    = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        }

        public void logOff(View view){

            FirebaseAuth.getInstance().signOut();
            logoff.setVisibility(View.INVISIBLE);
            activity_main.setVisibility(View.INVISIBLE);
            login.setVisibility(View.VISIBLE);
            signup.setVisibility(View.VISIBLE);

        }

    }