package sugar6400.github.io.so2support;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import static sugar6400.github.io.so2support.ItemDataBase.JsonMaxDataNum;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener {

    //原料リスト
    //アイテムのデータ(名前，スタック数, etc...)
    public static ItemDataBase itemDataBase;
    public static int[] imageIDs;

    private ArrayList<CalcItemData> srcList;
    private ArrayList<CalcItemData> prodList;
    private ItemListAdapter srcAdapter;
    private ItemListAdapter prodAdapter;

    //ポップアップ用変数
    //private View popupView;
    private PopupItemEdit popupWindow;

    private TextView eqText;
    private TextView GPHText;

    //private PopupHolder popupHolder;
    //private CalcItemData popupHolder;

    private TimePickerDialog timePickerDialog;
    private TextView timeHourText;
    private TextView timeMinuteText;
    private int taskMinute;

    // キーボード表示を制御するためのオブジェクト
    InputMethodManager inputMethodManager;
    // 背景のレイアウト
    private ConstraintLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        eqText = findViewById(R.id.eqText);
        GPHText = findViewById(R.id.GPH);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //アイテムデータの読み込み
        itemDataBase = new ItemDataBase(this);
        initImageID();

        initItemListView();
        initItemProdListView();

        popupWindow = new PopupItemEdit(CalcActivity.this);

        initTimePicker();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void initTimePicker() {
        taskMinute = 0;
        timeHourText = findViewById(R.id.timeHourText);
        timeMinuteText = findViewById(R.id.timeMinuteText);
        timeMinuteText.setText(String.format("0分 時間を入力してね→"));
        timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        taskMinute = hourOfDay * 60 + minute;
                        if (hourOfDay != 0) {
                            timeHourText.setText(String.format("%d時間", hourOfDay));
                        } else {
                            timeHourText.setText("");
                        }
                        if (minute != 0) {
                            timeMinuteText.setText(String.format("%d分", minute));
                        } else {
                            timeMinuteText.setText("");
                        }
                        reCalc();
                    }
                },
                0, 0, true);
    }

    private void initImageID() {
        imageIDs = new int[JsonMaxDataNum];

        Resources res = getResources();
        for (int i = 0; i < imageIDs.length; i++) {
            String imageFileName = "sprite_item2x_" + String.valueOf(itemDataBase.getItemInt(i + 1, "image"));
            imageIDs[i] = res.getIdentifier(imageFileName, "drawable", getPackageName());
        }
    }

    private void initItemListView() {
        final ListView srcListView;
        srcListView = findViewById(R.id.srcList);
        srcList = new ArrayList<>();

        srcAdapter = new ItemListAdapter(CalcActivity.this, this);
        //テスト用のアイテム
        srcAdapter.setItemList(srcList);

        CalcItemData itemData = new CalcItemData();
        itemData.id = 1;
        itemData.num = 5;
        itemData.breakProb = 100;
        itemData.value = 90;
        itemData.isTool = false;
        itemData.catPosition = 0;
        itemData.itemPosition = 0;
        srcList.add(itemData);

        CalcItemData itemData2 = new CalcItemData();
        itemData2.id = 15;
        itemData2.num = 1;
        itemData2.breakProb = 10;
        itemData2.value = 2500;
        itemData2.isTool = true;
        itemData2.catPosition = 1;
        itemData2.itemPosition = 1;
        srcList.add(itemData2);

        srcAdapter.notifyDataSetChanged();
        srcListView.setAdapter(srcAdapter);

        //アイテムタップ時に編集画面を開く
        srcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPopup(true, position, (CalcItemData) srcList.get(position));
            }
        });
    }

    private void initItemProdListView() {
        final ListView prodListView;
        prodListView = findViewById(R.id.prodList);
        prodList = new ArrayList<>();
        prodAdapter = new ItemListAdapter(CalcActivity.this, this);
        //テスト用のアイテム
        prodAdapter.setItemList(prodList);

        CalcItemData itemData = new CalcItemData();
        itemData.id = 30;
        itemData.num = 16;
        itemData.breakProb = 100;
        itemData.value = 180;
        itemData.isTool = false;
        itemData.catPosition = 2;
        itemData.itemPosition = 0;
        prodList.add(itemData);

        prodAdapter.notifyDataSetChanged();
        prodListView.setAdapter(prodAdapter);

        //アイテムタップ時に編集画面を開く
        prodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPopup(false, position, prodList.get(position));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.srcPopupButton:
                    openPopup(true, -1, null);
                    break;
                case R.id.prodPopupButton:
                    openPopup(false, -1, null);
                    break;
                case R.id.openTimePicker:
                    timePickerDialog.show();
                    break;
            }
        }
    }

    // 画面タップ時の処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
        // キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        // 背景にフォーカスを移す
        mainLayout.requestFocus();
        */
        return true;
    }

    @Override
    protected void onDestroy() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        super.onDestroy();
    }

    //アイテム追加用ポップアップを開く
    private void openPopup(boolean isSrc, int position, CalcItemData holder) {

        //背景の指定
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //lolipop以上なら影付き表示
            popupWindow.setElevation(20);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        } else {
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        }

        //タップ時の他Viewの動作の設定
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        int width = 1000;
        popupWindow.setWindowLayoutMode(width, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(width);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        //中央に表示
        popupWindow.showAtLocation(findViewById(R.id.srcPopupButton), Gravity.CENTER, 0, 0);
        popupWindow.open(isSrc, position, holder);
    }

    public void closePopup() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public void reCalc() {
        //原料の総額
        long srcSum = 0;
        for (int i = 0; i < srcList.size(); i++) {
            if (srcList.get(i).isTool) {
                srcSum += srcList.get(i).value * srcList.get(i).num * (srcList.get(i).breakProb / 100);
            } else {
                srcSum += srcList.get(i).value * srcList.get(i).num;
            }
        }
        //成果の総額
        float prodSum = 0;
        for (int i = 0; i < prodList.size(); i++) {
            if (prodList.get(i).isTool) {
                prodSum += prodList.get(i).value * prodList.get(i).num * (prodList.get(i).breakProb / 100);
            } else {
                prodSum += prodList.get(i).value * prodList.get(i).num;
            }
        }
        //もし原料や成果がないなら計算式は表示しない
        if (srcList.size() == 0 || prodList.size() == 0 || taskMinute == 0) {
            eqText.setText(R.string.defaultEqu);
            GPHText.setText(R.string.defaultGPH);
        } else {
            float tax = (float) ((prodSum - srcSum) * 0.1);
            eqText.setText(String.format("{(成果:%,.1fG) － (原料:%,dG) － (税金:%,.1fG)} ÷ %.2f時間", prodSum, srcSum, tax, taskMinute / 60.0));
            GPHText.setText(String.format("時給 %,.1f G/h", (prodSum - srcSum - tax) * 60.0 / taskMinute));
        }
    }

    //原料リストにアイテムを追加する
    public void addSrc(CalcItemData itemData, int index) {
        if (index == -1) {
            CalcItemData additionalData = (CalcItemData) itemData.clone();
            srcList.add(additionalData);
        } else {
            srcList.set(index, (CalcItemData) itemData.clone());
        }
        srcAdapter.notifyDataSetChanged();
    }

    public void addProd(CalcItemData itemData, int index) {
        if (index == -1) {
            CalcItemData additionalData = (CalcItemData) itemData.clone();
            prodList.add(additionalData);
        } else {
            prodList.set(index, (CalcItemData) itemData.clone());
        }
        prodAdapter.notifyDataSetChanged();
    }

    //リスト要素を編集する
    public void onClickEdit(View v) {

    }

}
