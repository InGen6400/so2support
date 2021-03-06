package sugar6400.github.io.so2support.container;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ItemDataBase extends AsyncTask<Context, Void, Void> {

    //定数
    //カテゴリー数
    public static final int nCategory = 12;
    //Jsonに登録されている読み込まないといけないアイテム数
    public static final int JsonMaxDataNum = 1217;

    private BufferedReader br;
    private InputStream is;
    private String jsonText;

    private JSONObject itemJson;

    private ItemDataBaseListener listener;

    public ItemDataBase(ItemDataBaseListener listen) {
        listener = listen;
        if (jsonText == null) {
            Log.e("Json Error", "JsonText is null");
        }
    }

    //text(.json形式)ファイルを読み込んでJsonText形式にする
    private void readJson(Context context) {
        try {
            try {
                is = context.getAssets().open("item.json");
                br = new BufferedReader(new InputStreamReader(is));
                String str;
                while ((str = br.readLine()) != null) {
                    jsonText = jsonText + str + "\n";
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
        //Jsonデータに変換
        try {
            itemJson = new JSONObject(jsonText.substring(jsonText.indexOf("{"), jsonText.lastIndexOf("}") + 1));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Json Error", "ItemDataBase.java 30~");
        }
    }

    //jsonオブジェクトを取得
    public JSONObject getItemJson(int id) {
        try {
            return itemJson.getJSONObject(String.valueOf(id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //指定したタグの要素を取得
    public String getItemStr(int id, String tag) {
        if (itemJson != null) {
            try {
                if (itemJson.has(String.valueOf(id))) {
                    return itemJson.getJSONObject(String.valueOf(id)).getString(tag);
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "no data";
            }
        } else {
            return "data not loaded";
        }
    }

    //指定したタグ要素を取得(int)
    public int getItemInt(int id, String tag) {
        if (itemJson != null) {
            try {
                if (itemJson.has(String.valueOf(id))) {
                    return itemJson.getJSONObject(String.valueOf(id)).getInt(tag);
                } else {
                    return -1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return -2;
        }
    }

    @Override
    protected void onPreExecute() {
        listener.OnStartDataLoad();
    }

    @Override
    protected Void doInBackground(Context... context) {
        readJson(context[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void a) {
        listener.OnFinishDataLoad();
    }

    public interface ItemDataBaseListener {
        void OnStartDataLoad();

        void OnFinishDataLoad();
    }
}
