package sugar6400.github.io.so2support.container;

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
    public long value;
    //破損率
    public float breakProb;
    //道具かどうか
    public boolean isTool;
    //金額入力がプラスかどうか
    public boolean isPMvaluePlus;
    public boolean isPMnumPlus;

    public CalcItemData() {
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
        isPMvaluePlus = true;
        isPMnumPlus = true;
    }

    public int addNum(int input) {
        return isPMnumPlus ? (num += input) : (num -= input);
    }

    public long addValue(long input) {
        return isPMvaluePlus ? (value += input) : (value -= input);
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
