package com.example.android.firedata;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    public long minutedelayz = 0;
    public long secondelayz = 5;
    final String myTag = "Document was Uploaded";
    public boolean isTransmitting = false;
    public boolean killthread = false;

    TransmitDataTask mTransmitter;


    public static volatile boolean continueThread = false;

    //Declaring the sensors
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    public Sensor mThermometer;
    public Sensor mGyroscope;
    public Sensor mHygrometer;
    public Sensor mBarometer;
    public Sensor mPhotometer;
    public Sensor mMagnetometer;

    public float Xacc;
    public float Yacc;
    public float Zacc;
    public float Xdev;
    public float Ydev;
    public float Zdev;
    public float pressure;
    public float light;
    public float Xmag;
    public float Ymag;
    public float Zmag;
    public float humidity;
    public float temperature;

    public PopupWindow mPopupWindow;
    public LayoutInflater mInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Instantiating the Sensors
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mThermometer = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mHygrometer = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mPhotometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Now adding the event listeners

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mXTextView = (TextView) findViewById(R.id.x_mag_display);
                    TextView mYTextView = (TextView) findViewById(R.id.y_mag_display);
                    TextView mZTextView = (TextView) findViewById(R.id.z_mag_display);

                    mXTextView.setText(Float.toString(roundTwoDecimals(event.values[0])));
                    mYTextView.setText(Float.toString(roundTwoDecimals(event.values[1])));
                    mZTextView.setText(Float.toString(roundTwoDecimals(event.values[2])));

                    Xmag = event.values[0];
                    Ymag = event.values[1];
                    Zmag = event.values[2];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    //NOPE
                }
            }, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Failure! No TEMP SENSOR
            LinearLayout mItem = (LinearLayout) findViewById(R.id.magnetcontentdisplay);
            mItem.setVisibility(View.GONE);

        }


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mXTextView = (TextView) findViewById(R.id.x_accel_display);
                    TextView mYTextView = (TextView) findViewById(R.id.y_accel_display);
                    TextView mZTextView = (TextView) findViewById(R.id.z_accel_display);

                    mXTextView.setText(Float.toString(roundTwoDecimals(event.values[0])));
                    mYTextView.setText(Float.toString(roundTwoDecimals(event.values[1])));
                    mZTextView.setText(Float.toString(roundTwoDecimals(event.values[2])));

                    Xacc = event.values[0];
                    Yacc = event.values[1];
                    Zacc = event.values[2];


                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    //NOPE
                }
            }, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Failure! No TEMP SENSOR
            TextView mAccelViewOne = (TextView) findViewById(R.id.accelerationitemtextview);
            RelativeLayout mAccelViewTwo = (RelativeLayout) findViewById(R.id.accelerationitemcontentview);
            mAccelViewOne.setVisibility(View.GONE);
            mAccelViewTwo.setVisibility(View.GONE);

        }


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            // Success! There's a TEMP SENSOR.
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mTempView = (TextView) findViewById(R.id.temp_display);
                    float temp = event.values[0];
                    Log.v("TEMPERATURE:", Float.toString(temp));
                    mTempView.setText(Float.toString(roundTwoDecimals(temp)));

                    temperature = temp;

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, mThermometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Failure! No TEMP SENSOR
            TextView mTempView = (TextView) findViewById(R.id.temp_display);
            mTempView.setText("N/A");
            LinearLayout mTempItem = (LinearLayout) findViewById(R.id.temperature_item);
            mTempItem.setVisibility(View.GONE);

        }


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mXTextView = (TextView) findViewById(R.id.x_gyro_display);
                    TextView mYTextView = (TextView) findViewById(R.id.y_gyro_display);
                    TextView mZTextView = (TextView) findViewById(R.id.z_gyro_display);

                    mXTextView.setText(Float.toString(roundTwoDecimals(event.values[0])));
                    mYTextView.setText(Float.toString(roundTwoDecimals(event.values[1])));
                    mZTextView.setText(Float.toString(roundTwoDecimals(event.values[2])));

                    Xdev = event.values[0];
                    Ydev = event.values[1];
                    Zdev = event.values[2];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, mGyroscope, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Failure! No TEMP SENSOR

            LinearLayout mContentItem = (LinearLayout) findViewById(R.id.gyroscopecontentview);
            mContentItem.setVisibility(View.GONE);

        }


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mHygrometerTextView = (TextView) findViewById(R.id.humidity_display);
                    mHygrometerTextView.setText(Float.toString(roundTwoDecimals(event.values[0])));

                    humidity = event.values[0];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, mHygrometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Failure! No TEMP SENSOR

            LinearLayout mContentItem = (LinearLayout) findViewById(R.id.relativehumiditycontentdisplay);
            mContentItem.setVisibility(View.GONE);

        }


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mBaromterTextView = (TextView) findViewById(R.id.pressure_display);
                    mBaromterTextView.setText(Float.toString(roundTwoDecimals(event.values[0])));

                    pressure = event.values[0];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, mBarometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Failure! No TEMP SENSOR

            LinearLayout mContentItem = (LinearLayout) findViewById(R.id.pressurecontentdisplay);
            mContentItem.setVisibility(View.GONE);

        }


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            mSensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    TextView mPhotometerTextView = (TextView) findViewById(R.id.light_display);
                    mPhotometerTextView.setText(Float.toString(roundTwoDecimals(event.values[0])));

                    light = event.values[0];

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, mPhotometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            //  No LIGHT SENSOR

            LinearLayout mContentItem = (LinearLayout) findViewById(R.id.lightcontentdisplay);
            mContentItem.setVisibility(View.GONE);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_export) {

            if (isTransmitting == false) {
                Log.v("HELLO", "YOU TURNED THE TRANSMISION ON!");
                Toast.makeText(MainActivity.this, "Transmission Started", Toast.LENGTH_SHORT).show();
                isTransmitting = true;
                mTransmitter = new TransmitDataTask();
                mTransmitter.execute();


            } else if (isTransmitting == true) {
                isTransmitting = false;
                Log.v("HELLO", "YOU TURNED THE TRANSMISSION OFF!");
                Toast.makeText(MainActivity.this, "Transmission Terminated", Toast.LENGTH_SHORT).show();
                killthread = true;
                mTransmitter.quit();

            }
            return true;
        } else if (id == R.id.timer) {
            if (isTransmitting == false) {
                mInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final ViewGroup popupLayout = (ViewGroup) mInflater.inflate(R.layout.popup_delay, null);
                mPopupWindow = new PopupWindow(popupLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);

                Button mSubmitButton = (Button) popupLayout.findViewById(R.id.time_submit_button);
                mSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText ma = (EditText) popupLayout.findViewById(R.id.minute_entry_field);
                        EditText sa = (EditText) popupLayout.findViewById(R.id.second_entry_field);

                        if (ma.getText().toString().equals("")) {
                            minutedelayz = 0;
                        } else {
                            minutedelayz = Long.parseLong(ma.getText().toString());
                        }

                        if (sa.getText().toString().equals("") && !ma.getText().toString().equals("")) {
                            secondelayz = 0;
                        } else if (sa.getText().toString().equals("") && ma.getText().toString().equals("")) {
                            secondelayz = 5;
                        } else {
                            secondelayz = Long.parseLong(sa.getText().toString());
                        }

                        if (sa.getText().toString().equals("") && ma.getText().toString().equals("")) {
                            Toast.makeText(MainActivity.this, "Please enter custom delay", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Time interval has been changed", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                popupLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mPopupWindow.dismiss();
                        return false;
                    }
                });
            } else if (isTransmitting == true) {
                Toast.makeText(MainActivity.this, "Cannot change time while transmitting", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public float roundTwoDecimals(float f) {
        DecimalFormat twoForm = new DecimalFormat("#.##");
        return Float.valueOf(twoForm.format(f));
    }

    public void postData() {

        String fullUrl = "https://docs.google.com/forms/d/e/1FAIpQLSeawl4U659mEJ9kxNo3yOCGc2013mfkAq_ClCEF41qz5h0MSQ/formResponse";
        HttpRequest mReq = new HttpRequest();

        String data =
                "entry.1348672159=" + URLEncoder.encode(Float.toString(Xacc)) + "&" +
                        "entry.1552310814=" + URLEncoder.encode(Float.toString(Yacc)) + "&" +
                        "entry.411136646=" + URLEncoder.encode(Float.toString(Zacc)) + "&" +
                        "entry.777333585=" + URLEncoder.encode(Float.toString(Xdev)) + "&" +
                        "entry.446705904=" + URLEncoder.encode(Float.toString(Ydev)) + "&" +
                        "entry.950764398=" + URLEncoder.encode(Float.toString(Zdev)) + "&" +
                        "entry.837628201=" + URLEncoder.encode(Float.toString(pressure)) + "&" +
                        "entry.1937212565=" + URLEncoder.encode(Float.toString(light)) + "&" +
                        "entry.2096854127=" + URLEncoder.encode(Float.toString(Xmag)) + "&" +
                        "entry.2077950104=" + URLEncoder.encode(Float.toString(Ymag)) + "&" +
                        "entry.25398018=" + URLEncoder.encode(Float.toString(Zmag)) + "&" +
                        "entry.586660733=" + URLEncoder.encode(Float.toString(humidity)) + "&" +
                        "entry.1450849537=" + URLEncoder.encode(Float.toString(temperature));

        String response = mReq.sendPost(fullUrl, data);
        Log.i(myTag, response);
    }

    public class TransmitDataTask extends AsyncTask<Void, Void, Void> {

        public boolean done;

        @Override
        protected Void doInBackground(Void... params) {
            while (!done) {
                try {
                    TimeUnit.MINUTES.sleep(minutedelayz);
                    TimeUnit.SECONDS.sleep(secondelayz);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                postData();


            }

            return null;


        }

        public void quit() {
            done = true;
        }


    }
}
