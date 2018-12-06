package sugar6400.github.io.so2support.datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
        //リアルタイム同期でないならロードしてタイマーセット
        if (!pref.getString("sync_freq", "15").equals("-1"))
            LoadTimer();
    }

    public void LoadTimer() {
        long milliTimeUp = pref.getLong("SyncTimer", 15 * 1000 * 60)
                - Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo")).getTimeInMillis();
        if (milliTimeUp <= 0) {
            listener.OnSyncTimerTimeUp();
        } else {
            SetTimer(milliTimeUp);
        }
    }

    public void SetTimer() {
        SetTimer(Long.parseLong(pref.getString("sync_freq", "15")) * 1000 * 60);
    }

    public void SetTimer(long milli_time) {
        if (milli_time <= 0) {
            Log.w(TAG, "予期しない値あいたたい");
        } else {
            syncHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listener.OnSyncTimerTimeUp();
                }
            }, milli_time);
            nextSyncTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
            nextSyncTime.add(Calendar.MILLISECOND, (int) milli_time);
            pref.edit().putLong("SyncTimer", nextSyncTime.getTimeInMillis()).apply();
            Log.d(TAG, "NextSync:" + nextSyncTime.getTime().toString());
        }
    }

    public String getNextSyncDate(SimpleDateFormat format) {
        return format.format(nextSyncTime);
    }

    public void RemoveTimer() {
        syncHandler.removeCallbacksAndMessages(null);
    }

    interface SyncTimerListener {
        void OnSyncTimerTimeUp();
    }
}
