package sugar6400.github.io.so2support.ui;

import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import sugar6400.github.io.so2support.CalcActivity;
import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.adapters.CatAdapter;
import sugar6400.github.io.so2support.adapters.ItemSpinnerAdapter;
import sugar6400.github.io.so2support.container.CalcItemData;

import static sugar6400.github.io.so2support.CalcActivity.itemDataBase;
import static sugar6400.github.io.so2support.container.ItemDataBase.JsonMaxDataNum;
import static sugar6400.github.io.so2support.container.ItemDataBase.nCategory;

public class PopupItemEdit extends PopupWindow implements View.OnClickListener {

    //各種数値表示テキスト
    private EditText valueEditText;
    private EditText numEditText;
    private EditText probEditText;
    private CheckBox isToolChk;
    //表示文字列
    private String valuePrevText;
    private String numPrevText;

    private CalcActivity calcActivity;
    //ポップアップ用変数
    private View popupView;
    // 背景のレイアウト
    private ConstraintLayout mainLayout;
    private CalcItemData popupHolder;
    //各種追加ボタン
    private Button[] valueAddButtons;
    private Button[] numAddButtons;

    //region SpinnerValues
    //カテゴリースピナー
    private static ArrayList<Integer>[] catSpinnerItemId;
    //カテゴリ別スピナーadapter
    private ItemSpinnerAdapter[] itemSpinnerAdapter;
    //カテゴリスピナー用アダプター
    private CatAdapter catAdapter;
    //アイテムスピナー
    private Spinner itemSpinner;
    //カテゴリスピナー
    private Spinner catSpinner;
    //endregion

    private boolean isSrcList;
    private int editingIndex;

