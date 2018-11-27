package sugar6400.github.io.so2support.datas;

import java.util.HashMap;
import java.util.Map;

public class ReceiveData {

    public Map<String, Map<String, ReceiveItem>> receive_items;

    ReceiveData() {
        receive_items = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void from_map(String category, Map<String, Object> data) {
        for (String item_id : data.keySet()) {
            if (receive_items.containsKey(category)) {
                receive_items.get(category).put(item_id,
                        new ReceiveItem((Map<String, Object>) data.get(item_id)));
            } else {
                Map<String, ReceiveItem> addition = new HashMap<>();
                addition.put(item_id,
                        new ReceiveItem((Map<String, Object>) data.get(item_id)));
                receive_items.put(category, addition);
            }
        }
    }
}
