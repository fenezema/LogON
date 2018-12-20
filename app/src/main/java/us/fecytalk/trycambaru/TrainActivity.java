package us.fecytalk.trycambaru;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainActivity extends AppCompatActivity {
    protected TextView statusTrain;
    protected String nomorInduk,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        Bundle extras = getIntent().getExtras();
        statusTrain = findViewById(R.id.responTrain);
        nomorInduk = extras.getString("NRP");
        pass = extras.getString("Pass");

        final ApiInterface apiPredict = Server.getclient().create(ApiInterface.class); //absen
        JSONObject paramObject = new JSONObject();
        Call<ResponseApi> triggerLearn = apiPredict.kirimTrain(nomorInduk,pass);
        triggerLearn.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                statusTrain.setText(response.body().getValidation());
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(TrainActivity.this,"Training Failed",Toast.LENGTH_LONG).show();
            }
        });
    }
}
