package comi.carlos.servicios.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import comi.carlos.servicios.ConfirmacionServicioActivity;
import comi.carlos.servicios.MainActivity;
import comi.carlos.servicios.Modelos.Servicio;
import comi.carlos.servicios.Modelos.Usuario;
import comi.carlos.servicios.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class NuevoServicioFragment extends Fragment {

    View view;
    private EditText campoServicioNombre;
    private EditText campoServicioDescripcion;
   // private EditText campoServicioDisponibilidad;
    //CHECKBOXs
    private CheckBox campoDomingo, campoLunes, campoMartes, campoMiercoles, campoJueves, campoViernes, campoSabado;
    private Spinner categoriaServicio;
    private Spinner colorServicio;



    private EditText campoServicioHorario;
    private EditText campoServicioDireccion;
     private Button btnServicioPublicar;







    //Lo hice global para que estuviera en todos mis metodos

    //Para poder guardar en las tablas
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser userFB = FirebaseAuth.getInstance().getCurrentUser(); //vamos a obtener el usuario logeado para obtener sus datos



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_nuevoservicio,container,false);


        campoServicioNombre =  view.findViewById(R.id.servicioNombre);
        campoServicioDescripcion = view.findViewById(R.id.servicioDescripcion);
       // campoServicioDisponibilidad = view.findViewById(R.id.servicioDisponibilidad);
        campoServicioHorario = view.findViewById(R.id.servicioHorario);
        campoServicioDireccion = view.findViewById(R.id.servicioDireccion);
        btnServicioPublicar =(Button) view.findViewById(R.id.btnPublicar);
        categoriaServicio = (Spinner) view.findViewById(R.id.servicioCategoria);
        colorServicio = (Spinner) view.findViewById(R.id.servicioColor);

        campoDomingo = (CheckBox) view.findViewById(R.id.checkDomingo);
        campoLunes = (CheckBox) view.findViewById(R.id.checkLunes);
        campoMartes = (CheckBox) view.findViewById(R.id.checkMartes);
        campoMiercoles = (CheckBox) view.findViewById(R.id.checkMiercoles);
        campoJueves = (CheckBox) view.findViewById(R.id.checkJueves);
        campoViernes = (CheckBox) view.findViewById(R.id.checkViernes);
        campoSabado = (CheckBox) view.findViewById(R.id.checkSabado);


        inicializarFirebase();



       btnServicioPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarServicio();
            }
        });









       return view;

    }

    private void inicializarFirebase(){
        //FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); //Linea para activar la persistencia LA COMENTAMOS PORQUE ESTA MANERA ES INCORRECTA
        databaseReference = firebaseDatabase.getReference();
    }

    private void registrarServicio(){
        //faltan hacer validaciones
        try{
            //Validaciones

            if (campoServicioNombre.length()==0 || campoServicioDescripcion.length()==0 || campoServicioDireccion.length()==0){
                Toast.makeText(getContext(), "Hacen falta algunos detalles de tu servicio", Toast.LENGTH_SHORT).show();
            }
            else{

                Servicio s = new Servicio();
                s.setUid(UUID.randomUUID().toString());
                s.setNombre(campoServicioNombre.getText().toString().trim()); //trim espacios en blanco los ignora
                s.setDescripcion(campoServicioDescripcion.getText().toString().trim());
               s.setCategoria(categoriaServicio.getSelectedItem().toString());
                //Ahora van los checkbox

                if (campoDomingo.isChecked()){
                    s.setDomingo(true);
                }else{
                    s.setDomingo(false);
                }

                if (campoLunes.isChecked()){
                    s.setLunes(true);
                }else{
                    s.setLunes(false);
                }

                if (campoMartes.isChecked()){
                    s.setMartes(true);
                }else{
                    s.setMartes(false);
                }

                if (campoMiercoles.isChecked()){
                    s.setMiercoles(true);
                }else{
                    s.setMiercoles(false);
                }

                if (campoJueves.isChecked()){
                    s.setJueves(true);
                }else{
                    s.setJueves(false);
                }

                if (campoViernes.isChecked()){
                    s.setViernes(true);
                }else{
                    s.setViernes(false);
                }

                if (campoSabado.isChecked()){
                    s.setSabado(true);
                }else{
                    s.setSabado(false);
                }


                //Seguimos con el resto de los campos
                s.setHorario(campoServicioHorario.getText().toString().trim()); //Utilizamos el campo Telefono porque posiblemente no pueda tener telefono en FB
                s.setDireccion(campoServicioDireccion.getText().toString().trim());
                s.setColor(colorServicio.getSelectedItem().toString());
                s.setUidUsuario(userFB.getUid().trim());
                //Iremos a guardar tambien la posicion de los combobox
                s.setPosicionCategoria(String.valueOf(categoriaServicio.getSelectedItemPosition()));
                s.setPosicionColor(String.valueOf(colorServicio.getSelectedItemPosition()));


                //metodo para actualizar
                databaseReference.child("Servicio").child(s.getUid()).setValue(s); //cambiamos el valor por el objeto


                Toast.makeText(getActivity(),"Servicio registrado correctamente",Toast.LENGTH_SHORT).show(); //Mostrar otro fragment, en el FUTURO

                limpiarCajas();
                goMainConfirmation();

            }

        }catch (Exception e){
            Toast.makeText(getActivity(),"No se ha podido publicar",Toast.LENGTH_SHORT).show();

        }

    }

    private void goMainConfirmation(){
        Intent intent = new Intent(getActivity(), ConfirmacionServicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void limpiarCajas() {
        campoServicioNombre.setText("");
        campoServicioDescripcion.setText("");
        campoServicioDireccion.setText("");
        campoServicioHorario.setText("");
        campoServicioDireccion.setText("");
    }


}
