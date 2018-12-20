package us.fecytalk.trycambaru;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CamLoginActivity extends AppCompatActivity {
    private CameraView cameraView;
    private String nomorInduk="";
    private String pass="";
    private int flaagg = 1;
    private TextView statusKondisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_login);
        statusKondisi = findViewById(R.id.statusKondisi);
        statusKondisi.setText("Predict");
        cameraView = (CameraView) findViewById(R.id.camlogin);
        Bundle extras = getIntent().getExtras();
        nomorInduk = extras.getString("NRP");
        pass = extras.getString("Pass");
        Toast.makeText(CamLoginActivity.this, nomorInduk, Toast.LENGTH_SHORT).show();
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
                Call<ResponseApi> kirimabsen = apiPredict.kirimAbsen(nomorInduk,pass,","+myBase64Image); //absen
                kirimabsen.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        String response_res = response.toString();
                        Toast.makeText(CamLoginActivity.this, response_res, Toast.LENGTH_SHORT).show();
                        Toast.makeText(CamLoginActivity.this, "Successfully sent the image", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(CamLoginActivity.this);
                        builder.setTitle("Result");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setMessage(response.body().getValidation());
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        Toast.makeText(CamLoginActivity.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
                    }
                });
                //absen
//                Call<ResponseApi> kirimsignin = apiPredict.signin(nomorInduk,pass,myBase64Image); //absen
//                kirimsignin.enqueue(new Callback<ResponseApi>() {
//                    @Override
//                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseApi> call, Throwable t) {
//
//                    }
//                });
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
                    Toast.makeText(CamLoginActivity.this, "Image" + " Captured", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onPictureTaken - wrote to " + outFile.getAbsolutePath());

//                    refreshGallery(outFile);
                } catch (FileNotFoundException e) {
//                    print("FNF");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(CamLoginActivity.this, "Unsaved. Please Retry", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        };

        cameraView.addCameraKitListener(cameraKitEventListener);
        Button lala = (Button) findViewById(R.id.cambtn);
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

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
    public void switchCam(View v){
        if (flaagg==1){
            flaagg=0;
            cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        }
        else if(flaagg==0){
            flaagg=1;
            cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        }

    }
}
