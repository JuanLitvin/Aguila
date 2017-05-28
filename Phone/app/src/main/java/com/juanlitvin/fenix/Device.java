package com.juanlitvin.fenix;

public class Device {

    private String idDevice;
    private String name;
    private String ownerName;
    private String loggedName;

    public Device(String _idDevice, String _name, String _ownerName, String _loggedName) {
        idDevice = _idDevice;
        name = _name;
        ownerName = _ownerName;
        loggedName = _loggedName;
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

}
