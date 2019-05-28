package comi.carlos.servicios.Fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;

import comi.carlos.servicios.BuscarActivity;
import comi.carlos.servicios.Modelos.ModeloRecycler;
import comi.carlos.servicios.Modelos.ViewHolder;
import comi.carlos.servicios.PerfilAnuncioActivity;
import comi.carlos.servicios.R;

public class SearchFragment extends Fragment {

    View vista;

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private Button btnBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_search, container, false);


        //RecyclerView
        mRecyclerView = vista.findViewById(R.id.pictureRecyclerview);
        mRecyclerView.setHasFixedSize(true);
        btnBuscar = (Button) vista.findViewById(R.id.btnBuscar);


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarActivity();
            }
        });

        //set layout as LinearLayout

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //aqui puede dar problemas

        //Enviar query a Firebasedatabase

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Servicio");


        return vista;
    }


    //Cargar los datos en el onstart


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder>(
                ModeloRecycler.class,
                R.layout.cardview_picture,
                ViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, ModeloRecycler model, int position) {

                viewHolder.setDetails(getContext(),model.getUid(), model.getNombre(), model.getDescripcion(), model.getDireccion(), model.getColor(), model.getUidUsuario()); //aqui puede tronar

            }

            //Para poder enviar los datos a otro activity
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

               ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
               viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                   @Override
                   public void onItemClick(View view, int position) {
                       //Views
                       TextView  idUsuarioEnviar = view.findViewById(R.id.cardviewIdUsuario);
                       TextView idServicioEnviar = view.findViewById(R.id.cardviewIdServicio);
                       //Obtenemos la informacion desde las views.
                       String idUsuario = idUsuarioEnviar.getText().toString();
                       String idServicio = idServicioEnviar.getText().toString();

                       //Drawable mDrawable ? mImageView.getDrawable();
                       //Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

                       //Ahora pasamos la informacion al activity //CARLOS

                       Intent  intent = new Intent (view.getContext(), PerfilAnuncioActivity.class);
                       ByteArrayOutputStream stream  = new ByteArrayOutputStream();
                       //mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                       //byte[] bytes = stream.toByteArray();

                       //intent.putExtra("image",bytes); //poner el bitmap images como un vector de bytes
                       intent.putExtra("idUsuario", idUsuario);
                       intent.putExtra("idServicio", idServicio);
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

    private void buscarActivity() {
        Intent intent = new Intent (getActivity(), BuscarActivity.class);
        startActivity(intent);

    }




}
