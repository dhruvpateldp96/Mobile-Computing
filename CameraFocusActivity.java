package com.example.camerag14;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import static android.widget.Toast.LENGTH_SHORT;

public class CameraFocusActivity extends AppCompatActivity implements SurfaceHolder.Callback, SensorEventListener
{
    private Camera           camera;
    private boolean          isPreviewRunning = false;
    private SurfaceView      surfaceView;
    private SurfaceHolder    surfaceHolder;

    TextView txt1;
    FrameLayout cameraFrame;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mMagnetometer;
    showCamera sc;
    private static final String TAG = "MeasureApp";
    private String[] units = { "meters", "cms", "feet", "inches"};
    private double AngleA = 0.0;
    private double AngleB = 0.0;
    private double X1 = 0.0;
    private double X2 = 0.0;
    private float[] mGravity;
    private float[] mMagnetic;
    private double h;
    private double D;
    private double H;
    private double L;
    float[] value = new float[3];
    //private int unit;//default unit in mts
    //mts-0,cms-1,ft-2,in-3
    private Spinner spinner;
    DecimalFormat ThreeDForm = new DecimalFormat("#.###");
    float pressure;
    float accel[] =  new float[3];
    float result[] = new float[3];
    CharSequence test = "Results:\nObj Distance = "+D+"cms\nObj Height = "+H+"cms\nObj Length = "+L+"cms\nCam height = "+h+"cms";
    private PowerManager.WakeLock wl;
    private PowerManager pm;
    Toast toast;
    final String filename = "WeightDatabase.txt";
    String m = "";

    String filepath = "G_14-Storage";
    File myInternalFile;
    File directory;

