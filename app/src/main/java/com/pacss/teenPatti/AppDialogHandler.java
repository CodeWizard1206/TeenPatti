package com.pacss.teenPatti;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.ActionBar;
import java.util.Objects;

class AppDialogHandler {
    private Dialog customDialog;
    static int WRAP_CONTENT = 0;
    static int MATCH_PARENT = 1;

    AppDialogHandler(Context context) {
        this.customDialog = new Dialog(context);
    }

    void setCustomDialog(int Layout, int layoutType) {
        customDialog.setContentView(Layout);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = customDialog.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.setGravity(Gravity.CENTER);
        customDialog.setCancelable(false);
        if (layoutType == 0) {
            window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
        } else if (layoutType == 1) {
            window.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT);
        }
    }

    void showDialog() {
        customDialog.show();
    }

    Dialog getCustomDialog() {
        return customDialog;
    }

    void clear() {
        customDialog.dismiss();
    }
}
