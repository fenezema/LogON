package us.fecytalk.trycambaru;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity {
    private String nomorInduk, pass,lat,lon,idKelas;
    private TextView statusKondisi;
    private int flaagg = 1;
    private CameraView cameraView;
    FusedLocationProviderClient mFusedLocationClient;
    private Double lat2, long2;
    private float distanceInMeters=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        statusKondisi = findViewById(R.id.statusKondisi1);
        statusKondisi.setText("SignIN");
        cameraView = (CameraView) findViewById(R.id.camlogin1);
        Bundle extras = getIntent().getExtras();
        nomorInduk = extras.getString("NRP");
        pass = extras.getString("Pass");
        lat = extras.getString("Lat");
        lon = extras.getString("Lon");
        idKelas = extras.getString("IDKelas");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Logic to handle location object
//                            Toast.makeText(StepTwo.this, "lat"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                    lat2 = location.getLatitude();
                    long2 = location.getLongitude();
                    Location loc1 = new Location("");
                    loc1.setLatitude(Double.valueOf(lat));
                    loc1.setLongitude(Double.valueOf(lon));

                    Location loc2 = new Location("");
                    loc2.setLatitude(lat2);
                    loc2.setLongitude(long2);

                    distanceInMeters = loc1.distanceTo(loc2);
                    Toast.makeText(LogInActivity.this, String.valueOf(lat2) + "|" + String.valueOf(long2)+"|"+String.valueOf(distanceInMeters)+"m", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LogInActivity.this, "Turn On GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Timer timer = new Timer();
        timer.schedule(new AutoLoc(),0,5000);
        CameraKitEventListener cameraKitEventListener = new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                byte[] picture = cameraKitImage.getJpeg();
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                result = Bitmap.createScaledBitmap(result, 96, 96, true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
                final ApiInterface apiPredict = Server.getclient().create(ApiInterface.class); //absen
                Log.d("test", "onImage: " + myBase64Image);
                JSONObject paramObject = new JSONObject();
                if (distanceInMeters < 100){
                    Call<ResponseApi> kirimsignin = apiPredict.signin(nomorInduk,pass,","+myBase64Image,String.valueOf(lat2),String.valueOf(long2),idKelas); //absen
                    kirimsignin.enqueue(new Callback<ResponseApi>() {
                        @Override
                        public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                            final TextView respon = findViewById(R.id.responAPInya1);
                            TextView validationAPI = findViewById(R.id.validationAPInya1);
                            respon.setText("Jarak diketahui : "+String.valueOf(distanceInMeters)+"m");
                            validationAPI.setText(response.body().getValidation());
                            Toast.makeText(LogInActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                            builder.setTitle("Result");
                            final String[] response_login = response.body().getValidation().split(",");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (response_login[0].equals("ACCEPTED")){
                                        Intent toSignatureSignin = new Intent(LogInActivity.this,SignatureSignin.class);
                                        toSignatureSignin.putExtra("NRP",nomorInduk);
                                        toSignatureSignin.putExtra("Pass",pass);
                                        toSignatureSignin.putExtra("IDKelas",idKelas);
                                        toSignatureSignin.putExtra("Lat",String.valueOf(lat2));
                                        toSignatureSignin.putExtra("Lon",String.valueOf(long2));
                                        startActivity(toSignatureSignin);
                                    }
                                    else{
                                        dialog.dismiss();
                                    }

                                }
                            });
                            builder.setMessage("Jarak diketahui : "+String.valueOf(distanceInMeters)+"m"+"\n"+response.body().getValidation());
                            AlertDialog alert1 = builder.create();
                            alert1.show();
                        }

                        @Override
                        public void onFailure(Call<ResponseApi> call, Throwable t) {
                            Toast.makeText(LogInActivity.this, "Failed. Please Retry", Toast.LENGTH_SHORT).show();
                        }
                    });
                    FileOutputStream outStream = null;
                    try {
                        File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdCard.getAbsolutePath() + "/camtest");
                        dir.mkdirs();

                        String fileName = String.format("%s.jpg", nomorInduk);
                        File outFile = new File(dir, fileName);

                        outStream = new FileOutputStream(outFile);
                        result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        Toast.makeText(LogInActivity.this, "Image" + " Captured", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onPictureTaken - wrote to " + outFile.getAbsolutePath());

//                    refreshGallery(outFile);
                    } catch (FileNotFoundException e) {
//                    print("FNF");
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Toast.makeText(LogInActivity.this, "Unsaved. Please Retry", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LogInActivity.this, "You're too far from your class. Are you sure you're actually there?", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        };

        cameraView.addCameraKitListener(cameraKitEventListener);
        Button lala = (Button) findViewById(R.id.cambtn1);
        lala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.captureImage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

//    @Override
//    protected void onPause() {
//        cameraView.stop();
//        Intent i = new Intent(LogInActivity.this,SignInActivity.class);
//        i.putExtra("NRP",nomorInduk);
//        i.putExtra("Pass",pass);
//        startActivity(i);
//        super.onPause();
//    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
    public void switchCam1(View v){
        if (flaagg==1){
            flaagg=0;
            cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        }
        else if(flaagg==0){
            flaagg=1;
            cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        }

    }
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(LogInActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LogInActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LogInActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        7);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(LogInActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LogInActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LogInActivity.this,
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
    class AutoLoc extends TimerTask {
        public void run(){
            if (ActivityCompat.checkSelfPermission(LogInActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LogInActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(LogInActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Logic to handle location object
//                            Toast.makeText(StepTwo.this, "lat"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                        lat2 = location.getLatitude();
                        long2 = location.getLongitude();
                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.valueOf(lat));
                        loc1.setLongitude(Double.valueOf(lon));

                        Location loc2 = new Location("");
                        loc2.setLatitude(lat2);
                        loc2.setLongitude(long2);

                        distanceInMeters = loc1.distanceTo(loc2);
                        Toast.makeText(LogInActivity.this, String.valueOf(lat2) + "|" + String.valueOf(long2)+"|"+String.valueOf(distanceInMeters)+"m", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(LogInActivity.this, "Turn On GPS", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
