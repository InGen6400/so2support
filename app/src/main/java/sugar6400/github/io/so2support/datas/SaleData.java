package sugar6400.github.io.so2support.datas;

import java.util.Map;

public class SaleData {
    public long price;
    public int num;
    public byte area_id;
    public short pos_x;
    public short pos_y;
    public byte bundle;
    public int user;

    SaleData(Map<String, Object> data) {
        price = (long) data.get("price");
        num = (int) data.get("num");
        area_id = (byte) data.get("area_id");
        pos_x = (short) data.get("pos_x");
        pos_y = (short) data.get("pos_y");
        bundle = (byte) data.get("bundle");
        user = (int) data.get("user");
    }
}
