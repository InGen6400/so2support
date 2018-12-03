package sugar6400.github.io.so2support.container;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import sugar6400.github.io.so2support.datas.DataManager;

//作業データクラス
public class WorkData {
    //作業名
    @Expose
    private String name;
    //作業時間
    @Expose
    private int minutes;
    //時給
    @Expose
    private double wage;
    //アイコンのアイテムID
    @Expose
    private int icon_id;
    //原料リスト
    @Expose
    private ArrayList<CalcItemData> srcList;
    //完成品リスト
    @Expose
    private ArrayList<CalcItemData> prodList;
    //リスナー
    @Expose(serialize = false, deserialize = false)
    private OnWorkChangedListener listener;

    public WorkData(int minutes, double wage, OnWorkChangedListener listener) {
        this(DataManager.itemDataBase.getItemStr(0, "name"), minutes, wage, listener);
    }

    public WorkData(String name, int minutes, double wage, OnWorkChangedListener listener) {
        this(name, 1, minutes, wage, listener);
    }

    public WorkData(String name, int image, int minutes, double wage, OnWorkChangedListener listener) {
        this.icon_id = image;
        this.name = name;
        this.minutes = minutes;
        this.wage = wage;
        this.srcList = new ArrayList<>();
        this.prodList = new ArrayList<>();
        this.listener = listener;
    }

    public void setListener(OnWorkChangedListener listener) {
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        listener.OnWorkChanged(this);
    }

    public void addSrc(CalcItemData additionalData) {
        srcList.add(additionalData);
        listener.OnWorkChanged(this);
    }

    public void addProd(CalcItemData additionalData) {
        prodList.add(additionalData);
        if(!prodList.isEmpty()) {
            icon_id = prodList.get(0).id;
        }else{
            icon_id = 0;
        }
        listener.OnWorkChanged(this);
    }

    public void setSrc(CalcItemData newData, int index) {
        srcList.set(index, newData);
        listener.OnWorkChanged(this);
    }

    public void setProd(CalcItemData newData, int index) {
        prodList.set(index, newData);
        listener.OnWorkChanged(this);
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes){this.minutes = minutes;}

    public double getWage() {
        return wage;
    }

    public void setWage(double wage){this.wage = wage;}

    public int getIcon_id() {
        return icon_id;
    }

    public ArrayList<CalcItemData> getSrcList() {
        return srcList;
    }

    public ArrayList<CalcItemData> getProdList() {
        return prodList;
    }

    public interface OnWorkChangedListener {
        void OnWorkChanged(WorkData data);
    }
}
