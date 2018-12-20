package us.fecytalk.trycambaru;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignatureTrain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_train);
        Bundle bundle = getIntent().getExtras();
        String nomorInduk = bundle.getString("NRP");
        String pass = bundle.getString("Pass");
        final ApiInterface apiPredict = Server.getclient().create(ApiInterface.class); //absen
        Call<ResponseApi> kirimabsen = apiPredict.trainTTD(nomorInduk,pass); //absen
        kirimabsen.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                String response_res = response.toString();
                Toast.makeText(SignatureTrain.this, response_res, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignatureTrain.this);
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
                Toast.makeText(SignatureTrain.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
