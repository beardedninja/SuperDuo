package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("WIDGET", "updatingwidget");
        RemoteViews views;

        String[] selectionArgsDate = new String[1];
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        selectionArgsDate[0] = format.format(today);

        Log.d("Widget", "Checking number of matches on " + format.format(today));

        // query the content provider for today matches
        Cursor cursor = context.getContentResolver().query(
                DatabaseContract.scores_table.buildScoreWithDateAndScore(),
                null,
                null,
                selectionArgsDate,
                DatabaseContract.scores_table.TIME_COL + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String homeTeam = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
            String awayTeam = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
            int homeScore = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
            int awayScore = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

            Log.d("Widget", "Latest Match: " + homeTeam + " " + homeScore + " - " + awayScore + " " + awayTeam);

            cursor.close();

            views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.home_name, homeTeam);
            views.setTextViewText(R.id.away_name, awayTeam);
            String score = String.format(context.getString(R.string.score_text), homeScore, awayScore);
            views.setTextViewText(R.id.score, score);
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.app_widget_empty);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d("WIDGET", "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("WIDGET", "onEnabled");

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("WIDGET", "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }
}

