package com.takeiteasy.materialexpansionpanel.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.takeiteasy.materialexpansionpanel.R;

public class MaxHeightNestedScrollView extends NestedScrollView {

    private static final int DEFAULT_HEIGHT = 200;

    private int maxHeight;

    public MaxHeightNestedScrollView(Context context) {
        super(context);
    }

    public MaxHeightNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public MaxHeightNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightNestedScrollView);
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightNestedScrollView_maxHeight, DEFAULT_HEIGHT);

            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
