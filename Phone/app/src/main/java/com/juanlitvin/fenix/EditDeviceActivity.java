package com.juanlitvin.fenix;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class EditDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
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

                /*try {
                    JSONObject settings = User.getConfig().getJSONObject("settings");

                    //save changed modules on User for future edits without app restart
                    User.setConfig(User.getConfig().put("modules", modules));

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
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
