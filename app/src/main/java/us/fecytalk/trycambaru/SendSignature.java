package us.fecytalk.trycambaru;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendSignature extends AppCompatActivity {
    private SignaturePad mSignaturePad;
    private String nomorInduk,pass;
    private Button btnSendSignature,btnClearSendSignature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_signature);
        Bundle bundle = getIntent().getExtras();
        nomorInduk = bundle.getString("NRP");
        pass = bundle.getString("Pass");

        mSignaturePad = findViewById(R.id.signature_send);
        btnSendSignature = findViewById(R.id.btnSendSignature);
        btnClearSendSignature = findViewById(R.id.btnClearSendSignature);

        btnClearSendSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });
        btnSendSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap result = mSignaturePad.getSignatureBitmap();
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
                final ApiInterface apiPredict = Server.getclient().create(ApiInterface.class); //absen
                Log.d("test", "onImage: " + myBase64Image);
                Call<ResponseApi> kirimabsen = apiPredict.sendTTD(nomorInduk,pass,","+myBase64Image); //absen
                kirimabsen.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        String response_res = response.toString();
                        Toast.makeText(SendSignature.this, response_res, Toast.LENGTH_SHORT).show();
                        Toast.makeText(SendSignature.this, "Successfully sent the image", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SendSignature.this);
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
                        Toast.makeText(SendSignature.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                btnSendSignature.setEnabled(true);
                btnClearSendSignature.setEnabled(true);
            }

            @Override
            public void onClear() {
                btnSendSignature.setEnabled(false);
                btnClearSendSignature.setEnabled(false);
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
