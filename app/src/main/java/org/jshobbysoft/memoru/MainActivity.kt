/*
 * Copyright (c) 2022. JS HobbySoft
 * All rights reserved.
 */

package org.jshobbysoft.memoru

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var buttonDate: Button
    private lateinit var buttonTime: Button
    private var imageUri: Uri? = null

    // Using the viewModels() Kotlin property delegate from the activity-ktx
    // artifact to retrieve the ViewModel in the activity scope
    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val tV: TextView = findViewById(R.id.textView)
        val tV2: TextView = findViewById(R.id.textView2)

        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)

        if(sharedPreferences.getString("userDateKey",null).isNullOrEmpty()) {
            sharedPreferences.edit().putString("userDateKey", "01/25/2022").commit()
        }

        if(sharedPreferences.getString("userTimeKey",null).isNullOrEmpty()) {
            sharedPreferences.edit().putString("userTimeKey", "10:56:00").commit()
        }

        var userDateAndTimeFromPickers = sharedPreferences.getString(
            "userDateKey",
            "02/02/2002"
        ) + " " + sharedPreferences.getString("userTimeKey", "02:02:00")

        imageView = findViewById(R.id.imageView3)

        button = findViewById(R.id.buttonLoadPicture)
        button.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(gallery)
        }

        buttonDate = findViewById(R.id.buttonPickDate)
        buttonDate.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }
        viewModel.userDate.observe(this) { item ->
            // Perform an action with the latest item data
            if (item.isNotEmpty()) {
                sharedPreferences.edit().putString("userDateKey", item.toString()).commit()
                userDateAndTimeFromPickers = sharedPreferences.getString(
                    "userDateKey",
                    "02/02/2002"
                ) + " " + sharedPreferences.getString("userTimeKey", "02:02:00")
                updateDateString(userDateAndTimeFromPickers,tV2)
            }
        }

        buttonTime = findViewById(R.id.buttonPickTime)
        buttonTime.setOnClickListener {
            val newFragment = TimePickerFragment()
            newFragment.show(supportFragmentManager, "timePicker")
        }
        viewModel.userTime.observe(this) { item ->
            // Perform an action with the latest item data
            if (item.isNotEmpty()) {
                sharedPreferences.edit().putString("userTimeKey", item.toString()).commit()
                userDateAndTimeFromPickers = sharedPreferences.getString(
                    "userDateKey",
                    "02/02/2002"
                ) + " " + sharedPreferences.getString("userTimeKey", "02:02:00")
                updateDateString(userDateAndTimeFromPickers,tV2)
            }
        }

//      update string with "since" or "until" date
        updateDateString(userDateAndTimeFromPickers,tV2)

//      change text view size based on preference settings
        val params = tV.layoutParams as ConstraintLayout.LayoutParams
        params.height = sharedPreferences.getString("textViewSizeKey", "1000")!!.toInt()

//      update text position based on preference settings
        when (val positionOfText = sharedPreferences.getString("positionKey", "")) {
            "top" -> {
                params.topToTop = ConstraintLayout.LayoutParams.MATCH_PARENT
                params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            }
            "middle" -> {
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
            "bottom" -> {
                params.topToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToTop = tV2.id
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            }
            else -> println(positionOfText)
        }
        tV.layoutParams = params

//      use default image if no shared preference has been saved
        val backgroundUriString = sharedPreferences.getString("background_uri_key", "")
        if (backgroundUriString == "") {
            imageView.setImageResource(R.drawable.ic_settings_test_foreground)
        } else {
            val backgroundUri = Uri.parse(backgroundUriString)
            imageView.setImageURI(backgroundUri)
        }

        fun colorChangeTV(newColor: Int): Boolean {
            for (item: TextView? in listOf(tV, tV2)) {
                item?.setTextColor(ContextCompat.getColor(applicationContext, newColor))
            }
            return true
        }

//      update text colors based on preference settings
        when (val colorOfText = sharedPreferences.getString("textColorKey", "")) {
            "red" -> colorChangeTV(R.color.red)
            "green" -> colorChangeTV(R.color.green)
            "blue" -> colorChangeTV(R.color.blue)
            "black" -> colorChangeTV(R.color.black)
            "white" -> colorChangeTV(R.color.white)
            else -> println(colorOfText)
        }

//      update image scaling based on preference settings
        when (val scaleOfImage = sharedPreferences.getString("scaleTypeKey", "")) {
            "center" -> imageView.scaleType = ImageView.ScaleType.CENTER
            "center_crop" -> imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            "center_inside" -> imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            "fit_center" -> imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            "fit_start" -> imageView.scaleType = ImageView.ScaleType.FIT_START
            "fit_end" -> imageView.scaleType = ImageView.ScaleType.FIT_END
            else -> println(scaleOfImage)
        }

//      Loop the date/time calculator to get the time interval
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                tV.text = calculateDate(userDateAndTimeFromPickers)
                mainHandler.postDelayed(this, 1000)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                val contentResolver = applicationContext.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                imageUri?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }
                imageView.setImageURI(imageUri)
                val sharedPref =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
                with(sharedPref.edit()) {
                    putString("background_uri_key", imageUri.toString())
                    apply()
                }
            }
        }

    private fun calculateDate(endDate: String?): String {
        val ldtFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        val then = LocalDateTime.parse(endDate, ldtFormat)
        val now = LocalDateTime.now()
        val remaining = Duration.between(then, now)

        return String.format(
            "%02d days\n%02d hours\n%02d min\n%02d sec",
            remaining.seconds / 86400,
            abs((remaining.seconds % 86400) / 3600),
            abs((remaining.seconds % 3600) / 60),
            abs(remaining.seconds % 60)
        )
    }

    private fun updateDateString(uDT: String, uTV: TextView) {
        //      update "since date" or "until date" text view
        if (calculateDate(uDT).take(2).toInt() < 0) {
            uTV.text = getString(R.string.until_date, uDT)
        } else {
            uTV.text = getString(R.string.since_date, uDT)
        }
    }
}