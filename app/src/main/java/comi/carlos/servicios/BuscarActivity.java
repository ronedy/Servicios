package comi.carlos.servicios;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.ByteArrayOutputStream;

import comi.carlos.servicios.Modelos.ModeloRecycler;
import comi.carlos.servicios.Modelos.ViewHolder;

public class BuscarActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Servicios");
        //actionBar.setDisplayHomeAsUpEnabled(true); //para colocar el boton de atrás
        //actionBar.setDisplayShowHomeEnabled(true); //para colocar el boton de atrás

        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//aqui puede dar problemas

        //Enviar query a Firebasedatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Servicio");

    }

    private void firebaseSearch(String searchText){

        //Metodo para consultar la base de datos y ver si se encucentra en anuncio
        Query firebaseSearchQuery = mRef.orderByChild("nombre").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder>(

                        ModeloRecycler.class,
                        R.layout.cardview_picture,
                        ViewHolder.class,
                        firebaseSearchQuery

                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, ModeloRecycler model, int position) {

                        viewHolder.setDetails(getApplicationContext(),model.getUid(), model.getNombre(), model.getDescripcion(), model.getDireccion(), model.getColor(), model.getUidUsuario());

                    }


                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //Views
                                TextView idUsuarioEnviar = view.findViewById(R.id.cardviewIdUsuario);
                                TextView idServicioEnviar = view.findViewById(R.id.cardviewIdServicio);
                                //Obtenemos la informacion desde las views.
                                String idUsuario = idUsuarioEnviar.getText().toString();
                                String idServicio = idServicioEnviar.getText().toString();

                                //Drawable mDrawable ? mImageView.getDrawable();
                                //Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

                                //Ahora pasamos la informacion al activity //CARLOS

                                Intent intent = new Intent(view.getContext(), PerfilAnuncioActivity.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
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
                              //  Toast.makeText(getApplicationContext(), "Click largo", Toast.LENGTH_SHORT).show();
                            }
                        });


                        return viewHolder;


                    }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //Metodo para manda a traer el shape donde esta el icono de buscar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView  = (SearchView)MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Cargar los datos en el onstart

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder>(

                        ModeloRecycler.class,
                        R.layout.cardview_picture,
                        ViewHolder.class,
                        mRef

                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, ModeloRecycler model, int position) {

                        viewHolder.setDetails(getApplicationContext(), model.getUid(), model.getNombre(), model.getDescripcion(), model.getDireccion(), model.getColor(), model.getUidUsuario());
                    }

                    //Para poder enviar los datos a otro activity
                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //Views
                                TextView idUsuarioEnviar = view.findViewById(R.id.cardviewIdUsuario);
                                TextView idServicioEnviar = view.findViewById(R.id.cardviewIdServicio);
                                //Obtenemos la informacion desde las views.
                                String idUsuario = idUsuarioEnviar.getText().toString();
                                String idServicio = idServicioEnviar.getText().toString();

                                //Drawable mDrawable ? mImageView.getDrawable();
                                //Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

                                //Ahora pasamos la informacion al activity //CARLOS

                                Intent intent = new Intent(view.getContext(), PerfilAnuncioActivity.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
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
                                Toast.makeText(getApplicationContext(), "Click largo", Toast.LENGTH_SHORT).show();
                            }
                        });


                        return viewHolder;


                    }

                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
