/*
 * Copyright (c) 2022. JS HobbySoft
 * All rights reserved.
 */

package org.jshobbysoft.memoru

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import org.jshobbysoft.memoru.databinding.ActivityMainBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    /*
        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            return when (item.itemId) {
                R.id.action_settings -> {
                    val settingsIntent = Intent(this, MySettingsActivity::class.java)
                    startActivity(settingsIntent)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

    */
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

    fun calculateDate(endDate: String?): String {
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

    fun calculateDateOneLine(endDate: String?): String {
        val ldtFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        val then = LocalDateTime.parse(endDate, ldtFormat)
        val now = LocalDateTime.now()
        val remaining = Duration.between(then, now)

        return String.format(
            "%02d days %02d hours %02d min %02d sec",
            remaining.seconds / 86400,
            abs((remaining.seconds % 86400) / 3600),
            abs((remaining.seconds % 3600) / 60),
            abs(remaining.seconds % 60)
        )
    }

    fun updateDateString(uDT: String, uTV: TextView) {
        //      update "since date" or "until date" text view
        if (calculateDate(uDT).take(2).toInt() < 0) {
            uTV.text = getString(R.string.until_date, uDT)
        } else {
            uTV.text = getString(R.string.since_date, uDT)
        }
    }
}
