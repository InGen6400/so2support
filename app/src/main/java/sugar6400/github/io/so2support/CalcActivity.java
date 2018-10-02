package sugar6400.github.io.so2support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener {

    //原料リスト
    LinearLayout srcList;
    //アイテムのデータ(名前，スタック数, etc...)
    ItemData itemData;

    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        itemData = new ItemData(this);
        //原料リストを取得
        srcList = findViewById(R.id.srcList);

        //テスト用の5アイテム
        for (i = 0; i < 5; i++) {
            ItemButtonView itemButton = new ItemButtonView(this, itemData);
            itemButton.setOnClickListener(this);
            srcList.addView(itemButton);
        }
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

    //原料リストにアイテムを追加する
    private void addSrc() {

    }

    //リスト要素を編集する
    public void onClickEdit(View v) {

    }
}
