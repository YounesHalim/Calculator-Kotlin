package com.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        GlobalScope.launch(Dispatchers.Main) { typeWriterEffect() }
    }
    /**
     * Uses Kotlin Coroutine to simulate typewriter effect
     * @return Unit
     */
    private suspend fun typeWriterEffect() {
        val caret: TextView = findViewById<TextView?>(R.id.caret).also {
            it.append("|")
        }
        while (true) {
            caret.visibility =
                if (caret.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
            delay(500)
        }
    }
    private fun init() {
        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)
    }
    fun compute(view: View) {}
    fun clear(view: View) {
        tvExpression.text = "".also { tvResult.text = "" }
    }
    fun onDigit(view: View) {
        tvExpression.also { it.append((view as Button).text) }
    }
}