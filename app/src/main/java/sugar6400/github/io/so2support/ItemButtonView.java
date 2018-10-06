package sugar6400.github.io.so2support;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.TextView;

public class ItemButtonView extends ConstraintLayout {

    CalcItem calcItem;
    ItemData itemData;
    TextView nameText;
    TextView numText;
    TextView valueText;
    TextView breakProbText;
    TextView sumValueText;

    public ItemButtonView(Context context) {
        this(context, null, new ItemData(context));
    }

    public ItemButtonView(Context context, ItemData itemData) {
        this(context, null, itemData);
    }

    public ItemButtonView(Context context, AttributeSet attrs, ItemData itemData) {
        super(context, attrs);
        init();

        this.itemData = itemData;
        nameText = this.findViewById(R.id.name);
        numText = this.findViewById(R.id.num);
        valueText = this.findViewById(R.id.value);
        breakProbText = this.findViewById(R.id.breakProbText);
        sumValueText = this.findViewById(R.id.sumValueText);

        refresh();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_view, this);
        calcItem = new CalcItem();
    }

    //計算用アイテムデータをセット
    public void setItem(CalcItem calcItem) {
        this.calcItem = calcItem;
    }

    //描画内容を更新する
    public void refresh() {
        nameText.setText(itemData.getItemStr(calcItem.id, "name"));
        numText.setText("x" + String.valueOf(calcItem.num) + itemData.getItemStr(calcItem.id, "scale"));
        valueText.setText("金額:" + String.valueOf(calcItem.value) + "G");
        breakProbText.setText("破損:" + String.valueOf(calcItem.breakProb) + "%");
        double sum = calcItem.value * calcItem.num * (1 - calcItem.breakProb / 100.0);
        sumValueText.setText(String.format("合計:%1$.2fG", sum));
    }

    @Nullable
    private OnClickListener listener;

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.listener = l;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null && checkInside(ev)) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onClick(ItemButtonView.this);
                    }
                });
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean checkInside(MotionEvent ev) {
        int[] point = new int[2];

        getLocationOnScreen(point);

        int x = point[0];
        int y = point[1];

        return (ev.getRawX() >= x && ev.getRawX() <= x + getWidth()) &&
                (ev.getRawY() >= y && ev.getRawY() <= y + getHeight());
    }
}
