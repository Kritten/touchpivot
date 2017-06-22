package de.uni_weimar.touchpivot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DataManager dataManager = new DataManager();
    private TableLayout dataTable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (TableLayout)findViewById(R.id.dataTable);
        fill_table();
    }

    private void fill_table() {
        load_data_items();
        for(JSONObject item: dataManager.get_items()) {
            TableRow newTableRow = new TableRow(this);
            TextView textview = new TextView(this);
            try {
                textview.setText(item.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
                textview.setText("EXCEPTION OCCURRED");
            }
            newTableRow.addView(textview);
            dataTable.addView(newTableRow);
        }
    }

    private void load_data_items() {
        try {
            FileInputStream fileIn = openFileInput("file_short.ldjson");
            BufferedReader br = new BufferedReader( new InputStreamReader(fileIn));
            for(String line; (line = br.readLine()) != null; ) {
                JSONObject obj = new JSONObject(line);
                dataManager.add_item(obj);
                dataManager.add_item(obj);
                dataManager.add_item(obj);
                dataManager.add_item(obj);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