    //String[] files = new String[15];
    //int count = -1;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate(Bundle icicle)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(icicle);
        Log.e(getClass().getSimpleName(), "onCreate");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.frames); //main or frames
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        final Button distance = (Button) findViewById(R.id.button1);
        final Button height = (Button) findViewById(R.id.button7);
        Button weight = (Button) findViewById(R.id.get_weight);
        final Button rst_valider = (Button) findViewById(R.id.button2);
        txt1 = (TextView) findViewById(R.id.textView1);
        final Button adjh = (Button) findViewById(R.id.button3);
        final EditText edit = (EditText) findViewById(R.id.editText1);
        final Button length = (Button) findViewById(R.id.button4);
        //SR
        cameraFrame = (FrameLayout)findViewById(R.id.cameraFrame);


        distance.setEnabled(false);
        height.setEnabled(false);
        length.setEnabled(false);
        //weight.setEnabled(false);
        edit.setText("0.0");
        //unit = 0;
        txt1.setText(test);

        //surfaceCreated(surfaceHolder);
        //surfaceChanged(surfaceHolder, 0,  176, 144);

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        final File directory = contextWrapper.getDir(filepath, Context.MODE_APPEND);
        myInternalFile = new File(directory , filename);
		/*try
		{
		BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
	    buf.write("Measures:\n");
	    buf.newLine();
	    buf.close();
	    }
		catch(IOException e)
		{
			e.printStackTrace();
		}*/

        //Toast.makeText(this, "Adjust the height", Toast.LENGTH_SHORT);

        try
        {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG);
            wl.acquire();
        }
        catch (Exception ex)
        {
            Log.e("exception", "here 1");
        }

        camera.open();
        sc= new showCamera(this,camera);
        cameraFrame.addView(sc);

        //addListenerOnButton();
        //addListenerOnSpinnerItemSelection();
        distance.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                //old method
                AngleA = getDirection();//taking x value
                AngleA = Math.toRadians(90)-AngleA;
                D = Double.valueOf(ThreeDForm.format(Math.abs(h*(Math.tan((AngleA))))));
                test = "Results:\nObj Distance = "+D+"cms\nObj Height = "+H+"cms\nObj Length = "+L+"cms\nCam height = "+h+"cms";
                toast = Toast.makeText(getApplicationContext(), "Object distance calculated!", LENGTH_SHORT);
                toast.show();
                txt1.setText(test);
            }
        });

        height.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AngleB=0;
                while(AngleB==0)
                {
                    AngleB = getDirection();//taking x
                }
                H = Double.valueOf(ThreeDForm.format(h+Math.abs(D*Math.tan((AngleB)))));

                test = "Results:\nObj Distance = "+D+"cms\nObj Height = "+H+"cms\nObj Length = "+L+"cms\nCam height = "+h+"cms";
                toast = Toast.makeText(getApplicationContext(), "Object height calculated!", LENGTH_SHORT);
                toast.show();
                txt1.setText(test);
            }
        });

        rst_valider.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                AngleA = 0.0;
                AngleB = 0.0;
                X1 = 0.0;
                X2 = 0.0;
                D=0.0;
                H=0.0;
                L=0.0;
                toast = Toast.makeText(getApplicationContext(), "Values reset!", LENGTH_SHORT);
                toast.show();
                txt1.setText("Results\nObj Distance = "+D+"cms\nObj Height = "+H+"cms\nObj Length = "+L+"cms\nCam height = "+h+"cms");
            }
        });

        adjh.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                h = Double.parseDouble(edit.getText().toString());
                if(h==0)
                {
                    toast = Toast.makeText(getApplicationContext(), "Height cannot be 0!", LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    txt1.setText("Camera height = "+h+"\ncms");
                    distance.setEnabled(true);
                    height.setEnabled(true);
                    length.setEnabled(true);
                    toast = Toast.makeText(getApplicationContext(), "Phone height adjusted!", LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        length.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                if(X1==0.0)
                {
                    X1 = value[0];//taking z

                }
                else
                {
                    X2 = (value[0]);//taking z

                    float theta = (float) Math.abs(Math.abs(X1)-Math.abs(X2));
                    //arc of a circle logic;
                    L = Double.valueOf(ThreeDForm.format(theta * D));
                    test = "Results:\nObj Distance = "+D+"cms\nObj Height = "+H+"cms\nObj Length = "+L+"cms\nCam height = "+h+"cms";
                    toast = Toast.makeText(getApplicationContext(), "Object length calculated!", LENGTH_SHORT);
                    toast.show();
                    txt1.setText(test);
                }
            }
        });
        //final String data = "[{\"score\":0.9779489426051869,\"keypoints\":[{\"score\":0.999846875667572,\"part\":\"nose\",\"position\":{\"x\":301.53484901685385,\"y\":257.65654260299624}},{\"score\":0.9999767541885376,\"part\":\"leftEye\",\"position\":{\"x\":328.8400729556804,\"y\":233.78872112983768}},{\"score\":0.999970018863678,\"part\":\"rightEye\",\"position\":{\"x\":279.44834581772784,\"y\":235.45558286516854}},{\"score\":0.9994299411773682,\"part\":\"leftEar\",\"position\":{\"x\":355.4499746410736,\"y\":252.27030664794006}},{\"score\":0.999362587928772,\"part\":\"rightEar\",\"position\":{\"x\":242.94178175717855,\"y\":256.3932047830836}},{\"score\":0.9926291704177856,\"part\":\"leftShoulder\",\"position\":{\"x\":418.8653831148564,\"y\":402.28732638888886}},{\"score\":0.9835844039916992,\"part\":\"rightShoulder\",\"position\":{\"x\":213.73585752184766,\"y\":415.55511665106116}},{\"score\":0.9972991347312927,\"part\":\"leftElbow\",\"position\":{\"x\":449.2840980024969,\"y\":586.3944483458178}},{\"score\":0.9911842346191406,\"part\":\"rightElbow\",\"position\":{\"x\":185.52234511548062,\"y\":595.6762542915105}},{\"score\":0.9951977133750916,\"part\":\"leftWrist\",\"position\":{\"x\":396.5255344881398,\"y\":702.8404435861423}},{\"score\":0.9770950078964233,\"part\":\"rightWrist\",\"position\":{\"x\":300.9757334581773,\"y\":706.9298142946317}},{\"score\":0.925123929977417,\"part\":\"leftHip\",\"position\":{\"x\":385.26665886392004,\"y\":704.0870298845193}},{\"score\":0.934648871421814,\"part\":\"rightHip\",\"position\":{\"x\":253.53137679463168,\"y\":715.0381359238452}},{\"score\":0.9560098648071289,\"part\":\"leftKnee\",\"position\":{\"x\":411.30486306179773,\"y\":963.5716097066166}},{\"score\":0.9841309189796448,\"part\":\"rightKnee\",\"position\":{\"x\":265.5047889357054,\"y\":985.5657966604244}},{\"score\":0.9764760732650757,\"part\":\"leftAnkle\",\"position\":{\"x\":469.0088561173533,\"y\":1163.5944327403245}},{\"score\":0.9131665229797363,\"part\":\"rightAnkle\",\"position\":{\"x\":253.77960166978778,\"y\":1170.9496917915105}}]}";
        final String data1 = "[{\"score\":0.9779489426051869,\"keypoints\":[{\"score\":0.999846875667572,\"part\":\"nose\",\"position\":{\"x\":301.53484901685385,\"y\":257.65654260299624}},{\"score\":0.9999767541885376,\"part\":\"leftEye\",\"position\":{\"x\":328.8400729556804,\"y\":233.78872112983768}},{\"score\":0.999970018863678,\"part\":\"rightEye\",\"position\":{\"x\":279.44834581772784,\"y\":235.45558286516854}},{\"score\":0.9994299411773682,\"part\":\"leftEar\",\"position\":{\"x\":355.4499746410736,\"y\":252.27030664794006}},{\"score\":0.999362587928772,\"part\":\"rightEar\",\"position\":{\"x\":242.94178175717855,\"y\":256.3932047830836}},{\"score\":0.9926291704177856,\"part\":\"leftShoulder\",\"position\":{\"x\":418.8653831148564,\"y\":402.28732638888886}},{\"score\":0.9835844039916992,\"part\":\"rightShoulder\",\"position\":{\"x\":213.73585752184766,\"y\":415.55511665106116}},{\"score\":0.9972991347312927,\"part\":\"leftElbow\",\"position\":{\"x\":449.2840980024969,\"y\":586.3944483458178}},{\"score\":0.9911842346191406,\"part\":\"rightElbow\",\"position\":{\"x\":185.52234511548062,\"y\":595.6762542915105}},{\"score\":0.9951977133750916,\"part\":\"leftWrist\",\"position\":{\"x\":396.5255344881398,\"y\":702.8404435861423}},{\"score\":0.9770950078964233,\"part\":\"rightWrist\",\"position\":{\"x\":300.9757334581773,\"y\":706.9298142946317}},{\"score\":0.925123929977417,\"part\":\"leftHip\",\"position\":{\"x\":385.26665886392004,\"y\":704.0870298845193}},{\"score\":0.934648871421814,\"part\":\"rightHip\",\"position\":{\"x\":253.53137679463168,\"y\":715.0381359238452}},{\"score\":0.9560098648071289,\"part\":\"leftKnee\",\"position\":{\"x\":411.30486306179773,\"y\":963.5716097066166}},{\"score\":0.9841309189796448,\"part\":\"rightKnee\",\"position\":{\"x\":265.5047889357054,\"y\":985.5657966604244}},{\"score\":0.9764760732650757,\"part\":\"leftAnkle\",\"position\":{\"x\":469.0088561173533,\"y\":1163.5944327403245}},{\"score\":0.9131665229797363,\"part\":\"rightAnkle\",\"position\":{\"x\":253.77960166978778,\"y\":1170.9496917915105}}]}]";
        final String data2 = "hey there";
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("Score",data1.toString());

                    //StringEntity se = new StringEntity(obj.toString());
                    //se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    //post.setEntity(se);
                    //httpPost.setEntity(new ByteArrayEntity(params.toString().getBytes("UTF8")));

                    new SendDeviceDetails().execute(String.valueOf(postData));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*public void addListenerOnSpinnerItemSelection()
    {
        spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                Toast.makeText(arg0.getContext(), "Unit selected : " + arg0.getItemAtPosition(arg2).toString(),Toast.LENGTH_SHORT).show();

                if(arg2==0)
                {
                    //convert to mts
                    if(unit==1)
                    {//cm-m
                        h = Double.valueOf(ThreeDForm.format(h*0.01));
                        H = Double.valueOf(ThreeDForm.format(H*0.01));
                        D = Double.valueOf(ThreeDForm.format(D*0.01));
                        L = Double.valueOf(ThreeDForm.format(L*0.01));
                    }
                    else if(unit==2)
                    {//ft-m
                        h = Double.valueOf(ThreeDForm.format(h*.3048));
                        H = Double.valueOf(ThreeDForm.format(H*.3048));
                        D = Double.valueOf(ThreeDForm.format(D*.3048));
                        L = Double.valueOf(ThreeDForm.format(L*.3048));
                    }
                    else if(unit==3)
                    {//in-m
                        h = Double.valueOf(ThreeDForm.format(h*.0254));
                        H = Double.valueOf(ThreeDForm.format(H*.0254));
                        D = Double.valueOf(ThreeDForm.format(D*.0254));
                        L = Double.valueOf(ThreeDForm.format(L*.0254));
                    }
                    unit=0;
                    test = "Results:\nObj Distance = "+D+"\nObj Height = "+H+"\nObj Length = "+L+"\nCam height = "+h+"\n"+units[unit];
                    txt1.setText(test);
                }
                else if(arg2==1)
                {//convert to cms
                    if(unit==0)
                    {//m-cm
                        h = Double.valueOf(ThreeDForm.format(h*100));
                        H = Double.valueOf(ThreeDForm.format(H*100));
                        D = Double.valueOf(ThreeDForm.format(D*100));
                        L = Double.valueOf(ThreeDForm.format(L*100));
                    }
                    else if(unit==2)
                    {//ft-cm
                        h = Double.valueOf(ThreeDForm.format(h*30.48));
                        H = Double.valueOf(ThreeDForm.format(H*30.48));
                        D = Double.valueOf(ThreeDForm.format(D*30.48));
                        L = Double.valueOf(ThreeDForm.format(L*30.48));
                    }
                    else if(unit==3)
                    {//in-cm
                        h = Double.valueOf(ThreeDForm.format(h*2.54));
                        H = Double.valueOf(ThreeDForm.format(H*2.54));
                        D = Double.valueOf(ThreeDForm.format(D*2.54));
                        L = Double.valueOf(ThreeDForm.format(L*2.54));
                    }
                    unit=1;
                    test = "Results:\nObj Distance = "+D+"\nObj Height = "+H+"\nLength = "+L+"\nCam height = "+h+"\n"+units[unit];
                    txt1.setText(test);
                }
                else if(arg2==2)
                {//convert to feet
                    if(unit==0)
                    {//m-ft
                        h = Double.valueOf(ThreeDForm.format(h*3.28084));
                        H = Double.valueOf(ThreeDForm.format(H*3.28084));
                        D = Double.valueOf(ThreeDForm.format(D*3.28084));
                        L = Double.valueOf(ThreeDForm.format(L*3.28084));
                    }
                    else if(unit==1)
                    {//cm-ft
                        h = Double.valueOf(ThreeDForm.format(h*0.0328084));
                        H = Double.valueOf(ThreeDForm.format(H*0.0328084));
                        D = Double.valueOf(ThreeDForm.format(D*0.0328084));
                        L = Double.valueOf(ThreeDForm.format(L*0.0328084));
                    }
                    else if(unit==3)
                    {//in-ft
                        h = Double.valueOf(ThreeDForm.format(h*0.0833333));
                        H = Double.valueOf(ThreeDForm.format(H*0.0833333));
                        D = Double.valueOf(ThreeDForm.format(D*0.0833333));
                        L = Double.valueOf(ThreeDForm.format(L*0.0833333));
                    }
                    unit=2;
                    test = "Results:\nObj Distance = "+D+"\nObj Height = "+H+"\nObj Length = "+L+"\nCam height = "+h+"\n"+units[unit];
                    txt1.setText(test);
                }
                else
                {//convert to in
                    if(unit==0)
                    {//m-in
                        h = Double.valueOf(ThreeDForm.format(h*39.3701));
                        H = Double.valueOf(ThreeDForm.format(H*39.3701));
                        D = Double.valueOf(ThreeDForm.format(D*39.3701));
                        L = Double.valueOf(ThreeDForm.format(L*39.3701));
                    }
                    else if(unit==1)
                    {//cm-in
                        h = Double.valueOf(ThreeDForm.format(h*0.393701));
                        H = Double.valueOf(ThreeDForm.format(H*0.393701));
                        D = Double.valueOf(ThreeDForm.format(D*0.393701));
                        L = Double.valueOf(ThreeDForm.format(L*0.393701));
                    }
                    else if(unit==2)
                    {//ft-in
                        h = Double.valueOf(ThreeDForm.format(h*12));
                        H = Double.valueOf(ThreeDForm.format(H*12));
                        D = Double.valueOf(ThreeDForm.format(D*12));
                        L = Double.valueOf(ThreeDForm.format(L*12));
                    }
                    unit=3;
                    test = "Results:\nObj Distance = "+D+"\nObj Height = "+H+"\nObj Length = "+L+"\nCam height = "+h+"\n"+units[unit];
                    txt1.setText(test);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });
    }*/

    /*public void addListenerOnButton()
    {
        spinner = (Spinner) findViewById(R.id.spinner1);
    }*/





    @Override
    public void onBackPressed()
    {
        onCreate(null);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu)
    {
        Log.d(TAG,"Menu button pressed");
        getMenuInflater().inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {    Log.d(TAG,"selection");
        switch (item.getItemId())

        {
            case R.id.menu_save:
                Log.d(TAG,"save button");
                setContentView(R.layout.save);

                final EditText save_name = (EditText) findViewById(R.id.saveText1);
                final Button saveButton = (Button) findViewById(R.id.savebutton1);

                saveButton.setOnClickListener(new View.OnClickListener()
                {

                    public void onClick(View view)
                    {
                        Log.d(TAG,"save button pressed");

                        //Log.d(TAG,"1");
                        //count = count+1;
                        //Log.d(TAG,"count"+count);
                        //files[count]=filename.toString();
                        //Log.d(TAG,"2");
                        try//on press of save button
                        {
                            //Log.d(TAG,"3");
                            Log.d(TAG,"1");
                            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                            final File directory = contextWrapper.getDir(filepath, Context.MODE_APPEND);
                            myInternalFile = new File(directory , filename);
                            Log.d(TAG,"2");
                            BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
                            buf.append(save_name.getText().toString()+" "+test);
                            buf.newLine();
                            buf.close();
                            Log.d(TAG,"3");
                            //FileOutputStream fos = new FileOutputStream(myInternalFile);
                            //fos.write((save_name.getText().toString()+" "+test).getBytes());
                            //fos.close();
                            //m = m + save_name.getText().toString()+" "+test+"\n";
                            Log.d(TAG,"written to file");
                        }
                        catch (IOException e)
                        {
                            try
                            {
                                BufferedWriter buf = new BufferedWriter(new FileWriter(myInternalFile, true));
                                buf.write("\n\n"+save_name.getText().toString()+" "+test);
                            }
                            catch(IOException e2)
                            {
                                e.printStackTrace();
                            }
                        }
                        save_name.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(),filename+" saved to Internal Storage...", LENGTH_SHORT);
                        toast.show();
                    }
                });

                return true;

            case R.id.menu_search:
                Log.d(TAG,"view button");
                setContentView(R.layout.view);
                TextView viewText = (TextView) findViewById(R.id.viewtextView1);
                String myData = "";
                //Toast.makeText(MainActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                try
                {        	Log.d(TAG,"11");
                    ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                    final File directory = contextWrapper.getDir(filepath, Context.MODE_APPEND);
                    myInternalFile = new File(directory , filename);
                    Log.d(TAG,"12");
                    FileInputStream fis = new FileInputStream(myInternalFile);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br =  new BufferedReader(new InputStreamReader(in));
                    Log.d(TAG,"13");
                    String strLine;
                    while ((strLine=br.readLine()) != null)
                    {
                        myData = myData + "\n" + strLine;
                    }
                    in.close();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                viewText.setText(myData);
                Toast toast1 = Toast.makeText(this,"Data retrieved from Internal Storage...", LENGTH_SHORT);
                toast1.show();
                return true;

            case R.id.menu_delete:
                setContentView(R.layout.view);
                viewText = (TextView) findViewById(R.id.viewtextView1);
                try
                {
                    PrintWriter writer = new PrintWriter(myInternalFile);
                    writer.print("Measurements:\n");
                    writer.close();
                    Toast toast2 = Toast.makeText(this,"Data deleted.", LENGTH_SHORT);
                    toast2.show();
                    viewText.setText("");
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        try
        {
            Log.v("On resume called","------ wl aquire next!");
            wl.acquire();
        }
        catch(Exception ex)
        {
        }
        Log.e(getClass().getSimpleName(), "onResume");
        super.onResume();
        //
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        //
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause()
    {
        try
        {
            Log.v("on pause called", "on pause called");
            wl.release();
        }
        catch(Exception ex)
        {
            Log.e("Exception in on menu", "exception on menu");
        }
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /*@Override
    protected void onStop()
    {
        Log.e(getClass().getSimpleName(), "onStop");
        super.onStop();
        //
        mSensorManager.unregisterListener(this);

        //
    }*/

    @SuppressLint("LongLogTag")
    @Override
    protected void onUserLeaveHint()
    {
        try
        {
            Log.v("on user leave hint pressed", "on userlevve hint pressesd");
            wl.release();
        }
        catch(Exception ex)
        {
            Log.e("Exception in on menu", "exception on menu");
        }
        super.onUserLeaveHint();
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.e(getClass().getSimpleName(), "surfaceCreated");
        camera = Camera.open();
        //camera.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
        //Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO = camera.setFocusMode();
        Camera.Parameters parameters = camera.getParameters();

        //SR
        //if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        //}
        parameters.setPreviewFrameRate(20);
        parameters.setPreviewSize(176,144);
        camera.setParameters(parameters);
        try
        {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        float[] distances = new float[3];
        parameters.getFocusDistances(distances);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        Log.e(getClass().getSimpleName(), "surfaceChanged");
        if (isPreviewRunning)
        {
            camera.stopPreview();
        }
        Camera.Parameters p = camera.getParameters();
        //String focusMode = null;
        //focusMode = findSettableValue(p.getSupportedFocusModes(),Camera.Parameters.FOCUS_MODE_MACRO,"edof");
        p.setPreviewSize(w, h);
        camera.setParameters(p);
        try
        {
            camera.setPreviewDisplay(holder);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
        isPreviewRunning = true;
    }

    /*private static String findSettableValue(Collection<String> supportedValues,String... desiredValues)
    {
    	Log.i(TAG, "Supported values: " + supportedValues);
    	String result = null;
    	if (supportedValues != null)
    	{
    		for (String desiredValue : desiredValues)
    		{
    			if (supportedValues.contains(desiredValue))
    			{
    				result = desiredValue;
    				break;
    			}
    		}
    	}
    	Log.i(TAG, "Settable value: " + result);
    	return result;
    }*/

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e(getClass().getSimpleName(), "surfaceDestroyed");
        camera.stopPreview();
        isPreviewRunning = false;
        camera.release();
    }

    //
    public void onSensorChanged(SensorEvent event)
    {

        switch(event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values.clone();
                //onAccelerometerChanged(values[0],values[1],values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetic = event.values.clone();
                break;
            case Sensor.TYPE_PRESSURE:
                pressure = event.values[0];
                pressure = pressure*100;
            default:
                return;
        }
        if(mGravity != null && mMagnetic != null)
        {
            getDirection();
        }
    }

    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        String data = "";

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection httpURLConnection = null;
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL("13.58.40.172:8079/test");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                //httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                //httpURLConnection.setRequestMethod("POST");

                //httpURLConnection.setDoOutput(true);

                //DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                //wr.writeBytes("PostData=" + params[1]);
                //wr.flush();
                //wr.close();
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();

                /*InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;*/
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
                //Log.i(TAG,JsonResponse);
                Toast.makeText(CameraFocusActivity.this , JsonResponse ,Toast.LENGTH_LONG).show();
                //send to post execute
                return JsonResponse;


            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        /*@Override
        protected void onPostExecute(String result) {
            super.onPostExecute(data);
            Toast.makeText(CameraFocusActivity.this,"Answer" + data, LENGTH_LONG).show();
            //Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }*/
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub

    }

    private float getDirection()
    {
        float[] temp = new float[9];
        float[] R = new float[9];

        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic);

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, R);

        //Return the orientation values

        SensorManager.getOrientation(R, value);

        //value[0] - Z, value[1]-X, value[2]-Y in radians

        return value[1];       //return x
    }
}
