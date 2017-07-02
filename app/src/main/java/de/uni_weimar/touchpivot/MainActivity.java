package de.uni_weimar.touchpivot;


import android.support.v4.app.FragmentActivity;

import android.os.Bundle;

import android.widget.ListView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed;


public class MainActivity extends FragmentActivity{
    private DataManager dataManager = null;
    private GraphManager graphManger = null;
    private ListView dataTable = null;

    private SemiCircularRadialMenu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (ListView) findViewById(R.id.dataTable);
        // create the data manager (loads the data and displays it)
        dataManager = new DataManager(this);

        // init fan menu
        initDataSelectionMenu(dataManager.getColumns());

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

    private void initDataSelectionMenu(ArrayList<String> columns) {
        mMenu = (SemiCircularRadialMenu) findViewById(R.id.radial_menu);
        int col_count = columns.size();
        for (int i = 0; i < col_count; i++) {
            String name = columns.get(i);
            SemiCircularRadialMenuItem button = new SemiCircularRadialMenuItem(name, getResources().getDrawable(R.drawable.ic_rep), name);
            button.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed() {
                @Override
                public void onMenuItemPressed() {
                    Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_LONG).show();
                }
            });
            System.out.println("ID " + button.getMenuID());
            mMenu.addMenuItem(button.getMenuID(), button);
        }
    }

}
