package sugar6400.github.io.so2support.datas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiveItem {
    public List<SaleData> cheap5_day;
    public List<SaleData> cheap5_week;
    public double ave3;
    public SaleData cheapest;

    @SuppressWarnings("unchecked")
    ReceiveItem(Map<String, Object> data) {
        cheap5_day = new ArrayList<>();
        cheap5_week = new ArrayList<>();
        for (Map<String, Object> item : (List<Map<String, Object>>) data.get("cheap5_day")) {
            cheap5_day.add(new SaleData(item));
        }
        for (Map<String, Object> item : (List<Map<String, Object>>) data.get("cheap5_week")) {
            cheap5_week.add(new SaleData(item));
        }
        cheapest = new SaleData((Map<String, Object>) data.get("cheapest"));
        ave3 = (double) data.get("ave3");
    }
}
