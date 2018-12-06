package sugar6400.github.io.so2support.datas;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.container.ItemDataBase;

import static sugar6400.github.io.so2support.CalcActivity.RPEF_NAME;

public class DataManager implements SyncTimer.SyncTimerListener {

    private FirebaseFirestore db;

    private ReceiveData prices;
    //アイテムのデータ(名前，スタック数, etc...)
    public static ItemDataBase itemDataBase;

    private SyncTimer timer;

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

    private Date prevSyncDate;
    private SimpleDateFormat formatter;
    private TextView prevSyncTimeText;
    private Toast completeToast;

    public DataManager(Context c, ProgressBar inBar, final TextView prevSync) {
        offlineMessage = c.getString(R.string.offline_message);
        onlineMessage = c.getString(R.string.online_message);
        isLoading = false;
        categories = c.getResources().getStringArray(R.array.categoryList);

        prevSyncTimeText = prevSync;
        progressBar = inBar;

        pref = PreferenceManager.getDefaultSharedPreferences(c);
        sync_pref = c.getSharedPreferences(RPEF_NAME, Context.MODE_PRIVATE);
        completeToast = Toast.makeText(c, "で～たを取得したん(>ω<)", Toast.LENGTH_LONG);

        prices = new ReceiveData();
        formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
        snapshotListeners = new ArrayList<>();


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
        timer = new SyncTimer(c, this);
    }

    public boolean LoadPrices(boolean isSyncEnabled) {
        //ロード中でなければ
        if (!isLoading) {
            if (isRealTime()) {
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

    private boolean isRealTime() {
        return pref.getString("sync_freq", "15").equals("-1");
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void ReloadSync() {
        if (pref.getBoolean("isAutoSyncEnabled", true)) {
            if (isRealTime()) {
                setupDocumentListeners();
                timer.RemoveTimer();
            } else {
                removeDocumentListeners();
            }
        } else {
            Log.d(TAG, "同期OFF");
        }
    }

    @Override
    public void OnSyncTimerTimeUp() {
        LoadPrices(true);
    }
}
