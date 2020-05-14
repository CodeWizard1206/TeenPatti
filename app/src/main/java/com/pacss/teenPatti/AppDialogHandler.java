package com.pacss.teenPatti;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import androidx.appcompat.app.ActionBar;

import java.util.Objects;

public class AppDialogHandler {
    private Dialog customDialog;

    public AppDialogHandler(Context context) {
        this.customDialog = new Dialog(context);
    }

    public void setCustomDialog(int Layout) {
        customDialog.setContentView(Layout);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = customDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        customDialog.setCancelable(false);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT);
    }

    public void showDialog() {
        customDialog.show();
    }

    public Dialog getCustomDialog() {
        return customDialog;
    }

    public void clear() {
        customDialog.dismiss();
    }
}
