package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DataArrayAdapter extends ArrayAdapter<JSONObject> {
    private DataManager dataManager = null;
    private Context context = null;
    HashMap<String, TextView> mapColumns = new HashMap<>();

    public DataArrayAdapter(Activity context, DataManager dataManager) {
        super(context, 0, dataManager.get_items());
        this.dataManager = dataManager;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject dataItem = getItem(position);
        if (convertView == null) {
            LinearLayout layoutColumn = new LinearLayout(context);
//            layoutColumn.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutColumn.setLayoutParams(lparams1);
            for(String column: dataManager.getColumns()) {
                TextView textView = new TextView(context);
                textView.setPadding(3, 0, 3, 0);
                int id = TextView.generateViewId();
                textView.setId(id);
//                TableLayout.LayoutParams lparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                TableLayout.LayoutParams lparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1);
                textView.setLayoutParams(lparams);
                layoutColumn.addView(textView);
                mapColumns.put(column, textView);
            }
            convertView = layoutColumn;
        }
        try {
            for(String column: dataManager.getColumns()) {
                TextView textView =mapColumns.get(column);
                textView.setText(dataItem.getString(column));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
