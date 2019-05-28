package comi.carlos.servicios;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfirmacionServicioActivity extends AppCompatActivity {

   private Button okay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_servicio);


        okay = (Button) findViewById(R.id.okay);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    goMainScreen();


            }
        });
    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



}
