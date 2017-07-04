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

    public CustomClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        final View srcView  = view;
//        final TextView srcView  = (TextView) activity.findViewById(R.id.textView);
        final RelativeLayout destContainer  = (RelativeLayout) activity.findViewById(R.id.layout_graph_top);
        final LinearLayout rootView  = (LinearLayout) activity.findViewById(R.id.rootView);

        final float position_y = getAbsY( srcView );

        RelativeLayout parent = (RelativeLayout) srcView.getParent();
        parent.removeView(srcView);

        int dpi = (int) (50 * activity.getResources().getDisplayMetrics().density);
//        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(dpi, dpi);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        lparams.setMargins(0,(int) (10 * activity.getResources().getDisplayMetrics().density),0,0);
        srcView.setLayoutParams(params);
//        destView.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
        destContainer.addView(srcView);

        final ViewTreeObserver observer = srcView.getViewTreeObserver();

        observer.addOnPreDrawListener( new ViewTreeObserver.OnPreDrawListener() {
        @Override
           public boolean onPreDraw() {
               observer.removeOnPreDrawListener( this );
            srcView.setTranslationY(  position_y - getAbsY( srcView ));
                rootView.getOverlay().add( srcView );
            srcView.animate().translationX( 0 ).translationY( 0 )
                       .setInterpolator( new DecelerateInterpolator( 2 ) )
                       .setDuration( 1000 )
                       .withEndAction( new Runnable() {
                           @Override
                           public void run() {
                               rootView.getOverlay().remove( srcView );
                               destContainer.addView( srcView );
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
