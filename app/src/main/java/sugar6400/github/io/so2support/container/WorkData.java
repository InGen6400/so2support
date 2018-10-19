package sugar6400.github.io.so2support.container;

import java.util.ArrayList;

//作業データクラス
public class WorkData {
    //作業名
    private String name;
    //作業時間
    private int minutes;
    //時給
    private double wage;
    //原料リスト
    private ArrayList<CalcItemData> srcList;
    //完成品リスト
    private ArrayList<CalcItemData> prodList;
}
