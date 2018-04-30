package com.example.user.nicadepartments.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.nicadepartments.Api.Api;
import com.example.user.nicadepartments.Model.DepartmentModel;
import com.example.user.nicadepartments.R;
import com.example.user.nicadepartments.Views.UpdateDepartmentActivity;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by USER on 23/4/2018.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder > {

    private List<DepartmentModel> departments;
    private final String TAG= "MainActivity";

    ///Dialog
    private EditText newDepart;
    private Button addRecord;
    private Dialog dialDepartment;
    private TextView label;
    //////

    public DepartmentAdapter(List<DepartmentModel> departments) {
        this.departments = departments;
    }

    @Override
    public DepartmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DepartmentAdapter.ViewHolder holder, final int position) {
        final DepartmentModel DepartmentModel = departments.get(position);

        holder.name.setText(DepartmentModel.getDepartmentname());
        final Context context = holder.name.getContext();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(DepartmentModel.getId_department());
                refreshAdapter(position);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialDepartment= new Dialog(context);
                dialDepartment.setContentView(R.layout.dialog_conten);
                dialDepartment.setTitle("Modificar Departamento");
                newDepart = dialDepartment.findViewById(R.id.newDepartment);
                addRecord = dialDepartment.findViewById(R.id.addRecord);
                label = dialDepartment.findViewById(R.id.label);
                label.setText("Modificar Departamento");
                addRecord.setText("Guardar Cambios");
                dialDepartment.show();
                newDepart.setEnabled(true);
                addRecord.setEnabled(true);

                addRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isConnectedToInternet(context)!=false){
                            updateData(newDepart.getText().toString(),DepartmentModel.getId_department());
                            updateDataRealm(newDepart.getText().toString(),DepartmentModel.getId_department());
                            dialDepartment.dismiss();
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, departments.size());
                        }else{
                            updateDataRealm(newDepart.getText().toString(),DepartmentModel.getId_department());
                            dialDepartment.dismiss();
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, departments.size());
                        }

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        Button edit;
        Button delete;
        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

            edit = itemView.findViewById(R.id.button_edit);
            delete = itemView.findViewById(R.id.button_delete);
        }
    }

    private void deleteData(int id){

        final DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setId_department(id);
        Call<DepartmentModel> call = Api.instance().deleteDepartment(departmentModel.getId_department());
        call.enqueue(new Callback<DepartmentModel>() {
            @Override
            public void onResponse(Call<DepartmentModel> call, Response<DepartmentModel> response) {
                if(response != null){
                    Log.i(TAG, response.body().getDepartmentname());
                }

            }

            @Override
            public void onFailure(Call<DepartmentModel> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });

    }

    private void refreshAdapter( final int position){
        Realm realm = Realm.getDefaultInstance();

       // departments.remove(position);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                departments.get(position).deleteFromRealm();
            }
        });
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, departments.size());
    }
    private  boolean  isConnectedToInternet(Context context){
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isConnected= (wifi.isAvailable() && wifi.isConnectedOrConnecting() || (mobile.isAvailable() && mobile.isConnectedOrConnecting()));
        return isConnected;
    }

    private void updateData(String nameDepartment, int id){
        DepartmentModel departmentModel = new DepartmentModel();
        departmentModel.setId_department(id);
        departmentModel.setDepartmentname(nameDepartment);

        Call<DepartmentModel> call = Api.instance().updateDepartment(departmentModel.getId_department(),departmentModel);
        call.enqueue(new Callback<DepartmentModel>() {
            @Override
            public void onResponse(Call<DepartmentModel> call, Response<DepartmentModel> response) {
                if(response != null){ Log.i(TAG, response.body().getDepartmentname());}

            }

            @Override
            public void onFailure(Call<DepartmentModel> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    private void updateDataRealm(String name, int id){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DepartmentModel departmentModel =realm.where(DepartmentModel.class)
                .equalTo("id_department",id ).findFirst();
        departmentModel.setDepartmentname(name);
        realm.commitTransaction();
    }


}


