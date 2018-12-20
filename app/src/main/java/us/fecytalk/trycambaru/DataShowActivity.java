package us.fecytalk.trycambaru;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DataShowActivity extends AppCompatActivity {
    private SQLiteDatabase dbku;
    private SQLiteOpenHelper opendb;
    private ListView lvItems;
    public String nrp,pass;
    public int id_keg;
    public String nmKeg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);
        lvItems = (ListView)findViewById(R.id.lvItems);
        opendb = new SQLiteOpenHelper(this,"database.sql",null,1) {
            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        dbku=opendb.getWritableDatabase();
        dbku.execSQL("create table if not exists datasets(_id INTEGER PRIMARY KEY AUTOINCREMENT,nrp TEXT,namakeg TEXT, status INT, created TEXT, sent TEXT, delta TEXT);");
        makeInitData();
    }

    @Override
    protected void onResume(){
        super.onResume();
        dbku=opendb.getWritableDatabase();
        showData(nrp);
        showMean(nrp);
    }

    @Override
    protected void onStop(){
        dbku.close();
        opendb.close();
        super.onStop();
    }
    private void showMean(String nrp){
        String[] params = new String[]{nrp};
        String rataQuery="select avg(delta) as rerata from datasets where nrp=?";
        Cursor rataCur = dbku.rawQuery(rataQuery,params);
        TextView ratarata = (TextView)findViewById(R.id.rerata);
        float rerata = (float)0;
        rataCur.moveToFirst();
        try{
            rerata = Float.parseFloat(rataCur.getString(rataCur.getColumnIndex("rerata")))/1000;
        }
        catch (Exception e){
            rerata = (float)0;
        }

        ratarata.setText("Mean : "+String.valueOf(rerata)+"s");
        rataCur.close();
    }
    private void showData(String nrp){
        String[] params = new String[]{nrp};
        String query="select * from datasets where nrp=?";
        Cursor cur = dbku.rawQuery(query,params);
        TodoCursorAdapter todoAdapter = new TodoCursorAdapter(this,cur);
        lvItems.setAdapter(todoAdapter);
    }

    private void makeInitData(){
        nrp = getIntent().getStringExtra("NRP");
        pass = getIntent().getStringExtra("Pass");
        String query = "select * from datasets where nrp=?";
        String[] params = new String[]{nrp};
        Cursor cur = dbku.rawQuery(query,params);
        int n = cur.getCount();
//        String nn = Integer.toString(n);
//        Toast.makeText(this,nn,Toast.LENGTH_LONG).show();
        if (n<1){
            ContentValues dataku = new ContentValues();
            String[] keglist = {
                    "1. Normal tegak lurus kamera.",
                    "2. Normal tegak lurus kamera berkacamata.",
                    "3. Tersenyum tegak lurus kamera.",
                    "4. Sedih tegak lurus kamera.",
                    "5. Mengantuk tegak lurus kamera.",
                    "6. Normal menoleh ke kanan 30 derajat.",
                    "7. Normal menoleh ke kanan 30 derajat beracamata.",
                    "8. Tersenyum menoleh ke kanan 30 derajat.",
                    "9. Sedih menoleh ke kanan 30 derajat.",
                    "10. Mengantuk menoleh ke kanan 30 derajat.",
                    "11. Normal menoleh ke kiri 30 derajat.",
                    "12. Normal menoleh ke kiri 30 derajat berkacamata.",
                    "13. Tersenyum menoleh ke kiri 30 derajat.",
                    "14. Sedih menoleh ke kiri 30 derajat.",
                    "15. Mengantuk menoleh ke kiri 30 derajat.",
                    "16. Normal tegak lurus kamera muka basah.",
                    "17. Normal tegak lurus kamera berkacamata muka basah.",
                    "18. Tersenyum tegak lurus kamera muka basah.",
                    "19. Sedih tegak lurus kamera muka basah.",
                    "20. Mengantuk tegak lurus kamera muka basah.",
                    "21. Normal menoleh ke kanan 30 derajat muka basah.",
                    "22. Normal menoleh ke kanan 30 derajat berkacamata muka basah.",
                    "23. Tersenyum menoleh ke kanan 30 derajat muka basah.",
                    "24. Sedih menoleh ke kanan 30 derajat muka basah",
                    "25. Mengantuk menoleh ke kanan 30 derajat muka basah",
                    "26. Normal menoleh ke kiri 30 derajat muka basah",
                    "27. Normal menoleh ke kiri 30 derajat berkacamata muka basah.",
                    "28. Tersenyum menoleh ke kiri 30 derajat muka basah.",
                    "29. Sedih menoleh ke kiri 30 derajat muka basah.",
                    "30. Mengantuk menoleh ke kiri 30 derajat muka basah."
            };

            for (int i=0;i<30;i+=1){
                dataku.put("nrp",nrp);
                dataku.put("namakeg",keglist[i]);
                dataku.put("status",0);
                dbku.insert("datasets",null,dataku);
//                Toast.makeText(this,"New Data Added",Toast.LENGTH_LONG).show();
            }
            showData(nrp);
//            showMean(nrp);
        }
        else {
            showData(nrp);
//            showMean(nrp);
        }
        cur.close();
    }
    public class TodoCursorAdapter extends CursorAdapter {
        String st_check="";
        String but_check="";
        public TodoCursorAdapter(Context context, Cursor cursor){
            super(context,cursor,0);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            // Find fields to populate in inflated template
            TextView namaKegiatan = (TextView) view.findViewById(R.id.namaKegiatan);
            TextView statusKegiatan = (TextView) view.findViewById(R.id.statusKegiatan);
            Button butKegiatan = (Button)view.findViewById(R.id.butKegiatan);
            TextView waktuKegiatan = (TextView)view.findViewById(R.id.waktuKegiatan);
            // Extract properties from cursor
            nmKeg = cursor.getString(cursor.getColumnIndexOrThrow("namakeg"));
            int stats = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
            id_keg = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String waktu = cursor.getString(cursor.getColumnIndexOrThrow("delta"));
            String created = cursor.getString(cursor.getColumnIndexOrThrow("created"));
            String sent = cursor.getString(cursor.getColumnIndexOrThrow("sent"));
            if(stats == 0){
                st_check = "Data "+Integer.toString(id_keg)+" : Doesn't Exist";
                but_check = "Take Data "+id_keg;
                butKegiatan.setEnabled(true);
                butKegiatan.setBackgroundColor(Color.RED);
                waktuKegiatan.setText("null data");
            }
            else{
                st_check = "Data : Exist";
                but_check = "Data Taken";
                butKegiatan.setEnabled(false);
                butKegiatan.setBackgroundColor(Color.GREEN);
                waktuKegiatan.setText("Created at : \n"+created+"ms \nSent at : \n"+sent+"ms \nDelta : "+String.valueOf(Float.parseFloat(waktu)/1000)+"s");
            }
            // Populate fields with extracted properties
            namaKegiatan.setText(nmKeg);
            statusKegiatan.setText(st_check);
            butKegiatan.setText(but_check);
        }
    }
    public void newIntent(View v){
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        LinearLayout layChild = (LinearLayout)vwParentRow.getChildAt(1);
        TextView LLchild = (TextView)layChild.getChildAt(0);
        String nmKeg = LLchild.getText().toString();
        Button btnchild = (Button) vwParentRow.getChildAt(0);
        String id_raw = btnchild.getText().toString();
        Intent i = new Intent(DataShowActivity.this,CameraActivity.class);
        Bundle extras = new Bundle();
        String[] id_data = id_raw.split(" ");

//        LinearLayout layChild = (LinearLayout)vwParentRow.getChildAt(1);
//        TextView LLchild = (TextView)layChild.getChildAt(0);
//        String nmKeg = LLchild.getText().toString();


        extras.putString("nama_kegiatan",nmKeg);
        extras.putString("nrp",nrp);
        extras.putString("Pass",pass);
        extras.putInt("id_kegiatan",Integer.parseInt(id_data[2]));
        i.putExtras(extras);
        startActivity(i);
    }
}
