package com.juanlitvin.fenix;

import android.support.v4.util.ArrayMap;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.RequestParams;

import java.util.Map;

public class Device {

    private String idDevice;
    private String idOwner;
    private String idLogged;
    private String name;
    private String ownerName;
    private String loggedName;
    private boolean isOwner;

    public Device(String _idDevice, String _idOwner, String _idLogged, String _name, String _ownerName, String _loggedName, boolean _isOwner) {
        idDevice = _idDevice;
        idOwner = _idOwner;
        idLogged = _idLogged;
        name = _name;
        ownerName = _ownerName;
        loggedName = _loggedName;
        isOwner = _isOwner;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public String getIdOwner() {
        return idOwner;
    }

    public String getIdLogged() {
        return idLogged;
    }

    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getLoggedName() {
        return loggedName;
    }

    public boolean isOwner() {
        return isOwner;
    }

}
