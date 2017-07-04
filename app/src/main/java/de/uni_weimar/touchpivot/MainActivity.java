package de.uni_weimar.touchpivot;


import android.support.v4.app.FragmentActivity;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;


import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




public class MainActivity extends AppCompatActivity{
    private DataManager dataManager = null;
    private GraphManager graphManger = null;
    private ListView dataTable = null;

    private RelativeLayout dataView;
    private SemiCircularRadialMenu mMenu;
    private Button fanMenuButton;
    private boolean fanActivated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (ListView) findViewById(R.id.dataTable);
        // create the data manager (loads the data and displays it)
        dataManager = new DataManager(this);

        // init fan menu
        dataView = (RelativeLayout) findViewById(R.id.data_view);
        fanMenuButton = (Button) findViewById(R.id.fan_menu_button);
        fanMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFanMenu();
            }
        });
        initFanMenu(dataManager.getColumns());

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

    private void initFanMenu(ArrayList<String> columns) {
//      mMenu = new SemiCircularRadialMenu(this);
        mMenu = (SemiCircularRadialMenu) findViewById(R.id.radial_menu);
//        mMenu.setShowMenuText(true);


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
            Toast.makeText(MainActivity.this, this.name, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MainActivity.this, "hov "+this.name, Toast.LENGTH_SHORT).show();
        }
    }

}
