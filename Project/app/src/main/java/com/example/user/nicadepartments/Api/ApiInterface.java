package com.example.user.nicadepartments.Api;

import com.example.user.nicadepartments.Model.DepartmentModel;
import com.example.user.nicadepartments.Model.MunicipalityModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by USER on 04/03/2018.
 */

public interface ApiInterface {

    @GET("departments")
    Call<List<DepartmentModel>> getDepartment();

   @POST("departments")
    Call<DepartmentModel> createDepartment(@Body DepartmentModel departmentModel);

   @PUT("departments/{id}")
    Call<DepartmentModel> updateDepartment(@Path("id")int id,@Body DepartmentModel departmentModel);

    @DELETE("departments/{id}")
    Call<DepartmentModel> deleteDepartment (@Path("id")int id);


   ///@TODO Municipios

    @GET("departments/{id}/municipalities")
    Call<List<MunicipalityModel>> getMunicipality (@Path("id")int id);

    @POST("departments/{id}/municipalities")
    Call<MunicipalityModel> createMunicipality(@Path("id") int id,@Body MunicipalityModel municipalityModel );

    @PUT("departments/{idDepartment}/municipalities/{id}")
    Call<MunicipalityModel> updateMuncipality(@Path("idDepartment")int idDepartment,@Path("id") int id,@Body MunicipalityModel municipalityModel);

    @DELETE("departments/{idDepartment}/municipalities/{id}")
    Call<MunicipalityModel> deleteMunicipality (@Path("idDepartment")int idDepartment, @Path("id")int id);


}
