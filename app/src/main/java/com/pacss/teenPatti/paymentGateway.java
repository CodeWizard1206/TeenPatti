package com.pacss.teenPatti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.dataHandler.UserHandler;
import java.util.ArrayList;
import java.util.Objects;

public class paymentGateway extends AppCompatActivity {

    private UserHandler User = UserHandler.UserHandlerReference();
    private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
    private AppDialogHandler appDialogHandler;
    private TextView payStatusMsg;
    private AppCompatImageView payStatusImage;
    private static String chipAmount;
    private static String moneyAmount;
    private final String TAG = paymentGateway.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_gateway);

        incomingIntent();

        appDialogHandler = new AppDialogHandler(paymentGateway.this);
        appDialogHandler.setCustomDialog(R.layout.pay_status, AppDialogHandler.WRAP_CONTENT);
        payStatusImage = appDialogHandler.getCustomDialog().findViewById(R.id.statusIcon);
        payStatusMsg = appDialogHandler.getCustomDialog().findViewById(R.id.statusMsg);

        TextView userName = findViewById(R.id.userName);
        MaterialCardView backCard = findViewById(R.id.backCard);
        MaterialButton payButton = findViewById(R.id.payButton);
        AppCompatTextView moneyValue = findViewById(R.id.moneyValue);
        AppCompatTextView chipValue = findViewById(R.id.chipAmount);

        userName.setText(User.getUserName());
        moneyValue.setText(moneyAmount);
        chipValue.setText(chipAmount);

        backCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUPI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void incomingIntent() {
        if(getIntent().hasExtra("CHIPS") && getIntent().hasExtra("MONEY")) {
            chipAmount = getIntent().getStringExtra("CHIPS");
            moneyAmount = getIntent().getStringExtra("MONEY");
        }
    }

    private void payUsingUPI() {
        Log.v(TAG, "UPI Payment Request, REQ_UPI_PAY:SENT");
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", firebaseManager.getPayeeAccount())
                .appendQueryParameter("pn", firebaseManager.getPayeeName())
                .appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", "Coin Buying Transaction")
                .appendQueryParameter("am", moneyAmount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent Chooser = new Intent(Intent.ACTION_VIEW).setData(uri);
        Intent UpiAppChooser = Intent.createChooser(Chooser, "Pay using");
        if (null != UpiAppChooser.resolveActivity(getPackageManager())) {
            int UPI_PAYMENT = 0;
            startActivityForResult(UpiAppChooser, UPI_PAYMENT);
        } else {
            Toast.makeText(paymentGateway.this, "No UPI App Installed, Please Install One to Continue & Try Again!!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "UPI Pay Request Received, UPI_PAY_REQ: "+resultCode);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                assert data != null;
                if (Objects.requireNonNull(data.getStringExtra("response")).contains("SUCCESS") || Objects.requireNonNull(data.getStringExtra("response")).contains("Success") || Objects.requireNonNull(data.getStringExtra("response")).contains("success"))  {
                    String transactionResponse = data.getStringExtra("response");
                    Log.v(TAG,"Transaction Result Captured, TRX_RES: "+transactionResponse);
                    ArrayList<String> trxDataList = new ArrayList<>();
                    trxDataList.add(transactionResponse);
                    upiPaymentDataOperation(trxDataList);
                } else {
                    Log.e(TAG,"Null Transaction Result Captured, TRX_RES: NULL");
                    ArrayList<String> trxDataList = new ArrayList<>();
                    trxDataList.add("NOTHING");
                    upiPaymentDataOperation(trxDataList);
                }
            }
        }, 1000);

    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (firebaseManager.isConnectionAvailable(paymentGateway.this)) {

            String payResponse = data.get(0);
            String paymentCancel = "";
            String payStatus = "";
            String approvalRefNo = "";
            String[] response = payResponse.split("&");

            Log.v(TAG, "UPI Payment Operation Response, UPI_PAY_RESP: "+payResponse);

            for (String s : response) {
                String[] equalStr = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        payStatus = equalStr[1].toUpperCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment Cancelled By User";
                }
            }

            if (payStatus.equals("SUCCESS")) {
                Toast.makeText(this, "Transaction successful.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "PAYMENT SUCCESSFUL, PAY_SUCCESS_RESP : "+approvalRefNo);
                User.setTotalTokens(User.getTotalTokens() + Integer.parseInt(chipAmount));
                firebaseManager.updateTokens();
                paySuccess();
            }
            else if("Payment Cancelled By User".equals(paymentCancel)) {
                Toast.makeText(this, "Transaction Failed, Please Try Again!!!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Payment Cancelled On User Request, USER_CANCEL_RESP : "+approvalRefNo);
                payFail();
            }
            else {
                Toast.makeText(this, "Transaction Failed, Please Try Again!!!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Payment Failed : PAY_FAIL_RESP : "+approvalRefNo);
                payFail();
            }
        } else {
            Log.e(TAG, "Connectivity Issues Encountered!!!");
            Toast.makeText(this, "Internet connection is not available, please check and try again", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void paySuccess() {
        payStatusImage.setBackground(getDrawable(getResources().getIdentifier("pay_success", "drawable", getPackageName())));
        payStatusMsg.setText("Payment Successful");
        appDialogHandler.showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appDialogHandler.clear();
                finish();
            }
        }, 3000);
    }

    @SuppressLint("SetTextI18n")
    private void payFail() {
        payStatusImage.setBackground(getDrawable(getResources().getIdentifier("pay_fail", "drawable", getPackageName())));
        payStatusMsg.setText("Payment Unsuccessful!!!");
        appDialogHandler.showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appDialogHandler.clear();
                finish();
            }
        }, 3000);
    }
}
