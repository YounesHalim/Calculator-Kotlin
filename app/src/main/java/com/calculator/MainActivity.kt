package com.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class MainActivity : AppCompatActivity() {
    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private lateinit var operand: String
    private val operators: Regex = "[+\\-*/%√÷×]".toRegex()
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        GlobalScope.launch(Dispatchers.Main) { typeWriterEffect() }
    }

    /**
     * Uses Kotlin Coroutine to simulate typewriter effect
     * @author Younes Halim
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

    fun onOperator(view: View) {
        tvExpression.also {
            if (it.text.isEmpty() || onOperatorAdded(it)) {
                return@also
            }
            it.append((view as Button).text).apply { operand = this.toString() }
        }
    }

    /**
     * Returns a boolean if the expression ends with an operator
     * @author Younes Halim
     * @return Boolean
     */
    private fun onOperatorAdded(expression: TextView): Boolean {
        return expression.text.endsWith("%")
                || expression.text.endsWith("+")
                || expression.text.endsWith("-")
                || expression.text.endsWith("÷")
                || expression.text.endsWith("×")
                || expression.text.endsWith("√")
                || expression.text.endsWith(".")
    }
    fun compute(view: View) {
        tvExpression.apply {
            tvExpression.text = tvResult.text
        }
    }
    private fun results () {
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scope: Scriptable = context.initStandardObjects()
        val result: Any = context.evaluateString(scope, tvExpression.text.toString(), "Javascript", 1, null)
        Context.exit()
        if (result.toString().isNotEmpty()) {
            tvResult.text = result.toString()
            return
        }
        tvResult.text = ""
    }
    fun clear(view: View) {
        tvExpression.text = "".also { tvResult.text = "" }
    }
    fun onDigit(view: View) {
        tvExpression.also {
            it.append((view as Button).text)
        }
        if (tvExpression.text.contains(operators)) {
            results()
        } else return
    }

    /**
     * Clears the calculator TV number by number
     * @author Younes Halim
     */
    fun delete(view: View) {
        tvExpression.apply {
            if (this.text.isEmpty()) return@apply
            val expressionSize = this.text.trim().length
            var counter = 0
            counter++
            if (expressionSize >= counter) {
                this.text = this.text.substring(0, expressionSize - counter)
                if (onOperatorAdded(this)) return@apply
                results()
            }
        }
    }

}