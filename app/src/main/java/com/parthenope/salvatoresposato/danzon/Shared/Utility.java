package com.parthenope.salvatoresposato.danzon.Shared;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.SyncHttpClient;
import com.parthenope.salvatoresposato.danzon.Database.Area;
import com.parthenope.salvatoresposato.danzon.Database.GpsCoordinate;
import com.parthenope.salvatoresposato.danzon.Database.Interval;
import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.R;
import com.parthenope.salvatoresposato.danzon.WebService.object.AreaJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.DataJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.GpsCoordinateJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.IntervalJson;


public class Utility {

    /**
     *
     * @param context
     * @param title
     * @param message
     * @param textConfirm
     * @return
     */
    public static void showAlertDialog(final Context context,final String title,final String message,final String textConfirm){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title).setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(textConfirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    /**
     * Load data from web service
     */
    public static void loadDataFromService(final Context context,final HandlerLoadData handler){

        if(!isOnline(context)) {
            handler.onSuccess();
            return;
        }

        SyncHttpClient client = new SyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.get(GlobalConstant.URL_SERVICE, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                try {

                    String responseBodyStr = new String(responseBody);
                    parseJson(responseBodyStr);

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.onSuccess();
                        }
                    });

                }catch (Exception ex){
                    Variable.updateOrAddVariabile(GlobalConstant.KEY_VERSION_UPDATE_DATA, GlobalConstant.KEY_STATE_UPDATE_DATA_ERRORE);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.onFailure();
                        }
                    });
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Variable.updateOrAddVariabile(GlobalConstant.KEY_VERSION_UPDATE_DATA, GlobalConstant.KEY_STATE_UPDATE_DATA_ERRORE);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.onFailure();
                    }
                });
            }

            /**
             * Parse json
             * @param responseBodyStr
             */
            private void parseJson(String responseBodyStr) {

                DataJson dataJson = new Gson().fromJson(responseBodyStr, DataJson.class);
                String version = Variable.getValue(GlobalConstant.KEY_VERSION_UPDATE_DATA);
                if(version == null || !version.equals(""+dataJson.version)) {

                    Area.deleteAll(Area.class);
                    Interval.deleteAll(Interval.class);
                    GpsCoordinate.deleteAll(GpsCoordinate.class);

                    for (AreaJson item : dataJson.listAreas) {
                        long idArea = Area.InsertFromJson(item);
                        for (IntervalJson interval : item.intervals) {
                            interval.idArea = idArea;
                            Interval.InsertFromJson(interval);
                        }
                        for (GpsCoordinateJson coordinate : item.gpsCoordinate) {
                            coordinate.idArea = idArea;
                            GpsCoordinate.InsertFromJson(coordinate);
                        }
                    }

                    Variable.updateOrAddVariabile(GlobalConstant.KEY_VERSION_UPDATE_DATA, String.valueOf(dataJson.version));
                }

            }

        });
    }

    /**
     * Check internet
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public interface HandlerLoadData {
        void onSuccess();
        void onFailure();
    }

}
