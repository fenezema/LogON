package us.fecytalk.trycambaru;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.wonderkiln.camerakit.CameraKitVideo;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictSignature extends AppCompatActivity {
    private SignaturePad mSignaturePad;
    private String nomorInduk,pass;
    private Button predictSignature,clearPredictSignature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_signature);
        Bundle bundle = getIntent().getExtras();
        nomorInduk = bundle.getString("NRP");
        pass = bundle.getString("Pass");
        mSignaturePad = findViewById(R.id.signature_predict);
        predictSignature = findViewById(R.id.predictSignature);
        clearPredictSignature = findViewById(R.id.clearPredictSignature);
        clearPredictSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });
        predictSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap result = mSignaturePad.getSignatureBitmap();
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
                final ApiInterface apiPredict = Server.getclient().create(ApiInterface.class); //absen
                Log.d("test", "onImage: " + myBase64Image);
                Call<ResponseApi> kirimabsen = apiPredict.predictTTD(nomorInduk,pass,","+myBase64Image); //absen
                kirimabsen.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        String response_res = response.toString();
                        Toast.makeText(PredictSignature.this, response_res, Toast.LENGTH_SHORT).show();
                        Toast.makeText(PredictSignature.this, "Successfully sent the image", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PredictSignature.this);
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
                        Toast.makeText(PredictSignature.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_predict);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                predictSignature.setEnabled(true);
                clearPredictSignature.setEnabled(true);
            }

            @Override
            public void onClear() {
                predictSignature.setEnabled(false);
                clearPredictSignature.setEnabled(false);
            }
        });
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
