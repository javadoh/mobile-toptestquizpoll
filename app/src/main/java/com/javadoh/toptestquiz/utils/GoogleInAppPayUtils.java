package com.javadoh.toptestquiz.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.javadoh.toptestquiz.utils.billing.IabHelper;
import com.javadoh.toptestquiz.utils.billing.IabResult;
import com.javadoh.toptestquiz.utils.billing.Inventory;
import com.javadoh.toptestquiz.utils.billing.Purchase;

import org.json.JSONObject;

/**
 * Created by lliberal on 18-08-2016.
 */
public class GoogleInAppPayUtils {

    public static final String TAG = GoogleInAppPayUtils.class.getName();
    //PRODUCTO UNO
    static final String SKU_REMOVE_ADS = "remove_ads";
    //PRODUCTO DOS
    static final String SKU_UNLIMITED_TESTS = "unlimited_tests_surveys";
    //PRODUCTO TRES
    static final String SKU_MULTIPLE_ANSWERS = "multiple_answers";
    //PRODUCTO CUATRO
    static final String SKU_PREMIUM_REPORTS = "premium_reports";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10111;
    Activity activity;
    // The helper object
    IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnSbkMq/xVyYeCSEdXxj6AmJ24X9/pb6U6LnfpS1ehan4FdIihHkdWL9kwVxA3lAPgQfGvhkB/VtbO7zeu9C7NTbJKlZ1xZp1+UAFFI8KvwvUKl1/7Pbuxu4oC/+U/OthxO/CAt8Gt3C97E4qHqKkbeIsAciYwMqRQpNt4/SWy37yr4w1qRI34JTu6gDfS/AvOtZneZOKLCd0fLshHvHOuQ0qKFXfjvWXrjkWT5vj5wXaaMuxN+X/EQxLTs7XYU9QbZDpfy1neZOAUoBoG/V1oAT2f0xsxHtWXnw75K5qCKeB5ESOiB1Tv4U+iFgUwnu+xC38cwCIzuyRDwcXBsN95wIDAQAB";
    String payload = "ANY_PAYLOAD_STRING";
    public static int USERID_INAPPPAY, PRODUCT_INAPPPAY;
    public static Context CONTEXT_APP;
    private String url;
    Boolean isAdsDisabled = false;

    public GoogleInAppPayUtils(Activity launcher) {
        this.activity = launcher;
    }

    public void onCreate() {

        // Create the helper, passing it our context and the public key to
        // verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(activity, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set
        // this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    Constants.isInAppSetupCreated = false;
                    return;
                }

                // Have we been disposed off in the meantime? If so, quit.
                if (mHelper == null) {
                    Constants.isInAppSetupCreated = false;
                    return;
                }

                // IAB is fully set up. Now, let's get an inventory of stuff we
                // own.
                Constants.isInAppSetupCreated = true;
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
                return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase removeAdsPurchase = inventory.getPurchase(SKU_REMOVE_ADS);
            Purchase unlimitedTestsPurchase = inventory.getPurchase(SKU_UNLIMITED_TESTS);
            Purchase multipleAnswersPurchase = inventory.getPurchase(SKU_MULTIPLE_ANSWERS);
            Purchase premiumReportsPurchase = inventory.getPurchase(SKU_PREMIUM_REPORTS);

            Constants.isAdsDisabled = (removeAdsPurchase != null && verifyDeveloperPayload(removeAdsPurchase));
            Constants.isUnlimitedTests = (unlimitedTestsPurchase != null && verifyDeveloperPayload(unlimitedTestsPurchase));
            Constants.isMultipleAnswers = (multipleAnswersPurchase != null && verifyDeveloperPayload(multipleAnswersPurchase));
            Constants.isPremiumReports = (premiumReportsPurchase != null && verifyDeveloperPayload(premiumReportsPurchase));

            Log.d(TAG, "User has "
                    + (Constants.isAdsDisabled ? "REMOVED ADS"
                    : "NOT REMOVED ADS"));

            // setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling menu_buscador UI.");
        }
    };

    //USUARIO SELECCIONO EL PRODUCTO REMOVER PUBLICIDAD (remove_ads) PRODUCTO 1
    public void purchaseRemoveAds() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHelper.launchPurchaseFlow(activity, SKU_REMOVE_ADS,
                        RC_REQUEST, mPurchaseFinishedListener, payload);

            }
        });
    }
    //USUARIO SELECCIONO EL PRODUCTO EXAMENES ILIMITADOS (unlimited_tests) PRODUCTO 2
    public void purchaseUnlimitedTests() {

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mHelper.launchPurchaseFlow(activity, SKU_UNLIMITED_TESTS,
                        RC_REQUEST, mPurchaseFinishedListener, payload);

            }
        });
    }

    //USUARIO SELECCIONO EL PRODUCTO MULTIPLES RESPUESTAS (multiple_answers) PRODUCTO 3
    public void purchaseMultipleAnswers() {

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mHelper.launchPurchaseFlow(activity, SKU_MULTIPLE_ANSWERS,
                        RC_REQUEST, mPurchaseFinishedListener, payload);

            }
        });
    }

    //USUARIO SELECCIONO EL PRODUCTO MULTIPLES RESPUESTAS (multiple_answers) PRODUCTO 4
    public void purchasePremiumReports() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHelper.launchPurchaseFlow(activity, SKU_PREMIUM_REPORTS,
                        RC_REQUEST, mPurchaseFinishedListener, payload);

            }
        });
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
                + data);
        if (mHelper == null)
            return true;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            return false;
        } else {

            Log.d(TAG, "onActivityResult handled by IABUtil.");

            return true;
        }

    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct.
         * It will be the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase
         * and verifying it here might seem like a good approach, but this will
         * fail in the case where the user purchases an item on one device and
         * then uses your app on a different device, because on the other device
         * you will not have access to the random string you originally
         * generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different
         * between them, so that one user's purchase can't be replayed to
         * another user.
         *
         * 2. The payload must be such that you can verify it even when the app
         * wasn't the one who initiated the purchase flow (so that items
         * purchased by the user on one device work on other devices owned by
         * the user).
         *
         * Using your own server to store and verify developer payloads across
         * app installations is recommended.
         */
        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: "
                    + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
                // bought the premium upgrade!
                Constants.isAdsDisabled = true;
                removeAds();
            }

            /*JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                url = Constants.URL_SERVIDOR_RMT_APP_DYNAMIC_TEST + Constants.USER_HAS_PAID_PRODUCT + "?userId=" + USERID_INAPPPAY + "&productId=" + PRODUCT_INAPPPAY;
                //######################### AQUI SE TIENE QUE REGISTRAR UN PUSH CON EL NUEVO VALOR DE COMPRA ###################//
                //if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
                    //URL DEL API QUE INVOCAMOS CON SU RESPECTIVA OPERACION
                    jsonObject.put("user_id", USERID_INAPPPAY);
                    jsonObject.put("product_id", PRODUCT_INAPPPAY);

                    Log.d(TAG, "Sending new user product has purchased, user: "+USERID_INAPPPAY+", product: "+PRODUCT_INAPPPAY);

                    new GeneralHttpTask(CONTEXT_APP, null, null, "INAPPPAY", jsonObject, null).execute(url);
                /*} else if (purchase.getSku().equals(SKU_UNLIMITED_TESTS)) {
                    jsonObject.put("product_id", PRODUCT_INAPPPAY);

                } else if (purchase.getSku().equals(SKU_MULTIPLE_ANSWERS)) {
                    jsonObject.put("product_id", PRODUCT_INAPPPAY);

                } else if (purchase.getSku().equals(SKU_PREMIUM_REPORTS)) {

                }
            }catch (Exception e){
                Log.d(TAG, "Error on purchased succesful product json: "+jsonObject+". ",e);
                e.printStackTrace();
            }*/
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!

    public void onDestroy() {

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        //alert("Error: " + message);
    }

    void alert(final String message) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder bld = new AlertDialog.Builder(activity);
                bld.setMessage(message);
                bld.setNeutralButton("OK", null);
                Log.d(TAG, "Showing alert dialog: " + message);
                bld.create().show();
            }
        });
    }


    private void removeAds() {
        isAdsDisabled = true;
    }

}
