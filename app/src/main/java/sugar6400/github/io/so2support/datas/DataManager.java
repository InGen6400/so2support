package sugar6400.github.io.so2support.datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import sugar6400.github.io.so2support.container.ItemDataBase;

public class DataManager {

    private FirebaseFirestore db;

    private ReceiveData prices;
    //アイテムのデータ(名前，スタック数, etc...)
    public static ItemDataBase itemDataBase;

    private String TAG = "DataManager";
    private boolean isLoading;
    private ProgressBar progressBar;
    private SharedPreferences pref;

    private OnCompleteListener<QuerySnapshot> onCompleteListener;

    public DataManager(Context c, ProgressBar inBar) {
        prices = new ReceiveData();
        isLoading = false;
        progressBar = inBar;
        onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        Map<String, Object> data = document.getData();
                        Log.d(TAG, id + " => " + data);
                        //ローカルに保存
                        prices.from_map(id, data);
                    }
                    if (pref.getBoolean("isAutoSyncEnabled", true)) {
                        SaveNextSync();
                    }
                    Log.w(TAG, "Cache:" + task.getResult().getMetadata().isFromCache());
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
                isLoading = false;
                progressBar.setVisibility(View.GONE);
            }
        };
        //アイテムデータの読み込み
        itemDataBase = new ItemDataBase(c);
        db = FirebaseFirestore.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(c);
        long nextSyncTime = getNextSyncTime();
        if (nextSyncTime == 0) {
            LoadPrices(pref.getBoolean("isAutoSyncEnabled", true));
        } else {
            LoadPrices(false);
            setNextSyncTimer(nextSyncTime);
        }
    }

    public ReceiveItem getReceiveItem(String category, String id) {
        return prices.receive_items.get(category).get(id);
    }

    public ReceiveItem getReceiveItem(String id) {
        for (String key : prices.receive_items.keySet()) {
            if (prices.receive_items.get(key).containsKey(id)) {
                return prices.receive_items.get(key).get(id);
            }
        }
        return null;
    }

    public ReceiveItem getReceiveItem(int id) {
        return getReceiveItem(String.valueOf(id));
    }

    public int getItemElement(int id, String tag) {
        return itemDataBase.getItemInt(id, tag);
    }

    public boolean LoadPrices(boolean isSyncEnabled) {
        //ロード中でなければ
        if (!isLoading) {
            isLoading = true;
            progressBar.setVisibility(View.VISIBLE);
            //自動同期がONなら
            if (isSyncEnabled) {
                db.collection("price_data")
                        .get()
                        .addOnCompleteListener(onCompleteListener);
            } else {
                db.collection("price_data")
                        .get(com.google.firebase.firestore.Source.CACHE)
                        .addOnCompleteListener(onCompleteListener);
            }
            Log.d(TAG, "Loading");
            return true;
        } else {
            return false;
        }
    }

    private long getNextSyncTime() {
        if (pref.getBoolean("isAutoSyncEnabled", true)) {
            return Math.max(pref.getLong("SyncTimer", 0) - Calendar.getInstance().getTimeInMillis(), 0);
        }
        return 0;
    }

    private void setNextSyncTimer(long mili_sec) {
        if (mili_sec != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadPrices(false);
                }
            }, mili_sec);
            Calendar nextSyncTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
            nextSyncTime.add(Calendar.MILLISECOND, (int) mili_sec);
            Log.d(TAG, "Next Sync: " + nextSyncTime.getTimeZone().getDisplayName() + "\t" +
                    nextSyncTime.get(Calendar.YEAR)
                    + "_" + (nextSyncTime.get(Calendar.MONTH) + 1)
                    + "月" + nextSyncTime.get(Calendar.DAY_OF_MONTH)
                    + "日" + nextSyncTime.get(Calendar.HOUR_OF_DAY)
                    + "時" + nextSyncTime.get(Calendar.MINUTE)
                    + "分" + nextSyncTime.get(Calendar.SECOND));
        } else {
            // TODO: リアルタイム動機
        }
    }

    public void SaveNextSync() {
        int syncFreqMinute = Integer.parseInt(pref.getString("sync_freq", "0"));
        Calendar nextSyncTime = Calendar.getInstance();
        nextSyncTime.add(Calendar.MINUTE, syncFreqMinute);
        pref.edit().putLong("SyncTimer", nextSyncTime.getTimeInMillis()).apply();
        setNextSyncTimer(getNextSyncTime());
    }

    public boolean isLoading() {
        return isLoading;
    }

    public interface OnPriceDataLoadedListener {
        void onPriceDataLoaded();
    }
}