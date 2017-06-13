package com.example.round.neopul15;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.round.neopul15.R.drawable.fertilizer;

/**
 * Created by Round on 2017-06-12.
 */

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnLongClickListener{

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    TextView nickname, email, seed, fruit;
    View header;
    NavigationView navigationView;
    GridLayout grid;

    private OverlayService mOverlayService;
    private Intent overLayService;

    private Boolean mConnected = false;
    private Boolean mClear = false;
    private static IBinder mOverlayBinder;

    BackPressCloseHandler backPressCloseHandler;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("MainActivity","onServiceConnection");
            mOverlayBinder = iBinder;
            mOverlayService = ((OverlayService.LocalBinder) iBinder).getService();
            mConnected = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("MainActivity","onServiceDIsconencted");
            //mConnected = false;
        }
    };

    @Override
    public boolean onLongClick(View view){

        if(mOverlayService.onTest((ImageView)findViewById(view.getId()))) {
            Log.i("StartActivity",view.toString());
            mOverlayService.onLongClick((ImageView) findViewById(view.getId()));
            finish();
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        overLayService = new Intent(StartActivity.this, OverlayService.class);

        if(!isServiceRunning(OverlayService.class)) {
            bindService(overLayService, mServiceConnection, BIND_AUTO_CREATE);
            startService(overLayService);
        }

        pref = getApplicationContext().getSharedPreferences("Login",getApplicationContext().MODE_PRIVATE);
        editor = pref.edit();

        grid=(GridLayout)findViewById(R.id.grid);

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        getUserInform();
        getPlant();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        backPressCloseHandler = new BackPressCloseHandler(this);

        Button storeButton = (Button)findViewById(R.id.storeButton);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,StoreMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)getSystemService(getApplicationContext().ACTIVITY_SERVICE);

        Log.i("MainActivity","isServiceRunning : "+mOverlayService);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("MainActivity", true+"");
                Log.i("MainActivity","*************"+mOverlayBinder);
                mOverlayService = ((OverlayService.LocalBinder) mOverlayBinder).getService();
                mConnected = true;
                return true;
            }
        }
        Log.i ("MainActivity", false+"");
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.i("MainActivity","onResume");
        if(mConnected){
            if(mOverlayService.getSize() >0){
                mOverlayService.invisible();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        Log.i("MainActivity","onPause");
        if(mConnected) {
            mOverlayService.visible();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("MainActivity","onDestroy");
        if(mConnected) {
//            if(mOverlayService.getSize() == 0 && mClear == false)
//                unbindService(mServiceConnection);
            //unregisterReceiver(restartService);
        }
    }

    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserInform(){

        String url="http://202.31.200.143/user/"+pref.getString("id","");

        final JsonObjectRequest request = new JsonObjectRequest(url,null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response){
                        Log.i("StartActivity","getUserInform : "+response.toString());

                        nickname =(TextView)header.findViewById(R.id.nicknameText);
                        email = (TextView)header.findViewById(R.id.emailText);
                        seed = (TextView)header.findViewById(R.id.seednumText);
                        fruit = (TextView)header.findViewById(R.id.fruitnumText);

                        Menu menu = navigationView.getMenu();
                        MenuItem medicine = menu.findItem(R.id.nav_medicine);
                        MenuItem fertilizer = menu.findItem(R.id.nav_fertilizer);
                        MenuItem pesticide = menu.findItem(R.id.nav_pesticide);

                        try{
                            String name = response.getString("nickname");
                            int getSeed = response.getInt("seed");
                            int getFruit = response.getInt("fruit");
                            int getMedicine = response.getInt("water");
                            int getFerilizer = response.getInt("ferilizer");
                            int getPesticideNum = response.getInt("pesticideNum");

                            nickname.setText(name);
                            email.setText(pref.getString("id",""));
                            seed.setText(Integer.toString(getSeed));
                            fruit.setText(Integer.toString(getFruit));
                            medicine.setTitle("Medicine     x"+Integer.toString(getMedicine));
                            fertilizer.setTitle("Fertilizer   x"+Integer.toString(getFerilizer));
                            pesticide.setTitle("Pesticide    x"+Integer.toString(getPesticideNum));


                        }catch (JSONException e){
                            Log.i("MainActivity","JSONException :"+e.toString());
                        }
                    }
                },new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.i("MainActivity","getUserInform Error"+error.toString());
                    }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getPlant(){

        String url="http://202.31.200.143/user/plant/"+pref.getString("id","");

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response){
                        Log.i("StoreItemActivity",response.toString());

                        for(int i=0;i<response.length();i++){

                            try{
                                JSONObject object = response.getJSONObject(i);

                                int id = object.getInt("plantNo");
                                String flower = object.getString("flowrNo");
                                String pollen = object.getString("potNo");
                                Log.i("StartActivity","plant"+flower+pollen);
                                int plantId = getResources().getIdentifier("plantImage"+Integer.toString(i+1),"id",getPackageName());
                                ImageView plant = (ImageView)findViewById(plantId);
                                plant.setOnLongClickListener(StartActivity.this);
                                plant.set

                                plantId = getResources().getIdentifier("plant"+flower+pollen,"drawable",getPackageName());
                                plant.setImageResource(plantId);
                                plant.setTag(plantId);

                            }catch (JSONException e){
                                Log.i("StoreItemActivity",e.toString());
                            }
                        }
                    }
                },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.i("StoreItemActivity",error.toString());
            }
        });

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    protected class BackPressCloseHandler {

        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity,
                    "\'Back\'Press the button once more to exit.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
