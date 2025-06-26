package com.example.pennywise4

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var badgeImage: ImageView
    private lateinit var levelText: TextView
    private lateinit var xpBar: ProgressBar

    private var xp = 0
    private var level = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        pieChart = findViewById(R.id.categoryPieChart)
        badgeImage = findViewById(R.id.imageViewBadge)
        levelText = findViewById(R.id.textViewLevel)
        xpBar = findViewById(R.id.xpProgress)

        val db = AppDatabase.getDatabase(this)
        val expenseDao = db.expenseDao()

        CoroutineScope(Dispatchers.IO).launch {
            val expenses = expenseDao.getAllExpenses()

            val categoryTotals = mutableMapOf<String, Float>()
            var totalXP = 0
            var stayedWithinBudget = true

            expenses.forEach { expense ->
                val amount = expense.amount.toFloat()
                val category = expense.category
                categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount

                // XP: 10 per expense
                totalXP += 10

                // Check goal compliance
                val min = expense.minGoal ?: 0.0
                val max = expense.maxGoal ?: Double.MAX_VALUE
                if (amount < min || amount > max) {
                    stayedWithinBudget = false
                }
            }

            val pieEntries = categoryTotals.map { (cat, amt) ->
                PieEntry(amt, cat)
            }

            level += totalXP / 100
            xp = totalXP % 100

            launch(Dispatchers.Main) {
                updatePieChart(pieEntries)
                updateXPUI(level, xp)

                if (stayedWithinBudget && expenses.isNotEmpty()) {
                    badgeImage.visibility = View.VISIBLE
                    Toast.makeText(this@ReportActivity, "🎉 You stayed within your budget goals!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updatePieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Spending per Category")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun updateXPUI(level: Int, xp: Int) {
        levelText.text = "Level: $level (XP: $xp/100)"
        xpBar.progress = xp
    }
}
