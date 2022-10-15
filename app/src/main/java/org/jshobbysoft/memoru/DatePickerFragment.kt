/*
 * Copyright (c) 2022. JS HobbySoft
 * All rights reserved.
 */

package org.jshobbysoft.memoru

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import java.util.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    // Using the activityViewModels() Kotlin property delegate from the
    // fragment-ktx artifact to retrieve the ViewModel in the activity scope
    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val gameDate = (month+1).toString().padStart(2,'0') + "/" +
                day.toString().padStart(2,'0') + "/" + year.toString()
        viewModel.setDate(gameDate)
    }
}
