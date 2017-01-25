package com.ateam.digitaludharbuyer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Scanner extends AppCompatActivity {

    ImageView imageView;
    Button button;
    public final static int QRcodeWidth = 500;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
//        button = (Button) findViewById(R.id.home);
//
//        // Capture button clicks
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//
//                // Start NewActivity.class
//                Intent myIntent = new Intent(Scanner.this, MainActivity.class);
//                startActivity(myIntent);
//            }
//        });

        imageView = (ImageView) findViewById(R.id.imageView);


        Intent intent = getIntent();
//        Log.w("bundle","ddd");
//        Bundle bundle = intent.getExtras();
//        Log.w("bundle",bundle.toString());
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Object value = bundle.get(key);
//                Log.d("intentextra", String.format("%s %s (%s)", key,
//                        value.toString(), value.getClass().getName()));
//            }
//        }
//        Log.w("sumit", intent.getExtras());
        String message = intent.getStringExtra("message");
        Log.d("nicks", message);
        try {
            bitmap = TextToImageEncode(message);

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(bitmap);


    }


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @Override
    protected void onStop(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent MainactivityIntent = new Intent(Scanner.this,ThreeTabsActivity.class);
        startActivity(MainactivityIntent);
    }
}

