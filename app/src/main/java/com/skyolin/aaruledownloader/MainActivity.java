package com.skyolin.aaruledownloader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.skyolin.aaruledownloader.SQHelper.DatabaseHandler;
import com.skyolin.aaruledownloader.SQHelper.ruleField;
import com.skyolin.aaruledownloader.Util.JSONParser;
import com.skyolin.aaruledownloader.Util.ruleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends Activity {

    static final int ID_REF = 1;

    public static  DatabaseHandler db;
    ListView listView;
    SpotsDialog LoadDG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.downloadListView);

        db = new DatabaseHandler(this);
        LoadDG = new SpotsDialog(this);

        new LoadRules().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem ref_item = menu.add(Menu.NONE, ID_REF, 0, R.string.refresh);
        ref_item.setIcon(android.R.drawable.ic_menu_rotate);
        ref_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_REF:
                new LoadRules().execute();
                break;
        }
        return false;
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadRules extends AsyncTask<String, String, String> {

        JSONParser jParser = new JSONParser();
        JSONArray rules = null;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadDG.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            JSONObject json = jParser.makeHttpRequest("http://www.aareadapp.com/test/admin/api/site.php?t=" + String.valueOf(db.getUpdateTime()) );

            try {
                rules = json.getJSONArray("sitelist");
                // looping through All Rules
                for (int i = 0; i < rules.length(); i++) {
                    JSONObject c = rules.getJSONObject(i);

                    // Storing each json item in variable
                    String idx = c.getString("siteindex");
                    String name = c.getString("sitename");
                    int ver = c.getInt("version");
                    String url = c.getString("siteurl");


                    ruleField newrule = new ruleField(idx, name, ver, url);
                    ruleField oldRule = db.getRule(idx);
                    if(oldRule == null)
                        db.addRule(newrule);
                    else
                        db.updateRule(newrule);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            final List<ruleField> ruleList = db.getAllRULE();
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    // updating UI from Background Thread
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ListAdapter adapter = new ruleAdapter(getApplicationContext(), R.id.downloadListView, ruleList);
                            listView.setAdapter(adapter);
                            db.setUpdateTime();
                            LoadDG.dismiss();
                        }
                    });
                }
            });
        }

    }
}
