package us.fecytalk.trycambaru;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity {
    private Button tombolNRP,absenNRP,predictNRP,sendSignature,trainSignature,predictSignature,signinNRP;
    public  String nrp_mhs,pass_mhs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Bundle bundle = getIntent().getExtras();
        nrp_mhs = bundle.getString("NRP");
        pass_mhs = bundle.getString("Pass");
        tombolNRP = findViewById(R.id.tombolNRP);
        absenNRP = findViewById(R.id.absenNRP);
        predictNRP = findViewById(R.id.predictNRP);
        sendSignature = findViewById(R.id.btnSendSignature);
        trainSignature = findViewById(R.id.btnTrainSignature);
        predictSignature = findViewById(R.id.btnPredictTTD);
        signinNRP = findViewById(R.id.signinNRP);
        checkPermission();

        tombolNRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InputActivity.this,DataShowActivity.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });
        absenNRP.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent i = new Intent(InputActivity.this,TrainActivity.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });

        predictNRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InputActivity.this,CamLoginActivity.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });

        sendSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InputActivity.this,SendSignature.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });

        trainSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InputActivity.this,SignatureTrain.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });

        predictSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InputActivity.this,PredictSignature.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });

        signinNRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InputActivity.this,SignInActivity.class);
                i.putExtra("NRP",nrp_mhs);
                i.putExtra("Pass",pass_mhs);
                startActivity(i);
            }
        });

    }
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(InputActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(InputActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(InputActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        7);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(InputActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(InputActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(InputActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        7);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
}
