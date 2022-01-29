package com.example.android.alephba

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.android.alephba.domain.PriceGetterUseCase
import com.example.android.alephba.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AlephbaWidgetConfigureActivity]
 */
@AndroidEntryPoint
class AlephbaWidget : AppWidgetProvider() {

    @Inject
    lateinit var priceGetterUseCase: PriceGetterUseCase

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("Babak", "Widget")
        CoroutineScope(Dispatchers.Main).launch {
            val bitcoinPrice = priceGetterUseCase.invoke().firstOrNull()
            // There may be multiple widgets active, so update all of them
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, bitcoinPrice)
            }
        }

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    bitcoinPrice: String?
) {

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    var finalPrice =
        bitcoinPrice ?: "Fetching"

    val views = RemoteViews(context.packageName, R.layout.alephba_widget).apply {
        setOnClickPendingIntent(R.id.rootView, pendingIntent)
    }
    views.setTextViewText(R.id.appwidget_price, finalPrice)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}