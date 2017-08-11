package com.juanlitvin.fenix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
        }
        v = LayoutInflater.from(getContext()).inflate(R.layout.devices_list, null);

        final Device device = devices.get(position);

        TextView lblDeviceName = (TextView) v.findViewById(R.id.lblDeviceName);
        TextView lblUserName = (TextView) v.findViewById(R.id.lblUserName);
        ImageButton btnVoice = (ImageButton) v.findViewById(R.id.btnVoice);
        ImageButton btnLogout = (ImageButton) v.findViewById(R.id.btnLogout);
        ImageButton btnEdit = (ImageButton) v.findViewById(R.id.btnEdit);
        ImageButton btnRemove = (ImageButton) v.findViewById(R.id.btnRemove);

        if (device.isOwner()) { //only will set listeners to owner functions if user is owner of the device
            //listeners

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.context.startActivity(new Intent(MainActivity.context, EditDeviceActivity.class).putExtra("id", device.getIdDevice()));
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showYesNoDialog("Remove?", "Would you like to remove the device \"" + device.getName() + "\". This device will no longer show as owned as you, unless you register it again.", new YesNoHandler() {
                        @Override
                        public void onYes() {
                            final Device device = User.getDevices().get(position);
                            User.getDevices().remove(device);
                            notifyDataSetChanged();

                            RequestParams params = new RequestParams();
                            params.put("device-id", device.getIdDevice());

                            Map<String, String> headers = new ArrayMap<>();
                            headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");
                            headers.put("Auth", User.getApiKey());

                            RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/user/phone/removemirror", params, headers, new RESTClient.ResponseHandler() {
                                @Override
                                public void onSuccess(int code, String responseBody) {

                                }

                                @Override
                                public void onFailure(int code, String responseBody, Throwable error) {
                                    Toast.makeText(getContext(), "Hubo un error al intentar eliminar el dispositivo", Toast.LENGTH_SHORT).show();
                                    User.getDevices().add(position, device);
                                    notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public void onNo() {

                        }
                    });
                }
            });
        } else {
            btnEdit.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
        }

        if (device.getIdLogged().equals(User.getIdUser())) {
            //user id is same as logged, so this user is logged in to this mirror (might be owner and not be logged in)
            btnVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)MainActivity.context).startActivityForResult(new Intent(MainActivity.context, VoiceActivity.class).putExtra("id-device", device.getIdDevice()), MainActivity.REQUEST_CODE_VOICE);
                }
            });
        } else {
            btnVoice.setVisibility(View.GONE);
        }

        //global listeners
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog("Sign out?", "Would you like to sign out of the device \"" + device.getName() + "\".", new YesNoHandler() {
                    @Override
                    public void onYes() {
                        final Device device = User.getDevices().get(position);
                        User.getDevices().remove(device);
                        notifyDataSetChanged();

                        RequestParams params = new RequestParams();
                        params.put("device-id", device.getIdDevice());

                        Map<String, String> headers = new ArrayMap<>();
                        headers.put("Token", "?QKGe,q$uxkwi7cJ-h4zsuW],^{BFEurhNkfW~-TAnUGc%TGJ4PqmIIp3(FNBj%O");
                        headers.put("Auth", User.getApiKey());

                        RESTClient.post("http://juanlitvin.com/api/aguila/v1/index.php/user/phone/signoutmirror", params, headers, new RESTClient.ResponseHandler() {
                            @Override
                            public void onSuccess(int code, String responseBody) {

                            }

                            @Override
                            public void onFailure(int code, String responseBody, Throwable error) {
                                Toast.makeText(getContext(), "Hubo un error al intentar cerrar sesión en el dispositivo", Toast.LENGTH_SHORT).show();
                                User.getDevices().add(position, device);
                                notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onNo() {

                    }
                });
            }
        });

        lblDeviceName.setText(device.getName());
        lblUserName.setText(device.getLoggedName());

        return v;
    }

    private void showYesNoDialog(String title, String message, final YesNoHandler handler) {
        new AlertDialog.Builder(getContext())
                .setTitle(title.length() == 0 ? null : title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.onYes();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.onNo();
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private interface YesNoHandler {
        void onYes();
        void onNo();
    }

}
