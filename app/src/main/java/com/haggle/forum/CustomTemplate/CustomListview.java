package com.haggle.forum.CustomTemplate;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomListview extends ListView {
    private boolean blockLayoutChildren;

    public CustomListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListview(Context context) {
        super(context);
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
