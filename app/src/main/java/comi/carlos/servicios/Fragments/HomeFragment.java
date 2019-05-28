package comi.carlos.servicios.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

import comi.carlos.servicios.BuscarActivity;
import comi.carlos.servicios.EditarPerfilActivity;
import comi.carlos.servicios.Modelos.ModeloRecycler;
import comi.carlos.servicios.Modelos.ModeloRecyclerUsuarios;
import comi.carlos.servicios.Modelos.Seguidor;
import comi.carlos.servicios.Modelos.Usuario;
import comi.carlos.servicios.Modelos.ViewHolder;
import comi.carlos.servicios.Modelos.ViewHolderUsuarios;
import comi.carlos.servicios.PerfilAnuncioActivity;
import comi.carlos.servicios.PerfilPersonaActivity;
import comi.carlos.servicios.R;

public class HomeFragment extends Fragment {

    View vista;

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario

    CardView followers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_home, container, false);


        //RecyclerView
        mRecyclerView = vista.findViewById(R.id.recyclerviewSeguidores);
        mRecyclerView.setHasFixedSize(true);




        //set layout as LinearLayout

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //aqui puede dar problemas

        //Enviar query a Firebasedatabase

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Usuario");


        return vista;
    }


    //Cargar los datos en el onstart


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ModeloRecyclerUsuarios, ViewHolderUsuarios> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModeloRecyclerUsuarios, ViewHolderUsuarios>(
                ModeloRecyclerUsuarios.class,
                R.layout.cardview_followers,
                ViewHolderUsuarios.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(ViewHolderUsuarios viewHolder, ModeloRecyclerUsuarios model, int position) {

                viewHolder.setDetails(getContext(),model.getUid(), model.getNombre(), model.getTelefono(), model.getDireccion(), model.getUrlFoto()); //aqui puede tronar


            }

            //Para poder enviar los datos a otro activity
            @Override
            public ViewHolderUsuarios onCreateViewHolder(ViewGroup parent, int viewType){

                ViewHolderUsuarios viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new ViewHolderUsuarios.ClickListener(){
                    @Override
                    public void onItemClick(View view, int position) {
                        //Views
                        TextView  idUsuarioEnviar = view.findViewById(R.id.cardFollowerUid);

                        //Obtenemos la informacion desde las views.
                        String idUsuario = idUsuarioEnviar.getText().toString();



                        Intent  intent = new Intent (view.getContext(), PerfilPersonaActivity.class);
                        ByteArrayOutputStream stream  = new ByteArrayOutputStream();
                        //mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        //byte[] bytes = stream.toByteArray();

                        //intent.putExtra("image",bytes); //poner el bitmap images como un vector de bytes
                        intent.putExtra("idUsuarioPersona", idUsuario);

                        startActivity(intent);


                    }

                    @Override
                    public void onItemlongClick(View view, int position) {
                        //Cuando presione cuando sea largo, posiblemente no lo usemos
                        Toast.makeText(getActivity(), "Click largo", Toast.LENGTH_SHORT).show();
                    }
                });



                //  return super.onCreateViewHolder(parent, viewType); Lo quitamos para darle click
                return viewHolder;

            }

        };

        //Set adaptador al recycler

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }



}
