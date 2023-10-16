package com.javadoh.toptestquiz.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by luiseliberal on 22/05/16.
 */
public class Utils {

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private static boolean FLAG_GOOGLE_SERVICES;

    //REVERSE ADRESS
    private String address1 = "", address2 = "", city = "", state = "", country = "", county = "", PIN = "";


    public static boolean checkGooglePlayServices(Activity mContext){
        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mContext);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
		/*
		* Google Play Services is missing or update is required
		*  return code could be
		* SUCCESS,
		* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
		* SERVICE_DISABLED, SERVICE_INVALID.
		*/

            //COMENTO EL DIALOGO DE SOLICITUD DE INSTALACION DE SERVICIOS GOOGLE PLAY
            //GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
            //        mContext, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;
    }


       /* public void getAddress(String curLatitude, String curLongitude) {
            address1 = "";
            address2 = "";
            city = "";
            state = "";
            country = "";
            county = "";
            PIN = "";

            try {

                JSONObject jsonObj = Utils.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + curLatitude + ","
                        + curLongitude + "&sensor=true");
                String Status = jsonObj.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray Results = jsonObj.getJSONArray("results");
                    JSONObject zero = Results.getJSONObject(0);
                    JSONArray address_components = zero.getJSONArray("address_components");

                    for (int i = 0; i < address_components.length(); i++) {
                        JSONObject zero2 = address_components.getJSONObject(i);
                        String long_name = zero2.getString("long_name");
                        JSONArray mtypes = zero2.getJSONArray("types");
                        String Type = mtypes.getString(0);

                        if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                            if (Type.equalsIgnoreCase("street_number")) {
                                address1 = long_name + " ";
                            } else if (Type.equalsIgnoreCase("route")) {
                                address1 = address1 + long_name;
                            } else if (Type.equalsIgnoreCase("sublocality")) {
                                address2 = long_name;
                            } else if (Type.equalsIgnoreCase("locality")) {
                                // Address2 = Address2 + long_name + ", ";
                                city = long_name;
                            } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                                county = long_name;
                            } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                state = long_name;
                            } else if (Type.equalsIgnoreCase("country")) {
                                country = long_name;
                            } else if (Type.equalsIgnoreCase("postal_code")) {
                                PIN = long_name;
                            }
                        }

                        // JSONArray mtypes = zero2.getJSONArray("types");
                        // String Type = mtypes.getString(0);
                        // Log.e(Type,long_name);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/

        public String getAddress1() {
            return address1;

        }

        public String getAddress2() {
            return address2;

        }

        public String getCity() {
            return city;

        }

        public String getState() {
            return state;

        }

        public String getCountry() {
            return country;

        }

        public String getCounty() {
            return county;

        }

        public String getPIN() {
            return PIN;

        }


   /* public static JSONObject getJSONfromURL(String url) {

        // initialize
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;

        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }*/


    public static boolean isFlagGoogleServices() {
        return FLAG_GOOGLE_SERVICES;
    }

    public static void setFlagGoogleServices(boolean flagGoogleServices) {
        FLAG_GOOGLE_SERVICES = flagGoogleServices;
    }


    public static SecretKey generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(password.getBytes(), "AES");
    }

    public static byte[] encryptString(String message, SecretKey secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
/* Encrypt the message. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherText;
    }

    public static String decryptString(byte[] cipherText, SecretKey secret) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

    /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

}
