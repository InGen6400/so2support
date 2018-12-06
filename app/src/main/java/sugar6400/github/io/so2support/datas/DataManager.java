package sugar6400.github.io.so2support.datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.container.ItemDataBase;

import static sugar6400.github.io.so2support.CalcActivity.RPEF_NAME;

public class DataManager {

    private FirebaseFirestore db;

    private ReceiveData prices;
    //アイテムのデータ(名前，スタック数, etc...)
    public static ItemDataBase itemDataBase;

    private String[] categories;

    private String TAG = "DataManager";
    private static final String PrevSyncKey = "prev_sync";
    private String offlineMessage;
    private String onlineMessage;

    private boolean isLoading;
    private ProgressBar progressBar;
    private SharedPreferences pref;
    private SharedPreferences sync_pref;

    private OnCompleteListener<QuerySnapshot> onCompleteListener;
    private ArrayList<ListenerRegistration> snapshotListeners;

    private Handler syncHandler;

    private Date prevSyncDate;
    private SimpleDateFormat formatter;
    private TextView prevSyncTimeText;
    private Toast completeToast;

    public DataManager(Context c, ProgressBar inBar, final TextView prevSync) {
        prevSyncTimeText = prevSync;
        offlineMessage = c.getString(R.string.offline_message);
        onlineMessage = c.getString(R.string.online_message);
        categories = c.getResources().getStringArray(R.array.categoryList);
        syncHandler = new Handler();
        prices = new ReceiveData();
        snapshotListeners = new ArrayList<>();
        isLoading = false;
        progressBar = inBar;
        pref = PreferenceManager.getDefaultSharedPreferences(c);
        sync_pref = c.getSharedPreferences(RPEF_NAME, Context.MODE_PRIVATE);
        completeToast = Toast.makeText(c, "で～たを取得したん(>ω<)", Toast.LENGTH_LONG);
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
        getPrevSync();
        setPrevSyncText(false);
        onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean fromCache = task.getResult().getMetadata().isFromCache();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        Map<String, Object> data = document.getData();
                        Log.d(TAG, "Loaded: " + id);
                        //ローカルに保存
                        prices.from_map(id, data);
                    }
                    if (pref.getBoolean("isAutoSyncEnabled", true)) {
                        ReloadNextSync();
                        if (fromCache) {
                            completeToast.setText("で～たの取得ができんかったんよね\n(´･ω･`)");
                        } else {
                            completeToast.setText("で～たを取得したん(>ω<)");
                        }
                        completeToast.show();
                    }
                    if (fromCache) {
                        setPrevSyncText(true);
                    } else {
                        resetPrevSyncText(false);
                    }
                    Log.w(TAG, "Cache:" + fromCache);
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
        long nextSyncTime = getNextSyncTime();
        if (nextSyncTime == 0 && !isRealtime()) {
            LoadPrices(pref.getBoolean("isAutoSyncEnabled", true));
        } else {
            LoadPrices(false);
            setNextSyncTimer(nextSyncTime);
        }
    }

    public boolean LoadPrices(boolean isSyncEnabled) {
        //ロード中でなければ
        if (!isLoading) {
            if(isRealtime()) {
                Log.d(TAG, "リアルタイム");
                return false;
            } else {
                isLoading = true;
                prevSyncTimeText.setText("同期中...");
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
            }
        } else {
            Log.d(TAG, "ロード中");
            return false;
        }
    }

    public void setupDocumentListeners() {
        for (String cat : categories) {
            DocumentReference docRef = db.collection("price_data").document(cat);
            snapshotListeners.add(docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        String id = snapshot.getId();
                        Map<String, Object> data = snapshot.getData();
                        Log.d(TAG, "Listener Loaded: " + id);
                        //ローカルに保存
                        prices.from_map(id, data);
                        completeToast.setText(id + "の価格で～た\nを取得したん(>ω<)");
                        completeToast.show();
                        resetPrevSyncText(false);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            }));
            Log.d(TAG, cat+": Started Listen");
        }
    }

    public void removeDocumentListeners() {
        Log.d(TAG, "Stopped Listeners");
        for (ListenerRegistration register : snapshotListeners) {
            register.remove();
        }
    }

    private void getPrevSync() {
        try {
            prevSyncDate = formatter.parse(sync_pref.getString(PrevSyncKey, "no_data"));
        } catch (ParseException e) {
            prevSyncDate = null;
        }
    }

    private void resetPrevSyncText(boolean isCache) {
        prevSyncDate = new Date();
        setPrevSyncText(isCache);
    }

    private void setPrevSyncText(boolean isCache) {
        String date;
        if (prevSyncDate != null) {
            date = formatter.format(prevSyncDate);
            sync_pref.edit().putString(PrevSyncKey, date).apply();
        } else {
            date = "みつからんかった(´･ω･`)";
            isCache = true;
        }
        prevSyncTimeText.setText(isCache ? offlineMessage + ": " + date : onlineMessage + ": " + date);
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

    private boolean isRealtime(){
        return pref.getLong("SyncTimer", 15*1000*60) == 0;
    }

    private long getNextSyncTime() {
        if (pref.getBoolean("isAutoSyncEnabled", true)) {
            return Math.max(pref.getLong("SyncTimer", 15*1000*60) - Calendar.getInstance().getTimeInMillis(), 0);
        }
        return 0;
    }

    private void setNextSyncTimer(long mili_sec) {
        if (mili_sec != 0) {
            removeDocumentListeners();
            syncHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadPrices(true);
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
            // TODO: リアルタイム動機（誤字った！！治すの面倒だ．そんな事書いてる暇があったら直したらどうだ．一行がとても長くなっているぞ気をつけろまじでほんと．）
            setupDocumentListeners();
        }
    }

    public void ReloadNextSync() {
        syncHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Removed Sync Callback");
        int syncFreqMinute = Integer.parseInt(pref.getString("sync_freq", "0"));
        if(syncFreqMinute != 0) {
            Calendar nextSyncTime = Calendar.getInstance();
            nextSyncTime.add(Calendar.MINUTE, syncFreqMinute);
            pref.edit().putLong("SyncTimer", nextSyncTime.getTimeInMillis()).apply();
        }else{
            pref.edit().putLong("SyncTimer", 0).apply();
        }
        setNextSyncTimer(getNextSyncTime());
    }

    public boolean isLoading() {
        return isLoading;
    }
}
