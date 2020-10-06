package com.haggle.forum.CustomTemplate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

public class NonScrollableListView extends ListView {
    private boolean blockLayoutChildren;

    public NonScrollableListView(Context context) {
        super(context);
    }

    public NonScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
    @Override
    protected void layoutChildren() {
        if (!blockLayoutChildren) {
            super.layoutChildren();
        }
    }

    public void setBlockLayoutChildren(boolean blockLayoutChildren) {
        this.blockLayoutChildren = blockLayoutChildren;
    }
}
