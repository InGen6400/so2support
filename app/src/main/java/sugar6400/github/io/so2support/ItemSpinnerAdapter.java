package sugar6400.github.io.so2support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static sugar6400.github.io.so2support.CalcActivity.imageIDs;
import static sugar6400.github.io.so2support.CalcActivity.itemDataBase;

public class ItemSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<Integer> itemIDs;

    static class ViewHolder {
        TextView text;
        ImageView image;
    }

    ItemSpinnerAdapter(Context c, int itemLayoutId, ArrayList<Integer> idList) {
        inflater = LayoutInflater.from(c);
        layoutID = itemLayoutId;

        itemIDs = idList;
    }

    @Override
    public int getCount() {
        return itemIDs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutID, null);
            holder = new ViewHolder();

            holder.image = convertView.findViewById(R.id.image_view);
            holder.text = convertView.findViewById(R.id.text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(imageIDs[itemIDs.get(position) - 1]);
        holder.text.setText(itemDataBase.getItemStr(itemIDs.get(position), "name"));

        return convertView;
    }
}
