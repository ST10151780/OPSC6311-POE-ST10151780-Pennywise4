package com.example.pennywise4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Add_category : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        val categoryInput = findViewById<EditText>(R.id.editTextText2)
        val saveButton = findViewById<Button>(R.id.button2)

        // Handle save button click
        saveButton.setOnClickListener {
            val categoryName = categoryInput.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    db.categoryDao().insertCategory(Category(name = categoryName))

                    launch(Dispatchers.Main) {
                        Toast.makeText(this@Add_category, "Category saved", Toast.LENGTH_SHORT).show()
                        categoryInput.text.clear()
                    }
                }
            }
        }
    }
}