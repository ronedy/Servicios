package comi.carlos.servicios.Modelos;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import comi.carlos.servicios.R;

public class ViewHolder extends RecyclerView.ViewHolder {


    View mView;

    //Para cargar los datos del usuario que publico esto
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;

    String uidUser;
    String uidServicio;
    String nombreUsuarioPublicado;

    //Para enviar los parametros para el otro activity
    TextView idServicioEnviar;
    TextView idUsuarioEnviar;


    public ViewHolder(View itemView){
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
    public void setDetails(Context ctx, String uid, String nombre, String descripcion, String direccion, String color, final String uidUsuario){
        //Vistas
        TextView nombreView = mView.findViewById(R.id.cardviewNombre);
        TextView descripcionView = mView.findViewById(R.id.cardviewDescripcion);
        TextView direccionView = mView.findViewById(R.id.cardviewDireccion);
        LinearLayout imageViewBackground = (LinearLayout) mView.findViewById(R.id.personalizarColor);
       // ImageView imagenView = mView.findViewById(R.id.ImagenView);
        //Para cargar los datos del usuario
        final TextView uidUsuarioView = mView.findViewById(R.id.cardviewNombreUsuario);

        //Para colocar los uid de los respectivos anuncios, incluyendo al usuario.
        TextView uidServicioSendView = mView.findViewById(R.id.cardviewIdServicio);
        TextView uidUsuarioSendView = mView.findViewById(R.id.cardviewIdUsuario);

        nombreView.setText(nombre);
        descripcionView.setText(descripcion);
        direccionView.setText(direccion);
        uidServicioSendView.setText(uid); //el uid del servicio
        uidUsuarioSendView.setText(uidUsuario);

        //AHORA COLOCAMOS EL COLOR DE LA CARDVIEW

        if(color.equals("Celeste")){
            //imageViewBackground.setBackgroundColor('#2196F3');
            imageViewBackground.setBackgroundColor(Color.parseColor("#2196F3"));
        }
        if(color.equals("Rojo")){
            imageViewBackground.setBackgroundColor(Color.parseColor("#dd2c00"));
        }

        if(color.equals("Amarillo")){
            imageViewBackground.setBackgroundColor(Color.parseColor("#f9a825"));
        }

        if(color.equals("Verde")){
            imageViewBackground.setBackgroundColor(Color.parseColor("#689f38"));
        }

        if(color.equals("Naranja")){
            imageViewBackground.setBackgroundColor(Color.parseColor("#f57f17"));
        }

        if(color.equals("Violeta")){
            imageViewBackground.setBackgroundColor(Color.parseColor("#ab47bc"));
        }



        //Picasso.get().load(image).into(ImagenView);
       // uidUsuarioView.setText(uidUsuario);

        inicializarFirebase(); //Muchas veces, tal vez lo pueda volver lento

        //Para agregarle el nombre al usuario desde el ID (Metodo un poco dificil)
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Usuario p = objSnaptshot.getValue(Usuario.class);

                    String uid  = p.getUid();

                    if (uidUsuario.equals(uid)){
                        uidUsuarioView.setText(p.getNombre());
                    }

                }
                //salgo del for

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener{
            void onItemClick(View view, int position);
            void onItemlongClick(View view, int position);
            }

            public void setOnClickListener(ViewHolder.ClickListener clickListener){

        mClickListener = clickListener;
            }







    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }


    private void cargarNombrePublicante() {

        databaseReference.child("Servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Servicio s = objSnaptshot.getValue(Servicio.class);

                     uidServicio = s.getUidUsuario();


                 //Segundo for para recorrer el servicio
                    databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                                Usuario u = objSnaptshot.getValue(Usuario.class);

                                if (uidServicio.equals(u.getUid())){
                                   nombreUsuarioPublicado = u.getNombre();

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }) ;


                }
                //salgo del for

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
