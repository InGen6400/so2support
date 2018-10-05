package sugar6400.github.io.so2support;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener {

    //定数
    //カテゴリー数
    private static final int nCategory = 12;
    //Jsonに登録されている読み込まないといけないアイテム数
    private static final int JsonMaxDataNum = 1217;

    //原料リスト
    LinearLayout srcList;
    //アイテムのデータ(名前，スタック数, etc...)
    public static ItemData itemData;

    //カテゴリースピナー
    private static ArrayList<Integer>[] catSpinnerItemId;
    //カテゴリ別スピナーadapter
    private ItemAdapter[] itemAdapters;
    //カテゴリスピナー用アダプター
    private CatAdapter catAdapter;
    //アイテムスピナー
    private Spinner itemSpinner;
    //カテゴリスピナー
    private Spinner catSpinner;
    //選択中のカテゴリID
    private int selectedCatID = 0;
    //選択中のアイテムID
    private int selectedItemID = 0;

    //ポップアップ用変数
    private View popupView;
    private PopupWindow popupWindow;

    //各種追加ボタン
    private Button[] valueAddButtons;
    private Button[] numAddButtons;
    //各種数値表示テキスト
    private EditText valueEditText;
    private EditText numEditText;

    private PopupHolder popupHolder;

    // キーボード表示を制御するためのオブジェクト
    InputMethodManager inputMethodManager;
    // 背景のレイアウト
    private ConstraintLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        popupHolder = new PopupHolder();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainLayout = popupView.findViewById(R.id.popupLayout);

        //原料リストを取得
        srcList = findViewById(R.id.srcList);

        //アイテムデータの読み込み
        itemData = new ItemData(this);

        //テスト用の5アイテム
        for (int i = 0; i < 5; i++) {
            ItemButtonView itemButton = new ItemButtonView(this, itemData);
            itemButton.setOnClickListener(this);
            srcList.addView(itemButton);
        }

        initCategorySpinner();
        initItemSpinner();

        valueEditText = popupView.findViewById(R.id.valueText);
        numEditText = popupView.findViewById(R.id.numText);
        valueAddButtons = new Button[5];
        numAddButtons = new Button[5];
        setAddButtons();
    }

    private void initCategorySpinner() {
        catSpinner = popupView.findViewById(R.id.catSpinner);
        catAdapter = new CatAdapter(this.getApplicationContext(), R.layout.spinner_item, nCategory);
        catSpinner.setAdapter(catAdapter);
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //選択されたカテゴリーIDを保存してアイテムスピナーの内容を更新
                selectedCatID = position;
                reloadItemSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initItemSpinner() {
        //スピナーデータの読み込み
        catSpinnerItemId = new ArrayList[nCategory];
        setSpinnerItemId();

        //アイテム一覧スピナーの初期設定
        itemSpinner = popupView.findViewById(R.id.itemSpinner);
        itemAdapters = new ItemAdapter[nCategory];
        for (int i = 0; i < nCategory; i++) {
            itemAdapters[i] = new ItemAdapter(this.getApplicationContext(), R.layout.spinner_item, catSpinnerItemId[i]);
        }

        //初期は原料カテゴリのスピナー
        itemSpinner.setAdapter(itemAdapters[0]);
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //アイテム選択時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //選択したアイテムのIDを取得
                selectedItemID = catSpinnerItemId[selectedCatID].get(position);
            }

            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setAddButtons() {
        for(int i=0; i<5; i++){
            int id = getResources().getIdentifier("addValue" + String.valueOf((int) Math.pow(10, i)), "id", getPackageName());
            valueAddButtons[i] = popupView.findViewById(id);
            valueAddButtons[i].setTag((float) Math.pow(10, i));
            valueAddButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupHolder.addValue((float) view.getTag());
                    valueEditText.setText(String.format("%,.2f", popupHolder.value));
                }
            });
            id = getResources().getIdentifier("addNum" + String.valueOf((int) Math.pow(10, i)), "id", getPackageName());
            numAddButtons[i] = popupView.findViewById(id);
            numAddButtons[i].setTag((int) Math.pow(10, i));
            numAddButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupHolder.addNum((int) view.getTag());
                    numEditText.setText(String.format("%,d", popupHolder.num));
                }
            });
        }
    }

    //アイテムスピナーの内容を更新
    private void reloadItemSpinner() {
        itemSpinner.setAdapter(itemAdapters[selectedCatID]);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.srcAddButton:
                    openPopup();
                case R.id.prodAddButton:
                case R.id.deleteButton:
                case R.id.itemView:
                case R.id.delValue:
                    popupHolder.value = 0;
                    valueEditText.setText("");
                case R.id.delNum:
                    popupHolder.num = 0;
                    numEditText.setText("");
            }
        }
        Toast.makeText(CalcActivity.this, "Click! " + String.valueOf(v.toString()), Toast.LENGTH_SHORT).show();
    }

    // 画面タップ時の処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        // 背景にフォーカスを移す
        mainLayout.requestFocus();
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
    private void openPopup() {
        popupWindow = new PopupWindow(CalcActivity.this);
        popupView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setContentView(popupView);

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
        popupWindow.showAtLocation(findViewById(R.id.srcAddButton), Gravity.CENTER, 0, 0);

        popupHolder.reset();
    }

    //アイテムをカテゴリーごとに振り分けてスピナーに登録
    private void setSpinnerItemId(){
        String cat;
        int catId;

        for (int i = 0; i < nCategory; i++) {
            catSpinnerItemId[i] = new ArrayList<Integer>();
        }

        for(int i = 1; i<=JsonMaxDataNum; i++){
            cat = itemData.getItemStr(i, "category");
            if(cat != null){
                catId = catStr2int(cat);
                if (catId != -1) {
                    catSpinnerItemId[catId].add(itemData.getItemInt(i, "item_id"));
                } else {
                    Log.e("Spinner", "CalcActivity 95~");
                }
            }
        }
    }

    private int catStr2int(String catStr) {
        switch (catStr) {
            case "原料":
                return 0;
            case "本":
                return 1;
            case "薬":
                return 2;
            case "剣":
                return 3;
            case "鎧":
                return 4;
            case "盾":
                return 5;
            case "杖":
                return 6;
            case "アクセサリ":
                return 7;
            case "地図":
                return 8;
            case "道具":
                return 9;
            case "生物":
                return 10;
            case "食物":
                return 11;
        }
        return -1;
    }

    //原料リストにアイテムを追加する
    private void addSrc() {

    }

    //リスト要素を編集する
    public void onClickEdit(View v) {

    }

}
