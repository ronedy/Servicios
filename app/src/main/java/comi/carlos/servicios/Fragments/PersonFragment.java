package comi.carlos.servicios.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import comi.carlos.servicios.EditarPerfilActivity;
import comi.carlos.servicios.LoginActivity;
import comi.carlos.servicios.MainActivity;
import comi.carlos.servicios.Modelos.Seguidor;
import comi.carlos.servicios.Modelos.Servicio;
import comi.carlos.servicios.Modelos.Usuario;
import comi.carlos.servicios.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonFragment extends Fragment {

    View vista;


    private TextView campoNombre;
    private TextView campoEmail;
    private TextView campoTelefono;
    private CircleImageView campoFotoPerfil;
    private ImageView campoFotoPortada;
    private TextView campoDescripcion;
    private TextView campoOcupacion;
    private TextView campoDireccion;


    private int corazones=0; //para ver los seguidores que posee este usuario
    private TextView cantidadFollowers;

    private Button boton;
    private ImageButton btnEditarPerson;

    private int cantidadPost=0;
    private TextView campoCantidadPost;
    //para ver las cantidades de post

    private String enviarIdUsuario; //Esto es para que pueda editar

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {




       // return inflater.inflate(R.layout.fragment_person,container,false);

        vista = inflater.inflate(R.layout.fragment_person,container,false);

       campoNombre = (TextView) vista.findViewById(R.id.personaNombre);
        campoEmail = (TextView) vista.findViewById(R.id.personaCorreo);
       campoTelefono = (TextView) vista.findViewById(R.id.personaTelefono);
        campoFotoPerfil= (CircleImageView) vista.findViewById(R.id.personaImagenPerfil);
        campoFotoPortada = (ImageView) vista.findViewById(R.id.personaImagenPortada);
        campoDescripcion = (TextView) vista.findViewById(R.id.personaDescripcion);
        campoOcupacion = (TextView) vista.findViewById(R.id.personaOcupacion);
        campoDireccion = (TextView) vista.findViewById(R.id.personaDireccion);
        campoCantidadPost = (TextView) vista.findViewById(R.id.personaPost);
        btnEditarPerson = (ImageButton) vista.findViewById(R.id.btnEditarPerson);
        cantidadFollowers = (TextView) vista.findViewById(R.id.personaFollowers);
        //boton = (Button) vista.findViewById(R.id.btnSalir);

        inicializarFirebase();
        cargarPerfil();
        cargarPost();
        cargarSeguidores();


        if(user!=null){
            campoNombre.setText(user.getDisplayName());
            //campoEmail.setText(user.getEmail());
            campoTelefono.setText(user.getPhoneNumber());
            //Metodo para poner imagen
            Glide.with(this).load(user.getPhotoUrl()).crossFade().centerCrop().into(campoFotoPerfil);
            Glide.with(this).load(user.getPhotoUrl()).crossFade().centerCrop().into(campoFotoPortada);
            //Picasso.with(getContext()).load(user.getPhotoUrl()).into(campoFotoPerfil);
            //Picasso.with(getContext()).load(user.getPhotoUrl()).into(campoFotoPortada);

        }

        //Dandole acciones a los botones
        btnEditarPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verEditarPerfilActivity();
            }
        });



        return vista;

    }
//Metodo para poder enviar el id al activity de editar el perfil

    private void verEditarPerfilActivity() {
        Intent intent = new Intent (getActivity(), EditarPerfilActivity.class);
        //String idUsuarioEditar = enviarIdUsuario;

        intent.putExtra("idUsuarioEditar", enviarIdUsuario);

        startActivity(intent);

    }
    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }


    private void cargarPerfil() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Usuario p = objSnaptshot.getValue(Usuario.class);

                    String uid = p.getUid();


                    if(user.getUid().equals(uid) && user!=null){

                        enviarIdUsuario = p.getUid();
                       campoDescripcion.setText(p.getDescripcion());
                       campoOcupacion.setText(p.getOcupacion());
                       campoTelefono.setText(p.getTelefono());
                       campoDireccion.setText(p.getDireccion());
                       campoEmail.setText(p.getCorreo());

                    }


                }
                //salgo del for



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cargarPost() {
        databaseReference.child("Servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Servicio s = objSnaptshot.getValue(Servicio.class);

                    String uid = s.getUidUsuario();


                    if(user.getUid().equals(uid) && user!=null){
                        cantidadPost++;
                    }


                }
                //salgo del for
                campoCantidadPost.setText(String.valueOf(cantidadPost));

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

                    if (s.getSigueA().equals(user.getUid()) && s.getActivo()==true){
                        corazones++;
                    }

                }

                cantidadFollowers.setText(String.valueOf(corazones));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
