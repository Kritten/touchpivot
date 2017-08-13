package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.widget.ListView;
import android.content.res.Resources;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by micro on 22.06.2017.
 */

public class DataManager {
    private Resources resources;
    private Activity activity = null;
    //
    private ArrayList<JSONObject> list_data_items = new ArrayList<>();
    private ArrayList<JSONObject> selected_items = new ArrayList<>();
    private String selected_pivot = new String();
    private ArrayList<String> list_columns = new ArrayList<>();
    private boolean pivoted_flag = false;

    public DataManager(MainActivity mainActivity) {
        activity = mainActivity;
        resources = activity.getResources();
        init();
    }
    /*
        This function takes an item and appends it to the list of items
     */
    public void addItem(JSONObject obj) {
        list_data_items.add(obj);
    }
    /*
        This function returns the current size of the list
     */
    public int getCountItems() {
        return list_data_items.size();
    }
    /*
        This function returns all items
     */
    public ArrayList<JSONObject> get_items() {
        if (pivoted_flag){
            return selected_items;
        }
        return list_data_items;
    }
    /*
        This function returns the number of columns
     */
    public int getCountColumns() {
        return list_columns.size();
    }

    public ArrayList<String> getColumn(String column) {
        ArrayList<String> result = new ArrayList<>();
        for(JSONObject item: this.get_items()) {
            try {
                result.add(item.getString(column));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /*
        This function returns all items
     */
    public ArrayList<String> getColumns() {
//        if (pivoted_flag ==true){
//            return selected_pivot;
//        }
        return list_columns;
    }
    /*
        This function loads the data and initializes the adapter.
     */
    private void init() {
        this.loadDataItems();
        ListView dataTable = (ListView)activity.findViewById(R.id.dataTable);
        DataArrayAdapter adapter = new DataArrayAdapter(activity, this);
        dataTable.setAdapter(adapter);
        dataTable.setDivider(null);
    }
    /*
        This function loads the data from the file and adds each item to the list
     */
    private void loadDataItems() {
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader(resources.openRawResource(R.raw.exported_data)));
//            BufferedReader br = new BufferedReader( new InputStreamReader(resources.openRawResource(R.raw.file_short)));
            for(String line; (line = br.readLine()) != null; ) {
                JSONObject obj = new JSONObject(line);
                this.addItem(obj);
                //this.addItem(obj);
                //this.addItem(obj);
                //this.addItem(obj);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(list_data_items.size() > 0) {
            JSONObject obj = list_data_items.get(0);
            JSONArray columns = obj.names();

            for(int i = 0; i < columns.length(); i++){
                try {
                    list_columns.add(columns.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(list_columns);

        }
    }

    public void setPivot(String col) {
        if (col.equals("")){
            selected_pivot = col;
            pivoted_flag = false;
        } else {
            this.selected_pivot = col;
            selected_items = create_pivot_data();
            pivoted_flag = true;
        }

        ListView dataTable = (ListView)activity.findViewById(R.id.dataTable);
        DataArrayAdapter adapter = new DataArrayAdapter(activity, this);
        dataTable.setAdapter(adapter);
        dataTable.setDivider(null);
    }

    private ArrayList<JSONObject> create_pivot_data(){
        Map<String, ArrayList<JSONObject>> pivot_item_lists = new HashMap<String ,ArrayList<JSONObject>>();

        // setup arrays that hold items with the same pivot value
        for (JSONObject item: list_data_items){
            try {
                String key = item.get(selected_pivot).toString();
                // System.out.println("#### KEY: "+ key);
                if (pivot_item_lists.containsKey(key)){
                    pivot_item_lists.get(key).add(item);
                } else {
                    ArrayList<JSONObject> item_list = new ArrayList<>();
                    item_list.add(item);
                    pivot_item_lists.put(key, item_list);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // iterate over pivot item list to create the new merged JSON data items
        ArrayList<JSONObject> pivot_items = new ArrayList<>();
        for (ArrayList<JSONObject> list:pivot_item_lists.values()){
            JSONObject merged_item = new JSONObject();
            //merge items in the list
            for (JSONObject item:list) {
                // go over all the json fields
                for (String key : list_columns) {
                    try {
                        Object val = item.get(key);
                        if (merged_item.has(key)){
                            Object item_val = merged_item.get(key);
                            if (val instanceof Integer) {
                                int new_value = (int) val + (int) item_val;
                                merged_item.put(key, new_value);
                            } else if (val instanceof String) {
                                if (item_val.toString().toLowerCase().contains(((String) val).toLowerCase())) {
                                } else {
                                    String new_value = item_val + ", " + val;
                                    merged_item.put(key, new_value);
                                }
                            }
                        } else {
                            merged_item.put(key, val);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // average the integer values
            for (String key : list_columns) {
                try {
                    if (merged_item.has(key)) {
                        Object item_val = merged_item.get(key);
                        if (item_val instanceof Integer) {
                            int new_value = (int) item_val / list.size();
                            merged_item.put(key, new_value);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            pivot_items.add(merged_item);
        }
        // create new ids
        int id_counter = 0;
        for (JSONObject item: pivot_items){
            try {
                if (item.has("id")){
                    item.put("id", id_counter);
                    id_counter++;
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return pivot_items;
    }

}