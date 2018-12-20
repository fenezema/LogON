package us.fecytalk.trycambaru;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends AppCompatActivity {
    private EditText nrp,pass;
    private Button cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        nrp = findViewById(R.id.textNRP);
        pass = findViewById(R.id.textPass);
        cont = findViewById(R.id.cont);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this,InputActivity.class);
                i.putExtra("NRP",nrp.getText().toString());
                i.putExtra("Pass",pass.getText().toString());
                startActivity(i);
            }
        });
    }
}
