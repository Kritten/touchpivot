package de.uni_weimar.touchpivot;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by micro on 22.06.2017.
 */

public class DataManager {
    private ArrayList<JSONObject> list_data_items = new ArrayList<>();

    public void add_item(JSONObject obj) {
        list_data_items.add(obj);
    }
    public int size() {
        return list_data_items.size();
    }
    public ArrayList<JSONObject> get_items() {
        return list_data_items;
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