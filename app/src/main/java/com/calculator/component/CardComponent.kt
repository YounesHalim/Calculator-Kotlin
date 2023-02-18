package com.calculator.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.calculator.R
import com.google.android.material.card.MaterialCardView

class CardComponent {
    /**
     * Helper function that returns a MaterialCardView container with the given context.
     * The returned MaterialCardView has a black background color, rounded corners with a radius of 10dp
     * and margins of 16dp on all sides.
     *
     * @author Younes Halim
     * @param context The context of the current state of the application.
     * @return MaterialCardView
     */

    fun materialCardViewContainer(context: Context): MaterialCardView {
        val cardContainer = MaterialCardView(context)
        cardContainer.id = View.generateViewId()
        cardContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            this.setMargins(16, 16, 16, 16)
        }
        cardContainer.apply {
            this.setCardBackgroundColor(Color.parseColor("#1c1c1c"))
            this.radius = 10f
        }
        return cardContainer
    }

    /**
     * Helper function that returns a new LinearLayout container with default properties set.
     * It sets the ID of the container using View.generateViewId() and applies a vertical orientation to the layout.
     * It also sets the margins to 16 pixels on all sides using setMargins(). The container can be used to add child views in a vertical layout.
     *
     * @author Younes Halim
     * @return LinearLayout
     */
    fun linearLayoutContainer(context: Context): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.id = View.generateViewId()
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 16, 16, 16)
        }
        return linearLayout
    }

    /**
     * Helper function that creates a new TextView with specified layout parameters and sets its text to a formatted string.
     * The key parameter is used to format the date and time, and the value parameter is used to format the expression and result.
     * The formatted strings are generated using callback functions that take the respective parts of the input strings and return formatted strings.
     * The TextView is then returned for use in a layout.
     * @param String
     * @param Any?
     * @param Context
     * @author Younes Halim
     * @return TextView
     */
    @SuppressLint("SetTextI18n")
    fun setHistoryData(key: String, value: Any?, context: Context): TextView {
        val textView = TextView(context)
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(5, 5, 5, 5)
        }
        textView.setTextColor(ContextCompat.getColor(context, R.color.teal_200))
        fun callbackFormattedTime(time: String): () -> String =
            { "Date: " + time.split("T")[0] + "\nTime: " + time.split("T")[1].subSequence(0, 8)  }

        fun callbackFormattedResults(results: String): () -> String =
            { if (results === "Empty" || results.isEmpty()) results else "Expression: " + results.split(";")[0] + "\nResult: " + results.split(";")[1] }
        textView.text =
            "${callbackFormattedTime(key)()}\n${callbackFormattedResults(value.toString())()}"
        return textView
    }
}