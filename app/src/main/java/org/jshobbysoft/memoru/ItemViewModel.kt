/*
 * Copyright (c) 2022. JS HobbySoft
 * All rights reserved.
 */

package org.jshobbysoft.memoru

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {

    private val _date = MutableLiveData("")
    val userDate: LiveData<String> get() = _date

    private val _time = MutableLiveData("")
    val userTime: LiveData<String> get() = _time

    fun setDate(userDate: String) {
        _date.value = userDate
    }

    fun setTime(userTime: String) {
        _time.value = userTime
    }
}
