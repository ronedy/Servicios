package comi.carlos.servicios;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import comi.carlos.servicios.Modelos.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private String recibirIdUsuario; //para hacer la consulta en la base de datos
    private EditText campoEditarDescripcion, campoEditarOcupacion, campoEditarTelefono, campoEditarCorreo, campoEditarDireccion;
    private EditText campoEditarNombrePersona;
    private CircleImageView campoEditarPerfil;
    private ImageView campoEditarPortada;
    private Button btnEditarPerfil; //para guardar el perfil



        //INSTANCIAS DE FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Editar Perfil");

        //Recibimos el id a editar
        //Obtenemos la informacion del intent
        recibirIdUsuario = getIntent().getStringExtra("idUsuarioEditar");


        //Encontramos las variables
        campoEditarNombrePersona = (EditText) findViewById(R.id.editarNombrePersona);
        campoEditarDescripcion = (EditText)findViewById(R.id.editarDescripcion);
        campoEditarOcupacion = (EditText)findViewById(R.id.editarOcupacion);
        campoEditarTelefono = (EditText)findViewById(R.id.editarTelefono);
        campoEditarCorreo = (EditText)findViewById(R.id.editarCorreo);
        campoEditarDireccion = (EditText)findViewById(R.id.editarDireccion);
        campoEditarPortada = (ImageView) findViewById(R.id.editarPortada);
        campoEditarPerfil = (CircleImageView)findViewById(R.id.imagenFondo);
        btnEditarPerfil = (Button) findViewById(R.id.btnEditarPerfil);


        inicializarFirebase();
        cargarPerfilUsuario();


        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarPerfil();

            }
        });

    }

    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void cargarPerfilUsuario() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Usuario u = objSnaptshot.getValue(Usuario.class);

                    String uid = u.getUid();


                    if(recibirIdUsuario.equals(uid) && user!=null){

                        campoEditarNombrePersona.setText(u.getNombre());
                        campoEditarDescripcion.setText(u.getDescripcion());
                        campoEditarOcupacion.setText(u.getOcupacion());
                        campoEditarDireccion.setText(u.getDireccion());
                        campoEditarCorreo.setText(u.getCorreo());
                        campoEditarTelefono.setText(u.getTelefono());
                        //ahora cargamos las imagenes
                        Glide.with(getApplicationContext()).load(u.getUrlFoto()).crossFade().centerCrop().into(campoEditarPerfil);
                        Glide.with(getApplicationContext()).load(u.getUrlFoto()).crossFade().centerCrop().into(campoEditarPortada);


                    }

                }
                //salgo del for



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void editarPerfil() {
        try {

            if (campoEditarDescripcion.length() == 0 || campoEditarTelefono.length() < 8
                    || campoEditarOcupacion.length() == 0 || campoEditarCorreo.length() == 0 || campoEditarDireccion.length() == 0) {
                Toast.makeText(this, "Hace falta llenar todos los campos", Toast.LENGTH_SHORT).show();

            } else {
                Usuario u = new Usuario();
                u.setUid(recibirIdUsuario);
                u.setNombre(campoEditarNombrePersona.getText().toString().trim()); //trim espacios en blanco los ignora
                u.setDescripcion(campoEditarDescripcion.getText().toString().trim());
                u.setOcupacion(campoEditarOcupacion.getText().toString().trim());
                u.setTelefono(campoEditarTelefono.getText().toString().trim());
                u.setCorreo(campoEditarCorreo.getText().toString().trim());
                u.setDireccion(campoEditarDireccion.getText().toString().trim());
                u.setUrlFoto(user.getPhotoUrl().toString());
                //metodo para actualizar
                databaseReference.child("Usuario").child(u.getUid()).setValue(u); //cambiamos el valor por el objeto
                //DAR MENSAJE DE QUE SE EDITO CORRECTAMENTE
                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
                goMainScreen();
            }


        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

    }


}
