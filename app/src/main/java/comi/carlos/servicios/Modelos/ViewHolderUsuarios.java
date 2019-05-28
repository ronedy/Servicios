package comi.carlos.servicios.Modelos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import comi.carlos.servicios.Fragments.HomeFragment;
import comi.carlos.servicios.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderUsuarios extends RecyclerView.ViewHolder {

    View mView;

    //Para cargar los datos del usuario que publico esto
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario

    String uidUser;
    String uidServicio;
    String nombreUsuarioPublicado;

    public ViewHolderUsuarios(View itemView){
        super(itemView);

        mView = itemView;

        //Item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        //Item Long Click
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemlongClick(view,getAdapterPosition());
                return true;
            }
        });



    }


    //Set details to recycler view row ( I mean the cardview
    public void setDetails( final Context ctx, final String uid, final String nombre, final String telefono, final String direccion, final String urlFoto){

        final CardView cardView = mView.findViewById(R.id.CardViewFollowers);


       final TextView nombreView = mView.findViewById(R.id.cardFollowerNombre);
       final TextView telefonoView = mView.findViewById(R.id.cardFollowerTelefono);
       final  TextView direccionView = mView.findViewById(R.id.cardFollowerDireccion);
        final CircleImageView imagenView = mView.findViewById(R.id.imgPerfil);
       final TextView uidUsuarioSendView = mView.findViewById(R.id.cardFollowerUid);
       final TextView tageo = mView.findViewById(R.id.tageo);
        final TextView followersCantidad = mView.findViewById(R.id.cardFollowersUsuario);
        final TextView postCantidad = mView.findViewById(R.id.cardFollowersPost);

        nombreView.setText(nombre);
        telefonoView.setText(telefono);
        direccionView.setText(direccion);
        uidUsuarioSendView.setText(uid); //el uid del servicio
        //uidUsuarioSendView.setText(uidUsuario);
        Picasso.with(ctx).load(urlFoto).into(imagenView);
        cardView.setVisibility(View.VISIBLE);



        inicializarFirebase();

        databaseReference.child("Seguidor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int corazones=0;
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Seguidor s = objSnaptshot.getValue(Seguidor.class);

                    if(s.getSigueA().equals(user.getUid()) && s.getUsuario().equals(uid) && s.getActivo()==true){
                        //Vistas

                        tageo.setText("Seguidor");
                        tageo.setBackgroundResource(R.drawable.boton_redondo_celestito);

                    }

                    if (uid.equals(s.getSigueA()) && s.getActivo()==true){
                        corazones++;
                    }

                }

                followersCantidad.setText(String.valueOf(corazones));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Referencia

        //Ahora cargaré los POST
        databaseReference.child("Servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int post=0;
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Servicio s = objSnaptshot.getValue(Servicio.class);

                    if(uid.equals(s.getUidUsuario()) && user!=null){
                        post++;
                    }

                }
                //salgo del for
                postCantidad.setText(String.valueOf(post));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private ViewHolderUsuarios.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemlongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderUsuarios.ClickListener clickListener){

        mClickListener = clickListener;
    }




    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }

}
