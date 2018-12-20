package us.fecytalk.trycambaru;

import android.content.DialogInterface;
import android.content.Intent;
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

public class SignatureSignin extends AppCompatActivity {
    private SignaturePad mSignaturePad;
    private String nomorInduk,pass,idKelas,lat,lon;
    private Button btnSigninSignature,btnClearSigninSignature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_signin);

        Bundle extras = getIntent().getExtras();
        nomorInduk = extras.getString("NRP");
        pass = extras.getString("Pass");
        idKelas = extras.getString("IDKelas");
        lat = extras.getString("Lat");
        lon = extras.getString("Lon");
        Toast.makeText(SignatureSignin.this, lat+"|"+lon, Toast.LENGTH_SHORT).show();

        mSignaturePad = findViewById(R.id.signature_signinPad);
        btnSigninSignature = findViewById(R.id.btnSigninSignature);
        btnClearSigninSignature = findViewById(R.id.btnSigninClearSignature);


        btnClearSigninSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });
        btnSigninSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap result = mSignaturePad.getSignatureBitmap();
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
                final ApiInterface apiPredict = Server.getclient().create(ApiInterface.class); //absen
                Log.d("test", "onImage: " + myBase64Image);
                Call<ResponseApi> kirimabsen = apiPredict.signinTTD(nomorInduk,pass,","+myBase64Image,idKelas,lat,lon); //absen
                kirimabsen.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        String response_res = response.toString();
                        Toast.makeText(SignatureSignin.this, response_res, Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignatureSignin.this, "Successfully sent the image", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignatureSignin.this);
                        builder.setTitle("Result");
                        final String respon_sigSignin = response.body().getValidation();
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (respon_sigSignin.contains("ACCEPTED")){
                                    Intent i = new Intent(SignatureSignin.this,BeforeQuitActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    dialog.dismiss();
                                    Toast.makeText(SignatureSignin.this,"REJECTED",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setMessage(response.body().getValidation());
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        Toast.makeText(SignatureSignin.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
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
                btnSigninSignature.setEnabled(true);
                btnClearSigninSignature.setEnabled(true);
            }

            @Override
            public void onClear() {
                btnSigninSignature.setEnabled(false);
                btnClearSigninSignature.setEnabled(false);
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
