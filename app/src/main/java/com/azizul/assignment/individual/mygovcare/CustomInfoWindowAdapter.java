package com.azizul.assignment.individual.mygovcare;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){

        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.info_window_title);

        if(title != null && !title.equals("")){
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView) view.findViewById(R.id.info_window_snippet);

        if(snippet != null && !snippet.equals("")){
            tvSnippet.setText(snippet);
        }

        // --- Constrain width and allow text wrapping ---
        int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        int maxWidth = (int) (screenWidth * 0.75);
        tvTitle.setMaxWidth(maxWidth);
        tvSnippet.setMaxWidth(maxWidth);

        // --- Apply Theme Colors ---
        GradientDrawable background = (GradientDrawable) view.getBackground();
        // It'''s important to mutate the drawable so we don'''t modify the resource for all instances
        GradientDrawable mutableBackground = (GradientDrawable) background.mutate();

        // Get the background color from the current theme
        TypedValue backgroundValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.infoWindowBackground, backgroundValue, true);
        mutableBackground.setColor(backgroundValue.data);

        // Get the stroke color from the current theme
        TypedValue strokeValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.infoWindowStrokeColor, strokeValue, true);

        // The stroke width is 2dp, let'''s convert it to pixels
        int strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());
        mutableBackground.setStroke(strokeWidth, strokeValue.data);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
