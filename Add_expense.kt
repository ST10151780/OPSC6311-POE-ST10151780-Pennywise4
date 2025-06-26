package com.example.pennywise4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.*

class Add_expense : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null

    companion object {
        const val REQUEST_CODE_IMAGE_PICK = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val editTextStartTime = findViewById<EditText>(R.id.editTextStartTime)
        val editTextEndTime = findViewById<EditText>(R.id.editTextEndTime)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val editTextAmount = findViewById<EditText>(R.id.editTextAmount)
        val minEditText = findViewById<EditText>(R.id.editTextMinGoal)
        val maxEditText = findViewById<EditText>(R.id.editTextMaxGoal)

        val db = AppDatabase.getDatabase(this)
        val categoryDao = db.categoryDao()

        CoroutineScope(Dispatchers.IO).launch {
            val categories = categoryDao.getAllCategories()  // Get from DB
            val categoryNames = categories.map { it.name }   // Extract names for display

            runOnUiThread {
                val adapter = ArrayAdapter(
                    this@Add_expense,
                    android.R.layout.simple_spinner_item,
                    categoryNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }
        }

        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
                val formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                editTextDate.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        //  Start Time Picker
        editTextStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, h, m ->
                val formattedTime = String.format("%02d:%02d", h, m)
                editTextStartTime.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        //  End Time Picker
        editTextEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, h, m ->
                val formattedTime = String.format("%02d:%02d", h, m)
                editTextEndTime.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }



        val buttonSelectPhoto = findViewById<Button>(R.id.buttonSelectPhoto)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        imageView = findViewById(R.id.imageViewPreview)

        // Photo selection
        buttonSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        // Save expense
        buttonSave.setOnClickListener {
            val date = editTextDate.text.toString()
            val startTime = editTextStartTime.text.toString()
            val endTime = editTextEndTime.text.toString()
            val description = editTextDescription.text.toString()
            val category = spinnerCategory.selectedItem?.toString() ?: ""
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val minGoal = minEditText.text.toString().toDoubleOrNull()
            val maxGoal = maxEditText.text.toString().toDoubleOrNull()

            if (date.isBlank() || startTime.isBlank() || endTime.isBlank() ||
                description.isBlank() || category.isBlank() || amount == null) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expense = Expense(
                date = date,
                startTime = startTime,
                endTime = endTime,
                description = description,
                category = category,
                amount = amount,
                photoUri = selectedImageUri?.toString(),
                minGoal = minGoal,
                maxGoal = maxGoal
            )


            // Save to RoomDB
            val db = AppDatabase.getDatabase(this)
            CoroutineScope(Dispatchers.IO).launch {
                db.expenseDao().insertExpense(expense)
                runOnUiThread {
                    Toast.makeText(this@Add_expense, "Expense saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
        }
    }
}