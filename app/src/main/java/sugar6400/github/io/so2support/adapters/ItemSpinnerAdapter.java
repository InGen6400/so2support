package sugar6400.github.io.so2support.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.datas.DataManager;
import sugar6400.github.io.so2support.ui.MyGlideModule;

//import static sugar6400.github.io.so2support.CalcActivity.imageIDs;

public class ItemSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private ArrayList<Integer> itemIDs;

    static class ViewHolder {
        TextView text;
        ImageView image;
    }

    public ItemSpinnerAdapter(Context c, int itemLayoutId, ArrayList<Integer> idList) {
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

            holder.image = convertView.findViewById(R.id.spinner_icon);
            holder.text = convertView.findViewById(R.id.spinner_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // TODO:Glide
        //holder.image.setImageResource(imageIDs[itemIDs.get(position) - 1]);

        String fileName = "sprite_item2x_" + String.valueOf(DataManager.itemDataBase.getItemInt(itemIDs.get(position), "image")) + ".png";
        Glide.with(parent).load(Uri.parse("file:///android_asset/" + fileName)).apply(MyGlideModule.iconOption).into(holder.image);
        holder.text.setText(DataManager.itemDataBase.getItemStr(itemIDs.get(position), "name"));

        return convertView;
    }
}
