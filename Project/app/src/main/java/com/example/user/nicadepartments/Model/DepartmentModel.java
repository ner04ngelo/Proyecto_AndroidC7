package com.example.user.nicadepartments.Model;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by USER on 2/4/2018.
 */

public class DepartmentModel  extends RealmObject {
    @SerializedName("department_name")
    private String departmentname;

    @SerializedName("id")
    private int id_department;

    public int getId_department() {
        return id_department;
    }

    public void setId_department(int id_department) {
        this.id_department = id_department;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {

        this.departmentname = departmentname;
    }
}
