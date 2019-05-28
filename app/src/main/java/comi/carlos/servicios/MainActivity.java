package comi.carlos.servicios;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.support.v4.app.Fragment; //Borrar
import android.view.LayoutInflater;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import comi.carlos.servicios.Fragments.HomeFragment;
import comi.carlos.servicios.Fragments.NuevoServicioFragment;
import comi.carlos.servicios.Fragments.PersonFragment;
import comi.carlos.servicios.Fragments.SearchFragment;
import comi.carlos.servicios.Modelos.ModeloRecycler;
import comi.carlos.servicios.Modelos.Usuario;
import comi.carlos.servicios.Modelos.ViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;
import android.support.v4.view.MenuItemCompat;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Implementa el NavigationView para poder darle acciones a los items que se puedan seleccionar

     //Para el menu lateral de la izquierda
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;

  //Para inicializar las variables de Firebase y poder hacer distintas cosas entre las cuales estan
    //-VERIFICAR SI EL USUARIO EXISTE O YA FUE REGISTRADO ANTERIORMENTE

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    DatabaseReference mRef2;//Para buscar en recyclerview
    RecyclerView mRecyclerView2;
    FirebaseDatabase mFirebaseDatabase2;


    //Para ver las listas de usuarios que hay
   private List<Usuario> listUsuarios = new ArrayList<Usuario>();
    ArrayAdapter<Usuario> arrayAdapterPersona;

    ListView listV_usuarios;
    Usuario usuarioSelected;

    //Para saber si esta registrado
    boolean isRegistrado = false;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // es publico
    NavigationView navigationView; //para el menu lateral

    //Creacion de la barra de menu lateral



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Consultamos a la base de datos
        mFirebaseDatabase2 = FirebaseDatabase.getInstance();
        mRef2 = mFirebaseDatabase2.getReference("Servicio");

      //  NavigationView navigationView; //para el menu lateral
        drawerLayout = findViewById(R.id.drawableId); //Encontrando el menu
        navigationView = findViewById(R.id.navigationId);
        navigationView.setNavigationItemSelectedListener(this); //para darle la instancia


