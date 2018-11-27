package sugar6400.github.io.so2support.datas;

import java.util.Map;

public class SaleData {
    public long price;
    public long num;
    public long area_id;
    public long pos_x;
    public long pos_y;
    public long bundle;
    public long user;

    SaleData(Map<String, Object> data) {
        price = (long) data.get("price");
        num = (long) data.get("num");
        area_id = (long) data.get("area_id");
        pos_x = (long) data.get("pos_x");
        pos_y = (long) data.get("pos_y");
        bundle = (long) data.get("bundle");
        user = (long) data.get("user");
    }

    public String toString() {
        String ret = "price:" + price + " num:" + num + " area_id:" + area_id + " pos_x:" + pos_x + " pos_y:" + pos_y
                + " bundle:" + bundle + " user:" + user;
        return ret;
    }
}