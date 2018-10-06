package sugar6400.github.io.so2support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static sugar6400.github.io.so2support.CalcActivity.imageIDs;
import static sugar6400.github.io.so2support.CalcActivity.itemDataBase;

public class ItemListAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CalcItemData> itemList;

    public ItemListAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItemList(ArrayList<CalcItemData> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
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

        float value = itemList.get(position).value;
        int num = itemList.get(position).num;
        String name = itemDataBase.getItemStr(id, "name");
        String scale = itemDataBase.getItemStr(id, "scale");
        String numText = String.valueOf(num) + scale;

        ((TextView) convertView.findViewById(R.id.name)).setText(name);
        ((TextView) convertView.findViewById(R.id.num)).setText(numText);
        ((TextView) convertView.findViewById(R.id.value)).setText("単価:" + String.valueOf(value) + "G");
        ((TextView) convertView.findViewById(R.id.sumValueText)).setText("総額:" + String.valueOf(num * value) + "G");
        if (itemList.get(position).isTool) {
            ((TextView) convertView.findViewById(R.id.breakProbText)).setText("道具/破損率:" + String.valueOf(itemList.get(position).breakProb) + "%");
        } else {
            ((TextView) convertView.findViewById(R.id.breakProbText)).setText("");
        }

        ((ImageView) convertView.findViewById(R.id.itemImage)).setImageResource(imageIDs[itemList.get(position).id - 1]);
        ImageButton delButton = convertView.findViewById(R.id.itemDeleteButton);
        delButton.setTag(position);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.remove((int) v.getTag());
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
