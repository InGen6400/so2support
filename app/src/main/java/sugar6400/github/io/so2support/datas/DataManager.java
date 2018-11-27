package sugar6400.github.io.so2support.datas;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import sugar6400.github.io.so2support.container.ItemDataBase;

public class DataManager {

    private FirebaseFirestore db;

    private ReceiveData prices;
    //アイテムのデータ(名前，スタック数, etc...)
    public static ItemDataBase itemDataBase;

    private String TAG = "DataManager";
    private boolean isLoading;
    private OnPriceDataLoadedListener listener;
    private ProgressBar progressBar;

    public DataManager(Context c, ProgressBar inBar) {
        prices = new ReceiveData();
        isLoading = false;
        progressBar = inBar;
        //アイテムデータの読み込み
        itemDataBase = new ItemDataBase(c);
        db = FirebaseFirestore.getInstance();
    }

    public ReceiveItem getReceiveItem(String category, String id) {
        return prices.receive_items.get(category).get(id);
    }

    public int getItemElement(int id, String tag) {
        return itemDataBase.getItemInt(id, tag);
    }

    public boolean LoadPrices(OnPriceDataLoadedListener in_listener) {
        if (!isLoading) {
            isLoading = true;
            listener = in_listener;
            progressBar.setVisibility(View.VISIBLE);
            db.collection("price_data")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    Log.d(TAG, id + " => " + document.getData());
                                    prices.from_map(id, document.getData());
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                            isLoading = false;
                            listener.onPriceDataLoaded();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
            Log.d(TAG, "Loading");
            return true;
        } else {
            return false;
        }
    }

    public interface OnPriceDataLoadedListener {
        void onPriceDataLoaded();
    }
}
