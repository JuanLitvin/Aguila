package com.juanlitvin.fenix;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter {

    List<Device> devices;

    public DeviceListAdapter(Context _context, List<Device> _devices) {
        super(_context, R.layout.devices_list);
        devices = _devices;
    }

    @Override
    public int getCount(){
        return devices != null ? devices.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
        }
        v = LayoutInflater.from(getContext()).inflate(R.layout.devices_list, null);

        Device device = devices.get(position);

        TextView lblDeviceName = (TextView) v.findViewById(R.id.lblDeviceName);
        TextView lblUserName = (TextView) v.findViewById(R.id.lblUserName);

        lblDeviceName.setText(device.getName());
        lblUserName.setText(device.getOwnerName());

        return v;
    }

}