//Encontrando los elementos dentro del header en el menú

        /*mRecyclerView2 =(RecyclerView)findViewById(R.id.pictureRecyclerview);
        mRecyclerView2.setHasFixedSize(true);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));*/

        TextView menuNombreUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menuNombreUsuario);
        TextView menuCorreoUsuario = (TextView) navigationView.getHeaderView(0).findViewById(R.id.menuCorreoUsuario);
        CircleImageView menuFotoUsuario = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.menuImagenPerfil);


        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuario");

        Fragment selectedFragment = new SearchFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        inicializarFirebase();
        listarDatos(); //Para cargar los datos del perfil

        //Para ver si accedio desde firebase
        // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //SI YA INICIO SESION

        if (user!=null){

//PRUEBA PARA VER SI EL USUARIO ES NULLO O NO
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

         //AGREGANDO LOS DATOS DEL USUARIO AL MENU LATERAL NAVIGATIONVIEW

            menuNombreUsuario.setText(name);
            menuCorreoUsuario.setText(email);
            Glide.with(this).load(user.getPhotoUrl()).crossFade().centerCrop().into(menuFotoUsuario);


      }else{
            //SINO QUE LO MANDE AL LOGIN
            goLoginScreen();
        }



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }




    private void listarDatos() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUsuarios.clear(); //por si tiene algo almacenado en caché
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Usuario p = objSnaptshot.getValue(Usuario.class);

                    listUsuarios.add(p);
                  //  Toast.makeText(MainActivity.this, "Nombre: " + p.getUid(), Toast.LENGTH_SHORT).show();
                    String uid = p.getUid();

                    if(user.getUid().equals(uid)){
                        isRegistrado = true;
                    }

                  //  arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    //listV_personas.setAdapter(arrayAdapterPersona);
                }

                if (isRegistrado==true){
                    //Toast.makeText(MainActivity.this, "Usuario SI registrado", Toast.LENGTH_SHORT).show();
                }else{
                   // Toast.makeText(MainActivity.this, "Usuario NO registrado", Toast.LENGTH_SHORT).show();
                    goRegistrarseScreen();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    //Metodos para hacer distintas cosas

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goRegistrarseScreen(){
        Intent intent = new Intent(this, RegistrarseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout (){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();;
        goLoginScreen();
    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){

                        case R.id.buscar:
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.home:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.agregarServicio:
                            selectedFragment = new NuevoServicioFragment();
                            break;

                        case R.id.yo:
                            selectedFragment = new PersonFragment();
                            break;


                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,

                            selectedFragment).commit();

                    return true;
                }
            };


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment selectedFragment = null; //para pasar entre los fragments



        if(menuItem.getItemId()==R.id.menuHome)
        {
            //Para hacer funcionar los fragment
            selectedFragment = new HomeFragment(); //Para poder pasar al fragment seleccionado
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, //Para pasar el fragment al contenedor de fragments
                    selectedFragment).commit();

            //Para seleccionar


            drawerLayout.closeDrawers(); //Para cerrar el menu despues de seleccionar un item
        }

        if(menuItem.getItemId()==R.id.menuBuscar)
        {
            //Para hacer funcionar los fragment
            selectedFragment = new SearchFragment(); //Para poder pasar al fragment seleccionado
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, //Para pasar el fragment al contenedor de fragments
                    selectedFragment).commit();

            drawerLayout.closeDrawers(); //Para cerrar el menu despues de seleccionar un item
        }

        if(menuItem.getItemId()==R.id.menuYo)
        {
            //Para hacer funcionar los fragment
            selectedFragment = new PersonFragment(); //Para poder pasar al fragment seleccionado
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, //Para pasar el fragment al contenedor de fragments
                    selectedFragment).commit();
            drawerLayout.closeDrawers(); //Para cerrar el menu despues de seleccionar un item
        }

        if(menuItem.getItemId()==R.id.MenuNuevo)
        {

            //Para hacer funcionar los fragment
            selectedFragment = new NuevoServicioFragment();//Para poder pasar al fragment seleccionado
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, //Para pasar el fragment al contenedor de fragments
                    selectedFragment).commit();
            drawerLayout.closeDrawers();//Para cerrar el menu despues de seleccionar un item

        }

        if(menuItem.getItemId()==R.id.menuLogout)
        {
            //Para hacer funcionar los fragment
           logout();
           goLoginScreen();
        }

        if(menuItem.getItemId()==R.id.acercaDe)
        {

            Intent intent = new Intent(getApplicationContext(), InformacionActivity.class);
            startActivity(intent);

        }


        return false;
    }



   /* private void firebasSearch(String searchText){


         try {

             Query firebaseSearchQuery = mRef2.orderByChild("nombre").startAt(searchText).endAt(searchText + "\uf8ff");
             FirebaseRecyclerAdapter<ModeloRecycler,ViewHolder> firebaseRecyclerAdapter =
                     new FirebaseRecyclerAdapter<ModeloRecycler, ViewHolder>(
                             ModeloRecycler.class,
                             R.layout.cardview_picture,
                             ViewHolder.class,
                             firebaseSearchQuery ) {
                         @Override
                         protected void populateViewHolder(ViewHolder viewHolder, ModeloRecycler model, int position) {

                             viewHolder.setDetails(getApplicationContext(), model.getNombre(), model.getDescripcion(), model.getDireccion(), model.getUidUsuario() );
                         }
                     };

             mRecyclerView2.setAdapter(firebaseRecyclerAdapter);

         }catch (Exception e){

             Toast.makeText(this,"Ha habido un error" + e,Toast.LENGTH_SHORT).show();

         }
    }*/



  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                firebasSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                firebasSearch(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }

      if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


     /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/




}
