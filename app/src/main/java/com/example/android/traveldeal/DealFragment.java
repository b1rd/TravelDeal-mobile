package com.example.android.traveldeal;

/**
 * Created by b1rd on 10.04.16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DealFragment extends Fragment {

    ArrayAdapter<String> mDealAdapter;
    private ProgressDialog progressDialog;

    public DealFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.dealfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            updateDeals();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mDealAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_deal, // The name of the layout ID.
                        R.id.list_item_deal_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_deal);
        listView.setAdapter(mDealAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String deal = mDealAdapter.getItem(position);
//                Toast.makeText(getActivity(), deal, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, deal);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void updateDeals(){
        FetchDealTask dealTask = new FetchDealTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        dealTask.execute(location);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateDeals();
    }

    public class FetchDealTask extends AsyncTask<String, Void, String[]>{
        ProgressDialog progressDialog;

        private final String LOG_TAG = FetchDealTask.class.getSimpleName();

        private String[] getDealDataFromJson(String dealJsonStr, int numDays)
                throws JSONException {
            JSONObject dealsObject = new JSONObject(dealJsonStr);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray dealArray = dealsObject.optJSONArray("deals");
//            JSONArray dealArray = new JSONArray(dealJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            String[] resultStrs = new String[dealArray.length()];
            for(int i = 0; i < dealArray.length(); i++) {
                JSONObject deal = dealArray.getJSONObject(i);

                resultStrs[i] = deal.getString("title")+"\n"+deal.getString("description");
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected void onPreExecute()
        {
                /*
                 * This is executed on UI thread before doInBackground(). It is
                 * the perfect place to show the progress dialog.
                 */
            progressDialog = ProgressDialog.show((getActivity()),"", "Loading...");
        }

        @Override
        protected String[] doInBackground(String... params){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String dealJsonStr = null;

            try {
                Bundle args = getActivity().getIntent().getExtras();

                String[] baseUrlArray = {
                        "http://ivanledyaev.com:5000/deals/planes",
                        "http://ivanledyaev.com:5000/deals/trains",
                        "http://ivanledyaev.com:5000/deals/ships",
                        "http://ivanledyaev.com:5000/deals"};
                String baseUrl = "http://ivanledyaev.com:5000/deals";
                if(args != null) {
                    int value= args.getInt("EXTRA_LINK");
                    System.out.println("hooray!");
                    System.out.println(value);
                    baseUrl = baseUrlArray[value];
                }

                URL url = new URL(baseUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                dealJsonStr = buffer.toString();
            } catch (IOException e){
                Log.e("DealFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e){
                        Log.e("DealFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getDealDataFromJson(dealJsonStr, 7);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            progressDialog.dismiss();
            if (result != null){
                mDealAdapter.clear();
                for (String dealStr : result){
                    mDealAdapter.add(dealStr);
                }
            }
        }
    }
}