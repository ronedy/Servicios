package comi.carlos.servicios;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.Arrays;

//agregando
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button btnRegistrar;
    private Button botoneye;
    private EditText password;

    //para facebook
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    //Para Google
   // private GoogleApiClient googleApiClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // btnRegistrar = (Button) findViewById(R.id.botonregistrarse);
        //password = (EditText) findViewById(R.id.txtContrasena);
        //botoneye = (Button) findViewById(R.id.botoneye);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //for facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //cuando accese por facebook vamos a darle un token a firebase
                //no solamente nos iremos al menu

               handleFacebookAccessToken(loginResult.getAccessToken());

            }




            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Se canceló la operación", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Ocurrió un error al ingresar", Toast.LENGTH_SHORT).show();

            }
        });



       firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //se ejecuta cuando algo cambia de la aplicacion
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    goMainScreen();
                }

            }
        };

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        //Para evitar errores
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);

        //este metodo es para iniciar sesion con firebase
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {

                //se ejecuta cuando termina all the process

                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Error token", Toast.LENGTH_LONG).show(); //Aqui deberia haber algo en string
                }

                progressBar.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);


            }
        });

    }

    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode,resultCode,data);

    }

    //Para iniciar esto lo haremos del OnStart del activity

    @Override
    protected void onStart() {
        super.onStart();
       firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    //cuando dejemos de escuchar lo mejor es en el metodo onStop del activity

    @Override
    protected void onStop() {
        super.onStop();
       firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }


    //Metodos para que funcione el login que no tienen que ver con facebook



/*
    public void onClick(View view){
        Intent miIntent =null;

        switch(view.getId()){
          //  case R.id.botonregistrarse:
            //    miIntent = new Intent(LoginActivity.this, RegistrarseActivity.class);
              //  break;

        }

        if(miIntent !=null){
            startActivity(miIntent);
        }

    }

    public void hideAndShow(){

        botoneye.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                password.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;

                            case MotionEvent.ACTION_UP:
                                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                break;
                        }
                        return true;
                    }
                });
    }*/


}
