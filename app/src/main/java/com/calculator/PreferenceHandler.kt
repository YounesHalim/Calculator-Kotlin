package com.calculator

import android.content.Context
import android.content.SharedPreferences


class PreferenceHandler (context : Context){
    private val preferences: SharedPreferences = context.getSharedPreferences("my_pref",Context.MODE_PRIVATE)

    // Will take a list of history object and save it in shared preferences
    /**
     * @author Younes Halim
     * @param List<History>
     */
    fun saveComputedData(data: List<History>) {
        with(preferences.edit()) {

        }
    }

    // Will return a list of history object
    /**
     * @author Younes Halim
     * @return List<History>
     */
    fun readData (): List<History> {
        with(preferences.all) {
            return this.map {   }.toList()
        }
    }
}