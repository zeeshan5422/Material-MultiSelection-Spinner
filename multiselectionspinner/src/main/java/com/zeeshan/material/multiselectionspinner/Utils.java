package com.zeeshan.material.multiselectionspinner;

import android.content.Context;

public class Utils {

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
