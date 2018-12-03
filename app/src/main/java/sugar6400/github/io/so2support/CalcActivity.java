package sugar6400.github.io.so2support;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.beardedhen.androidbootstrap.TypefaceProvider;

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

    public DataManager dataManager;
    private ProgressBar progressBar;

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
    private WorkData editWork;
    private DrawerLayout drawerLayout;
    //private int showingWorkPosition = -1;
    private EditText workNameText;

    private Toast mainToast;

    private EditText newNameText;
    private AlertDialog newNameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_calc);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressBar = findViewById(R.id.progressBar);
        dataManager = new DataManager(this, progressBar, (TextView) findViewById(R.id.prevSyncTimeText));
        workNameText = findViewById(R.id.work_name);

        eqText = findViewById(R.id.eqText);
        GPHText = findViewById(R.id.GPH);

        Toolbar myToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        workList = new WorkList(this, (ListView) findViewById(R.id.test));

        drawerLayout = findViewById(R.id.drawer_layout);


        newNameText = new EditText(this);
        newNameDialog = new AlertDialog.Builder(this)
                .setTitle("新規作業名")
                .setView(newNameText)
                .setPositiveButton("追加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newWork(newNameText.getText().toString());
                        newNameText.setText("");
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newNameText.setText("");
                    }
                }).create();

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

        initImageID();

        popupWindow = new PopupItemEdit(CalcActivity.this);
        initTimePicker();

        initItemListView();
        initItemProdListView();
        if (workList.getCount() == 0 || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isStartNewWork", true)) {
            newWork();
        } else {
            //セーブデータが存在していたら，一番上のデータを表示する
            loadWork(workList.getItem(0), 0);
        }


        workNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editWork.setName(workNameText.getText().toString());
            }
        });
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        dataManager.ReloadNextSync();
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
                case R.id.new_work_button:
                    newNameDialog.show();
                    break;
                case R.id.setting_btn:
                    startActivity(new Intent(CalcActivity.this, SettingsActivity.class));
                    break;
                case R.id.sync_btn:
                    dataManager.LoadPrices(true);
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
        timeHourText = findViewById(R.id.timeHourText);
        timeMinuteText = findViewById(R.id.timeMinuteText);
        timeMinuteText.setText("0分 時間を入力してね→");
        timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editWork.setMinutes(hourOfDay * 60 + minute);
                        reDraw();
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

        srcAdapter = new ItemListAdapter(CalcActivity.this, this);

        CalcItemData itemData = new CalcItemData();
        itemData.id = 1;
        itemData.num = 5;
        itemData.breakProb = 100;
        itemData.value = 90;
        itemData.isTool = false;
        itemData.catPosition = 0;
        itemData.itemPosition = 0;
        //editWork.getSrcList().add(itemData);

        CalcItemData itemData2 = new CalcItemData();
        itemData2.id = 15;
        itemData2.num = 1;
        itemData2.breakProb = 10;
        itemData2.value = 2500;
        itemData2.isTool = true;
        itemData2.catPosition = 1;
        itemData2.itemPosition = 1;
        //editWork.getSrcList().add(itemData2);

        srcAdapter.notifyDataSetChanged();

        srcListView.setAdapter(srcAdapter);
        //アイテムタップ時に編集画面を開く
        srcListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPopup(true, position, editWork.getSrcList().get(position));
            }
        });
    }

    private void initItemProdListView() {
        final ListView prodListView;
        prodListView = findViewById(R.id.prodList);
        prodAdapter = new ItemListAdapter(CalcActivity.this, this);

        CalcItemData itemData = new CalcItemData();
        itemData.id = 30;
        itemData.num = 16;
        itemData.breakProb = 100;
        itemData.value = 180;
        itemData.isTool = false;
        itemData.catPosition = 2;
        itemData.itemPosition = 0;
        //editWork.getProdList().add(itemData);

        prodAdapter.notifyDataSetChanged();

        prodListView.setAdapter(prodAdapter);

        //アイテムタップ時に編集画面を開く
        prodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openPopup(false, position, editWork.getProdList().get(position));
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
        ArrayList<CalcItemData> srcList = editWork.getSrcList();
        ArrayList<CalcItemData> prodList = editWork.getProdList();
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
        editWork.setWage(GPH);
        return GPH;
    }

    private double reDraw() {
        workNameText.setText(editWork.getName());
        taskMinute = editWork.getMinutes();
        int hourOfDay = taskMinute / 60;
        int minute = taskMinute % 60;
        timePickerDialog.updateTime(hourOfDay, minute);
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
        srcAdapter.setItemList(editWork.getSrcList());
        prodAdapter.setItemList(editWork.getProdList());
        srcAdapter.notifyDataSetChanged();
        prodAdapter.notifyDataSetChanged();

        return reCalc();
    }

    //原料リストにアイテムを追加する
    public void addSrc(CalcItemData itemData, int index) {
        if (index == -1) {
            CalcItemData additionalData = (CalcItemData) itemData.clone();
            editWork.addSrc(additionalData);
        } else {
            editWork.setSrc((CalcItemData) itemData.clone(), index);
        }
        srcAdapter.notifyDataSetChanged();
    }

    public void addProd(CalcItemData itemData, int index) {
        if (index == -1) {
            CalcItemData additionalData = (CalcItemData) itemData.clone();
            editWork.addProd(additionalData);
        } else {
            editWork.setProd((CalcItemData) itemData.clone(), index);
        }
        prodAdapter.notifyDataSetChanged();
    }

    private void newWork() {
        editWork = new WorkData("新規作業", 1, 0, 0, workList);
        showToast("新しい作業！(*ﾟ▽ﾟ*)ﾜｸﾜｸ", Toast.LENGTH_SHORT);
        reDraw();
    }

    private void newWork(String name) {
        editWork = new WorkData(name, 1, 0, 0, workList);
        showToast("新しい作業！(*ﾟ▽ﾟ*)ﾜｸﾜｸ", Toast.LENGTH_SHORT);
        reDraw();
    }

    //作業データを読み込む
    public void loadWork(WorkData workData, int position) {
        editWork = workData;
        editWork.setListener(workList);
        reDraw();

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
