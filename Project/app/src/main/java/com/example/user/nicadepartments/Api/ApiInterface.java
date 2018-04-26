package com.example.user.nicadepartments.Api;

import com.example.user.nicadepartments.Model.DepartmentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by USER on 04/03/2018.
 */

public interface ApiInterface {

    @GET("departments")
    Call<List<DepartmentModel>> getDepartment();

   /* @POST("departments")
    Call<DepartmentModel> createDepartment(@Body DepartmentModel departmentModel);*/

    @DELETE("departments/{id}")
    Call<DepartmentModel> deleteDepartment (@Path("id")int id);

}