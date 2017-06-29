package de.uni_weimar.touchpivot;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by micro on 29.06.2017.
 */

public class CustomClickListener implements View.OnClickListener {
    private final Activity activity;

    public CustomClickListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        final TextView srcView  = (TextView) activity.findViewById(R.id.textView);
        final RelativeLayout destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
        final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);

//        RelativeLayout parent =srcView (RelativeLayout) srcView.getParent();
//        parent.removeView();
        final TextView destView = new TextView(this.activity);
        destView.setText("to");
        int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);
//        lparams.setMargins(0,(int) (10 * activity.getResources().getDisplayMetrics().density),0,0);
        destView.setLayoutParams(lparams);
        destView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
        destContainer.addView(destView);
//                final TextView destView  = (TextView) activity.findViewById(R.id.destTextView);

//                final LinearLayout rootView  = (LinearLayout) findViewById(R.id.rootView);
        final ViewTreeObserver observer = destView.getViewTreeObserver();

        observer.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
        @Override
           public boolean onPreDraw() {
            srcView.setVisibility(View.GONE);
               observer.removeOnPreDrawListener( this );
               destView.setTranslationY(  getAbsY( srcView ) - getAbsY( destView ));
                rootView.getOverlay().add( destView );
               destView.animate().translationX( 0 ).translationY( 0 )
                       .setInterpolator( new DecelerateInterpolator( 2 ) )
                       .setDuration( 1000 )
                       .withEndAction( new Runnable() {
                           @Override
                           public void run() {
                               rootView.getOverlay().remove( destView );
                               destContainer.addView( destView );
                           }
                       } );
               return true;
           }
        }
        );
    }

    private float getAbsY( View view ) {
        if( view.getParent() == view.getRootView() ) {
            return view.getY();
        } else {
            return view.getY() + getAbsY( ( View )view.getParent() );
        }
    }
}
