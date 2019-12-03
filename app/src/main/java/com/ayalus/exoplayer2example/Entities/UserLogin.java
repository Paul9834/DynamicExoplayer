package com.ayalus.exoplayer2example.Entities;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("fail")
    @Expose
    private Boolean fail;
    @SerializedName("errors")
    @Expose
    private String errors;

    public Boolean getFail() {
        return fail;
    }

    public void setFail(Boolean fail) {
        this.fail = fail;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

}