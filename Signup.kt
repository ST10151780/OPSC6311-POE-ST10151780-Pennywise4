package com.example.pennywise4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signupBtn = findViewById<Button>(R.id.Signupbtn)

        val firstNameEdit = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEdit = findViewById<EditText>(R.id.editTextLastName)
        val emailEdit = findViewById<EditText>(R.id.editTextEmail2)
        val passwordEdit = findViewById<EditText>(R.id.editTextPassword2)
        val confirmPasswordEdit = findViewById<EditText>(R.id.editTextConfirmPassword)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        signupBtn.setOnClickListener {
            val firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
            val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
            val username = findViewById<EditText>(R.id.editTextText).text.toString()
            val email = findViewById<EditText>(R.id.editTextEmail2).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword2).text.toString()
            val confirmPassword =
                findViewById<EditText>(R.id.editTextConfirmPassword).text.toString()

            if (firstName.isBlank() || lastName.isBlank() || username.isBlank() ||
                email.isBlank() || password.isBlank() || confirmPassword.isBlank()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            CoroutineScope(Dispatchers.IO).launch {
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    runOnUiThread {
                        Toast.makeText(this@Signup, "Username already exists", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    userDao.insert(User(username, password, firstName, lastName, email))
                    runOnUiThread {
                        Toast.makeText(this@Signup, "Account created! Logging you in...", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Signup, Dashboard::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}
