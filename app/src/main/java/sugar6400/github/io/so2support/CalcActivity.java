package sugar6400.github.io.so2support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int nCategory = 12;

    private int JsonMaxDataNum = 1217;
    //原料リスト
    LinearLayout srcList;
    //カテゴリースピナー
    private static ArrayList<Integer>[] catSpinnerItemId;
    //カテゴリ別スピナーadapter
    private ItemAdapter[] itemAdapters;
    //アイテムスピナー
    private Spinner itemSpinner;
    //アイテムのデータ(名前，スタック数, etc...)
    ItemData itemData;

    //選択中のカテゴリID
    private int selectedCatID = 0;
    //選択中のアイテムID
    private int selectedItemID = 0;

    private View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

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

        initItemSpinner();
    }

    private void initCategorySpinner() {
        Spinner catSpinner = popupView.findViewById(R.id.catSpinner);
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

    private void reloadItemSpinner() {
        itemSpinner.setAdapter(itemAdapters[selectedCatID]);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.srcAddButton:
                case R.id.prodAddButton:
                case R.id.deleteButton:
                case R.id.itemView:
            }
        }
        Toast.makeText(CalcActivity.this, "Click! " + String.valueOf(v.toString()), Toast.LENGTH_SHORT).show();
    }

    private void setSpinnerItemId(){
        String cat;
        int catId;

        for (int i = 0; i < nCategory; i++) {
            catSpinnerItemId[i] = new ArrayList<Integer>();
        }

        for(int i=1; i<=JsonMaxDataNum; i++){
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
