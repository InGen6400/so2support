package sugar6400.github.io.so2support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout srcList;

    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        srcList = findViewById(R.id.srcList);

        for (i = 0; i < 5; i++) {
            ItemButtonView itemButton = new ItemButtonView(this);
            itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CalcActivity.this, "Click! " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                }
            });
            srcList.addView(itemButton);
        }
    }


    @Override
    public void onClick(View v) {
    }

    public void onClickEdit(View v) {

    }
}
