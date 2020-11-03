package com.libs.dialog;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

/**
 * 用于在服务、广播等不可见类中弹对话框
 *
 * @author Created by qlang on 2017/1/6.
 */
public class DialogActivity extends Activity {
    private static DialogContextProvider mContextProvider;

    public static void show(Application app, DialogContextProvider contextProvider) {
        mContextProvider = contextProvider;
        Intent intent = new Intent(app.getApplicationContext(), DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        app.getApplicationContext().startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContextProvider.onContext(this);
    }

    public interface DialogContextProvider {
        void onContext(Activity activity);
    }
}
