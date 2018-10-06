package sugar6400.github.io.so2support;

public class CalcItemData {
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

    CalcItemData() {
        this(1);
    }

    CalcItemData(int id) {
        this.id = id;
        num = 0;
        value = 0;
        isTool = false;
        breakProb = 100;
    }
}
