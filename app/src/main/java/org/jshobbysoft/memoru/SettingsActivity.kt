/*
 * Copyright (c) 2022. JS HobbySoft
 * All rights reserved.
 */

package org.jshobbysoft.memoru

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val pref1 : EditTextPreference? = findPreference("beginning_date_time")
            pref1?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val newDateString = newValue?.toString()
                    val re = Regex("[01][0-9]/[0-3][0-9]/[12][09][0-9][0-9] [0-2][0-9]:[0-5][0-9]:[0-9][0-9]")
                    val result = newDateString?.matches(re)
                    if (result == true) {
                        true
                    } else {
                        Toast.makeText(requireActivity(), R.string.toastBadDate,Toast.LENGTH_LONG).show()
                        false
                    }
                }
            val pref2 : EditTextPreference? = findPreference("textViewSizeKey")
            pref2?.setOnBindEditTextListener {
                    editText -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }
}