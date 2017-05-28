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
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SettingsConfigActivity extends AppCompatActivity {

    JSONObject settings;
    JSONArray availableModules;
    List<View> pendingViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_config);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutSettings);

        try {
            settings = new JSONObject(getIntent().getStringExtra("settings"));
            availableModules = new JSONArray(getIntent().getStringExtra("available-modules"));

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
                    View view = getConfigViewByFieldConfig(fieldValue, key, fieldKey);

                    TextView lbl = new TextView(this);
                    lbl.setText(fieldKey.substring(0, 1).toUpperCase() + fieldKey.substring(1) + ":");
                    layout.addView(lbl);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 0, 0, dpToPx(20));
                    view.setLayoutParams(params);
                    layout.addView(view);
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

    private View getConfigViewByFieldConfig(JSONObject fieldValue, final String pkg, final String field) throws JSONException {
        switch (fieldValue.getString("method")) {
            case "spinner":
                final List<String> items = getStringListFromJsonArray(fieldValue.getJSONArray("items"));

                Spinner spinner = new Spinner(getApplicationContext());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            settings.getJSONObject(pkg).put(field, items.get(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                //set default value
                for (int i = 0; i < spinner.getCount();i++) {
                    if (spinner.getItemAtPosition(i).toString().equals(settings.getJSONObject(pkg).getString(field))) {
                        spinner.setSelection(i);
                    }
                }

                return spinner;
            case "text":
                EditText editText = new EditText(this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                editText.setTag(pkg + "\"" + field);
                editText.setText(settings.getJSONObject(pkg).getString(field));

                pendingViews.add(editText); //add to pending views for its value to be saved later
                return editText;
            case "datetime":
                Calendar now = Calendar.getInstance();
                final DateTime dt = new DateTime(User.getConfig().getJSONObject("settings").getJSONObject(pkg).getLong("millis"));
                final DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
                                TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                        try {
                                            DateTime datetime = new DateTime(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute, 0);

                                            settings.getJSONObject(pkg).put(field, Long.toString(datetime.getMillis()));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, dt.getHourOfDay(), dt.getMinuteOfHour(), dt.getSecondOfDay(), true);
                                tpd.show(getFragmentManager(), "timePickerDialog");
                            }
                        }, dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth()
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
                try {
                    //save edittext's changes
                    for (View view : pendingViews) {
                        try {
                            String pkg = view.getTag().toString().split("\"")[0];
                            String field = view.getTag().toString().split("\"")[1];
                            String value = "";

                            if (view instanceof EditText) {
                                value = ((EditText)view).getText().toString();
                            }

                            settings.getJSONObject(pkg).put(field, value);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Could not save settings for\n" + view.getTag().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    User.setConfig(User.getConfig().put("settings", settings)); //get user config, add editted settings and save to user.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}