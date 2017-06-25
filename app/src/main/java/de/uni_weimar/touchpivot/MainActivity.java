package de.uni_weimar.touchpivot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ogaclejapan.arclayout.R.attr.arc_angle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Toast toast = null;
    private DataManager dataManager = null;
    private ListView dataTable = null;
    private View fab;
    private View menuLayout;
    private ArcLayout arcLayout;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTable = (ListView)findViewById(R.id.dataTable);
//        create the data manager (loads the data and displays it)
        dataManager = new DataManager(this);


        fab = findViewById(R.id.fab);
        menuLayout = findViewById(R.id.menu_layout);
        btn = (Button) findViewById(R.id.arc_item);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);

        int col_count = 4;

        for (int i = 0; i < col_count - 1; i++) {
            Button b = (Button) getLayoutInflater().inflate(R.layout.arc_button, null);
            // FrameLayout.LayoutParams lp =
            //String s = b.getLayoutParams();
            System.out.println(b);
            b.setText(Integer.toString(i));
            //int angle = ((90 - 20) / col_count) * i + 10;
            //b.setLayoutParams(new ArcLayout.LayoutParams(arc_angle, angle));
            b.setOnClickListener(this);
            arcLayout.addView(b);
            System.out.println(Integer.toString(i));
        }

        View a = arcLayout.getChildAt(0);

        /*
        private Button addMoreButtons(){
            FloatingActionButton newFab = LayoutInflater
                    .from(this)
                    .inflate(R.layout.button, null);

            myLayout.addView(newFab);
        }
        LayoutInflater inflater = (ViewInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View b =  inflater.inflate(R.id.arc_item, null);
        View view = LayoutInflater.from(this).inflate(R.id.arc_item, null);
        myLayout.addView(view);


        for (int i = 0; i < col_count; i++) {
            Button myButton = new Button(this);
            myButton.setBackgroundResource(R.drawable.path_white_oval);
            myButton.setHeight(36);
            myButton.setWidth(36);
            myButton.setText( Integer.toString(i));
            myButton.setBackgroundColor(Color.YELLOW);
            int angle = ((90 - 20) / col_count) * i + 10;
            myButton.setLayoutParams(new ArcLayout.LayoutParams(arc_angle, angle));
            arcLayout.addView(myButton);

        }
        */

        fab.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onFabClick(v);
            return;
        }

        if (v instanceof Button) {
            showToast((Button) v);
        }

    }
    private void showToast(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Clicked: " + btn.getText();
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();

    }

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            Button v = (Button) arcLayout.getChildAt(i);

            System.out.println(v.getText());
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
            System.out.println("hello "+i);
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }
}
