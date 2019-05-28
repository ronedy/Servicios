package comi.carlos.servicios;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.UUID;

import comi.carlos.servicios.Modelos.Seguidor;
import comi.carlos.servicios.Modelos.Servicio;
import comi.carlos.servicios.Modelos.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPersonaActivity extends AppCompatActivity {

    private String recibirIdUsuario; //para hacer la consulta en la base de datos
    private TextView campoPersonaNombre, campoPersonaDescripcion, campoPersonaOcupacion, campoPersonaTelefono, campoPersonaCorreo, campoPersonaDireccion;
    private CircleImageView campoImagenPerfil;
    private ImageView campoImagenPortada;

    //para conseguir los numeros de los post que ese usuario ha hecho personalmente
    private int cantidadPost=0;
    private TextView cantidadPostPerfil;
    private Button btnFollow;
    private Button btnUnfollow;
    private Boolean isFollower;
    private Button btnSeguirDeNuevo;
    String uidSeguidor;
    private TextView campoFollowers;
    private int corazones=0;


    private ImageButton btnEditarPerfil;


    //Inicializar firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_persona);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");


        //Encontramos las variables
        campoPersonaNombre = (TextView)findViewById(R.id.personaPerfilNombre);
        campoPersonaDescripcion = (TextView)findViewById(R.id.personaPerfilDescripcion);
        campoPersonaOcupacion = (TextView)findViewById(R.id.personaPerfilOcupacion);
        campoPersonaTelefono = (TextView)findViewById(R.id.personaPerfilTelefono);
        campoPersonaCorreo = (TextView)findViewById(R.id.personaPerfilCorreo);
        campoPersonaDireccion = (TextView)findViewById(R.id.personaPerfilDireccion);
        campoImagenPortada = (ImageView) findViewById(R.id.personaPerfilPortada);
        campoImagenPerfil = (CircleImageView)findViewById(R.id.personaPerfilImagen);
        btnEditarPerfil = (ImageButton) findViewById(R.id.btnGoEditarActivity);
        cantidadPostPerfil = (TextView) findViewById(R.id.personaPerfilPost);
        btnFollow = (Button) findViewById(R.id.btnFollow);
        btnUnfollow = (Button) findViewById(R.id.btnUnfollow);
        btnSeguirDeNuevo = (Button) findViewById(R.id.btnSeguirDeNuevo);
        campoFollowers = (TextView) findViewById(R.id.personaPerfilFollowers);



        //Obtenemos la informacion del intent
        recibirIdUsuario = getIntent().getStringExtra("idUsuarioPersona");
        inicializarFirebase();
        cargarPerfilUsuario();
        cargarPostPerfil();
        comprobarSeguidor(); // para ver si en realidad sigue o no sigue
        idModificarSeguidor();//para obtener el id del seguidor
        cargarSeguidores();



        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verEditarPerfilActivity();
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registrarSeguidor();
                goMainScreen();
            }
        });

        btnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //dejarDeSeguir();
                Seguidor editar = new Seguidor();
                editar.setUid(uidSeguidor);
                editar.setUsuario(user.getUid());
                editar.setSigueA(recibirIdUsuario);
                editar.setPrimeraVez(false);
                editar.setActivo(false);
                // seguirAgain();
                databaseReference.child("Seguidor").child(uidSeguidor).setValue(editar); //cambiamos el valor por el objeto
                goMainScreen();
            }
        });

        btnSeguirDeNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Seguidor editar = new Seguidor();
                editar.setUid(uidSeguidor);
                editar.setUsuario(user.getUid());
                editar.setSigueA(recibirIdUsuario);
                editar.setPrimeraVez(false);
                editar.setActivo(true);
               // seguirAgain();
                databaseReference.child("Seguidor").child(uidSeguidor).setValue(editar); //cambiamos el valor por el objeto
               goMainScreen();
            }
        });





    }
    private void verEditarPerfilActivity() {
        Intent intent = new Intent (getApplicationContext(), EditarPerfilActivity.class);
        String idUsuarioEditar = recibirIdUsuario;

        intent.putExtra("idUsuarioEditar", idUsuarioEditar);

        startActivity(intent);

    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }



    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }

    private void cargarPerfilUsuario() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Usuario u = objSnaptshot.getValue(Usuario.class);

                    String uid = u.getUid();


                    if(recibirIdUsuario.equals(uid) && user!=null){

                        campoPersonaNombre.setText(u.getNombre());
                        campoPersonaDescripcion.setText(u.getDescripcion());
                        campoPersonaOcupacion.setText(u.getOcupacion());
                        campoPersonaDireccion.setText(u.getDireccion());
                        campoPersonaCorreo.setText(u.getCorreo());
                        campoPersonaTelefono.setText(u.getTelefono());
                        //ahora cargamos las imagenes
                        Glide.with(getApplicationContext()).load(u.getUrlFoto()).crossFade().centerCrop().into(campoImagenPerfil);
                        Glide.with(getApplicationContext()).load(u.getUrlFoto()).crossFade().centerCrop().into(campoImagenPortada);

                        //Le damos visibilidad al boton si en dado caso el usuario actual es igual al de FB
                       if (recibirIdUsuario.equals(user.getUid()) && user!=null){
                           //Aqui significa que si el usuario que esta logeado es igual al usuario que va a mostrar el perfil
                           //Entonces el boton de editar va a aparecer
                           btnEditarPerfil.setVisibility(View.VISIBLE);
                       }else{
                           //Si es otro usuario entonces si va a aparecer el boton de seguir
                           btnFollow.setVisibility(View.VISIBLE);
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

    private void cargarPostPerfil() {
        databaseReference.child("Servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Servicio s = objSnaptshot.getValue(Servicio.class);

                    String uid = s.getUidUsuario();


                    if(recibirIdUsuario.equals(uid) && user!=null){
                        cantidadPost++;
                    }


                }
                //salgo del for
                cantidadPostPerfil.setText(String.valueOf(cantidadPost));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void seguirAgain() {
        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);


                  if (s.getUsuario().equals(user.getUid()) && s.getUid().equals(recibirIdUsuario)){
                      Seguidor editar = new Seguidor();
                      editar.setUid(s.getUid());
                      editar.setUsuario(s.getUsuario());
                      editar.setActivo(true);
                      editar.setPrimeraVez(s.getPrimeraVez()); //deberia ser false siempre

                      databaseReference.child("Seguidor").child(editar.getUid()).setValue(editar); //cambiamos el valor por el objeto
                    break;

                  }


                }
                //salgo del for

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void seguirDeNuevo() {
        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);

                    String uid = s.getSigueA();


                    if(s.getSigueA().equals(recibirIdUsuario) && user!=null && s.getPrimeraVez()==false){

                        try {
                            Seguidor seg = new Seguidor();
                            seg.setUid(s.getUid());
                            seg.setUsuario(s.getUsuario()); //trim espacios en blanco los ignora
                           seg.setSigueA(s.getSigueA());
                           seg.setPrimeraVez(false);
                           seg.setActivo(true);



                            //metodo para actualizar
                            databaseReference.child("Seguidor").child(seg.getUid()).setValue(seg); //cambiamos el valor por el objeto

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }

                    }else{


                    }

                }
                //salgo del for
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void registrarSeguidor() {
        try {
                Seguidor seguidor = new Seguidor();
                seguidor.setUid(UUID.randomUUID().toString());
                seguidor.setUsuario(user.getUid());
                seguidor.setSigueA(recibirIdUsuario);
                seguidor.setActivo(true);
                seguidor.setPrimeraVez(true);
                //metodo para actualizar
                databaseReference.child("Seguidor").child(seguidor.getUid()).setValue(seguidor); //cambiamos el valor por el objeto
                //DAR MENSAJE DE QUE SE EDITO CORRECTAMENTE
                Toast.makeText(this, "Has seguido correctamente", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

    }



    private void comprobarSeguidor() {
        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);

                    if(s.getUsuario().equals(user.getUid()) && s.getSigueA().equals(recibirIdUsuario) && s.getActivo()==true && s.getPrimeraVez()==true){

                        btnUnfollow.setVisibility(View.VISIBLE);
                        btnFollow.setVisibility(View.INVISIBLE);
                        btnSeguirDeNuevo.setVisibility(View.INVISIBLE);


                        break;
                    }

                    if(s.getUsuario().equals(user.getUid()) && s.getSigueA().equals(recibirIdUsuario) && s.getActivo()==true && s.getPrimeraVez()==false){

                        btnUnfollow.setVisibility(View.VISIBLE);
                        btnFollow.setVisibility(View.INVISIBLE);
                        btnSeguirDeNuevo.setVisibility(View.INVISIBLE);


                        break;
                    }

                    if(s.getUsuario().equals(user.getUid()) && s.getSigueA().equals(recibirIdUsuario) && s.getActivo()==false && s.getPrimeraVez()==false){

                        btnUnfollow.setVisibility(View.INVISIBLE);
                        btnFollow.setVisibility(View.INVISIBLE);
                        btnSeguirDeNuevo.setVisibility(View.VISIBLE);


                        break;
                    }


                }

    //ESO SIRVE PARA OCULTAR EL BOTON SI EL USUARIO ESTA VISITANDO SU MISMO PERFIL
                if(user.getUid().equals(recibirIdUsuario)){
                    btnUnfollow.setVisibility(View.INVISIBLE);
                    btnFollow.setVisibility(View.INVISIBLE);
                }


                //salgo del for
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void dejarDeSeguir() {
        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);

                    String uid = s.getSigueA();


                    //Condicion para ver si sigue o no sigue
                    if(s.getSigueA().equals(recibirIdUsuario) && s.getUsuario().equals(user.getUid())){
                       s.setActivo(false);
                       s.setPrimeraVez(false);
                        databaseReference.child("Seguidor").child(s.getUid()).setValue(s); //cambiamos el valor por el objeto
                        break;


                    }else{



                    }


                }
                //salgo del for
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void idModificarSeguidor() {
        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);

                    String uid = s.getSigueA();


                    //Condicion para ver si sigue o no sigue
                    if(s.getSigueA().equals(recibirIdUsuario) && s.getUsuario().equals(user.getUid())){

                      uidSeguidor = s.getUid();
                      break;
                    }

                }
                //salgo del for
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cargarSeguidores() {
        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);

                    String uid = s.getUid();

                   if (s.getSigueA().equals(recibirIdUsuario) && s.getActivo()==true){
                       corazones++;
                   }

                }

                campoFollowers.setText(String.valueOf(corazones));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }










}
