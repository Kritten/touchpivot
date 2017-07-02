package de.uni_weimar.touchpivot;


import android.support.v4.app.FragmentActivity;

import android.os.Bundle;

import android.widget.ListView;

import android.widget.Toast;


import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenu;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem;
import com.touchmenotapps.widget.radialmenu.semicircularmenu.SemiCircularRadialMenuItem.OnSemiCircularRadialMenuPressed;


public class MainActivity extends FragmentActivity{
    private DataManager dataManager = null;
    private ListView dataTable = null;

    private SemiCircularRadialMenu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (ListView) findViewById(R.id.dataTable);
        // create the data manager (loads the data and displays it)
        dataManager = new DataManager(this);

        initDataSelectionMenu(4);
    }

    private void initDataSelectionMenu(Integer col_count) {
        mMenu = (SemiCircularRadialMenu) findViewById(R.id.radial_menu);
        for (int i = 0; i < col_count; i++) {
            String name = "Col_"+ Integer.toString(i);
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
