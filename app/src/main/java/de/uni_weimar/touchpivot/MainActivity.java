package de.uni_weimar.touchpivot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private DataManager dataManager = null;
    private GraphManager graphManger = null;
    private ListView dataTable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (ListView)findViewById(R.id.dataTable);
//        create the data manager (loads the data and displays it)
        dataManager = new DataManager(this);
        graphManger = new GraphManager(this);

        graphManger.showStats(dataManager);

//        List<List<Entry>> data = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();
        int counter = 0;
        for(String column: dataManager.getColumns()) {
            Set<String> set = new HashSet<>();
            for(String value: dataManager.getColumn(column)) {
                set.add(value);
            }
            entries.add(new BarEntry(counter, set.size()));
            counter += 1;
        }

//        graphManger.renderGraphBottom(entries);
        graphManger.renderGraphBottom(entries, dataManager.getColumns());
    }
}
