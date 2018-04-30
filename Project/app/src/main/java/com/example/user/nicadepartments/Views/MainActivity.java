package com.example.user.nicadepartments.Views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.nicadepartments.Adapter.DepartmentAdapter;
import com.example.user.nicadepartments.Api.Api;
import com.example.user.nicadepartments.Model.DepartmentModel;
import com.example.user.nicadepartments.R;
import com.tumblr.remember.Remember;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG= "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter departmentsAdapter;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView label;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noData;


    ///Dialog
    private EditText newDepart;
    private Button addRecord;
    private Dialog dialDepartment;
    //////

    private static final String IS_FIRST_TIME = "is_first_time";



    //Variables for progressBar
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    //////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initViews();
        configureRecyclerView();
        LoadData();


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
        switch (item.getItemId()){
            case  R.id.action_addDepartment:
                dialDepartment= new Dialog(MainActivity.this);
                dialDepartment.setContentView(R.layout.dialog_conten);
                dialDepartment.setTitle("Nuevo Departamento");
                newDepart = dialDepartment.findViewById(R.id.newDepartment);
                addRecord = dialDepartment.findViewById(R.id.addRecord);
                label = dialDepartment.findViewById(R.id.label);
                label.setText("Nuevo Departamento");
                addRecord.setText("Agregar");
                dialDepartment.show();
                newDepart.setEnabled(true);
                addRecord.setEnabled(true);

                addRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isConnectedToInternet(getApplicationContext())!=false){
                            sendHttpRequest(newDepart.getText().toString());
                            dialDepartment.dismiss();
                            fetchHttpRequest();
                        }else{
                            saveRecord(newDepart.getText().toString());
                            dialDepartment.dismiss();
                            getFromDataBase();
                        }

                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void initViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.carga);
        recyclerView = findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.getProgressDrawable().setColorFilter(
                Color.parseColor("#415576"), android.graphics.PorterDuff.Mode.SRC_IN);
        appBarLayout = findViewById(R.id.appBar);
        noData = findViewById(R.id.noData);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        fetchHttpRequest();
                    }
                },5000);
            }
        });
    }

    /**
     * To configure the RecyclerView
     */
    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    /**
     * To make an http request
     */

    private void LoadData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressStatus < 100){
                    mProgressStatus++;
                    android.os.SystemClock.sleep(50);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(mProgressStatus);
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFirstTime()) {
                            if(isConnectedToInternet(getApplicationContext())!=false){
                                fetchHttpRequest();
                                storeFirstTime();
                            }else{
                                Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
                                getFromDataBase();
                            }
                        } else {
                            if(isConnectedToInternet(getApplicationContext())==false){
                                Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
                                getFromDataBase();
                            }else{
                                getFromDataBase();
                            }



                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        appBarLayout.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);


                    }
                });
            }
        }).start();
    }

    private void fetchHttpRequest() {

        Call<List<DepartmentModel>> call = Api.instance().getDepartment();
        call.enqueue(new Callback<List<DepartmentModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<DepartmentModel>> call, Response<List<DepartmentModel>> response) {
                if (response.body() != null) {
                    DepartmentAdapter departmentAdapter = new DepartmentAdapter(response.body());

                    if(departmentAdapter.getItemCount()!=0){
                        noData.setVisibility(View.INVISIBLE);
                        sync(response.body());
                        recyclerView.setAdapter(departmentAdapter);
                        departmentAdapter.notifyDataSetChanged();
                        getFromDataBase();
                    }else{
                        recyclerView.setAdapter(departmentAdapter);
                        noData.setVisibility(View.VISIBLE);
                    }


                }


            }

            @Override
            public void onFailure(@NonNull Call<List<DepartmentModel>> call, @NonNull Throwable t) {
                Log.i("Debug: ", t.getMessage());
            }
        });

    }


    private void storeFirstTime() {
        Remember.putBoolean(IS_FIRST_TIME, true);
    }

    private boolean isFirstTime() {
        return Remember.getBoolean(IS_FIRST_TIME, false);
    }

    private void sync(List<DepartmentModel> departmentModels) {
        Realm realm = Realm.getDefaultInstance();


        for(DepartmentModel departmentModel : departmentModels) {
            DepartmentModel department= realm.where(DepartmentModel.class).equalTo("id_department",departmentModel.getId_department()).findFirst();
            if(department==null){
                store(departmentModel);
            }

        }
    }

    private void store(DepartmentModel departmentModelFromApi) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        DepartmentModel departmentModel = realm.createObject(DepartmentModel.class); // Create a new object

        departmentModel.setDepartmentname(departmentModelFromApi.getDepartmentname());
        departmentModel.setId_department(departmentModelFromApi.getId_department());


        realm.commitTransaction();
    }


    private void getFromDataBase() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DepartmentModel> query = realm.where(DepartmentModel.class);

        RealmResults<DepartmentModel> results = query.findAll();

        departmentsAdapter = new DepartmentAdapter(results);
        if(departmentsAdapter.getItemCount()!=0){
            noData.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(departmentsAdapter);
            departmentsAdapter.notifyDataSetChanged();
        }else {
            recyclerView.setAdapter(departmentsAdapter);
            noData.setVisibility(View.VISIBLE);
        }

    }


    private  boolean  isConnectedToInternet(Context context){
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isConnected= (wifi.isAvailable() && wifi.isConnectedOrConnecting() || (mobile.isAvailable() && mobile.isConnectedOrConnecting()));
        return isConnected;
    }

    private void sendHttpRequest(String departName) {
        DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setDepartmentname(departName);


        Call<DepartmentModel> call = Api.instance().createDepartment(departmentModel);
        call.enqueue(new Callback<DepartmentModel>() {
            @Override
            public void onResponse(Call<DepartmentModel> call, Response<DepartmentModel> response) {
                Log.i(TAG, response.body().getDepartmentname());

            }

            @Override
            public void onFailure(Call<DepartmentModel> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());

            }
        });
    }

    private int getID(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DepartmentModel> query = realm.where(DepartmentModel.class);

        RealmResults<DepartmentModel> results = query.findAll();
        int id = 0;

        for (int i =0; i<results.size(); i++){
            id = results.get(i).getId_department();
        }

        return id;

    }

    private void saveRecord(String departmentName){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int id = getID()+1;
        DepartmentModel departmentModel = realm.createObject(DepartmentModel.class); // Create a new object

        departmentModel.setDepartmentname(departmentName);
        departmentModel.setId_department(id);

        Toast.makeText(getApplicationContext(),String.valueOf(id), Toast.LENGTH_LONG).show();

        realm.commitTransaction();
    }
}
