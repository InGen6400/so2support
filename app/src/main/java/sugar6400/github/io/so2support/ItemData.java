package sugar6400.github.io.so2support;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ItemData {

    private BufferedReader br;
    private InputStream is;
    private String jsonText;

    private int[] id2im;
    private JSONObject itemJson;

    ItemData(Context c) {
        readJson(c);
        if (jsonText == null) {
            Log.e("Json Error", "JsonText is null");
        }
        try {
            itemJson = new JSONObject(jsonText.substring(jsonText.indexOf("{"), jsonText.lastIndexOf("}") + 1));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Error", "ItemData.java 30~");
        }
    }

    public void readJson(Context context) {
        try {
            try {
                is = context.getAssets().open("item.json");
                br = new BufferedReader(new InputStreamReader(is));
                String str;
                while ((str = br.readLine()) != null) {
                    jsonText += str + "\n";
                }
            } catch (Exception e) {
                Log.e("json", "why");
            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getItemJson(int id) {
        try {
            return itemJson.getJSONObject(String.valueOf(id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getItemStr(int id, String tag) {
        try {
            return itemJson.getJSONObject(String.valueOf(id)).getString(tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getItemInt(int id, String tag) {
        try {
            return itemJson.getJSONObject(String.valueOf(id)).getInt(tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
