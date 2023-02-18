package com.calculator.controller
import android.content.Context
import android.content.SharedPreferences
import com.calculator.model.HistoricalData

class PreferenceHandlerController(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

    /**
     * Takes HistoricalData object and saves it using shared preferences API
     * @author Younes Halim
     * @param HistoricalData
     */
    fun saveComputedExpressions(history: HistoricalData) {
        with(preferences.edit()) {
            putString(history.dateOfExecution, "${history.mathematicalExpression};${history.result}")
            this.apply()
        }
    }
    /**
     * Returns MutableMap of shared preferences data
     * @author Younes Halim
     * @return MutableMap<String, Any*>
     */
    fun getSharedPreferencesDataAsMutableMap(): MutableMap<String, *> {
        val size = preferences.all.size
        return if(size == 0) mutableMapOf("1" to "Empty") else preferences.all
    }

    /**
     * Clears all data in shared preferences
     * @author Younes Halim
     */
    fun clearHistory() {
        val size = preferences.all.size
        if(size == 0) return
        preferences.edit().apply {
            this.clear().apply()
        }
    }
}