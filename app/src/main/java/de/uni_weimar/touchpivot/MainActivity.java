package de.uni_weimar.touchpivot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private DataManager dataManager = null;
    private GraphManager graphManger = null;
    private ListView dataTable = null;

    private RelativeLayout dataView;
    private SemiCircularRadialMenu mMenu;
    private Button fanMenuButton;
    private boolean fanActivated = false;

    ArrayList<String> columnHeaders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        dataTable = (ListView) findViewById(R.id.dataTable);
//        create the data manager (loads the data)
        dataManager = new DataManager(this);
//        create the graph manager displays the data)
        graphManger = new GraphManager(this, dataManager);
        columnHeaders = dataManager.getColumns();

        // init fan menu
        dataView = (RelativeLayout) findViewById(R.id.data_view);
        fanMenuButton = (Button) findViewById(R.id.fan_menu_button);
        fanMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFanMenu();
            }
        });
        initFanMenu(columnHeaders);

        System.out.println(dataManager.getColumns());

//        graphManger.renderGraphBottom(entries);
//        graphManger.renderGraphBottom(entries, dataManager.getColumns());

    }

    private void setPivotColumn(String column) {
        ArrayList<String> listPivotColumn = dataManager.getColumn(column);
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

    private void highlightHover(String column) {
        int row_count = dataTable.getChildCount();
        for (int i = 0; i < row_count; i++){
            int idx = columnHeaders.indexOf(column);
            LinearLayout row = (LinearLayout) dataTable.getChildAt(i);
            int col_count = row.getChildCount();
            for (int j = 0; j < col_count; j++){
                if (j == idx) {
                    row.getChildAt(j).setBackgroundColor(Color.WHITE);
                } else {
                    row.getChildAt(j).setBackgroundColor(getResources().getColor(R.color.highlight_grey));
                }
            }
        }
    }

    private void initFanMenu(ArrayList<String> columns) {
//      mMenu = new SemiCircularRadialMenu(this);
        mMenu = (SemiCircularRadialMenu) findViewById(R.id.radial_menu);
        mMenu.setShowMenuText(true);


        int col_count = columns.size();
        for (int i = 0; i < col_count; i++) {
            String name = columns.get(i);

            final SemiCircularRadialMenuItem button = new SemiCircularRadialMenuItem(name, getResources().getDrawable(R.drawable.ic_rep), name);
            button.setOnSemiCircularRadialMenuPressed(new OnSemiCircularRadialMenuPressed(name));

            button.setOnSemiCircularRadialMenuHovered(new OnSemiCircularRadialMenuHovered(name));
            System.out.println("ID " + button.getMenuID());
            mMenu.addMenuItem(button.getMenuID(), button);
        }
        // adapted sample code from https://github.com/strider2023/Radial-Menu-Widget-Android (1.7.2017)
    }

    private void showFanMenu() {
        if (fanActivated){
            mMenu.setVisibility(View.GONE);
            mMenu.dismissMenu();
            fanActivated = false;
        } else {
            mMenu.setVisibility(View.VISIBLE);
            mMenu.showMenu();
            fanActivated = true;
        }
    }


    public class OnSemiCircularRadialMenuPressed implements SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed
    {

        String name;
        public OnSemiCircularRadialMenuPressed(String n) {
            this.name = n;
        }

        @Override
        public void onMenuItemPressed() {
            highlightHover(name);
            setPivotColumn(name);
        }
    }

    public class OnSemiCircularRadialMenuHovered implements SemiCircularRadialMenuItem.OnSemiCircularRadialMenuHovered
    {

        String name;
        public OnSemiCircularRadialMenuHovered(String n) {
            this.name = n;
        }

        @Override
        public void onMenuItemHovered() {
            setPivotColumn(name);
            highlightHover(name);
//            Toast.makeText(MainActivity.this, "hov "+this.name, Toast.LENGTH_SHORT).show();
        }
    }

}
