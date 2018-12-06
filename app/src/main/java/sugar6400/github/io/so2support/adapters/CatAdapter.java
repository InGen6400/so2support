package sugar6400.github.io.so2support.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import sugar6400.github.io.so2support.R;
import sugar6400.github.io.so2support.ui.GlideApp;
import sugar6400.github.io.so2support.ui.MyGlideModule;

public class CatAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int layoutID;
    private int num_cat;

    static class ViewHolder {
        TextView text;
        ImageView image;
    }

    public CatAdapter(Context c, int catLayoutId, int num_cat) {
        inflater = LayoutInflater.from(c);
        layoutID = catLayoutId;

        this.num_cat = num_cat;
        /*
        imageIDs = new int[num_cat];

        Resources res = c.getResources();
        for (int i = 0; i < num_cat; i++) {
            String imageFileName = "sprite_category2x_" + String.valueOf(i);
            imageIDs[i] = res.getIdentifier(imageFileName, "drawable", c.getPackageName());
        }*/
    }

    @Override
    public int getCount() {
        return num_cat;
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

//        holder.image.setImageResource(imageIDs[position]);
        String fileName = "sprite_category2x_" + String.valueOf(position) + ".png";
        Glide.with(parent).load(Uri.parse("file:///android_asset/" + fileName)).apply(MyGlideModule.iconOption).into(holder.image);
        holder.text.setText(convertView.getResources().getStringArray(R.array.categoryList)[position]);

        return convertView;
    }
}
