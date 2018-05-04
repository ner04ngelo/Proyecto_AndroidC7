package com.example.user.nicadepartments.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.nicadepartments.Api.Api;
import com.example.user.nicadepartments.Model.DepartmentModel;
import com.example.user.nicadepartments.Model.MunicipalityModel;
import com.example.user.nicadepartments.R;
import com.example.user.nicadepartments.Views.municipality_activity;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by USER on 30/4/2018.
 */

public class MunicipalityAdapter extends RecyclerView.Adapter<MunicipalityAdapter.ViewHolder >{

   private List<MunicipalityModel> municipality;
    private final String TAG= "Municipality_activity";

    private EditText newMunicipali;
    private Button addRecord;
    private Dialog dialMunicipali;
    private TextView label;

    public MunicipalityAdapter(List<MunicipalityModel> municipalities) {
        municipality = municipalities;
    }

    @Override
    public MunicipalityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_municipality, parent, false);
        return new MunicipalityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MunicipalityAdapter.ViewHolder holder, final int position) {
        final MunicipalityModel municipalityModel = municipality.get(position);

        holder.name.setText(municipalityModel.getMunicipality_name());
        final Context context = holder.name.getContext();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Advertencia")
                        .setMessage("¿Seguro que quiere borrar este elemento?")
                        .setIcon(R.mipmap.ic_logo_laucher)
                        .setPositiveButton("Sí",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteData(municipalityModel.getIdDpeartment(),municipalityModel.getId());
                                        refreshAdapter(position);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();


            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialMunicipali = new Dialog(context);
                dialMunicipali.setContentView(R.layout.dialog_conten);
                dialMunicipali.setTitle("Modificar Municipio");
                newMunicipali = dialMunicipali.findViewById(R.id.newDepartment);
                newMunicipali.setHint("Nombre del Municipio");
                addRecord = dialMunicipali.findViewById(R.id.addRecord);
                label = dialMunicipali.findViewById(R.id.label);
                label.setText("Modificar Municipio");
                addRecord.setText("Guardar Cambios");
                dialMunicipali.show();
                newMunicipali.setEnabled(true);
                addRecord.setEnabled(true);

                addRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isConnectedToInternet(context)!=false){
                            updateData(newMunicipali.getText().toString(),municipalityModel.getId(),municipalityModel.getIdDpeartment());
                            updateDataRealm(newMunicipali.getText().toString(),municipalityModel.getId());
                            dialMunicipali.dismiss();
                           /* notifyItemRemoved(position);
                            notifyItemRangeChanged(position, municipality.size());*/
                           notifyDataSetChanged();
                        }else{
                            updateDataRealm(newMunicipali.getText().toString(),municipalityModel.getId());
                            dialMunicipali.dismiss();
                           /* notifyItemRemoved(position);
                            notifyItemRangeChanged(position, municipality.size());*/
                           notifyDataSetChanged();
                        }

                    }
                });
            }

        });


    }

    @Override
    public int getItemCount() {
        return municipality.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        Button edit;
        Button delete;
        public ViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameMunicpality);

            edit = itemView.findViewById(R.id.button_edit);
            delete = itemView.findViewById(R.id.button_delete);
        }
    }

    private void deleteData(int idDepartment,int id){

        final MunicipalityModel municipalityModel = new MunicipalityModel();
        municipalityModel.setId(id);
        municipalityModel.setIdDpeartment(idDepartment);
        Call<MunicipalityModel> call = Api.instance().deleteMunicipality(municipalityModel.getIdDpeartment(),municipalityModel.getId());
        call.enqueue(new Callback<MunicipalityModel>() {
            @Override
            public void onResponse(Call<MunicipalityModel> call, Response<MunicipalityModel> response) {
                if(response != null){
                    //Log.i(TAG, response.body().getMunicipality_name());
                }

            }

            @Override
            public void onFailure(Call<MunicipalityModel> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });

    }

    private void refreshAdapter( final int position){
        Realm realm = Realm.getDefaultInstance();

        // Municipality.remove(position);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                municipality.get(position).deleteFromRealm();
            }
        });
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, municipality.size());
    }
    private  boolean  isConnectedToInternet(Context context){
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isConnected= (wifi.isAvailable() && wifi.isConnectedOrConnecting() || (mobile.isAvailable() && mobile.isConnectedOrConnecting()));
        return isConnected;
    }


    private void updateData(String  nameMunicipality, int id, int idDepartment){
        MunicipalityModel municipalityModel = new MunicipalityModel();
        municipalityModel.setMunicipality_name(nameMunicipality);
        municipalityModel.setId(id);
        municipalityModel.setIdDpeartment(idDepartment);

        Call<MunicipalityModel> call = Api.instance().updateMuncipality(idDepartment,municipalityModel.getId(),municipalityModel);
        call.enqueue(new Callback<MunicipalityModel>() {
            @Override
            public void onResponse(Call<MunicipalityModel> call, Response<MunicipalityModel> response) {
                if(response != null){ Log.i(TAG, response.body().getMunicipality_name());}

            }

            @Override
            public void onFailure(Call<MunicipalityModel> call, Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    private void updateDataRealm(String name, int id){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        MunicipalityModel municipalityModel =realm.where(MunicipalityModel.class)
                .equalTo("id",id ).findFirst();
        municipalityModel.setMunicipality_name(name);
        realm.commitTransaction();
    }


}
