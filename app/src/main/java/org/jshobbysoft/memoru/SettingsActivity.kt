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
        supportActionBar?.show()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // Require a numeric input for the text size
            val pref2 : EditTextPreference? = findPreference("textViewSizeKey")
            pref2?.setOnBindEditTextListener {
                    editText -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            // Limit the length of the description
            val pref3 : EditTextPreference? = findPreference("textDescKey")
            pref3?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    val newDescString = newValue?.toString()
                    if (newDescString!!.length <= 30) {
                        true
                    } else {
                        Toast.makeText(requireActivity(),R.string.toastBadDesc, Toast.LENGTH_LONG).show()
                        false
                    }
                }
        }
    }
}