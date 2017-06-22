package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by micro on 22.06.2017.
 */

public class DataManager {
    private Resources resources;
    private Activity activity = null;
    private ArrayList<JSONObject> list_data_items = new ArrayList<>();

    public DataManager(MainActivity mainActivity) {
        activity = mainActivity;
        resources = activity.getResources();
        init();
    }

    public void add_item(JSONObject obj) {
        list_data_items.add(obj);
    }
    public int size() {
        return list_data_items.size();
    }
    public ArrayList<JSONObject> get_items() {
        return list_data_items;
    }

    private void init() {
        this.load_data_items();
        ListView dataTable  = (ListView)activity.findViewById(R.id.dataTable);
        DataArrayAdapter adapter = new DataArrayAdapter(activity, get_items());
        dataTable.setAdapter(adapter);
    }


    private void load_data_items() {
        try {
            System.out.println(new InputStreamReader(resources.openRawResource(R.raw.file_short)));
            BufferedReader br = new BufferedReader( new InputStreamReader(resources.openRawResource(R.raw.file_short)));
            for(String line; (line = br.readLine()) != null; ) {
                JSONObject obj = new JSONObject(line);
                this.add_item(obj);
                this.add_item(obj);
                this.add_item(obj);
                this.add_item(obj);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DataArrayAdapter extends ArrayAdapter<JSONObject> {

        public DataArrayAdapter(Context context, ArrayList<JSONObject> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            JSONObject dataItem = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.data_item, parent, false);
            }
            // Lookup view for data population
            TextView textViewID = (TextView) convertView.findViewById(R.id.textViewID);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            TextView textViewCount = (TextView) convertView.findViewById(R.id.textViewCount);
//            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            try {
                textViewID.setText(dataItem.getString("id"));
                textViewName.setText(dataItem.getString("name"));
                textViewCount.setText(dataItem.getString("count_of_something"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}

//        try {
//            FileOutputStream fileout=openFileOutput("mytextfile.txt", MODE_PRIVATE);
//            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
//            outputWriter.write("kritten");
//            outputWriter.close();
//
//            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
//                    Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }