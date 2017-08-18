package com.apptrust.wingtaxi.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.apptrust.wingtaxi.R;


/**
 * Created by rares on 01.08.2017.
 */

public class TaxiAddresRecyclerView  extends RecyclerView {
    public TaxiAddresRecyclerView(Context context) {
        super(context);
    }

    public TaxiAddresRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * <p>Adds a child view. If no layout parameters are already set on the child, the
     * default parameters for this ViewGroup are set on the child.</p>
     * <p>
     * <p><strong>Note:</strong> do not invoke this method from
     * {@link #draw(Canvas)}, {@link #onDraw(Canvas)},
     * {@link #dispatchDraw(Canvas)} or any related method.</p>
     *
     * @param child the child view to add
     * @see #generateDefaultLayoutParams()
     */
    @Override
    public void addView(View child) {
        //super.addView(child);

        LayoutInflater li = LayoutInflater.from(getContext());
        View cv = li.inflate(R.layout.custonview_plan, null);
        //((ViewGroup)child.getParent()).removeView(child);
        ((FrameLayout) cv.findViewById(R.id.addedView)).addView(child);
        super.addView(cv);
    }
}
