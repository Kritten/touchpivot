package de.uni_weimar.touchpivot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
//        create the data manager (loads the data)
        dataManager = new DataManager(this);
//        create the graph manager displays the data)
        graphManger = new GraphManager(this, dataManager);

        System.out.println(dataManager.getColumns());

        ArrayList<String> listPivotColumn = dataManager.getColumn("annotatorA");
        List<Entry> entries = new ArrayList<>();

         Map<String, Integer> map = new HashMap<>();
        for(String value: listPivotColumn) {
            if(map.containsKey(value)) {
                map.put(value, map.get(value) + 1);
            } else {
                map.put(value, 1);
            }
        }

        System.out.println(map);
        int counter = 0;
        List<String> labels = new ArrayList<>();
        for(Map.Entry<String, Integer> value: map.entrySet()) {
            labels.add(value.getKey());
            entries.add(new Entry(counter, value.getValue()));
            counter += 1;
        }
        graphManger.addGraph(entries, BarChart.class, GraphManager.Location.Bottom, labels, true);

    }
}
