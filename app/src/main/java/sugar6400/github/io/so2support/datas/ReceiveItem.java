package sugar6400.github.io.so2support.datas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiveItem {
    public double cheap5_day;
    public double cheap5_week;
    public List<SaleData> cheapest;

    @SuppressWarnings("unchecked")
    ReceiveItem(Map<String, Object> data) {
        cheap5_day = (double) data.get("cheap5_day_ave");
        cheap5_week = (double) data.get("cheap5_week_ave");
        cheapest = new ArrayList<>();
        for (Object sale : (ArrayList<Object>) data.get("cheapest_now")) {
            cheapest.add((SaleData) sale);
        }
    }
}
