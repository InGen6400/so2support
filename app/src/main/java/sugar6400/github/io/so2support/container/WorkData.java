package sugar6400.github.io.so2support.container;

import java.util.ArrayList;

import static sugar6400.github.io.so2support.CalcActivity.itemDataBase;

//作業データクラス
public class WorkData {
    //作業名
    private String name;
    //作業時間
    private int minutes;
    //時給
    private double wage;
    //アイコンのアイテムID
    private int icon_id;
    //原料リスト
    private ArrayList<CalcItemData> srcList;
    //完成品リスト
    private ArrayList<CalcItemData> prodList;

    WorkData(int minutes, double wage, ArrayList<CalcItemData> srcList, ArrayList<CalcItemData> prodList) {
        this(itemDataBase.getItemStr(prodList.get(0).id, "name"), minutes, wage, srcList, prodList);
    }

    public WorkData(String name, int minutes, double wage,
                    ArrayList<CalcItemData> srcList, ArrayList<CalcItemData> prodList) {
        this.name = name;
        this.minutes = minutes;
        this.wage = wage;
        this.srcList = new ArrayList<>(srcList);
        this.prodList = new ArrayList<>(prodList);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public double getWage() {
        return wage;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public ArrayList<CalcItemData> getSrcList() {
        return srcList;
    }

    public ArrayList<CalcItemData> getProdList() {
        return prodList;
    }
}
