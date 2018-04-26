package com.example.user.nicadepartments;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.nicadepartments.Adapter.DepartmentAdapter;
import com.example.user.nicadepartments.Api.Api;
import com.example.user.nicadepartments.Model.DepartmentModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG= "MainActivity";
    private RecyclerView recyclerView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noData;

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
        LoadData();

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.carga);
        recyclerView = findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        appBarLayout = findViewById(R.id.appBar);
        noData = findViewById(R.id.noData);
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
        initViews();
        configureRecyclerView();


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
                        fetchHttpRequest();
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
                        recyclerView.setAdapter(departmentAdapter);
                        departmentAdapter.notifyDataSetChanged();
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









  /*  private void sendHttpRequest() {
        DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setDepartmentname("Paulo");


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

*/


}
