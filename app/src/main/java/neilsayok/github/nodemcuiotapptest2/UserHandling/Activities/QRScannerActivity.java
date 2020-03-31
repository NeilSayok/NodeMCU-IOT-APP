package neilsayok.github.nodemcuiotapptest2.UserHandling.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import neilsayok.github.nodemcuiotapptest2.R;

public class QRScannerActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    static final private int ACTIVITY_CODE = 2;
    static final private String INTENT_STR = "QR_CODE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);


        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        final QRScannerActivity activity = this;
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent();
                        intent.putExtra(INTENT_STR,result.getText());
                        setResult(ACTIVITY_CODE,intent);
                        finish();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent();
        intent.putExtra(INTENT_STR,"XXX");
        setResult(ACTIVITY_CODE,intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public static String getIntentStr() {
        return INTENT_STR;
    }

    public static int getActivityCode() {
        return ACTIVITY_CODE;
    }
}

