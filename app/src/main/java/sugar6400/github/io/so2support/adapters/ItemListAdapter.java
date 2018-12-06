package sugar6400.github.io.so2support.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import sugar6400.github.io.so2support.CalcActivity;
import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.container.CalcItemData;
import sugar6400.github.io.so2support.datas.DataManager;
import sugar6400.github.io.so2support.ui.MyGlideModule;

//import static sugar6400.github.io.so2support.CalcActivity.imageIDs;

public class ItemListAdapter extends BaseAdapter implements ListAdapter {

    private LayoutInflater inflater;
    private ArrayList<CalcItemData> itemList;
    private CalcActivity calcActivity;

    public ItemListAdapter(Context context, CalcActivity activity) {
        this.calcActivity = activity;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItemList(ArrayList<CalcItemData> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        if(itemList != null)
            return itemList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_view, parent, false);

        int id = itemList.get(position).id;

        long value = itemList.get(position).value;
        int num = itemList.get(position).num;
        String name = DataManager.itemDataBase.getItemStr(id, "name");
        String scale = DataManager.itemDataBase.getItemStr(id, "scale");
        String numText = String.valueOf(num) + scale;

        ((TextView) convertView.findViewById(R.id.name)).setText(name);
        ((TextView) convertView.findViewById(R.id.num)).setText(numText);
        ((TextView) convertView.findViewById(R.id.value)).setText("単価:" + String.format("%,dG", value));
        if (itemList.get(position).isTool) {
            ((TextView) convertView.findViewById(R.id.sumValueText)).setText("総額:" + String.format("%,.1fG", num * value * itemList.get(position).breakProb / 100.0));
        } else {
            ((TextView) convertView.findViewById(R.id.sumValueText)).setText("総額:" + String.format("%,dG", num * value));
        }
        if (itemList.get(position).isTool) {
            ((TextView) convertView.findViewById(R.id.breakProbText)).setText("道具/破損率:" + String.format("%.1f", itemList.get(position).breakProb) + "%");
        } else {
            ((TextView) convertView.findViewById(R.id.breakProbText)).setText("");
        }

        //Glide
        ImageView imageView = convertView.findViewById(R.id.itemImage);
        String fileName = "sprite_item2x_" + String.valueOf(calcActivity.dataManager.getItemElement(id, "image")) + ".png";
        Glide.with(parent).load(Uri.parse("file:///android_asset/" + fileName)).apply(MyGlideModule.iconOption).into(imageView);

        ImageButton delButton = convertView.findViewById(R.id.itemDeleteButton);
        delButton.setTag(position);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.remove((int) v.getTag());
                notifyDataSetChanged();
                calcActivity.reCalc();
            }
        });

        return convertView;
    }
}
