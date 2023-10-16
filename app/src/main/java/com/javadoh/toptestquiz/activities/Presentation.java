package com.javadoh.toptestquiz.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.javadoh.toptestquiz.R;
import com.javadoh.toptestquiz.utils.Constants;
import com.javadoh.toptestquiz.utils.GoogleInAppPayUtils;

import java.io.IOException;

/**
 * Created by lliberal on 18-08-2016.
 */
public class Presentation extends Activity {

    public static final String TAG = Presentation.class.getName();
    private ImageView imagePpal;
    private Animation animation, animationEnd;
    private Context context;
    //IN APP BILLING STORE
    GoogleInAppPayUtils inAppPayApi = new GoogleInAppPayUtils(this);
    //SONIDO
    final MediaPlayer player = new MediaPlayer();
    int media_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //IN APP BILLING GOOGLE
        try {

            //VERIFICAMOS LA CONEXION A INTERNET Y DEPENDIENDO DE ELLO INICIAMOS O NO IN APP PAY CALL
            //isConnectedToInternet();

            /*if(Constants.internetOn) {
                //IN APP BILLING GOOGLE
                inAppPayApi.onCreate();
            }else{
                Log.d(TAG, "No hay conexión a internet, por lo tanto no iniciamos las llamadas a inapp pay google");
                }*/
        }catch (Exception e){
            Log.d(TAG, "Información: necesitas de los servicios de google play para usar la tienda.");
        }

        setContentView(R.layout.presentation_fade_in_out);
        context = this;
        imagePpal = (ImageView)findViewById(R.id.imgpresentacion);
        //AUDIO DE FONDO
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setPlayer(Presentation.this);
        player.setLooping(false);
        player.start();

        animation = AnimationUtils.loadAnimation(context, R.anim.fadein);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                animationEnd = AnimationUtils.loadAnimation(context, R.anim.fadeout);
                imagePpal.startAnimation(animationEnd);
                //ANIMACION ANIDADA DE FIN
                animationEnd.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent actividadBusq = new Intent(getApplicationContext(), Login.class);
                        //ENVIAMOS LA PETICION DE INICIO DE LA ACTIVIDAD DE BUSQUEDA
                        startActivity(actividadBusq);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imagePpal.startAnimation(animation);
    }

    public void setPlayer(Context mContext){

        AssetFileDescriptor afd;
        try {
            afd = mContext.getResources().openRawResourceFd(R.raw.javadoh);
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG, getString(R.string.msgErrorTaskServer),e);
            e.printStackTrace();
        }
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

    /*private void isConnectedToInternet() {
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
    }*/
}
