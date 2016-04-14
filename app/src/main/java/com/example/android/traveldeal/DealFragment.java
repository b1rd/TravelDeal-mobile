package com.example.android.traveldeal;

/**
 * Created by b1rd on 10.04.16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Arrays;
import java.util.List;

public class DealFragment extends Fragment {

    ArrayAdapter<String> mDealAdapter;

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
            FetchDealTask dealTask = new FetchDealTask();
            dealTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Победа",
                "Сапсан",
                "WizzAir",
                "AirBaltic",
                "RyanAir",
                "Аэрофлот"
        };
        List<String> weekDeal = new ArrayList<String>(Arrays.asList(data));


        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mDealAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_deal, // The name of the layout ID.
                        R.id.list_item_deal_textview, // The ID of the textview to populate.
                        weekDeal);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_deal);
        listView.setAdapter(mDealAdapter);

        return rootView;
    }

    public class FetchDealTask extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = FetchDealTask.class.getSimpleName();

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            JSONArray dealArray = new JSONArray(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < numDays; i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject deal = dealArray.getJSONObject(i);

                resultStrs[i] = deal.getString("title");//+"\n"+deal.getString("body");
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String dealJsonStr = null;

            try {
                String baseUrl = "http://jsonplaceholder.typicode.com/posts";
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
                return getWeatherDataFromJson(dealJsonStr, 5);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            if (result != null){
                mDealAdapter.clear();
                for (String dealStr : result){
                    mDealAdapter.add(dealStr);
                }
            }
        }
    }
}