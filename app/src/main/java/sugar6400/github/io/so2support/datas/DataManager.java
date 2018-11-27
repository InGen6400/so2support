package sugar6400.github.io.so2support.datas;

import com.google.firebase.firestore.FirebaseFirestore;

public class DataManager {

    private FirebaseFirestore db;

    private ReceiveData prices;

    DataManager() {
        db = FirebaseFirestore.getInstance();
    }

}
