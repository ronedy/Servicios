package comi.carlos.servicios;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import comi.carlos.servicios.Modelos.Usuario;

public class RegistrarseActivity extends AppCompatActivity {

    private ImageView campoImagen;
    private TextView campoNombre;
    private EditText campoCorreo;
    private EditText campoDescripcion;
    private EditText campoTelefono;
    private EditText campoOcupacion;
    private EditText campoDireccion;
    private Button btnRegistrar;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //vamos a obtener el usuario logeado para obtener sus datos
    //Lo hice global para que estuviera en todos mis metodos

    //Para poder guardar en las tablas
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        campoImagen = (ImageView) findViewById(R.id.editarImagenPerfil);
        campoNombre = (TextView) findViewById(R.id.editarNombre);
        campoDescripcion = (EditText) findViewById(R.id.editarDescripcion);
        campoTelefono = (EditText) findViewById(R.id.editarTelefono);
        campoOcupacion = (EditText) findViewById(R.id.editarOcupacion);
        campoCorreo = (EditText) findViewById(R.id.editarCorreo);
        campoDireccion = (EditText) findViewById(R.id.editarDireccion);


        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        inicializarFirebase(); //Metodo para inicializar firebase




        if (user != null){
            //siempre hacemos la validacion para evitar errores
            Glide.with(this).load(user.getPhotoUrl()).into(campoImagen);
            campoNombre.setText(user.getDisplayName());
            campoTelefono.setText(user.getPhoneNumber());
            campoCorreo.setText(user.getEmail());




        }else{
            goLoginScreen(); //Vamos al login para que inicie sesión de nuevo
        }


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void registrarUsuario(){
        //faltan hacer validaciones
        try{
            //Validaciones

            if (campoTelefono.length() < 8 || campoDescripcion.length()==0 || campoCorreo.length()==0 || campoOcupacion.length()==0 || campoDireccion.length()==0){
                Toast.makeText(this, "Debes llenar todos tus datos", Toast.LENGTH_SHORT).show();
            }
            else{


                Usuario u = new Usuario();
                u.setUid(user.getUid());
                u.setNombre(user.getDisplayName().trim()); //trim espacios en blanco los ignora
                u.setDescripcion(campoDescripcion.getText().toString().trim());
                u.setCorreo(user.getDisplayName().trim());
                u.setTelefono(campoTelefono.getText().toString().trim()); //Utilizamos el campo Telefono porque posiblemente no pueda tener telefono en FB
                u.setOcupacion(campoOcupacion.getText().toString().trim());
                u.setDireccion(campoDireccion.getText().toString().trim());
                u.setUrlFoto(user.getPhotoUrl().toString()); //Pasamos de URI a String.

                //metodo para actualizar
                databaseReference.child("Usuario").child(u.getUid()).setValue(u); //cambiamos el valor por el objeto


                Toast.makeText(this,"Usuario registrado correctamente",Toast.LENGTH_SHORT).show();

                goMainScreen(); //Para tirarlo al main
            }



        }catch (Exception e){
            Toast.makeText(this,"Ha habido un error",Toast.LENGTH_SHORT).show();

        }

    }


}
