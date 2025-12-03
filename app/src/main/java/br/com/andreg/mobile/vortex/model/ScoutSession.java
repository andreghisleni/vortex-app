package br.com.andreg.mobile.vortex.model;

import com.google.gson.annotations.SerializedName;

public class ScoutSession {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}