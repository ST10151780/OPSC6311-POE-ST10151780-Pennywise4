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

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById<Button>(R.id.loginbtn)
        val createBtn = findViewById<Button>(R.id.createbtn)
        val usernameEdit = findViewById<EditText>(R.id.editTextUsername)
        val passwordEdit = findViewById<EditText>(R.id.editTextPassword)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        loginBtn.setOnClickListener {
            val username = usernameEdit.text.toString()
            val password = passwordEdit.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val user = userDao.getUserByUsernameAndPassword(username, password)

                runOnUiThread {
                    if (user != null) {
                        Toast.makeText(this@Login, "Login successful!", Toast.LENGTH_SHORT).show()


                        val intent = Intent(this@Login, Dashboard::class.java)
                        intent.putExtra("username", user.username)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        createBtn.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
    }
}
