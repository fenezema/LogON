package us.fecytalk.trycambaru;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {
    private CameraView cameraView;
    private Button lala;
    private int flaagg=1;
    private CameraKitEventListener cameraKitEventListener;
    private TextView seli,kegTitle;
    private SQLiteDatabase dbku;
    private SQLiteOpenHelper Opendb;
    private int id_kegg;
    private String nama_kegg;
    private String nomorInduk,pass;
    private Long tstart=(long)0;
    private Long tstop=(long)0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        kegTitle = (TextView)findViewById(R.id.titleKegiatan);
        seli = findViewById(R.id.seli);
        cameraView = (CameraView) findViewById(R.id.camera);
        Bundle extras = getIntent().getExtras();
        id_kegg = extras.getInt("id_kegiatan");
        Toast.makeText(CameraActivity.this, "ID sekarang adalah : "+Integer.toString(id_kegg), Toast.LENGTH_SHORT).show();
        nama_kegg = extras.getString("nama_kegiatan");
        nomorInduk = extras.getString("nrp");
        pass = extras.getString("Pass");
//        Toast.makeText(CameraActivity.this, nama_kegg, Toast.LENGTH_SHORT).show();
        kegTitle.setText(nama_kegg);
        Opendb=new SQLiteOpenHelper(this, "database.sql",null,1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        dbku=Opendb.getWritableDatabase();
        cameraKitEventListener = new CameraKitEventListener() {
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
                final ApiInterface api = Server.getclient().create(ApiInterface.class);
//                final ApiInterface apiPredict = Server.getclientPredict().create(ApiInterface.class); absen
                Log.d("test", "onImage: "+myBase64Image);
                JSONObject paramObject = new JSONObject();
                Call<ResponseApi> kirim =api.kirim(nomorInduk,pass,"data:image/jpeg;base64,"+myBase64Image);
//                Call<ResponseApi> kirimabsen = api.kirimAbsen(nomorInduk,"data:image/jpeg;base64,"+myBase64Image); absen
                tstart = System.currentTimeMillis();
                kirim.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        tstop=System.currentTimeMillis();
                        Long ress=tstop-tstart;
                        String resse=ress.toString();
                        seli.setText(resse+"ms");
                        Toast.makeText(CameraActivity.this, "Successfully sent the image", Toast.LENGTH_SHORT).show();
                        Toast.makeText(CameraActivity.this,response.body().getValidation(),Toast.LENGTH_SHORT).show();
                        update(resse,String.valueOf(tstart),String.valueOf(tstop));
                    }
                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(CameraActivity.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
                    }
                });
//                kirimabsen.enqueue(new Callback<ResponseApi>() {
//                    @Override
//                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
//                        Toast.makeText(CameraActivity.this, "Successfully sent the image", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseApi> call, Throwable t) {
//                        Toast.makeText(CameraActivity.this, "Failed to send. Retry", Toast.LENGTH_SHORT).show();
//                    }
//                }); absen
                FileOutputStream outStream = null;
                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/camtest");
                    dir.mkdirs();

                    String fileName = String.format("%s.jpg", nama_kegg);
                    File outFile = new File(dir, fileName);

                    outStream = new FileOutputStream(outFile);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    Toast.makeText(CameraActivity.this, nama_kegg+".jpg"+" Captured", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onPictureTaken - wrote to " + outFile.getAbsolutePath());

//                    refreshGallery(outFile);
                } catch (FileNotFoundException e) {
//                    print("FNF");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    Toast.makeText(CameraActivity.this, "Unsaved. Please Retry", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        };

        cameraView.addCameraKitListener(cameraKitEventListener);
        lala = (Button) findViewById(R.id.cam);
        lala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.captureImage();
            }
        });
    }
    @Override
    protected void onStop(){
        dbku.close();
        Opendb.close();
        super.onStop();
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
    public void switching(View v){
        if (flaagg==1){
            flaagg=0;
            cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        }
        else if(flaagg==0){
            flaagg=1;
            cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        }

    }
    private void update(String resse,String tstart,String tstop){
        ContentValues dataku = new ContentValues();
        dataku.put("status",1);
        dataku.put("delta",resse);
        dataku.put("created",tstart);
        dataku.put("sent",tstop);
        dbku.update("datasets",dataku,"_id="+id_kegg,null);
        Toast.makeText(this,"Data Updated",Toast.LENGTH_LONG).show();
    }
}
