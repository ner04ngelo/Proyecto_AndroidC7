package com.example.user.nicadepartments.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.nicadepartments.Adapter.DepartmentAdapter;
import com.example.user.nicadepartments.Adapter.MunicipalityAdapter;
import com.example.user.nicadepartments.Api.Api;
import com.example.user.nicadepartments.Model.DepartmentModel;
import com.example.user.nicadepartments.Model.MunicipalityModel;
import com.example.user.nicadepartments.R;
import com.tumblr.remember.Remember;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class municipality_activity extends AppCompatActivity {

    private final String TAG= "Municipality Activity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter municipalityAdapter;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView label;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noData;
    private int id_department;

    ///Dialog
    private EditText newDepart;
    private Button addRecord;
    private Dialog dialDepartment;
    //////





    //Variables for progressBar
    private ProgressBar mProgressBar;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    //////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality);

        getSupportActionBar().setTitle("Municipios");
        /*toolbar = findViewById(R.id.toolbarMunicipality);
        setSupportActionBar(toolbar);*/

        Intent intent = getIntent();
        Bundle bundelExtras = intent.getExtras();
        if(!bundelExtras.isEmpty()){
            id_department = bundelExtras.getInt("ID");
        }

        initViews();
        configureRecyclerView();
        LoadData();
    }


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
                dialDepartment= new Dialog(municipality_activity.this);
                dialDepartment.setContentView(R.layout.dialog_conten);
                dialDepartment.setTitle("Nuevo Municipio");
                newDepart = dialDepartment.findViewById(R.id.newDepartment);
                newDepart.setHint("Nombre del municipio");
                addRecord = dialDepartment.findViewById(R.id.addRecord);
                label = dialDepartment.findViewById(R.id.label);
                label.setText("Nuevo Municipio");
                addRecord.setText("Agregar");
                dialDepartment.show();
                newDepart.setEnabled(true);
                addRecord.setEnabled(true);

                addRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isConnectedToInternet(getApplicationContext())!=false){
                            sendHttpRequest(id_department,newDepart.getText().toString());
                            dialDepartment.dismiss();
                            fetchHttpRequestMunicipality(id_department);
                        }else{
                            saveRecord(newDepart.getText().toString(),id_department);
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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutMunicipality);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.carga);
        recyclerView = findViewById(R.id.municpalityRecycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbarMunicipality);
        mProgressBar.getProgressDrawable().setColorFilter(
                Color.parseColor("#415576"), android.graphics.PorterDuff.Mode.SRC_IN);

        noData = findViewById(R.id.noData);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        if(isConnectedToInternet(municipality_activity.this)!=false){
                            fetchHttpRequestMunicipality(id_department);
                        }else{
                            getFromDataBase();
                        }
                    }
                },5000);
            }
        });
    }

    private void fetchHttpRequestMunicipality(int id) {

        Call<List<MunicipalityModel>> call = Api.instance().getMunicipality(id);
        call.enqueue(new Callback<List<MunicipalityModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MunicipalityModel>> call, Response<List<MunicipalityModel>> response) {
                if (response.body() != null) {
                    MunicipalityAdapter municipalityAdapter = new MunicipalityAdapter(response.body());
                  // Toast.makeText(getApplicationContext(),String.valueOf(response.body().get(1).getIdDpeartment()),Toast.LENGTH_LONG).show();

                    if(municipalityAdapter.getItemCount()!=0){
                        noData.setVisibility(View.INVISIBLE);
                        sync(response.body());
                        recyclerView.setAdapter(municipalityAdapter);
                        municipalityAdapter.notifyDataSetChanged();
                        getFromDataBase();
                    }else{
                        recyclerView.setAdapter(municipalityAdapter);
                        noData.setVisibility(View.VISIBLE);
                    }


                }


            }

            @Override
            public void onFailure(@NonNull Call<List<MunicipalityModel>> call, @NonNull Throwable t) {
                Log.i("Debug: ", t.getMessage());
            }
        });

    }

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


                            if(isConnectedToInternet(getApplicationContext())!=false){
                                fetchHttpRequestMunicipality(id_department);

                            }else{
                                Toast.makeText(getApplicationContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
                                getFromDataBase();
                            }


                        recyclerView.setVisibility(View.VISIBLE);

                        mProgressBar.setVisibility(View.INVISIBLE);


                    }
                });
            }
        }).start();
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void sendHttpRequest(int id ,String municipalityName) {
        MunicipalityModel municipalityModel = new MunicipalityModel();
        municipalityModel.setMunicipality_name(municipalityName);
        municipalityModel.setIdDpeartment(id);


        Call<MunicipalityModel> call = Api.instance().createMunicipality(id,municipalityModel);
        call.enqueue(new Callback<MunicipalityModel>() {
            @Override
            public void onResponse(Call<MunicipalityModel> call, Response<MunicipalityModel> response) {

            }

            @Override
            public void onFailure(Call<MunicipalityModel> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());

            }
        });
    }

    private void saveRecord(String municipalityName, int idDepartment){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        MunicipalityModel municipalityModel = realm.createObject(MunicipalityModel.class); // Create a new object

        municipalityModel.setMunicipality_name(municipalityName);
        municipalityModel.setId(idDepartment);
        municipalityModel.setIdDpeartment(idDepartment);


        realm.commitTransaction();
    }


    private void store(MunicipalityModel municipalityModelFromApi) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        MunicipalityModel municipalityModel = realm.createObject(MunicipalityModel.class); // Create a new object

        municipalityModel.setMunicipality_name(municipalityModelFromApi.getMunicipality_name());
        municipalityModel.setId(municipalityModelFromApi.getId());
        municipalityModel.setIdDpeartment(municipalityModelFromApi.getIdDpeartment());


        realm.commitTransaction();
    }

    private void sync(List<MunicipalityModel> municipalityModels) {
        Realm realm = Realm.getDefaultInstance();

        for(MunicipalityModel municipalityModel : municipalityModels) {
            MunicipalityModel municipality= realm.where(MunicipalityModel.class).equalTo("id",municipalityModel.getId()).findFirst();
            if(municipality==null){
                store(municipalityModel);
            }

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

    private void getFromDataBase() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MunicipalityModel> query = realm.where(MunicipalityModel.class).equalTo("idDpeartment",id_department);

        RealmResults<MunicipalityModel> results = query.findAll();

        municipalityAdapter = new MunicipalityAdapter (results);
        if(municipalityAdapter.getItemCount()!=0){
            noData.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(municipalityAdapter);
            municipalityAdapter.notifyDataSetChanged();
        }else {
            recyclerView.setAdapter(municipalityAdapter);
            noData.setVisibility(View.VISIBLE);
        }

    }


}

