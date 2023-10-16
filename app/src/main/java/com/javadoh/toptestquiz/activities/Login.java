package com.javadoh.toptestquiz.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GeneralHttpTask;
import com.javadoh.toptestquiz.utils.GoogleInAppPayUtils;
import com.javadoh.toptestquiz.utils.Utils;
import com.javadoh.toptestquiz.utils.LoginDataBaseHelper;
import com.javadoh.toptestquiz.utils.bean.DynamicTestResponse;
import com.javadoh.toptestquiz.utils.bean.MemoryBean;
import com.javadoh.toptestquiz.utils.bean.user.PerfilUsuarioNube;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

/**
* Created by luiseliberal on 29-06-2015.
*/

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getName();
    Button btnSignIn;
    TextView btnSignUp, txtForgotPass;
    EditText editTextUserNameToLogin, editTextPasswordToLogin, editTextUserLoginReg, editTextUserDni,
            editTextUserEmail, editTextPassword, editTextConfirmPassword, editTextPasswordExams,
            editTextPasswordExamsConfirm;
    LoginDataBaseHelper loginDataBaseAdapter;
    String url = "";
    PerfilUsuarioNube usuarioPerfil;
    JSONObject jsonObject;
    //ADMOB
    AdView mAdView;
    //ADMOB INTERSTITIAL
    InterstitialAd mInterstitialAd;
    //IN APP BILLING STORE
    GoogleInAppPayUtils inAppPayApi = new GoogleInAppPayUtils(Login.this);
    private Toolbar toolbar;
    private boolean flagCancelDialogSignUp;
    private TextInputLayout layoutTextUser, layoutTextPassword, layoutTextUserReg, layoutTextPassReg,
            layoutTextPassConfirmReg, layoutTextPassExamReg, layoutTextPassExamConfReg,
            layoutTextDniReg, layoutTextEmailReg;
    private static String inputValidationOk;
    //DESIGN
    private Typeface face;
    boolean validEmail = false, validUserLogin = false, validPassword = false, validSecondaryPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        try {
            //VERIFICAMOS LA CONEXION A INTERNET Y DEPENDIENDO DE ELLO INICIAMOS O NO IN APP PAY CALL
            isConnectedToInternet();
        }catch (Exception e){
            Toast.makeText(this, getResources().getString(R.string.errorNoInternet), Toast.LENGTH_SHORT);
        }

        //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
        if(!Constants.isAdsDisabled) {
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        //CHEQUEAMOS SI GOOGLE PLAY SERVICES ESTA INSTALADO
        if (Utils.checkGooglePlayServices(this)) {
           Utils.setFlagGoogleServices(true);
        }else{Utils.setFlagGoogleServices(false);}

        //SET CONTEXTO GRAL PARA LOS FRAGMENTOS
        MemoryBean.setContext(getBaseContext());
        //CREAR INSTANCIA DE BASE DE DATOS SQLITE
        loginDataBaseAdapter=new LoginDataBaseHelper(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        //FONT DE ESTILO A
        face = Typeface.createFromAsset(getAssets(), "croissant_sandwich.ttf");
        //txtInfoTurnAdvice.setTypeface(face);

        //GET REFERENCIAS DE VISTA
        //imgNews = (ImageView) findViewById(R.id.imgNews);
        editTextUserNameToLogin = (EditText) findViewById(R.id.editTextUserNameToLogin);
        layoutTextUser = (TextInputLayout) findViewById(R.id.layoutTextUser);
        editTextUserNameToLogin.addTextChangedListener(new MyTextWatcher(editTextUserNameToLogin));
        editTextPasswordToLogin = (EditText) findViewById(R.id.editTextPasswordToLogin);
        layoutTextPassword = (TextInputLayout) findViewById(R.id.layoutTextPassword);
        editTextPasswordToLogin.addTextChangedListener(new MyTextWatcher(editTextPasswordToLogin));
        btnSignIn=(Button)findViewById(R.id.buttonSignIn);
        btnSignUp=(TextView)findViewById(R.id.buttonSignUp);
        txtForgotPass = (TextView)findViewById(R.id.txtForgotPass);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //VERIFICAMOS LA CONEXION A INTERNET Y DEPENDIENDO DE ELLO INICIAMOS O NO IN APP PAY CALL
            isConnectedToInternet();
        }catch (Exception e){
            Toast.makeText(this, getResources().getString(R.string.errorNoInternet), Toast.LENGTH_SHORT);
        }
    }

    //EVENTO DEL BOTON SIGN IN
    public void signIn(View V)
    {
        //GET USERNAME Y PASSWORD
        String userLogin = editTextUserNameToLogin.getText().toString();
        String password = editTextPasswordToLogin.getText().toString();

        //NUEVA CONEXION AL SERVIDOR DEJAMOS COMENTADO LA DE BD LOCAL PARA FUTURAS IMPLEMENTACIONES
        ProgressBar progressBar = new ProgressBar(Login.this);
        DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
        JSONObject jsonObject = new JSONObject();
        Dialog dialog = new Dialog(Login.this);

        try {

            if(Constants.internetOn) {
                //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.USER_LOGIN + "?user_login=" + userLogin + "&user_password=" + password;
                jsonObject.put("password_used", password);

                new GeneralHttpTask(Login.this, progressBar, dynamicTestResponse, "LOGIN", jsonObject, usuarioPerfil, dialog).execute(url);
            }else{Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_SHORT).show();}
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //EVENTO DEL BOTON SIGN UP
    public void signUp(View V) {
        LayoutInflater li = LayoutInflater.from(Login.this);
        final View vistaDialogoSignUp = li.inflate(R.layout.signup, null);
        flagCancelDialogSignUp = false;

        AlertDialog.Builder builderSignUp = new AlertDialog.Builder(Login.this);
        //GET REFERENCIAS DE VISTA

        editTextUserLoginReg = (EditText)vistaDialogoSignUp.findViewById(R.id.editTextUserLogin);
        layoutTextUserReg = (TextInputLayout)vistaDialogoSignUp.findViewById(R.id.layoutTextUserReg);
        editTextUserLoginReg.addTextChangedListener(new MyTextWatcher(editTextUserLoginReg));
        editTextUserEmail = (EditText)vistaDialogoSignUp.findViewById(R.id.editTextUserEmail);
        layoutTextEmailReg = (TextInputLayout)vistaDialogoSignUp.findViewById(R.id.layoutTextEmailReg);
        editTextUserEmail.addTextChangedListener(new MyTextWatcher(editTextUserEmail));
        editTextPassword = (EditText)vistaDialogoSignUp.findViewById(R.id.editTextPassword);
        layoutTextPassReg = (TextInputLayout)vistaDialogoSignUp.findViewById(R.id.layoutTextPassReg);
        editTextPassword.addTextChangedListener(new MyTextWatcher(editTextPassword));
        editTextConfirmPassword = (EditText)vistaDialogoSignUp.findViewById(R.id.editTextConfirmPassword);
        layoutTextPassConfirmReg = (TextInputLayout)vistaDialogoSignUp.findViewById(R.id.layoutTextPassConfirmReg);
        editTextConfirmPassword.addTextChangedListener(new MyTextWatcher(editTextConfirmPassword));
        editTextPasswordExams = (EditText)vistaDialogoSignUp.findViewById(R.id.editTextPasswordExams);
        layoutTextPassExamReg = (TextInputLayout)vistaDialogoSignUp.findViewById(R.id.layoutTextExamPassReg);
        editTextPasswordExams.addTextChangedListener(new MyTextWatcher(editTextPasswordExams));
        editTextPasswordExamsConfirm = (EditText)vistaDialogoSignUp.findViewById(R.id.editTextConfirmPasswordExams);
        layoutTextPassExamConfReg = (TextInputLayout)vistaDialogoSignUp.findViewById(R.id.layoutTextPassExamConfReg);
        editTextPasswordExamsConfirm.addTextChangedListener(new MyTextWatcher(editTextPasswordExamsConfirm));

                builderSignUp.setView(vistaDialogoSignUp);
                //set dialog message
                builderSignUp
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.btn_enviar),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                })
        .setNegativeButton(getString(R.string.btn_cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        final AlertDialog alertDialog = builderSignUp.create();
        // show it
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String userLogin = editTextUserLoginReg.getText().toString();
                String userPassword = editTextPassword.getText().toString();
                //String userDni = editTextUserDni.getText().toString();
                String userEmail = editTextUserEmail.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String secondaryUserPassword = editTextPasswordExams.getText().toString();
                String secondaryConfirmedPassword = editTextPasswordExamsConfirm.getText().toString();

                ///VALIDAR PASSWORD Y CONFIRMACI&Oacute;N DE PASSWORD SEAN IGUALES
                if (!userPassword.equalsIgnoreCase(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.passPpalCambioNoCoincide), Toast.LENGTH_LONG).show();
                } else if (secondaryUserPassword.equalsIgnoreCase(userPassword)) {
                    Toast.makeText(Login.this, getString(R.string.msg_pass_edit_error_match_ppal_sec), Toast.LENGTH_SHORT).show();
                }
                else if(!secondaryUserPassword.equalsIgnoreCase(secondaryConfirmedPassword)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.passSecCambioNoCoincide), Toast.LENGTH_LONG).show();
                }
                else {
                    if (validUserLogin && validEmail && validPassword && validSecondaryPassword){
                        //ENVIO DE DATOS A LA BASE DE DATOS REMOTA MONGO
                        try {
                            ProgressBar progressBar = new ProgressBar(Login.this);
                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                            PerfilUsuarioNube perfilUsuarioNube = new PerfilUsuarioNube();
                            jsonObject = new JSONObject();

                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                            //url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.GET_LAST_USER;
                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.ADD_NEW_USER;

                            jsonObject.put("user_login", userLogin);
                            //jsonObject.put("user_dni", userDni);
                            jsonObject.put("user_email", userEmail);
                            SecretKey secretMainPassword = Utils.generateKey(userPassword);
                            SecretKey secretSecondPassword = Utils.generateKey(secondaryUserPassword);
                            Log.d(TAG, secretMainPassword.toString());
                            Log.d(TAG, secretSecondPassword.toString());
                            jsonObject.put("user_password", userPassword);//PASAR AL SECRETKEY DESPUES
                            jsonObject.put("user_secondary_password", secondaryUserPassword);//PASAR AL SECRETKEY DESPUES
                            jsonObject.put("user_ads_disabled", false);
                            jsonObject.put("user_unlimited_exams", true);
                            jsonObject.put("user_exams_multi_answers", false);
                            jsonObject.put("user_has_premium_reports", false);
                            jsonObject.put("exp_date_unlimited_tests", "");
                            jsonObject.put("exp_date_premium_reports", "");

                            //new GeneralHttpTask(Login.this, progressBar, dynamicTestResponse, "GET_LAST_USER", jsonObject, perfilUsuarioNube, alertDialog).execute(url);
                            new GeneralHttpTask(Login.this, progressBar, dynamicTestResponse, "ADD_NEW_USER", jsonObject,
                                    perfilUsuarioNube, alertDialog).execute(url);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
                            if (!Constants.isAdsDisabled) {
                                //ADMOB INTERSTITIAL
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                }
                            }
                        }

                }else{Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgRegisterPresenterValid), Toast.LENGTH_LONG).show();}
               }//FIN ELSE
            }
        });
    }

    //EVENTO DEL BOTON SIGN UP
    public void forgotPasswordDialog(View V)
    {

        LayoutInflater li = LayoutInflater.from(Login.this);
        final View vistaDialogo = li.inflate(R.layout.login_dialog_forgot_password, null);

        final EditText txtEmailRecoverPass = (EditText)vistaDialogo.findViewById(R.id.txtEmailRecoverPass);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Login.this);

        alertDialogBuilder.setView(vistaDialogo);
        //set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_accept),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //SALVAMOS COMO PREFERENCIA EL VALOR DEL CHECKBOX DE TERMINOS
                                //savePreferences("CheckBox_Value", checkBoxTerms.isChecked());
                                //checkBoxTerms.setChecked(true);

                                if (Constants.internetOn) {
                                    String email = txtEmailRecoverPass.getText().toString();

                                    if (email != "") {

                                        //ENVIO DE DATOS A LA BASE DE DATOS REMOTA MONGO
                                        try {
                                            ProgressBar progressBar = new ProgressBar(Login.this);
                                            DynamicTestResponse dynamicTestResponse = new DynamicTestResponse();
                                            PerfilUsuarioNube perfilUsuarioNube = new PerfilUsuarioNube();

                                            //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                                            url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.FORGOT_PASSWORD_RECOVER + email;
                                            jsonObject = new JSONObject();

                                            new GeneralHttpTask(Login.this, progressBar, dynamicTestResponse, "FORGOT_PASSWORD", jsonObject, perfilUsuarioNube, dialog).execute(url);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.d(TAG, getString(R.string.errorEmailEnvio), e);
                                        } finally {
                                            //VALIDAMOS SI EL USUARIO COMPRO LA APLICACION PARA NO MOSTRAR MAS LAS APLICACIONES
                                            if (!Constants.isAdsDisabled) {
                                                //ADMOB INTERSTITIAL
                                                if (mInterstitialAd.isLoaded()) {
                                                    mInterstitialAd.show();
                                                }
                                            }
                                        }
                                    }

                                    dialog.cancel();
                                } else {
                                    Toast.makeText(getBaseContext(), getString(R.string.errorNoInternet), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.btn_cancelar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

}

    //EVENTO DEL BOTON SIGN UP
    public void newsDialog()
    {
        LayoutInflater li = LayoutInflater.from(Login.this);
        final View vistaDialogo = li.inflate(R.layout.login_dialog_news_updates, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Login.this);

        alertDialogBuilder.setView(vistaDialogo);
        alertDialogBuilder.setTitle(getString(R.string.dialogoNewsTitle));
        //set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        //IN APP BILLING GOOGLE
        inAppPayApi.onDestroy();
        super.onDestroy();
        //CERRAR INSTANCIA DE BD
        loginDataBaseAdapter.close();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.exitTitle))
                .setMessage(getResources().getString(R.string.exitMsgConfirm))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Login.super.onBackPressed();
                    }
                }).create().show();
    }

    //ADMOB INTERSTITIAL
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscador, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItemStore = menu.findItem(R.id.action_store);
        menuItemStore.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_faq:

                LayoutInflater li = LayoutInflater.from(Login.this);

                View vistaDialogoFaq = li.inflate(R.layout.dialog_faq_from_menu, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        Login.this);

                alertDialogBuilder.setView(vistaDialogoFaq);
                alertDialogBuilder.setTitle("FAQ");
                //set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                break;

            case R.id.action_news:

                newsDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        //IN APP BILLING GOOGLE
        inAppPayApi.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Request: " + requestCode + ", Result: " + resultCode + ", data: " + data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "IN APP BILL OK");
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "IN APP BILL: The user canceled.");
        }
    }

    private boolean validateUser(View view) {

        String text = "";
        Pattern p = Pattern.compile("[^A-Za-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m;
        boolean b;
            switch (view.getId()) {
                case R.id.editTextUserNameToLogin:
                    text = editTextUserNameToLogin.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextUserNameToLogin.setError(getString(R.string.errorUserLogin));
                        requestFocus(editTextUserNameToLogin);
                        inputValidationOk = "ERROR";
                        validUserLogin = false;
                        return false;
                    }else{layoutTextUser.setErrorEnabled(false);
                        inputValidationOk = "OK";
                        validUserLogin = true;}
                    break;
                case R.id.editTextUserLogin:

                    text = editTextUserLoginReg.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextUserLoginReg.setError(getString(R.string.errorUserLogin));
                        requestFocus(editTextUserLoginReg);
                        inputValidationOk = "ERROR";
                        validUserLogin = false;
                        return false;
                    }else{layoutTextUserReg.setErrorEnabled(false);
                        inputValidationOk = "OK";
                        validUserLogin = true;}
                    break;
            }
        return true;
    }

    private boolean validatePassword(View view) {

        String text = "";
        Pattern p = Pattern.compile("[^A-Za-z0-9]", Pattern.CASE_INSENSITIVE);
        boolean b;
        Matcher m;
        switch (view.getId()) {
                case R.id.editTextPasswordToLogin:
                    text = editTextPasswordToLogin.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextPasswordToLogin.setError(getString(R.string.errorPassLogin));
                        requestFocus(editTextPasswordToLogin);
                        inputValidationOk = "ERROR";
                        validPassword = false;
                        return false;
                    }else{layoutTextPassword.setErrorEnabled(false);
                        inputValidationOk = "OK";
                        validPassword = true;}
                    break;

                case R.id.editTextPassword:
                    text = editTextPassword.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextPassword.setError(getString(R.string.errorPassLogin));
                        requestFocus(editTextPassword);
                        inputValidationOk = "ERROR";
                        validPassword = false;
                        return false;
                    }else{layoutTextPassReg.setErrorEnabled(false);
                        inputValidationOk = "OK";}
                        validPassword = true;
                    break;

                case R.id.editTextConfirmPassword:
                    text = editTextConfirmPassword.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextConfirmPassword.setError(getString(R.string.errorPassLogin));
                        requestFocus(editTextConfirmPassword);
                        inputValidationOk = "ERROR";
                        validPassword = false;
                        return false;
                    }else{layoutTextPassConfirmReg.setErrorEnabled(false);
                        inputValidationOk = "OK";
                        validPassword = true;}
                    break;

                case R.id.editTextPasswordExams:
                    text = editTextPasswordExams.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextPasswordExams.setError(getString(R.string.errorPassLogin));
                        requestFocus(editTextPasswordExams);
                        inputValidationOk = "ERROR";
                        validSecondaryPassword = false;
                        return false;
                    }else{layoutTextPassExamReg.setErrorEnabled(false);
                        inputValidationOk = "OK";
                        validSecondaryPassword = true;}
                    break;

                case R.id.editTextConfirmPasswordExams:
                    text = editTextPasswordExamsConfirm.getText().toString();
                    m = p.matcher(text);
                    b = m.find();
                    if (b) {
                        editTextPasswordExamsConfirm.setError(getString(R.string.errorPassLogin));
                        requestFocus(editTextPasswordExamsConfirm);
                        inputValidationOk = "ERROR";
                        validSecondaryPassword = false;
                        return false;
                    }else{layoutTextPassExamConfReg.setErrorEnabled(false);
                        inputValidationOk = "OK";
                        validSecondaryPassword = true;}
                    break;
            }
        return true;
    }

    /*private boolean validateDni(View view) {

        String text = editTextUserDni.getText().toString();
        Pattern p = Pattern.compile("[^0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        if (b) {
            editTextUserDni.setError(getString(R.string.errorDniRegister));
            requestFocus(editTextUserDni);
            inputValidationOk = "ERROR";
            return false;

        } else {
            if(text.length() < 14) {
                layoutTextUser.setErrorEnabled(false);
                inputValidationOk = "OK";
            }
            else{return false;}
        }
        return true;
    }*/

    private boolean validateEmail(View view) {

        String text = editTextUserEmail.getText().toString();

        if (text.isEmpty() || !isValidEmail(text)) {
            layoutTextEmailReg.setError(getString(R.string.errorEmailRegister));
            requestFocus(editTextUserEmail);
            inputValidationOk = "ERROR";
            validEmail = false;
            return false;
        } else {
            layoutTextEmailReg.setErrorEnabled(false);
            inputValidationOk = "OK";
            validEmail = true;
        }
        return true;
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
                case R.id.editTextUserNameToLogin:
                case R.id.editTextUserLogin:
                    validateUser(view);
                    break;
                case R.id.editTextPasswordToLogin:
                case R.id.editTextPassword:
                case R.id.editTextPasswordExams:
                case R.id.editTextConfirmPassword:
                case R.id.editTextConfirmPasswordExams:
                    validatePassword(view);
                    break;

                /*case R.id.editTextUserDni:
                    validateDni(view);
                    break;*/

                case R.id.editTextUserEmail:
                    validateEmail(view);
                    break;
            }
        }
    }

    private void isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean NisConnected = activeNetwork.isConnectedOrConnecting();
        if (NisConnected) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Constants.internetOn = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Constants.internetOn = true;
            }
            else {
                Constants.internetOn = false;
            }
        }
    }

}
