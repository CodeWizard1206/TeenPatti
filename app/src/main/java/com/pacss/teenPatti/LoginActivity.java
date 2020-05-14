package com.pacss.teenPatti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.google.android.material.button.MaterialButton;
import com.pacss.teenPatti.dataHandler.UserHandler;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton guestLogin, fbLogin, gLogin;
    private String guestUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        final AppDialogHandler customDialog = new AppDialogHandler(LoginActivity.this);
        customDialog.setCustomDialog(R.layout.login_name_dialog);
        final AppCompatEditText guestUsernameInput = customDialog.getCustomDialog().findViewById(R.id.userNameInput);
        final MaterialButton done = customDialog.getCustomDialog().findViewById(R.id.acceptNameButton);
        final MaterialButton cancel = customDialog.getCustomDialog().findViewById(R.id.cancelButton);

        guestLogin = findViewById(R.id.loginBtn);
        fbLogin = findViewById(R.id.fbLoginBtn);
        gLogin = findViewById(R.id.gPlayLoginBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.clear();
                LoginActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guestUsernameInput.getText().toString().equals("")) {
                    guestUsernameInput.setError("");
                } else {
                    String userName = guestUsernameInput.getText().toString().replaceAll("\\s+", "").toLowerCase();
                    guestUsername = userName + "@Guest" + UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
                    UserHandler User = UserHandler.getUserHandlerReference();
                    User.setUser(guestUsernameInput.getText().toString(), guestUsername, "Admin");
                    User.setChipAmount(0);
                    customDialog.clear();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
                LoginActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.showDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
