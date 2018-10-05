package sugar6400.github.io.so2support;

public class PopupHolder {
    float value;
    float breakProb;
    int category;
    int item;
    int num;

    PopupHolder() {
        reset();
    }

    public void reset() {
        item = 0;
        category = 0;
        breakProb = 100;
        num = 0;
        value = 0;
    }

    public int addNum(int input) {
        return num += input;
    }

    public float addValue(float input) {
        return value += input;
    }
}
