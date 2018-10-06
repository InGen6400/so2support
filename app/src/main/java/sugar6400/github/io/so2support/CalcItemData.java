package sugar6400.github.io.so2support;

public class CalcItemData implements Cloneable {
    //catSpinnerでの位置
    public int catPosition;
    //spinnerでの位置
    public int itemPosition;
    //アイテムのid
    public int id;
    //個数
    public int num;
    //価格
    public float value;
    //破損率
    public float breakProb;
    //道具かどうか
    public boolean isTool;

    CalcItemData() {
        this(1);
    }

    CalcItemData(int id) {
        reset();
        this.id = id;
    }

    public void reset() {
        id = 1;
        num = 0;
        value = 0;
        breakProb = 100;
        isTool = false;
        catPosition = 0;
        itemPosition = 0;
    }

    public int addNum(int input) {
        return num += input;
    }

    public float addValue(float input) {
        return value += input;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            System.err.println(ex);
            return null;
        }
    }
}
