package com.example.android.traveldeal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.download);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DealFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_filter){
            // openPrefferedLocationInMap();
            showPopUpFilter();
            return true;
        }

        if (id == R.id.action_signin) {
            startActivity(new Intent(this, SignInActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPopUpFilter(){
        String[] items = {"Planes", "Trains", "Ships", "Wanna know all the news"};
        new AlertDialog.Builder(MainActivity.this)
            .setTitle("I'm interested in")
            // .setMessage("Are you sure you want to delete this entry?")
            .setSingleChoiceItems(items, 0, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    System.out.println(selectedPosition);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("EXTRA_LINK", selectedPosition);
                    startActivity(intent);
                    // Do something useful withe the position of the selected radio button
                }
            })
//            .setPositiveButton(, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    // continue with delete
//                }
//            })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
            .show();
    }
    // private void openPrefferedLocationInMap(){
    //     SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    //     String location = sharedPrefs.getString(
    //             getString(R.string.pref_location_key),
    //             getString(R.string.pref_location_default)
    //     );

    //     Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
    //             .appendQueryParameter("q", location)
    //             .build();

    //     Intent intent = new Intent(Intent.ACTION_VIEW);
    //     intent.setData(geoLocation);

    //     if (intent.resolveActivity(getPackageManager()) != null){
    //         startActivity(intent);
    //     } else {
    //         Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed");
    //     }
    // }
}
