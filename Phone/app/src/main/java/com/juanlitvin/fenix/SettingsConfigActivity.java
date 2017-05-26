package com.juanlitvin.fenix;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class SettingsConfigActivity extends AppCompatActivity {

    JSONObject config;
    JSONArray availableModules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_config);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutSettings);

        try {
            config = new JSONObject(getIntent().getStringExtra("config"));
            availableModules = new JSONArray(getIntent().getStringExtra("available-modules"));

            JSONObject settings = config.getJSONObject("settings");

            Iterator<String> iterator = settings.keys();
            while(iterator.hasNext()) {
                //each module. has multiple keys
                final String key = (String)iterator.next();
                JSONObject moduleFields = getJSONObjectByModulePackage(key).getJSONObject("fields");
                Iterator<String> moduleIterator = moduleFields.keys();
                while (moduleIterator.hasNext()) {
                    //each key in each module
                    final String fieldKey = (String) moduleIterator.next();
                    JSONObject fieldValue = moduleFields.getJSONObject(fieldKey);
                    View view = getConfigViewByFieldConfig(fieldValue);

                    TextView lbl = new TextView(this);
                    lbl.setText(fieldKey.substring(0, 1).toUpperCase() + fieldKey.substring(1) + ":");
                    layout.addView(lbl);

                    layout.addView(view);

                    /*View view = getLayoutInflater().inflate(R.layout.alert_input_simple, null);
                    ((TextView)view.findViewById(R.id.text1)).setText(key + "\n" + moduleKey + ":");
                    final EditText txt = ((EditText)view.findViewById(R.id.text2));
                    txt.setHint("Value");
                    txt.setText(value);

                    new AlertDialog.Builder(this)
                            .setTitle("Settings")
                            .setView(view)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        if (!txt.getText().toString().isEmpty()) {
                                            setting.getJSONObject(key).put(moduleKey, txt.getText().toString());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();*/
                }
            }

            //finished editing settings
            //save settings
            /*config.put("settings", setting);*/

            //save to User
            /*User.setConfig(config);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View getConfigViewByFieldConfig(JSONObject fieldValue) throws JSONException {
        switch (fieldValue.getString("method")) {
            case "spinner":
                Spinner spinner = new Spinner(getApplicationContext());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getStringListFromJsonArray(fieldValue.getJSONArray("items"))));
                return spinner;
            case "text":
                EditText editText = new EditText(this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return editText;
            case "datetime":
                Calendar now = Calendar.getInstance();
                final DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                            }
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
                );

                Button button = new Button(this);
                button.setText("Set date");
                button.setBackgroundColor(Color.parseColor("#FF0000"));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dpd.show(getFragmentManager(), "DateTimePicker");
                    }
                });

                return button;
            default:
                EditText editText2 = new EditText(this);
                editText2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return editText2;
        }
    }

    private List<String> getStringListFromJsonArray(JSONArray items) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            list.add(items.getString(i));
        }
        return list;
    }

    public int dpToPx(int dp) {
        return Math.round(dp * (this.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private JSONObject getJSONObjectByModulePackage(String pkg) throws JSONException {
        for (int i = 0; i < availableModules.length(); i++) {
            try {
                JSONObject temp = availableModules.getJSONObject(i);
                if (temp.getString("package").equals(pkg)) {
                    return availableModules.getJSONObject(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setMessage("Saving...");
                dialog.show();

                try {
                    /*JSONObject settings = User.getConfig().getJSONObject("settings");

                    User.sendConfigChange(settings.toString(), mapToJsonObjectString(newModules), new RESTClient.ResponseHandler() {
                        @Override
                        public void onSuccess(int code, String responseBody) {
                            dialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }

                        @Override
                        public void onFailure(int code, String responseBody, Throwable error) {
                            dialog.dismiss();
                        }
                    });*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
