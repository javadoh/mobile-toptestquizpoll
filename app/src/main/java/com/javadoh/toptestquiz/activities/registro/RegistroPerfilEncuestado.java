package com.javadoh.toptestquiz.activities.registro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.javadoh.toptestquiz.activities.pruebas.PruebaPrincipal;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.activities.Login;
import com.javadoh.toptestquiz.activities.PostLogin;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.LoginDataBaseHelper;
import com.javadoh.toptestquiz.utils.Utils;
import com.javadoh.toptestquiz.utils.bean.exam.ExamenUsuarioNube;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioEncuestado;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luiseliberal on 18-07-2015.
 */
public class RegistroPerfilEncuestado extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = RegistroPerfilEncuestado.class.getName();
    boolean validDni = false, validNombres = false, validApellidos = false, validDireccion = false,
            validEmail = false, validEdad = false, validOcupacion = false;
    EditText dni, nombresUsuario, apellidosUsuario, direccion, edad, email,
            ocupacion; //otros1, otros2, otros3, otros4, otros5;
    RadioGroup genero;
    RadioButton masculino, femenino;
    Button btnIniciarTest, btnRegresar;
    LoginDataBaseHelper loginDataBaseAdapter;
    private int radioButtonValue = 0;
    PerfilUsuarioEncuestado usuarioEncuestado;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    PerfilUsuarioNube encuestador;
    ExamenUsuarioNube usuarioPerfilExamenes;
    private static int EXAM_WICH_AUX;
    private TextInputLayout layoutDni, layoutNombreUsuario, layoutApellidoUsuario, layoutDireccion,
            layoutEdad, layoutEmail, layoutOcupacion;
    String inputValidationOk, passwordUsedForLogin;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    //FLAG VERSION DESPUES DE KITKAT
    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_perfil_encuestado);

        //INSTANCIA DEL ADAPTADOR DE BASE DE DATOS
        loginDataBaseAdapter = new LoginDataBaseHelper(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        //GET REFERENCIAS DE LAS VISTAS
        dni = (EditText) findViewById(R.id.dniUsuario);
        layoutDni = (TextInputLayout) findViewById(R.id.layoutDni);
        dni.addTextChangedListener(new MyTextWatcher(dni));
        nombresUsuario = (EditText) findViewById(R.id.nombresUsuario);
        layoutNombreUsuario = (TextInputLayout) findViewById(R.id.layoutNombreUsuario);
        nombresUsuario.addTextChangedListener(new MyTextWatcher(nombresUsuario));
        apellidosUsuario = (EditText) findViewById(R.id.apellidosUsuario);
        layoutApellidoUsuario = (TextInputLayout) findViewById(R.id.layoutApellidoUsuario);
        apellidosUsuario.addTextChangedListener(new MyTextWatcher(apellidosUsuario));
        direccion = (EditText) findViewById(R.id.direccion);
        layoutDireccion = (TextInputLayout) findViewById(R.id.layoutDireccion);
        direccion.addTextChangedListener(new MyTextWatcher(direccion));
        genero = (RadioGroup) findViewById(R.id.genero);
        masculino = (RadioButton) findViewById(R.id.radioMasculino);
        femenino = (RadioButton) findViewById(R.id.radioFemenino);
        edad = (EditText) findViewById(R.id.edad);
        layoutEdad = (TextInputLayout) findViewById(R.id.layoutEdad);
        edad.addTextChangedListener(new MyTextWatcher(edad));
        email = (EditText) findViewById(R.id.email);
        layoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        email.addTextChangedListener(new MyTextWatcher(email));
        ocupacion = (EditText) findViewById(R.id.ocupacion);
        layoutOcupacion = (TextInputLayout) findViewById(R.id.layoutOcupacion);
        ocupacion.addTextChangedListener(new MyTextWatcher(ocupacion));
        /*otros1 = (EditText) findViewById(R.id.otros1);
        otros2 = (EditText) findViewById(R.id.otros2);
        otros3 = (EditText) findViewById(R.id.otros3);
        otros4 = (EditText) findViewById(R.id.otros4);
        otros5 = (EditText) findViewById(R.id.otros5);*/

        //VALIDAMOS QUE EL FLAG DE GOOGLE PLAY SERVICES SEA TRUE Y BUSCAMOS ADRESS
        if (Utils.isFlagGoogleServices()) {
            buildGoogleApiClient();
        } else {
            //HABILITAMOS EL CAMPO DIRECCION
            direccion.setVisibility(View.VISIBLE);
        }

        Bundle sesion = getIntent().getExtras();
        encuestador = (PerfilUsuarioNube) sesion.get("SESSION_USER");
        usuarioPerfilExamenes = (ExamenUsuarioNube) sesion.get("SESSION_EXAMS");
        passwordUsedForLogin = sesion.getString("PASSWORD_USED");

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if (!encuestador.isUser_ads_disabled()) {
            //ADMOB INICIALIZACION BANNER
            mAdView = (AdView) findViewById(R.id.adBannerView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView.loadAd(adRequest);
            //ADMOB INTERSTITIAL
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.adintersticial));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });
            requestNewInterstitial();
        }

        btnIniciarTest = (Button) findViewById(R.id.buttonIniciarTest);
        btnRegresar = (Button) findViewById(R.id.buttonCerrarSesion);

        btnIniciarTest.setOnClickListener(new View.OnClickListener() {

            private PerfilUsuarioEncuestado usuarioEncuestado;

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //ADMOB INTERSTITIAL
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                //VALIDAR QUE NO FALTE NINGUN CAMPO DEL FORMULARIO
                if (!validDni || !validNombres || !validApellidos || !validDireccion || !validEdad || !validEmail || !validOcupacion) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgRegisterPresenterValid), Toast.LENGTH_LONG).show();
                    return;
                /*} else if (diaEncuestado.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Estimado usuario, debe ingresar el d\u00eda en formato DDMMYY. " +
                            "Por favor, ingrese 6 d\u00edgitos.", Toast.LENGTH_LONG).show();
                    return;
                } else if (horaEncuestado.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Estimado usuario, debe ingresar la hora en formato MMSS. " +
                            "Por favor, ingrese 4 d\u00edgitos.", Toast.LENGTH_LONG).show();
                    return;*/
                } else {

                    String dniEncuestado = dni.getText().toString();
                    String nombresUsuarioEncuesta = nombresUsuario.getText().toString();
                    String apellidosUsuarioEncuesta = apellidosUsuario.getText().toString();

                    Calendar c = Calendar.getInstance();
                    String diaEncuestado = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
                    String horaEncuestado = new SimpleDateFormat("HH:mm:ss").format(c.getTime());
                    String direccionEncuestado = direccion.getText().toString();
                    String generoEncuestado = (radioButtonValue == 0 ? getResources().getString(R.string.txtRadioBtnMascRegPerf) : getResources().getString(R.string.txtRadioBtnFemRegPerf));
                    int edadEncuestado = Integer.parseInt(edad.getText().toString());
                    String emailEncuestado = email.getText().toString();
                    String ocupacionEncuestado = ocupacion.getText().toString();

                    final Context context = RegistroPerfilEncuestado.this;

                    final PerfilUsuarioEncuestado usuarioEncuestado = new PerfilUsuarioEncuestado();

                    usuarioEncuestado.setDniEncuestado(dniEncuestado);
                    usuarioEncuestado.setNombresEncuestado(nombresUsuarioEncuesta);
                    usuarioEncuestado.setApellidosEncuestado(apellidosUsuarioEncuesta);
                    usuarioEncuestado.setDiaEncuestado(diaEncuestado);
                    usuarioEncuestado.setHoraEncuestado(horaEncuestado);
                    usuarioEncuestado.setLocacionEncuestado(direccionEncuestado);
                    usuarioEncuestado.setGeneroEncuestado(generoEncuestado);
                    usuarioEncuestado.setEdadEncuestado(edadEncuestado);
                    usuarioEncuestado.setEmailEncuestado(emailEncuestado);
                    usuarioEncuestado.setOcupacionEncuestado(ocupacionEncuestado);


                /*PostLogin alert = new PostLogin();
                alert.showDialog((Activity)c, "Lista de Pruebas", usuarioPerfil, usuarioPerfilExamenes);*/

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getResources().getString(R.string.docsList));

                    CharSequence[] chars = {};
                    List<String> listaIdExams = new ArrayList<String>();

                    if (usuarioPerfilExamenes != null) {

                        for (int i = 0; i < usuarioPerfilExamenes.getExam_conf().size(); i++) {

                            //AGREGAMOS EN LA LISTA AUXILIAR EL ID DEL EXAMEN
                            listaIdExams.add(usuarioPerfilExamenes.getExam_conf().get(i).getExam_title());

                        }

                        chars = listaIdExams.toArray(new CharSequence[listaIdExams.size()]);

                        builder.setItems(chars,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        LayoutInflater li = LayoutInflater.from(RegistroPerfilEncuestado.this);
                                        View vistaDialogoFaq = li.inflate(R.layout.postlogin_dialog_exam_pass, null);

                                        final EditText etxtExamPass = (EditText) vistaDialogoFaq.findViewById(R.id.etxtExamPass);

                                        EXAM_WICH_AUX = which;

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                RegistroPerfilEncuestado.this);

                                        alertDialogBuilder.setView(vistaDialogoFaq);
                                        alertDialogBuilder.setTitle("EXAM PASSWORD");
                                        //set dialog message
                                        alertDialogBuilder
                                                .setCancelable(true)
                                                .setPositiveButton(getResources().getString(R.string.btnSignInLogin),
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                                String passIn = etxtExamPass.getText().toString();
                                                                try {
                                                                    if (usuarioPerfilExamenes.getExam_conf().get(EXAM_WICH_AUX).getExam_password().equalsIgnoreCase(passIn)) {

                                                                        Intent intentPruebaPrincipal = new Intent(getApplicationContext(), PruebaPrincipal.class);

                                                                        intentPruebaPrincipal.putExtra("SESSION_USER", encuestador);
                                                                        intentPruebaPrincipal.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                                                                        intentPruebaPrincipal.putExtra("SESSION_ENCUESTADO", usuarioEncuestado);
                                                                        intentPruebaPrincipal.putExtra("SESSION_EXAMID", EXAM_WICH_AUX);
                                                                        intentPruebaPrincipal.putExtra("PASSWORD_USED", passwordUsedForLogin);

                                                                        startActivity(intentPruebaPrincipal);
                                                                    } else {
                                                                        Toast.makeText(context, getResources().getString(R.string.msgPasswordExamNotMatch), Toast.LENGTH_SHORT).show();
                                                                        dialog.cancel();
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                    Log.d(TAG, "Error:", e);
                                                                }

                                                            }
                                                        });
                                        // create alert dialog
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        // show it
                                        alertDialog.show();
                                    }
                                });

                        builder.create().show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgNeedCreateTestFirst), Toast.LENGTH_LONG).show();
                    }

                }//FIN ELSE VALIDACIONES


            }//FIN ONCLICK

            private View.OnClickListener init(PerfilUsuarioEncuestado usuarioEncuestado) {
                this.usuarioEncuestado = usuarioEncuestado;
                return this;
            }
        }.init(usuarioEncuestado));


        btnRegresar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //CERRAMOS LA ACTIVIDAD DE CREAR PRUEBAS
                Intent intentRegresar = new Intent(RegistroPerfilEncuestado.this, PostLogin.class);
                intentRegresar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentRegresar.putExtra("SESSION_USER", encuestador);
                intentRegresar.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
                intentRegresar.putExtra("PASSWORD_USED", passwordUsedForLogin);

                startActivity(intentRegresar);
                finish();
            }
        });

        //EVENTOS DE RADIO BUTTONS
        genero.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                if (masculino.isChecked()) {
                    radioButtonValue = 0;
                } else if (femenino.isChecked()) {
                    radioButtonValue = 1;
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        loginDataBaseAdapter.close();
    }

    //METODOS PARA FUNCIONAMIENTO DE GOOGLE API LOCATION
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {

            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.errorInAppPayCreated),
                        Toast.LENGTH_SHORT).show();

                direccion.setText("NO GOOGLE PLAY SERVICES");
                //finish();
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(getApplicationContext(), "Sin permisos necesarios para acceder a la localización", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude()+", Longitude:"+mLastLocation.getLongitude(),Toast.LENGTH_LONG).show();

            Utils utils = new Utils();
            //utils.getAddress(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));

            direccion.setText(utils.getAddress1() + ", " + utils.getCity() +
                    ", " + utils.getState() + ", " + utils.getCountry());

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegistroPerfilEncuestado.this, PostLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SESSION_USER", encuestador);
        intent.putExtra("SESSION_EXAMS", usuarioPerfilExamenes);
        intent.putExtra("PASSWORD_USED", passwordUsedForLogin);
        startActivity(intent);
        finish();
    }

    private boolean validateAlphaNumeric(View view){

        String text = "";
        Pattern p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();

        switch (view.getId()) {

            case R.id.dniUsuario:
                text = dni.getText().toString();
                m = p.matcher(text);
                b = m.find();
                if (b){
                    layoutDni.setError(getString(R.string.errorDniRegister));
                    requestFocus(dni);
                    inputValidationOk = "ERROR";
                    validDni = false;
                    return false;
                }else{layoutDni.setErrorEnabled(false);
                    validDni = true;
                }
                break;

            case R.id.direccion:
                text = direccion.getText().toString();
                m = p.matcher(text);
                b = m.find();
                if (b){
                    layoutDireccion.setError(getString(R.string.errorDireccionEncuestado));
                    requestFocus(direccion);
                    inputValidationOk = "ERROR";
                    validDireccion = false;
                    return false;
                }else{layoutDireccion.setErrorEnabled(false);
                    validDireccion = true;
                }
                break;
        }
        return false;
    }

    private boolean validateString(View view) {

        String text = "";
        Pattern p = Pattern.compile("[^A-Za-z ]", Pattern.CASE_INSENSITIVE);
        Matcher m;
        boolean b;
        switch (view.getId()) {
            case R.id.nombresUsuario:
                text = nombresUsuario.getText().toString();
                m = p.matcher(text);
                b = m.find();
                if (b){
                    layoutNombreUsuario.setError(getString(R.string.errorNombreEncuestado));
                    requestFocus(nombresUsuario);
                    inputValidationOk = "ERROR";
                    validNombres = false;
                    return false;
                }else{layoutNombreUsuario.setErrorEnabled(false);
                    validNombres = true;
                }
                break;
            case R.id.apellidosUsuario:

                text = apellidosUsuario.getText().toString();
                m = p.matcher(text);
                b = m.find();
                if (b) {
                    layoutApellidoUsuario.setError(getString(R.string.errorApellidoEncuestado));
                    requestFocus(apellidosUsuario);
                    inputValidationOk = "ERROR";
                    validApellidos = false;
                    return false;
                }else{layoutApellidoUsuario.setErrorEnabled(false);
                    validApellidos = true;
                }
                break;

            case R.id.ocupacion:

                text = ocupacion.getText().toString();
                m = p.matcher(text);
                b = m.find();
                if (b) {
                    layoutOcupacion.setError(getString(R.string.errorOcupacionEncuestado));
                    requestFocus(ocupacion);
                    inputValidationOk = "ERROR";
                    validOcupacion = false;
                    return false;
                }else{
                    layoutOcupacion.setErrorEnabled(false);
                    validOcupacion = true;
                }
                break;
        }
        return true;
    }

    private boolean validateEdad(View view) {
        String text = edad.getText().toString();
        Pattern p = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        if(text.length() > 2 || b){
            validEdad = false;
            return false;
        }else{
            layoutEdad.setErrorEnabled(false);
            validEdad = true;
            return true;
        }
    }

    private boolean validateEmail(View view) {

        String text = email.getText().toString();

        if (text.isEmpty() || !isValidEmail(text)) {
            layoutEmail.setError(getString(R.string.errorEmailRegister));
            requestFocus(email);
            validEmail = false;
            return false;
        } else {
            layoutEmail.setErrorEnabled(false);
            validEmail = true;
            return true;
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {

            switch (view.getId()){
                case R.id.nombresUsuario:
                case R.id.apellidosUsuario:
                case R.id.ocupacion:
                    validateString(view);
                    break;
                case R.id.dniUsuario:
                case R.id.direccion:
                    validateAlphaNumeric(view);
                    break;

                case R.id.edad:
                    validateEdad(view);
                    break;

                case R.id.email:
                    validateEmail(view);
                    break;
            }
        }
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}