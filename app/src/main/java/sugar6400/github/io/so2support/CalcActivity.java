package sugar6400.github.io.so2support;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import sugar6400.github.io.so2support.adapters.ItemListAdapter;
import sugar6400.github.io.so2support.container.CalcItemData;
import sugar6400.github.io.so2support.container.WorkData;
import sugar6400.github.io.so2support.datas.DataManager;
import sugar6400.github.io.so2support.ui.PopupItemEdit;
import sugar6400.github.io.so2support.ui.WorkList;

import static sugar6400.github.io.so2support.container.ItemDataBase.JsonMaxDataNum;

public class CalcActivity extends AppCompatActivity implements View.OnClickListener, DataManager.OnPriceDataLoadedListener {

    //原料リスト
    public static int[] imageIDs;

    public final static String RPEF_NAME = "save_data";

    private DataManager dataManager;
    private ProgressBar progressBar;

    private ArrayList<CalcItemData> srcList;
    private ArrayList<CalcItemData> prodList;
    private ItemListAdapter srcAdapter;
    private ItemListAdapter prodAdapter;

    //ポップアップ用変数
    private PopupItemEdit popupWindow;

    private TextView eqText;
    private TextView GPHText;

    private TimePickerDialog timePickerDialog;
    private TextView timeHourText;
    private TextView timeMinuteText;
    private int taskMinute;

    private WorkList workList;
    private DrawerLayout drawerLayout;
    private int showingWorkPosition = -1;
    private EditText workNameText;

    private Toast mainToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressBar = findViewById(R.id.progressBar);
        dataManager = new DataManager(this, progressBar);
        dataManager.LoadPrices(this);

        Toolbar myToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        workList = new WorkList(this, (ListView) findViewById(R.id.test));

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(
                        this, drawerLayout, myToolbar, R.string.drawer_open, R.string.drawer_close
                ) {
                    //ドロワーが開いたとき，キーボードを非表示
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        drawerLayout.requestFocus();
                    }

                    //ドロワーが閉じたとき，削除トグルをoffに
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                        workList.deleteToggle.setChecked(false);
                    }
                };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        eqText = findViewById(R.id.eqText);
        GPHText = findViewById(R.id.GPH);

        initImageID();

        initItemListView();
        initItemProdListView();

        workNameText = findViewById(R.id.work_name);
        workNameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //EnterKeyが押されたかを判定
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {

                    //ソフトキーボードを閉じる
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    return true;
                }
                return false;
            }
        });
        workNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        popupWindow = new PopupItemEdit(CalcActivity.this);
        initTimePicker();

        if (workList.getCount() == 0) {
            WorkData workData = new WorkData("ポーションを作る", 30, 3924, srcList, prodList);
            workList.addWork(workData);
        } else {
            //セーブデータが存在していたら，一番上のデータを表示する
            loadWork(workList.getItem(0), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
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
                case R.id.save_button:
                    addWork();
                    break;
                case R.id.new_work_button:
                    newWork();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onPriceDataLoaded() {

    }

    private void initTimePicker() {
        taskMinute = 0;
        timeHourText = findViewById(R.id.timeHourText);
        timeMinuteText = findViewById(R.id.timeMinuteText);
        timeMinuteText.setText("0分 時間を入力してね→");
        timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        taskMinute = hourOfDay * 60 + minute;
                        if (hourOfDay != 0) {
                            String str = String.format(Locale.US, "%d時間", hourOfDay);
                            timeHourText.setText(str);
                        } else {
                            timeHourText.setText("");
                        }
                        if (minute != 0) {
                            timeMinuteText.setText(String.format(Locale.US, "%d分", minute));
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
            String imageFileName = "sprite_item2x_" + String.valueOf(dataManager.getItemElement(i + 1, "image"));
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
                openPopup(true, position, srcList.get(position));
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public double reCalc() {
        //原料の総額
        long srcSum = 0;
        double GPH = 0;
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
            GPH = (prodSum - srcSum - tax) * 60.0 / taskMinute;
            eqText.setText(String.format(Locale.US, "{(成果:%,.1fG) － (原料:%,dG) － (税金:%,.1fG)} ÷ %.2f時間", prodSum, srcSum, tax, taskMinute / 60.0));
            GPHText.setText(String.format(Locale.US, "時給 %,.1f G/h", GPH));
        }
        return GPH;
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

    public void addWork() {
        if (prodList.size() == 0 || srcList.size() == 0) {
            showToast("原料/完成品を追加して！ヽ(`Д´)ﾉﾌﾟﾝﾌﾟﾝ", Toast.LENGTH_SHORT);
            return;
        }
        WorkData workData = new WorkData(workNameText.getText().toString(), taskMinute, reCalc(), srcList, prodList);
        if(showingWorkPosition >= 0){
            //存在するなら更新
            workList.insertTop(showingWorkPosition, workData);
            showToast("「" + workData.getName() + "」\n作業の変更を保存したよ(^^)/", Toast.LENGTH_SHORT);
        }else {
            //存在しないなら追加
            workList.addWork(workData);
            showToast("「" + workData.getName() + "」\n作業リストに追加したよ(^^)/", Toast.LENGTH_SHORT);
        }
        //トップへ
        showingWorkPosition = 0;
    }

    private void newWork() {
        showingWorkPosition = -1;
        srcList.clear();
        prodList.clear();
        workNameText.setText("新規作業");
        timePickerDialog.updateTime(0, 0);
        timeHourText.setText("");
        timeMinuteText.setText("");
        taskMinute = 0;
        prodAdapter.notifyDataSetChanged();
        srcAdapter.notifyDataSetChanged();
        showToast("新しい作業！(*ﾟ▽ﾟ*)ﾜｸﾜｸ", Toast.LENGTH_SHORT);
        reCalc();
    }

    public void catchWorkDeleted(int position) {
        if (position == showingWorkPosition) {
            showingWorkPosition = -1;
        }
    }

    //作業データを読み込む
    public void loadWork(WorkData workData, int position) {
        taskMinute = workData.getMinutes();
        int hourOfDay = taskMinute / 60;
        int minute = taskMinute % 60;
        //タイムピッカーを設定
        timePickerDialog.updateTime(hourOfDay, minute);
        //時間のテキストを設定
        if (hourOfDay != 0) {
            timeHourText.setText(String.format(Locale.US, "%d時間", hourOfDay));
        } else {
            timeHourText.setText("");
        }
        if (minute != 0) {
            timeMinuteText.setText(String.format(Locale.US, "%d分", minute));
        } else {
            timeMinuteText.setText("");
        }
        workNameText.setText(workData.getName());

        srcList = new ArrayList<>(workData.getSrcList());
        prodList = new ArrayList<>(workData.getProdList());
        srcAdapter.setItemList(srcList);
        prodAdapter.setItemList(prodList);
        srcAdapter.notifyDataSetChanged();
        prodAdapter.notifyDataSetChanged();

        reCalc();
        workList.gotoTop(position);
        showingWorkPosition = 0;

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.START, true);
            }
        });
    }

    public void showToast(String message, int duration) {
        if (mainToast != null) {
            mainToast.cancel();
        }
        mainToast = Toast.makeText(this, message, duration);
        mainToast.show();
    }

}
