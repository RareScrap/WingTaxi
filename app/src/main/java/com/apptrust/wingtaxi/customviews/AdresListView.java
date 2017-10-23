package com.apptrust.wingtaxi.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.apptrust.wingtaxi.R;

/**
 * Created by rares on 01.08.2017.
 */

public class AdresListView extends View {
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public AdresListView(Context context) {
        super(context);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);
    }
}
