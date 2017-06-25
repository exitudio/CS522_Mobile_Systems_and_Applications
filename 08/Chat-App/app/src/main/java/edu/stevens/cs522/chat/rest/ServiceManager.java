package edu.stevens.cs522.chat.rest;

/**
 * Created by dduggan.
 */

import android.content.BroadcastReceiver;
import android.os.Build;

import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import edu.stevens.cs522.chat.rest.requests.SynchronizeRequest;

public class ServiceManager {
    public static ServiceManager _serviceManager;

    // Interval between syncs in ms (would be best to make it configurable)
    public static final int SYNC_INTERVAL = 10;

    private final static String TAG = ServiceManager.class.getCanonicalName();

	/*
	 * This library is used by the UI to manage the background services.  They will typically be
	 * scheduled using the AlarmManager, based on parameters specified in the ConfigInfo preferences.
	 */

    private Context context;

    private Random rand;

    private PendingIntent syncTrigger;
    private PendingIntent repeatIntent;

    private static final int SYNC_REQUEST = 1;

    /*
     * Credentials required for scheduling services.
     */
    public ServiceManager(Context context) {
        this.context = context;
        this.rand = new Random();
    }

    private PendingIntent createSyncTrigger() {
//        if (syncTrigger == null) {
//            Intent intent = new Intent(context, RequestService.class);
//            syncTrigger = PendingIntent.getService(context, SYNC_REQUEST, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        }
//        return syncTrigger;
        if (syncTrigger == null) {
            Intent intent = new Intent(context, RequestService.class);
            intent.putExtra(RequestService.SERVICE_REQUEST_KEY, new SynchronizeRequest()); // ADDED
            syncTrigger = PendingIntent.getService(context, SYNC_REQUEST, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        return syncTrigger;
    }

    private PendingIntent createRepeatIntent(){
        if (repeatIntent == null) {
            Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
            repeatIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        }
        return syncTrigger;
    }

    /*private void scheduleRepeating(PendingIntent trigger, int interval) {
        Log.d(TAG, "Secheduling repeating alarm with interval " + interval);
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        am.setExact(AlarmManager.ELAPSED_REALTIME,interval,trigger);
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, trigger);

        final int SDK_INT = Build.VERSION.SDK_INT;
        int delay = 2;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long runTime = System.currentTimeMillis() + delay * 1000;
        Log.i(TAG," SDK_INT="+SDK_INT);
        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, runTime, repeatIntent);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME,interval,trigger);
        } else if ((SDK_INT >= Build.VERSION_CODES.KITKAT) && (SDK_INT < Build.VERSION_CODES.M)) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, runTime, repeatIntent);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,interval,trigger);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, runTime, repeatIntent);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME,interval,trigger);
        }
    }

    public void scheduleSyncRepeating(int interval) {
        Log.d(TAG, "Scheduling repeating sync with interval: " + interval);
        scheduleRepeating(createSyncTrigger(), interval);
    }

    private void cancelAlarm(AlarmManager am, PendingIntent trigger) {
        try {
            am.cancel(trigger);
        } catch (Exception e) {
            Log.w(TAG, "Trying to cancel alarm that is not set.", e);
        }
    }

    private void cancel(PendingIntent trigger) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        cancelAlarm(am, trigger);
    }

    public void cancelSync() {
        Log.d(TAG, "Canceling sync alarm.");
        cancel(createSyncTrigger());
    }*/

    public void triggerSync(){
        Intent intent = new Intent(context, RequestService.class);
        intent.putExtra(RequestService.SERVICE_REQUEST_KEY, new SynchronizeRequest()); // ADDED
        context.startService(intent);
    }

    public void scheduleBackgroundOperations() {
        Log.d(TAG, "Scheduling background operations");
        _serviceManager = this;


        final int SDK_INT = Build.VERSION.SDK_INT;
        int delay = 2;

//        createSyncTrigger();
        createRepeatIntent();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long runTime = System.currentTimeMillis() + delay * 1000;
        Log.i(TAG," SDK_INT="+SDK_INT);
        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, runTime, repeatIntent);
//            alarmManager.set(AlarmManager.ELAPSED_REALTIME,interval,trigger);
        } else if ((SDK_INT >= Build.VERSION_CODES.KITKAT) && (SDK_INT < Build.VERSION_CODES.M)) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, runTime, repeatIntent);
//            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,interval,trigger);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, runTime, repeatIntent);
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME,interval,trigger);
        }


//        scheduleSyncRepeating(rand.nextInt(SYNC_INTERVAL));
    }

    public void cancelBackgroundOperations() {
        Log.d(TAG, "Canceling background operations");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            am.cancel(repeatIntent);
        } catch (Exception e) {
            Log.w(TAG, "Trying to cancel alarm that is not set.", e);
        }
//        cancelSync();
    }

    /**
     * Query if the device is currently charging (from mains or USB).
     *
     * @param context
     * @return
     */
    public static boolean isCharging(Context context) {
        Intent stickyBroadcast = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return isCharging(stickyBroadcast);
    }

    public static boolean isCharging(Intent batteryStatusBroadcast) {
        int status = batteryStatusBroadcast.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return (status == BatteryManager.BATTERY_STATUS_CHARGING) ||
                (status == BatteryManager.BATTERY_STATUS_FULL);
    }

    /**
     * Get the current battery level.
     *
     * @param context
     * @return
     */
    public static float getBatteryLevel(Context context) {
        Intent batteryStatusBroadcast = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return getBatteryLevel(batteryStatusBroadcast);
    }

    public static float getBatteryLevel(Intent batteryStatusBroadcast) {
        int level = batteryStatusBroadcast.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatusBroadcast.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return level / (float) scale;
    }

    public static class AlarmBroadcastReceiver extends BroadcastReceiver {
        final String LOG_TAG = this.getClass().getName();

        public AlarmBroadcastReceiver(){
            super();
        }
        @Override
        public void onReceive(Context ctx, Intent intent) {
            Log.i(LOG_TAG,"onReceive");
            _serviceManager.triggerSync();
            _serviceManager.scheduleBackgroundOperations();
        }
    }

}
