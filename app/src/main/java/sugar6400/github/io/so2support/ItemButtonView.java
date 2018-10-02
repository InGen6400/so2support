package sugar6400.github.io.so2support;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

public class ItemButtonView extends ConstraintLayout {

    public ItemButtonView(Context context) {
        this(context, null);
    }

    public ItemButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_view, this);
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
