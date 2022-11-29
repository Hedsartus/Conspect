package com.filenko.conspect.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentStart {

    public static void intentStartWithIntParam(Context context, Class<?> cls, String nameParm, int parm) {
        Intent intent = new Intent(context, cls);
        Bundle b = new Bundle();
        b.putInt(nameParm, parm);
        intent.putExtras(b);
        context.startActivity(intent);

    }
}
