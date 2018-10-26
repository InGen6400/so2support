package sugar6400.github.io.so2support.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.container.WorkData;

import static sugar6400.github.io.so2support.CalcActivity.imageIDs;

public class WorkListAdapter extends ArrayAdapter<WorkData> {
    private LayoutInflater inflater;
    private boolean isDeleteShow = false;

    public WorkListAdapter(Context context, ArrayList<WorkData> list) {
        super(context, 0, list);
        inflater = LayoutInflater.from(context);
    }

    public void setDeleteIcon(boolean isShow) {
        isDeleteShow = isShow;
        notifyDataSetChanged();
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.work_list_item, parent, false);
        }

        ImageView deleteImage = convertView.findViewById(R.id.delete_image);
        TextView nameText = convertView.findViewById(R.id.work_name);
        TextView wageText = convertView.findViewById(R.id.work_wage);
        ImageView icon = convertView.findViewById(R.id.work_icon);

        WorkData data = getItem(position);
        if (data != null) {
            nameText.setText(data.getName());
            wageText.setText("時給:" + String.format(Locale.US, "%,.1fG", data.getWage()));
            icon.setImageResource(imageIDs[data.getIcon_id() - 1]);
            deleteImage.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        }
        if (isDeleteShow) {
            deleteImage.setVisibility(View.VISIBLE);
        } else {
            deleteImage.setVisibility(View.GONE);
        }

        return convertView;
    }
}
