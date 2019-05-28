package comi.carlos.servicios;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.io.ByteArrayOutputStream;

import comi.carlos.servicios.Modelos.Servicio;
import comi.carlos.servicios.Modelos.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAnuncioActivity extends AppCompatActivity {

    TextView campoIdServicio, campoIdUsuario; //tenemos los id que necesitamos para consultar la base de datos.

    private TextView campoServicioPerfilNombre, campoServicioPerfilDescripcion, campoServicioPerfilDisponibilidad, campoServicioPerfilHorario, campoServicioPerfilDireccion, campoServicioPerfilTelefono;
    private TextView campoPublicante;
    private TextView campoCategoria;
    private CircleImageView campoImagenPublicante;
    private CheckBox campoDomingo, campoLunes, campoMartes, campoMiercoles, campoJueves, campoViernes, campoSabado;

    String dias=""; //para poder establecer los dias

    //Para botener instancias de firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;



    //botones
    private Button btnVerPerfil;
    private Button btnServicioEditar;
    private Button btnLlamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_anuncio);

        //Para cambiarle el titulo

       ActionBar actionBar = getSupportActionBar();
       actionBar.setTitle("Servicio");
       actionBar.setDisplayHomeAsUpEnabled(true); //para colocar el boton de atrás
        actionBar.setDisplayShowHomeEnabled(true); //para colocar el boton de atrás

        //Botones


        campoIdServicio = (TextView) findViewById(R.id.perfilServicioIdServicio);
        campoIdUsuario = (TextView) findViewById(R.id.perfilServicioIdUsuario);
        campoServicioPerfilTelefono = (TextView) findViewById(R.id.perfilServicioTelefono);


        //Encontramos los elementos
        campoServicioPerfilNombre = (TextView) findViewById(R.id.perfilServicioNombre);
        campoServicioPerfilDescripcion = (TextView) findViewById(R.id.perfilServicioDescripcion);
       // campoServicioPerfilDisponibilidad = (TextView) findViewById(R.id.perfilServicioDisponibilidad);
        campoCategoria = (TextView) findViewById(R.id.categoriaServicio);

        //CHECKBOX DE LOS DIAS

        campoDomingo = (CheckBox) findViewById(R.id.checkDomingo);
        campoLunes = (CheckBox) findViewById(R.id.checkLunes);
        campoMartes = (CheckBox) findViewById(R.id.checkMartes);
        campoMiercoles = (CheckBox) findViewById(R.id.checkMiercoles);
        campoJueves = (CheckBox) findViewById(R.id.checkJueves);
        campoViernes = (CheckBox) findViewById(R.id.checkViernes);
        campoSabado = (CheckBox) findViewById(R.id.checkSabado);


        campoServicioPerfilHorario = (TextView) findViewById(R.id.perfilServicioHorario);
        campoServicioPerfilDireccion = (TextView) findViewById(R.id.perfilServicioDireccion);

        //Para obtener el publicante
        campoPublicante = (TextView) findViewById(R.id.servicioPerfilPublicante2);
        campoImagenPublicante = (CircleImageView) findViewById(R.id.servicioPerfilImagenUsuario);

        //Obtenemos la informacion del intent
       // byte[] bytes = getIntent().getByteArrayExtra("image");
        String idUsuario = getIntent().getStringExtra("idUsuario");
        String idServicio = getIntent().getStringExtra("idServicio");
       // Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.lenght);

//Ahora vamos a poner los datos

        campoIdServicio.setText(idServicio);
        campoIdUsuario.setText(idUsuario);

        //Le damos accion a los botones
        btnVerPerfil = (Button) findViewById(R.id.btnVerPerfil);
        btnServicioEditar = (Button) findViewById(R.id.btnServicioPerfilEditar);
        btnLlamar = (Button) findViewById(R.id.btnLlamar);

        //dandole accion al boton llamar

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PerfilAnuncioActivity.this, "Llamando a "+campoPublicante.getText().toString(), Toast.LENGTH_SHORT).show();

                setBtnLlamar();


            }
        });

        btnServicioEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verEditarServicioActivity();
            }
        });

        btnVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verPerfilActivity();
            }
        });


        //Vamos a cargar la información

        if(user!=null){ //validamos por si las moscas

            inicializarFirebase();
            cargarServicio();
            cargarUsuario();

        }

    }

    private void setBtnLlamar(){
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+campoServicioPerfilTelefono.getText().toString()));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED)

            return;
        startActivity(i);
    }

    private void verPerfilActivity() {
        Intent intent = new Intent (getApplicationContext(), PerfilPersonaActivity.class);
        String idUsuarioEnviar = campoIdUsuario.getText().toString();

        intent.putExtra("idUsuarioPersona", idUsuarioEnviar);
        startActivity(intent);

    }

    private void verEditarServicioActivity() {
        Intent intent = new Intent (getApplicationContext(), EditarServicio.class);

        //OBTENEMOS LOS ID DEL USUARIO Y DEL SERVICIO PARA PODER ENVIARLOS POR SI QUIEREN EDITAR EL SERVICIO
        String idUsuarioSend = campoIdUsuario.getText().toString();
        String idServicioSend = campoIdServicio.getText().toString();


        intent.putExtra("idUsuarioSend", idUsuarioSend);
        intent.putExtra("idServicioSend", idServicioSend);

        startActivity(intent);

    }

    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }

    private void cargarServicio() {
        databaseReference.child("Servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Servicio s = objSnaptshot.getValue(Servicio.class);

                    String uid = s.getUid();



                    if(campoIdServicio.getText().equals(uid) && user!=null){

                        campoServicioPerfilNombre.setText(s.getNombre());
                        campoServicioPerfilDescripcion.setText(s.getDescripcion());
                      // campoServicioPerfilDisponibilidad.setText(s.getDisponibilidad());

                        if(s.isDomingo()){
                            campoDomingo.setChecked(true);
                        }
                        if(s.isLunes()){
                            campoLunes.setChecked(true);
                        }
                        if(s.isMartes()){
                            campoMartes.setChecked(true);
                        }
                        if(s.isMiercoles()){
                            campoMiercoles.setChecked(true);
                        }
                        if(s.isJueves()){
                            campoJueves.setChecked(true);
                        }
                        if(s.isViernes()){
                            campoViernes.setChecked(true);
                        }
                        if(s.isSabado()){
                            campoSabado.setChecked(true);
                        }


                        campoServicioPerfilHorario.setText(s.getHorario());
                        campoServicioPerfilDireccion.setText(s.getDireccion());
                        campoCategoria.setText(s.getCategoria());


                        //de una vez cuando cargamos el servicio definimos el boton editar
                        //Si aparece o no aparece en el anuncio
                        //Ademas tambien el usuario no puede llamarse así mismo
                        if(user.getUid().equals(campoIdUsuario.getText().toString())){
                            btnServicioEditar.setVisibility(View.VISIBLE);
                            btnLlamar.setVisibility(View.INVISIBLE);
                        }

                    }


                }
                //salgo del for



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cargarUsuario() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Usuario u = objSnaptshot.getValue(Usuario.class);

                    String uid = u.getUid();


                    if(campoIdUsuario.getText().equals(uid) && user!=null){

                        campoPublicante.setText(u.getNombre());
                        Glide.with(getApplicationContext()).load(u.getUrlFoto()).crossFade().centerCrop().into(campoImagenPublicante);
                        //para obtener el telefono
                        campoServicioPerfilTelefono.setText(u.getTelefono());
                    }


                }
                //salgo del for



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
