package org.jshobbysoft.memoru

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class ExampleAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // Perform this loop procedure for each widget that belongs to this
        // provider.
        appWidgetIds.forEach { appWidgetId ->

            val sharedPreferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)

            val memNumbersArray = sharedPreferences?.getString("widget","0")?.split("^")
            if (memNumbersArray!!.size == 12) {

                sharedPreferences.edit().putString("userDateKey",memNumbersArray[0]).apply()
                sharedPreferences.edit().putString("userTimeKey",memNumbersArray[1]).apply()
                sharedPreferences.edit().putString("background_uri_key",memNumbersArray[2]).apply()
                sharedPreferences.edit().putString("textColorKey",memNumbersArray[4]).apply()
                sharedPreferences.edit().putString("positionKey",memNumbersArray[5]).apply()
                sharedPreferences.edit().putString("textViewSizeKey",memNumbersArray[6]).apply()
                sharedPreferences.edit().putString("scaleTypeKey",memNumbersArray[7]).apply()
                sharedPreferences.edit().putString("widgetTextColorKey",memNumbersArray[8]).apply()
                sharedPreferences.edit().putString("textDescKey",memNumbersArray[9]).apply()
                sharedPreferences.edit().putBoolean("widgetUseKey",memNumbersArray[11].toBoolean()).apply()

                val userDateAndTimeFromPickers = memNumbersArray[0] + " " + memNumbersArray[1]
                val dateStringForWidgetUpdate = calculateDateOneLine(userDateAndTimeFromPickers)
                val textForWidget = if (calculateDateOneLine(userDateAndTimeFromPickers).take(2).toInt() < 0) {
                    "$dateStringForWidgetUpdate\nuntil $userDateAndTimeFromPickers"
                } else {
                    "$dateStringForWidgetUpdate\nsince $userDateAndTimeFromPickers"
                }

                // Get the layout for the widget
                val views = RemoteViews(
                    context.packageName,
                    R.layout.widget_second
                )

                views.setTextViewText(R.id.widget_TV, textForWidget)

                //      update text colors based on preference settings
                when (val colorOfText = memNumbersArray[8]) {
                    "red" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.red))
                    "green" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.green))
                    "blue" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.blue))
                    "black" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.black))
                    "white" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.white))
                    else -> println(colorOfText)
                }

                // Create an intent to launch the app when the textview is clicked
                val pendingIntentLaunchActivity: PendingIntent = PendingIntent.getActivity(
                    /* context = */ context,
                    /* requestCode = */  0,
                    /* intent = */ Intent(context, MainActivity::class.java),
                    /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(R.id.widget_TV, pendingIntentLaunchActivity)

                // https://github.com/juliancoronado/MinimalBitcoinWidget/blob/master/app/src/main/java/com/jcoronado/minimalbitcoinwidget/PriceWidget.kt
                // Create an Intent to update the widget when the refresh button is clicked
                val intentUpdate = Intent(context, ExampleAppWidgetProvider::class.java)
                intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId)

                val pendingIntentUpdateWidget: PendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    intentUpdate,
                    PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(R.id.button, pendingIntentUpdateWidget)

                // Tell the AppWidgetManager to perform an update on the current widget.
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)

        val memNumbersArray = sharedPreferences?.getString("widget","0")?.split("^")

        if (memNumbersArray!!.size == 12) {
            val userDateAndTimeFromPickers = memNumbersArray[0] + " " + memNumbersArray[1]

            val dateStringForWidgetUpdate = calculateDateOneLine(userDateAndTimeFromPickers)
            val textForWidget = if (calculateDateOneLine(userDateAndTimeFromPickers).take(2).toInt() < 0) {
                "$dateStringForWidgetUpdate\nuntil $userDateAndTimeFromPickers"
            } else {
                "$dateStringForWidgetUpdate\nsince $userDateAndTimeFromPickers"
            }

            // Get the layout for the widget
            val views = RemoteViews(
                context.packageName,
                R.layout.widget_second
            )

            views.setTextViewText(R.id.widget_TV, textForWidget)

            //      update text colors based on preference settings
            when (val colorOfText = memNumbersArray[8]) {
                "red" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.red))
                "green" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.green))
                "blue" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.blue))
                "black" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.black))
                "white" -> views.setTextColor(R.id.widget_TV, context.getColor(R.color.white))
                else -> println(colorOfText)
            }

            // Tell the AppWidgetManager to perform an update on the current
            // widget.
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,0)
            appWidgetManager.updateAppWidget(appWidgetId,views)
        }
    }

    private fun calculateDateOneLine(endDate: String?): String {
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
}
