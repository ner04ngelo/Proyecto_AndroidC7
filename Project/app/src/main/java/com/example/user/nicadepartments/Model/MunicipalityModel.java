package com.example.user.nicadepartments.Model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by USER on 30/4/2018.
 */

public class MunicipalityModel extends RealmObject {
    @SerializedName("id")
    private  int id;
    @SerializedName("name")
    private String municipality_name;

    @SerializedName("department_id")
    private int idDpeartment;

    public int getIdDpeartment() {
        return idDpeartment;
    }

    public void setIdDpeartment(int idDpeartment) {
        this.idDpeartment = idDpeartment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMunicipality_name() {
        return municipality_name;
    }

    public void setMunicipality_name(String municipality_name) {
        this.municipality_name = municipality_name;
    }
}
