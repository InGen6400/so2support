package sugar6400.github.io.so2support.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import sugar6400.github.io.so2support.CalcActivity;
import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.adapters.WorkListAdapter;
import sugar6400.github.io.so2support.container.WorkData;

import static sugar6400.github.io.so2support.CalcActivity.RPEF_NAME;

public class WorkList implements AdapterView.OnItemClickListener, WorkData.OnWorkChangedListener {

    private ArrayList<WorkData> workList;
    private WorkListAdapter workAdapter;
    private ListView listView;
    private CalcActivity calcActivity;
    public ToggleButton deleteToggle;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private static final String WORK_SAVE_KEY = "work_data";

    public WorkList(CalcActivity calcActivity, ListView listView) {
        this.calcActivity = calcActivity;
        this.listView = listView;

        deleteToggle = calcActivity.findViewById(R.id.work_delete_toggle);
        deleteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workAdapter.setDeleteIcon(isChecked);
            }
        });

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        pref = calcActivity.getSharedPreferences(RPEF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        workList = new ArrayList<>();
        load();
        workAdapter = new WorkListAdapter(calcActivity.getBaseContext(), workList);

        listView.setOnItemClickListener(this);
        listView.setAdapter(workAdapter);
    }

    public void addWork(WorkData workData) {
        workAdapter.insert(workData, 0);
        save();
    }

    public void gotoTop(int position) {
        WorkData temp = workAdapter.getItem(position);
        workAdapter.remove(temp);
        workAdapter.insert(temp, 0);
        save();
    }

    public void insertTop(int position, WorkData workData) {
        WorkData temp = workAdapter.getItem(position);
        workAdapter.remove(temp);
        workAdapter.insert(workData, 0);
        save();
    }

    private void save() {
        workAdapter.notifyDataSetChanged();
        String json = gson.toJson(workList);
        editor.putString(WORK_SAVE_KEY, json);
        editor.commit();
    }

    private void load() {
        Type type = new TypeToken<ArrayList<WorkData>>() {}.getType();
        String jsonStr = pref.getString(WORK_SAVE_KEY, null);
        if (jsonStr != null) {
            workList = gson.fromJson(jsonStr, type);
        }
    }

    public WorkData getItem(int position) {
        return (WorkData) listView.getItemAtPosition(position);
    }

    public int getCount() {
        return workList.size();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WorkData workData = (WorkData) listView.getItemAtPosition(position);
        if (deleteToggle.isChecked()) {
            workAdapter.remove(workData);
            save();
        } else {
            // TODO: オートセーブ動作，削除時や読み込み時の動作の違い
            calcActivity.loadWork(workData, position);
            calcActivity.showToast("「" + workData.getName() + "」を読み込んだよ～", Toast.LENGTH_SHORT);
        }
    }

    public WorkData getWork(int position) {
        return workList.get(position);
    }

    public void OnWorkChanged(WorkData data) {
        if (workList.contains(data)) {
            insertTop(workList.indexOf(data), data);
        } else {
            addWork(data);
        }
        save();
    }
}
