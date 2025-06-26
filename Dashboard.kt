package com.example.pennywise4

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val username = intent.getStringExtra("username") ?: "User"
        val welcomeText = findViewById<TextView>(R.id.textViewWelcome)

        welcomeText.text = "Hey $username"

        val btnCategories = findViewById<Button>(R.id.btnCategories)
        val btnExpenses = findViewById<Button>(R.id.btnExpenses)
        val btnReport = findViewById<Button>(R.id.btnReports)
        btnReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        btnCategories.setOnClickListener {
            val intent = Intent(this, Add_category::class.java)
            startActivity(intent)
        }

        btnExpenses.setOnClickListener {
            val intent = Intent(this, Add_expense::class.java)
            startActivity(intent)
        }
    }
}