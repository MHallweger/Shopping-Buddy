package com.moco.marvin.shoppingbuddy.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Marvin.H on 14.06.18.
 */

public class SqaureImageView extends AppCompatImageView {

    public SqaureImageView(Context context) {
        super(context);
    }

    public SqaureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SqaureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * The width will be exactly like the height.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
