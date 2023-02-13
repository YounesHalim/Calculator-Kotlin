package com.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.Undefined
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private lateinit var tempResult: String
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
     * The loop runs indefinitely and alternates the visibility of the caret every 500 milliseconds using delay function. This creates a blinking effect that simulates the appearance of a typing cursor.
     * @author Younes Halim
     * @return Unit
     */
    private suspend fun typeWriterEffect() {
        val caret: TextView = findViewById<TextView?>(R.id.caret).also { it.append("|") }
        while (true) {
            caret.visibility =
                if (caret.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
            delay(500)
        }
    }

    /**
     * initializes the TextViews 'tvExpression' and 'tvResult' by finding their respective IDs in the layout file using the findViewById method.
     * @author Younes Halim
     * @return Unit
     */
    private fun init() {
        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)
    }

    /**
     * Handles the logic for when a mathematical operator button is clicked in the calculator.
     * The function first checks if the expression text view is empty or if an operator has already been added to the expression.
     * If either of these conditions is met, the function returns without making any changes.
     * If the expression text view is not empty and no operator has been added, the function appends the operator text to the expression text view.
     * @return Unit
     * @author Younes Halim
     */
    fun onOperator(view: View) {
        tvExpression.also {
            if (it.text.isEmpty() || onOperatorAdded(it)) return@also
            it.append((view as Button).text)
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
        tvExpression.apply { tvExpression.text = tvResult.text }.also { tvResult.text = "" }
    }

    /**
     * @author Younes HALIM
     */
    private fun results() {
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scope: Scriptable = context.initStandardObjects()
        val result: Any = context.evaluateString(scope, tempResult, "Javascript", 1, null)
        Context.exit()
        if (result is Undefined) {
            tvResult.text = ""
            return
        }
        tvResult.text = result.toString()
        Log.i("Expression", scientificNotation(result = result.toString()))
    }

    fun clear(view: View) {
        tvExpression.text = "".also { tvResult.text = "" }
    }

    /**
     * @author Younes Halim
     */
    fun onDigit(view: View) {
        tvExpression.also {
            val temp = it.append((view as Button).text)
            tempResult = temp.toString()
        }
        if (tvExpression.text.contains(operators)) {
            tempResult = parsedExpression(tvExpression)
            results()
        } else return
    }

    /**
     * Clears the calculator TV number by number
     * Refreshes the results screen
     * @return Unit
     * @author Younes Halim
     */
    fun delete(view: View) {
        var counter = 0
        tvExpression.apply {
            if (this.text.isEmpty()) return@apply
            val expressionSize = this.text.trim().length
            if (expressionSize >= 0) {
                counter++
                this.text = this.text.substring(0, expressionSize - counter)
                tempResult = parsedExpression(this)
                if (onOperatorAdded(this)) return@apply
                results()
            }
        }
        counter = 0
    }

    /**
     * Helper function:
     * @param {TextView}
     * @return String
     * @author Younes Halim
     */
    private fun parsedExpression(expression: TextView): String {
        return expression.text.toString().replace(Regex("÷"), "/").replace(Regex("×"), "*")
    }

    /**
     * function formats a string result in scientific notation if the decimal length is greater than or equal to 3.
     * It splits the string into a list of parts separated by "." and checks the length of the decimal part.
     * If the decimal length is greater than or equal to 3, it shortens it to 3 decimal places and appends "e10^(decimalLength-3)" to represent the scientific notation.
     * The final result is joined back into a string and returned.
     * @param {String}
     * @author Younes Halim
     * @return String
     */
    private fun scientificNotation(result: String): String {
        if (!result.contains(".") || result.length >= 9) return result
        val expression: MutableList<String> = result.split(".") as MutableList<String>
        val decimalLength: Int = expression[1].length
        if (expression[1].length < 3) return result
        return expression.apply {
            set(1, "${this[1].substring(0, 3)}e10^${decimalLength - 3}")
        }.joinToString(".")
    }
}
