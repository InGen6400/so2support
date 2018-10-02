package sugar6400.github.io.so2support;

public class CalcItem {
    //アイテムのid
    public int id;
    //個数
    public int num;
    //価格
    public float value;
    //道具かどうか
    public boolean isTool;
    //破損率
    public float breakProb;

    CalcItem() {
        this(1);
    }

    CalcItem(int id) {
        this.id = id;
        num = 0;
        value = 0;
        isTool = false;
        breakProb = 100;
    }
}
