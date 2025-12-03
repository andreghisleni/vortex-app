package br.com.andreg.mobile.vortex.model;

import com.google.gson.annotations.SerializedName;

public class Member {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("visionId")
    private String visionId;
    @SerializedName("register")
    private String register;
    @SerializedName("session")
    private ScoutSession session; // Objeto aninhado para o Spinner

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVisionId() {
        return visionId;
    }

    public String getRegister() {
        return register;
    }

    public ScoutSession getSession() {
        return session;
    }
}