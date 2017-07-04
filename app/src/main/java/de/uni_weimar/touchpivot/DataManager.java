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
import java.util.List;

/**
 * Created by micro on 22.06.2017.
 */

public class DataManager {
    private Resources resources;
    private Activity activity = null;
    private ArrayList<JSONObject> list_data_items = new ArrayList<>();
    private ArrayList<String> list_columns = new ArrayList<>();

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
        This function returrns the current size of the list
     */
    public int getCountItems() {
        return list_data_items.size();
    }
    /*
        This function returns all items
     */
    public ArrayList<JSONObject> get_items() {
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
                this.addItem(obj);
                this.addItem(obj);
                this.addItem(obj);
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
}