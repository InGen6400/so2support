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
        Object recv = data.get("cheap5_day_ave");
        if(recv instanceof Double) {
            cheap5_day = (double) recv;
        }else{
            cheap5_day = (long) recv;
        }
        recv = data.get("cheap5_week_ave");
        if(recv instanceof Double){
            cheap5_week = (double) recv;
        }else{
            cheap5_week = (long) recv;
        }
        cheapest = new ArrayList<>();
        for (Object sale : (ArrayList<Object>) data.get("cheapest_now")) {
            cheapest.add(new SaleData((Map<String, Object>) sale));
        }
    }
}
