package sugar6400.github.io.so2support.datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SyncTimer {

    private SharedPreferences pref;
    private Handler syncHandler;
    private static final String TAG = "SyncTimer";
    private SyncTimerListener listener;
    private Calendar nextSyncTime;

    SyncTimer(Context c, SyncTimerListener callback) {
        listener = callback;
        syncHandler = new Handler();
        pref = PreferenceManager.getDefaultSharedPreferences(c);
        nextSyncTime = null;
    }

    public void LoadTimer() {
        long milliTimeUp = pref.getLong("SyncTimer", 15 * 1000 * 60)
                - Calendar.getInstance().getTimeInMillis();
        if (milliTimeUp <= 0) {
            listener.OnSyncTimerTimeUp();
            nextSyncTime = null;
        } else {
            SetTimer(milliTimeUp);
        }
    }

    public void SetTimer() {
        SetTimer(Long.parseLong(pref.getString("sync_freq", "15")) * 1000 * 60);
    }

    public void SetTimer(long milli_time) {
        if (milli_time <= 0) {
            nextSyncTime = null;
            Log.w(TAG, "予期しない値あいたたい");
        } else {
            syncHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listener.OnSyncTimerTimeUp();
                }
            }, milli_time);
            nextSyncTime = Calendar.getInstance();
            nextSyncTime.add(Calendar.MILLISECOND, (int) milli_time);
            pref.edit().putLong("SyncTimer", nextSyncTime.getTimeInMillis()).apply();
            Log.d(TAG, "NextSync:" + nextSyncTime.getTime().toString());
        }
    }

    public String getNextSyncDate(SimpleDateFormat format) {
        if (nextSyncTime == null)
            return "";
        return format.format(nextSyncTime.getTime());
    }

    public void RemoveTimer() {
        syncHandler.removeCallbacksAndMessages(null);
        nextSyncTime = null;
        Log.d(TAG, "Stop Timer");
    }

    interface SyncTimerListener {
        void OnSyncTimerTimeUp();
    }
}
