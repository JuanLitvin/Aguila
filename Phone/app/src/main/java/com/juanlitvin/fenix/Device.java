package com.juanlitvin.fenix;

public class Device {

    private String idDevice;
    private String name;
    private String ownerName;
    private String loggedName;
    private boolean isOwner;

    public Device(String _idDevice, String _name, String _ownerName, String _loggedName, boolean _isOwner) {
        idDevice = _idDevice;
        name = _name;
        ownerName = _ownerName;
        loggedName = _loggedName;
        isOwner = _isOwner;
    }

    public String getIdDevice() {
        return idDevice;
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
