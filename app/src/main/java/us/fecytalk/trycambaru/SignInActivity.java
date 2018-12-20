package us.fecytalk.trycambaru;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.Result;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission_group.CAMERA;

public class SignInActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private int flagCam = 0;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Checking for permission", Toast.LENGTH_LONG).show();
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void qrScanner(View view) {
        scannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />
        setContentView(scannerView);
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.<br />
        scannerView.startCamera();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        finish();
////        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
////        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
////            if (checkPermission()) {
////                if(scannerView == null) {
////                    scannerView = new ZXingScannerView(this);
////                    setContentView(scannerView);
////                }
////                scannerView.setResultHandler(this);
////                scannerView.startCamera();
////            } else {
////                requestPermission();
////            }
////        }
//    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        scannerView.stopCamera();
//    }

    @Override
    public void handleResult(final Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(SignInActivity.this);
            }
        });
        builder.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.stopCamera();
                Bundle bundle = getIntent().getExtras();
                String nrp = bundle.getString("NRP");
                String pass = bundle.getString("Pass");
                Intent i = new Intent(SignInActivity.this,LogInActivity.class);
                String[] temp = result.getText().split(",");
                String lat = temp[0];
                String lon = temp[1];
                String idKelas = temp[2];
                i.putExtra("NRP",nrp);
                i.putExtra("Pass",pass);
                i.putExtra("Lat",lat);
                i.putExtra("Lon",lon);
                i.putExtra("IDKelas",idKelas);
                startActivity(i);
            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}
