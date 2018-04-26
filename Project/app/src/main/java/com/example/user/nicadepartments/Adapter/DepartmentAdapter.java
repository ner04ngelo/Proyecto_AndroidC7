package com.example.user.nicadepartments.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.nicadepartments.Api.Api;
import com.example.user.nicadepartments.Model.DepartmentModel;
import com.example.user.nicadepartments.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by USER on 23/4/2018.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder > {

    private List<DepartmentModel> departments;
    private final String TAG= "MainActivity";

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
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(DepartmentModel.getId_department());
                refreshAdapter(position);
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
        departments.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, departments.size());
    }
}


