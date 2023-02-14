package com.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
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
     * The loop runs indefinitely and alternates the visibility of the caret every 500 milliseconds using delay function.
     * This creates a blinking effect that simulates the appearance of a typing cursor.
     * @author Younes Halim
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
     */
    private fun init() {
        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)
    }

    /**
     * Handles the logic for when a mathematical operator button is clicked in the calculator.
     * The function first checks if the expression text view is empty or if an operator has already been added to the expression and if the operator is not equal to plus/minus sign.
     * If either of these conditions is met, the function returns without making any changes.
     * If the expression text view is not empty and no operator has been added, the function appends the operator text to the expression text view.
     * @return Unit
     * @param View
     * @author Younes Halim
     */
    fun onOperator(view: View) {
        tvExpression.also {
            if (it.text.isEmpty() || onOperatorAdded(it) && (view as Button).id != R.id.plusMinusSymbol) return@also
            if (onPlusMinusOperator(view, it)) return@also
            it.append((view as Button).text)
        }
        scrollToEnd()
    }

    /**
     * Helper function for handling the "+/-" operator in a calculator application.
     * @return Boolean
     * @param View
     * @param TextView
     * @author Younes Halim
     */
    @SuppressLint("SetTextI18n")
    private fun onPlusMinusOperator(view: View, it: TextView): Boolean {
        if ((view as Button).id == R.id.plusMinusSymbol) {
            return if (!it.text.startsWith("-")) {
                it.text = "-${it.text}"
                tempResult = it.text.toString()
                true
            } else {
                it.text = it.text.subSequence(1, it.text.length)
                tempResult = it.text.toString()
                true
            }
        }
        return false
    }

    /**
     * Returns a boolean if the expression ends with an operator
     * @author Younes Halim
     * @param TextView
     * @return Boolean
     */
    private fun onOperatorAdded(expression: TextView): Boolean {
        charArrayOf('%', '+', '-', '÷', '×', '√', '.')
            .forEach { c: Char -> if (expression.text.endsWith(c)) return true }
        return false
    }

    /**
     * Updates the text of the "tvExpression" TextView to the text of the "tvResult" TextView and then sets the text of the "tvResult" TextView to an empty string.
     * @param View
     * @author Younes Halim
     */
    fun computedResult(view: View) {
        tvExpression.apply {
            if (tvResult.text.split(".")[1] == "0")
                tvExpression.text = tvResult.text.split(".")[0]
            else {
                tvExpression.text = tvResult.text
            }
            tvResult.text = ""
        }
    }
    /**
     * Evaluates the expression stored in "tempResult" using JavaScript engine and returns the result as a string.
     * @return String?
     * @author Younes HALIM
     */
    private fun results(): String? {
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scope: Scriptable = context.initStandardObjects()
        val result: Any = context.evaluateString(scope, tempResult, "Javascript", 1, null)
        Context.exit()
        if (result is Undefined) {
            tvResult.text = ""
            return null
        }
        tvResult.text = scientificNotation(result = result.toString())
        Log.i("Expression", scientificNotation(result = result.toString()))
        return result.toString()
    }

    /**
     * Clears the calculator screen.
     * @param View
     * @author Younes Halim
     */
    fun clear(view: View) {
        tvExpression.text = "".also { tvResult.text = "" }
    }

    /**
     * onDigit function updates the expression TextView by appending the text of the button that was clicked.
     * Sets the tempResult string to the updated expression.
     *
     * @author Younes Halim
     * @param View
     */
    fun onDigit(view: View) {
        tvExpression.also {
            val temp = it.append((view as Button).text)
            tempResult = temp.toString()
        }
        if (tvExpression.text.contains(operators)) {
            tempResult = parsedExpression(tvExpression)
            results()
        }
        scrollToEnd()
    }

    /**
     * Delete single character from tvExpression object
     * The modified expression is then passed to the "parsedExpression" method to convert mathematical symbols and is stored in the "tempResult" variable.
     * The "onOperatorAdded" method is called on the updated expression.
     * If it returns true, the function returns. Finally, the "results" method is called to update the result.
     * @return Unit
     * @param View
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
     * Helper function: format expressions for calculation
     * @param TextView
     * @return String
     * @author Younes Halim
     */
    private fun parsedExpression(expression: TextView): String {
        return expression.text.toString().replace(Regex("÷"), "/").replace(Regex("×"), "*")
    }

    /**
     * function formats a string result in scientific notation if the decimal length is greater than 7.
     * It splits the string into a list of parts separated by "." and checks the length of the decimal part.
     * If the decimal length is greater than or equal to 7, it shortens it to 4 decimal places and appends "e10^(decimalLength-3)" to represent the scientific notation.
     * The final result is joined back into a string and returned.
     * @param String
     * @param Int
     * @author Younes Halim
     * @return String
     */
    private fun scientificNotation(result: String, decimal: Int = 7): String {
        if (!result.contains(".") || result.split(".")[1].length < decimal) return result
        val expression: MutableList<String> = result.split(".") as MutableList<String>
        val decimalLength: Int = expression[1].length
        if (expression[1].length < decimal) return result
        return expression.apply {
            set(1, "${this[1].substring(0, 4)}e10^${decimalLength - 4}")
        }.joinToString(".")
    }

    /**
     * Used to scroll a horizontal scroll view to the end.
     * It finds the HorizontalScrollView by using the findViewById method and passing the id of the view.
     * Then it calls the post method to schedule a task to be executed on the UI thread.
     * Within the task, it calls the scrollTo method on the HorizontalScrollView and passes the width of the TextView and 0 as arguments to scroll to the end.
     * @author Younes Halim
     */
    private fun scrollToEnd() {
        findViewById<HorizontalScrollView>(R.id.horizontalScroll)
            .apply {
                this.post {
                    this.scrollTo(tvExpression.width, 0)
                }
            }
    }
}
