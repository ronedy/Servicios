package comi.carlos.servicios;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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

import comi.carlos.servicios.Modelos.Servicio;
import comi.carlos.servicios.Modelos.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarServicio extends AppCompatActivity {

    private String recibirIdUser; //para guardar el usuario que va a editar el servicio
    private String recibirIdServicio; //para hacer la consulta en la base de datos
    private EditText eServicioNombre;
    private EditText eServicioDescripcion;
    // private EditText campoServicioDisponibilidad;
    //CHECKBOXs
    private CheckBox eDomingo, eLunes, eMartes, eMiercoles, eJueves, eViernes, eSabado;

    private Spinner eCategoriaServicio;
    private Spinner eColorServicio;
    private EditText eServicioHorario;
    private EditText eServicioDireccion;
    private Button btnServicioGuardar;
    private Button btnEliminar;

    //Datos del usuario;
    private TextView nombreUsuario;
    private CircleImageView imagenUsuario;

    //INSTANCIAS DE FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Para obtener los datos del usuario


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_servicio);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Editar Servicio");


        //Para el usuario
        nombreUsuario = (TextView) findViewById(R.id.editarServicioPublicante);
        imagenUsuario = (CircleImageView) findViewById(R.id.editarServicioImagenPerfil);

        //Recibimos el id a editar
        //Obtenemos la informacion del intent
        recibirIdServicio = getIntent().getStringExtra("idServicioSend");
        recibirIdUser = getIntent().getStringExtra("idUsuarioSend");


        eServicioNombre = (EditText)  findViewById(R.id.editarServicioNombre);
        eServicioDescripcion = (EditText) findViewById(R.id.editarServicioDescripcion);
        // campoServicioDisponibilidad = view.findViewById(R.id.servicioDisponibilidad);
        eServicioHorario = (EditText) findViewById(R.id.editarServicioHorario);
        eServicioDireccion = (EditText) findViewById(R.id.editarServicioDireccion);
        btnServicioGuardar =(Button) findViewById(R.id.btnEditarServicio);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);
        eCategoriaServicio = (Spinner) findViewById(R.id.editarServicioCategoria);
        eColorServicio = (Spinner) findViewById(R.id.editarServicioColor);

        eDomingo = (CheckBox) findViewById(R.id.echeckDomingo);
        eLunes = (CheckBox) findViewById(R.id.echeckLunes);
        eMartes = (CheckBox) findViewById(R.id.echeckMartes);
        eMiercoles = (CheckBox) findViewById(R.id.echeckMiercoles);
        eJueves = (CheckBox) findViewById(R.id.echeckJueves);
        eViernes = (CheckBox) findViewById(R.id.echeckViernes);
        eSabado = (CheckBox) findViewById(R.id.echeckSabado);

        inicializarFirebase();
        //Primero debemos cargar al usuario para obtener su ID
        //Podiamos haberlo hecho de la manera facil con el user.getNombre()
        //Pero esta diseñado para poderlo modificar desde un futuro
        cargarPerfilUsuario();

        cargarServicioSeleccionado(); //cargamos el Servicio que el usuario va a editar


        btnServicioGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarServicio();
                goMainScreen();
            }
        });

        //Dandole accion al boton de eliminar

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarServicio();
            }
        });




    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void eliminarServicio() {

        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Confirmación de eliminación de servicio");
        dialogo1.setMessage("¿Estas seguro que quieres eliminar de manera permanente tu servicio: "+ eServicioNombre.getText().toString() + "?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                try{
                    Servicio s = new Servicio();
                    s.setUid(recibirIdServicio);
                    //para eliminar
                    databaseReference.child("Servicio").child(s.getUid()).removeValue();
                    Toast.makeText(EditarServicio.this, "Servicio eliminado correctamente", Toast.LENGTH_SHORT).show();
                    goMainScreen();

                }catch (Exception e){
                    Toast.makeText(EditarServicio.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {


            }
        });
        dialogo1.show();

    }

    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }



    private void cargarServicioSeleccionado() {
        databaseReference.child("Servicio").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Servicio s = objSnaptshot.getValue(Servicio.class);

                    String uid = s.getUid();


                    if(recibirIdServicio.equals(uid) && user!=null){

                        eServicioNombre.setText(s.getNombre());
                        eServicioDescripcion.setText(s.getDescripcion());
                        eServicioDireccion.setText(s.getDireccion());
                        eServicioHorario.setText(s.getHorario());
                       eCategoriaServicio.setSelection(Integer.parseInt(s.getPosicionCategoria())); //accedemos a las posiciones desde db
                       eColorServicio.setSelection(Integer.parseInt(s.getPosicionColor())); //accedemos a las posiciones desde la db


                        if (s.isDomingo()){
                            eDomingo.setChecked(true);
                        }
                        if (s.isLunes()){
                            eLunes.setChecked(true);
                        }

                        if (s.isMartes()){
                            eMartes.setChecked(true);
                        }
                        if (s.isMiercoles()){
                            eMiercoles.setChecked(true);
                        }
                        if (s.isJueves()){
                            eJueves.setChecked(true);
                        }
                        if (s.isViernes()){
                            eViernes.setChecked(true);
                        }
                        if (s.isSabado()){
                            eSabado.setChecked(true);
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

    private void cargarPerfilUsuario() {
        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Usuario u = objSnaptshot.getValue(Usuario.class);

                    String uid = u.getUid();


                    if(recibirIdUser.equals(uid) && user!=null){

                       nombreUsuario.setText(u.getNombre());
                        Glide.with(getApplicationContext()).load(u.getUrlFoto()).crossFade().centerCrop().into(imagenUsuario);

                    }

                }
                //salgo del for

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void editarServicio() {
        try {

            if (eServicioNombre.length() == 0 || eServicioDescripcion.length() == 0 || eServicioDireccion.length() == 0) {
                Toast.makeText(this, "Hace falta llenar algunos campos", Toast.LENGTH_SHORT).show();
            } else {
                Servicio s = new Servicio();
                s.setUid(recibirIdServicio);
                s.setNombre(eServicioNombre.getText().toString().trim()); //trim espacios en blanco los ignora
                s.setDescripcion(eServicioDescripcion.getText().toString().trim());
                s.setHorario(eServicioHorario.getText().toString().trim());
                s.setDireccion(eServicioDireccion.getText().toString().trim());
                s.setCategoria(eCategoriaServicio.getSelectedItem().toString().trim());
                s.setColor(eColorServicio.getSelectedItem().toString().trim());
                s.setUidUsuario(recibirIdUser);

                if (eDomingo.isChecked()){
                    s.setDomingo(true);
                }else{
                    s.setDomingo(false);
                }

                if (eLunes.isChecked()){
                    s.setLunes(true);
                }else{
                    s.setLunes(false);
                }

                if (eMartes.isChecked()){
                    s.setMartes(true);
                }else{
                    s.setMartes(false);
                }

                if (eMiercoles.isChecked()){
                    s.setMiercoles(true);
                }else{
                    s.setMiercoles(false);
                }

                if (eJueves.isChecked()){
                    s.setJueves(true);
                }else{
                    s.setJueves(false);
                }

                if (eViernes.isChecked()){
                    s.setViernes(true);
                }else{
                    s.setViernes(false);
                }

                if (eSabado.isChecked()){
                    s.setSabado(true);
                }else{
                    s.setSabado(false);
                }


                s.setPosicionCategoria(String.valueOf(eCategoriaServicio.getSelectedItemPosition()));
                s.setPosicionColor(String.valueOf(eColorServicio.getSelectedItemPosition()));


                //metodo para actualizar
                databaseReference.child("Servicio").child(recibirIdServicio).setValue(s); //cambiamos el valor por el objeto
                //DAR MENSAJE DE QUE SE EDITO CORRECTAMENTE
                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

    }







}
