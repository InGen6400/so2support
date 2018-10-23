package sugar6400.github.io.so2support.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import sugar6400.github.io.so2support.CalcActivity;
import sugar6400.github.io.so2support.adapters.WorkListAdapter;
import sugar6400.github.io.so2support.container.WorkData;

public class WorkList implements AdapterView.OnItemClickListener {

    //private ArrayList<WorkData> workList;
    private WorkListAdapter workAdapter;
    private ListView listView;
    private CalcActivity calcActivity;

    public WorkList(CalcActivity calcActivity, ListView listView) {
        this.calcActivity = calcActivity;
        this.listView = listView;
        workAdapter = new WorkListAdapter(calcActivity.getBaseContext());

        listView.setOnItemClickListener(this);
        listView.setAdapter(workAdapter);
    }

    public void addWork(WorkData workData) {
        workAdapter.add(workData);
    }

    private void gotoTop(int position) {
        WorkData temp = workAdapter.getItem(position);
        workAdapter.remove(temp);
        workAdapter.add(temp);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WorkData workData = (WorkData) listView.getItemAtPosition(position);
        calcActivity.loadWork(workData);
        Toast.makeText(calcActivity.getBaseContext(), workData.getName() + "をロードしました", Toast.LENGTH_SHORT).show();
    }
}
