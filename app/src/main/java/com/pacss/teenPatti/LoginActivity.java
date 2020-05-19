package com.pacss.teenPatti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.google.android.material.button.MaterialButton;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.dataHandler.UserHandler;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton guestLogin, fbLogin, gLogin;
    private SharedPreferences sharedPreferences;
    private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("LOGIN_DATA", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final AppDialogHandler customDialog = new AppDialogHandler(LoginActivity.this);
        customDialog.setCustomDialog(R.layout.login_name_dialog, AppDialogHandler.WRAP_CONTENT);
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
                guestUsernameInput.setText(null);
                LoginActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guestUsernameInput.getText().toString().equals("")) {
                    guestUsernameInput.setError("");
                } else {
                    UserHandler User = UserHandler.UserHandlerReference();
                    User.setUser(guestUsernameInput.getText().toString(), 0, "Guest", 0, 0, 0);
                    firebaseManager.guestUserLogin(User);
                    customDialog.clear();
                    customDialog.setCustomDialog(R.layout.loader_view, AppDialogHandler.WRAP_CONTENT);
                    customDialog.showDialog();
                    Log.d(LoginActivity.class.getSimpleName(), FirebaseManager.UserID);
                    editor.putString("USER_ID", FirebaseManager.UserID);
                    editor.putBoolean("LOG_STATUS", true);
                    editor.apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            customDialog.clear();
                            finish();
                        }
                    }, 4500);
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