    public PopupItemEdit(CalcActivity mother) {
        calcActivity = mother;
        popupView = mother.getLayoutInflater().inflate(R.layout.popup_layout, null);
        setContentView(popupView);

        mainLayout = popupView.findViewById(R.id.popupLayout);
        popupHolder = new CalcItemData();

        valueEditText = popupView.findViewById(R.id.valueText);
        numEditText = popupView.findViewById(R.id.numText);
        probEditText = popupView.findViewById(R.id.breakProbText);
        isToolChk = popupView.findViewById(R.id.isToolCheck);

        valueAddButtons = new Button[5];
        numAddButtons = new Button[5];
        isToolChk.setOnClickListener(this);

        popupView.findViewById(R.id.delValue).setOnClickListener(this);
        popupView.findViewById(R.id.delNum).setOnClickListener(this);
        popupView.findViewById(R.id.addButton).setOnClickListener(this);
        popupView.findViewById(R.id.PMchangeValue).setOnClickListener(this);
        popupView.findViewById(R.id.PMchangeNum).setOnClickListener(this);
        popupView.findViewById(R.id.cancelButton).setOnClickListener(this);

        setAddButtons();

        initCategorySpinner();
        initItemSpinner();

        setupEditTextFormatter();

    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.addButton:
                    popupHolder.itemPosition = itemSpinner.getSelectedItemPosition();
                    popupHolder.id = catSpinnerItemId[popupHolder.catPosition].get(popupHolder.itemPosition);
                    if (isSrcList) {
                        calcActivity.addSrc(popupHolder, editingIndex);
                    } else {
                        calcActivity.addProd(popupHolder, editingIndex);
                    }
                    popupHolder.reset();
                    calcActivity.closePopup();
                    calcActivity.reCalc();
                    break;
                case R.id.cancelButton:
                    if (editingIndex != -1) {
                        popupHolder.reset();
                    }
                    calcActivity.closePopup();
                    break;
                case R.id.PMchangeNum:
                    popupHolder.isPMnumPlus = !popupHolder.isPMnumPlus;
                    changePM(numAddButtons, popupHolder.isPMnumPlus);
                    break;
                case R.id.PMchangeValue:
                    popupHolder.isPMvaluePlus = !popupHolder.isPMvaluePlus;
                    changePM(valueAddButtons, popupHolder.isPMvaluePlus);
                    break;
                case R.id.delValue:
                    popupHolder.value = 0;
                    valueEditText.setText("");
                    break;
                case R.id.delNum:
                    popupHolder.num = 0;
                    numEditText.setText("");
                    break;
                case R.id.isToolCheck:
                    if (isToolChk.isChecked() == true) {
                        probEditText.setVisibility(View.VISIBLE);
                        popupHolder.isTool = true;
                    } else {
                        probEditText.setVisibility(View.INVISIBLE);
                        popupHolder.isTool = false;
                    }
                    break;
            }
        }
    }

    private void setupEditTextFormatter() {
        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                valuePrevText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.isEmpty() && str != valuePrevText) {
                    String numStr = str.replace(",", "");
                    //13桁(兆の桁)が上限とする
                    if (numStr.length() > 13) {
                        numStr = "9999999999999";
                    }
                    popupHolder.value = Long.parseLong(numStr);
                    String textWithComma = String.format("%,d", popupHolder.value);
                    if (str.matches(textWithComma)) return;

                    int cursorPos = textWithComma.length() - (str.length() - valueEditText.getSelectionEnd());
                    valueEditText.setTextKeepState(textWithComma);
                    if (cursorPos > 0) {
                        valueEditText.setSelection(cursorPos);
                    } else {
                        valueEditText.setSelection(0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        numEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                numPrevText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.isEmpty() && str != numPrevText) {
                    String valueStr = str.replace(",", "");
                    //10桁が上限とする
                    if (valueStr.length() > 8) {
                        valueStr = "99999999";
                    }
                    //int型に変換
                    popupHolder.num = Integer.parseInt(valueStr);
                    //カンマ区切り
                    String textWithComma = String.format("%,d", popupHolder.num);
                    if (str.matches(textWithComma)) return;

                    int cursorPos = textWithComma.length() - (str.length() - numEditText.getSelectionEnd());
                    numEditText.setTextKeepState(textWithComma);
                    if (cursorPos > 0) {
                        numEditText.setSelection(cursorPos);
                    } else {
                        numEditText.setSelection(0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        probEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.isEmpty()) {
                    popupHolder.breakProb = Float.parseFloat(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initCategorySpinner() {
        catSpinner = popupView.findViewById(R.id.catSpinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            catSpinner.setLayoutMode(Spinner.MODE_DROPDOWN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            catSpinner.setLayoutMode(Spinner.MODE_DIALOG);
        }
        catAdapter = new CatAdapter(calcActivity.getApplicationContext(), R.layout.spinner_item, nCategory);
        catSpinner.setAdapter(catAdapter);
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //選択されたカテゴリーIDを保存してアイテムスピナーの内容を更新
                popupHolder.catPosition = position;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            itemSpinner.setLayoutMode(Spinner.MODE_DROPDOWN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            itemSpinner.setLayoutMode(Spinner.MODE_DIALOG);
        }
        itemSpinnerAdapter = new ItemSpinnerAdapter[nCategory];
        for (int i = 0; i < nCategory; i++) {
            itemSpinnerAdapter[i] = new ItemSpinnerAdapter(calcActivity.getApplicationContext(), R.layout.spinner_item, catSpinnerItemId[i]);
        }
        //初期は原料カテゴリのスピナー
        itemSpinner.setAdapter(itemSpinnerAdapter[0]);

        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //アイテム選択時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //popupHolder.itemPosition = position;
            }

            //アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setAddButtons() {
        for (int i = 0; i < 5; i++) {
            int id = calcActivity.getResources().getIdentifier("addValue" + String.valueOf((int) Math.pow(10, i)), "id", calcActivity.getPackageName());
            valueAddButtons[i] = popupView.findViewById(id);
            valueAddButtons[i].setTag((long) Math.pow(10, i));
            valueAddButtons[i].setText("+" + String.valueOf((int) Math.pow(10, i)));
            valueAddButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupHolder.addValue((long) view.getTag());
                    valueEditText.setText(String.format("%d", popupHolder.value));
                }
            });
            id = calcActivity.getResources().getIdentifier("addNum" + String.valueOf((int) Math.pow(10, i)), "id", calcActivity.getPackageName());
            numAddButtons[i] = popupView.findViewById(id);
            numAddButtons[i].setTag((int) Math.pow(10, i));
            numAddButtons[i].setText("+" + String.valueOf((int) Math.pow(10, i)));
            numAddButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupHolder.addNum((int) view.getTag());
                    numEditText.setText(String.format("%d", popupHolder.num));
                }
            });
        }
    }

    //アイテムをカテゴリーごとに振り分けてスピナーに登録
    private void setSpinnerItemId() {
        String cat;
        int catId;

        for (int i = 0; i < nCategory; i++) {
            catSpinnerItemId[i] = new ArrayList<Integer>();
        }

        for (int i = 1; i <= JsonMaxDataNum; i++) {
            cat = itemDataBase.getItemStr(i, "category");
            if (cat != null) {
                catId = catStr2int(cat);
                if (catId != -1) {
                    catSpinnerItemId[catId].add(itemDataBase.getItemInt(i, "item_id"));
                } else {
                    Log.e("Spinner", "CalcActivity 95~");
                }
            }
        }
    }

    //アイテムスピナーの内容を更新
    private void reloadItemSpinner() {
        itemSpinner.setAdapter(itemSpinnerAdapter[popupHolder.catPosition]);
    }

    private void changePM(Button[] buttonList, boolean isPlus) {
        if (isPlus) {
            for (int i = 0; i < buttonList.length; i++) {
                buttonList[i].setText("+" + String.valueOf((int) Math.pow(10, i)));
            }
        } else {
            for (int i = 0; i < buttonList.length; i++) {
                buttonList[i].setText("-" + String.valueOf((int) Math.pow(10, i)));
            }
        }
    }

    public void open(boolean isSrcFlag, int index, CalcItemData editedHolder) {
        if (index != -1) {
            popupHolder = (CalcItemData) editedHolder.clone();
        }
        editingIndex = index;
        isSrcList = isSrcFlag;
        reload();
    }

    public void reload() {
        if (popupHolder.value > 0) {
            valueEditText.setText(String.valueOf(popupHolder.value));
        } else {
            valueEditText.setText("");
        }
        if (popupHolder.num > 0) {
            numEditText.setText(String.valueOf(popupHolder.num));
        } else {
            numEditText.setText("");
        }
        isToolChk.setChecked(popupHolder.isTool);
        if (isToolChk.isChecked()) {
            probEditText.setText(String.valueOf(popupHolder.breakProb));
            probEditText.setVisibility(View.VISIBLE);
        } else {
            probEditText.setText("100");
            probEditText.setVisibility(View.INVISIBLE);
        }
        if (isSrcList) {
            if (editingIndex > -1) {
                ((TextView) popupView.findViewById(R.id.settingText)).setText("原料・道具を編集");
            } else {
                ((TextView) popupView.findViewById(R.id.settingText)).setText("原料・道具を追加");
            }
        } else {
            if (editingIndex > -1) {
                ((TextView) popupView.findViewById(R.id.settingText)).setText("成果品を編集");
            } else {
                ((TextView) popupView.findViewById(R.id.settingText)).setText("成果品を追加");
            }
        }
        changePM(valueAddButtons, popupHolder.isPMvaluePlus);
        changePM(numAddButtons, popupHolder.isPMnumPlus);
        catSpinner.setSelection(popupHolder.catPosition, false);
        reloadItemSpinner();
        itemSpinner.post(new Runnable() {
            @Override
            public void run() {
                itemSpinner.setSelection(popupHolder.itemPosition, false);
            }
        });
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
}
