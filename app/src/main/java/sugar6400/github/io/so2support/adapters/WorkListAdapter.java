package sugar6400.github.io.so2support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.container.WorkData;

import static sugar6400.github.io.so2support.CalcActivity.imageIDs;

public class WorkListAdapter extends ArrayAdapter<WorkData> {
    LayoutInflater inflater;

    public WorkListAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.work_list_item, parent, false);
        }

        TextView nameText = (TextView) convertView.findViewById(R.id.work_name);
        TextView wageText = (TextView) convertView.findViewById(R.id.work_wage);
        ImageView icon = (ImageView) convertView.findViewById(R.id.work_icon);

        WorkData data = getItem(position);

        nameText.setText(data.getName());
        wageText.setText("時給:" + String.format("%,.1fG", data.getWage()));
        icon.setImageResource(imageIDs[data.getIcon_id() - 1]);

        return convertView;
    }
}
