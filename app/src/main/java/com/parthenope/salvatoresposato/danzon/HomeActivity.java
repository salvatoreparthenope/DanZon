package com.parthenope.salvatoresposato.danzon;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps.AreaManagement;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps.AreaStrategy;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify.GpsConObserverNotification;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify.GpsObserver;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify.GpsSubject;
import com.parthenope.salvatoresposato.danzon.Database.Area;
import com.parthenope.salvatoresposato.danzon.Database.Interval;
import com.parthenope.salvatoresposato.danzon.Database.GpsCoordinate;
import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;
import com.parthenope.salvatoresposato.danzon.Shared.Utility;
import com.parthenope.salvatoresposato.danzon.System.Services.ServiceSensor;
import com.rodolfonavalon.shaperipplelibrary.ShapeRipple;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import br.com.goncalves.pugnotification.notification.PugNotification;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements GpsObserver {

    GpsSubject gpsSubject;
    long actualIntervalId = 0;

    @BindView(R.id.ripple)
    ShapeRipple ripple;
    @BindView(R.id.textBigMessage)
    TextView textBigMessage;
    @BindView(R.id.textDescription)
    TextView textDescription;
    @BindView(R.id.textLastUpdate)
    TextView textLastUpdate;
    @BindView(R.id.imageIcon)
    ImageView imageIcon;
    @BindView(R.id.textPercentage)
    TextView textPercentage;
    @BindView(R.id.textPlace)
    TextView textPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(i);
            }
        });

        init();
    }

    /**
     * Check new update
     */
    private void loadData() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      Utility.loadVersionFromService(HomeActivity.this, new Utility.HandlerLoadData() {
                                          @Override
                                          public void onSuccess() {
                                              Utility.loadDataFromService(HomeActivity.this, new Utility.HandlerLoadData() {
                                                  @Override
                                                  public void onSuccess() {
                                                  }

                                                  @Override
                                                  public void onFailure() {
                                                      runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              onFailureM();
                                                          }
                                                      });
                                                  }
                                              });
                                          }

                                          @Override
                                          public void onFailure() {
                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      onFailureM();
                                                  }
                                              });
                                          }
                                      });

                                  }

                              },
                0,
                120000);
    }

    /**
     *
     */
    private void init() {
        loadData();
        registerBroadcastReceiver();
        initDefaultComponent();
        requestPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gpsSubject);
        stopService(new Intent(this, ServiceSensor.class));
    }

    /**
     *
     */
    private void onFailureM() {
        Toast.makeText(HomeActivity.this, getString(R.string.alert_error_message_load_data), Toast.LENGTH_LONG).show();
    }

    private void initDefaultComponent() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                textBigMessage.setTextColor(Color.parseColor("#000000"));
                ripple.setVisibility(View.GONE);
                textPercentage.setText("");
                textPlace.setText("");
                imageIcon.setImageResource(R.drawable.alert);
                textBigMessage.setText(R.string.unknown_position);
                textDescription.setText(R.string.unknown_position_text);
            }

        });
    }

    private void startService() {
        startService(new Intent(this, ServiceSensor.class));
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CHANGE_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.BODY_SENSORS)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.SEND_SMS, Manifest.permission.BODY_SENSORS}, 0);
            } else {
                startService();
            }
        } else {
            startService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length == 5) {
                    startService();
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.permissions_denied), Toast.LENGTH_LONG);
                }
        }
    }

    private void registerBroadcastReceiver() {

        gpsSubject = new GpsSubject();
        gpsSubject.attach(this);

        // Notifica push - La push notification non lavora su tutte le versioni di android
        //gpsSubject.attach(new GpsConObserverNotification(gpsSubject, getApplicationContext()))

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstant.ACTION_INTENT_UPDATE_LOCATION);
        registerReceiver(gpsSubject, intentFilter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void update() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Object[] state = gpsSubject.getState();

                // Interval not exist
                if (state == null) {
                    actualIntervalId = 0;
                    initDefaultComponent();
                    return;
                }

                Date lastTimeUpdateLocation = (Date) state[1];
                Interval lastTimeDangerousityInterval = (Interval) state[2];
                bindDataOnView(lastTimeUpdateLocation, lastTimeDangerousityInterval, (String) state[3]);
            }
        }).start();

    }

    /**
     * @param lastTimeUpdateLocation
     * @param lastTimeDangerousityInterval
     */
    private void bindDataOnView(final Date lastTimeUpdateLocation, final Interval lastTimeDangerousityInterval, final String descriptionArea) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
                textLastUpdate.setText("LU: " + dt.format(lastTimeUpdateLocation));
            }
        });

        actualIntervalId = lastTimeDangerousityInterval.getId();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                int imageIconResource = 0;
                String txtBigMessage = "";
                ripple.setVisibility(View.VISIBLE);
                ripple.setRippleDuration(4000);
                if (lastTimeDangerousityInterval.dangerousLevel < GlobalConstant.DANGEROUSITY_LEVEL_MIDDLE) {
                    txtBigMessage = getResources().getText(R.string.text_low_dangerousity).toString();
                    imageIconResource = R.drawable.alert_low_dangerousity;
                    textBigMessage.setTextColor(Color.parseColor("#009900"));
                    ripple.setRippleColor(Color.parseColor("#009900"));
                } else if (lastTimeDangerousityInterval.dangerousLevel < GlobalConstant.DANGEROUSITY_LEVEL_HIGH) {
                    txtBigMessage = getResources().getText(R.string.text_mid_dangerousity).toString();
                    imageIconResource = R.drawable.alert_mid_dangerousity;
                    textBigMessage.setTextColor(Color.parseColor("#999900"));
                    ripple.setRippleColor(Color.parseColor("#999900"));
                } else {
                    txtBigMessage = getResources().getText(R.string.text_high_dangerousity).toString();
                    imageIconResource = R.drawable.alert_high_dangerousity;
                    textBigMessage.setTextColor(Color.parseColor("#990000"));
                    ripple.setRippleColor(Color.parseColor("#990000"));
                }
                textPlace.setText(descriptionArea);
                textPercentage.setText("DL : " + lastTimeDangerousityInterval.dangerousLevel + "%");
                imageIcon.setImageResource(imageIconResource);
                textBigMessage.setText(txtBigMessage);

                // Check if english version
                if (getString(R.string.selector) == null) {
                    textDescription.setText(lastTimeDangerousityInterval.description.split("\\|")[1]);
                } else {
                    textDescription.setText(lastTimeDangerousityInterval.description.split("\\|")[0]);
                }
                Variable.updateOrAddVariabile(GlobalConstant.AREA_FOUNDED, GlobalConstant.KEY_LAST_LOCATION);

            }
        });
    }
}
