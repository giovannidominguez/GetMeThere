package edu.ucsb.cs.cs190i.jsegovia.getmethere;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static edu.ucsb.cs.cs190i.jsegovia.getmethere.eventFragment.duration;


public class MainActivity extends AppCompatActivity {

    public static String GOOGLEAPIKEY = "AIzaSyC_fIizx3QXVdP18uZKiucrnz5w4UCa_nw";
    public static int PLACE_PICKER_REQUEST = 1;
    public static Context context;
    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    private ListView lv;
    public static ArrayList<Event> events = new ArrayList<>();
    public static ArrayList<String> eventsAsStrings = new ArrayList<>();
    //public static Place place;
    public static Place currentPlace;
    public static ArrayAdapter<String> arrayAdapter;
    public static LatLng curr;
    private int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Get Me There ON TIME");
        context = getApplicationContext();

        //TODO: Hardcoded, remove later
        //events.add(new Event("Workout", "Rec Cen", new Time(11,0,0), new Time(11,45,0)));
        //events.add(new Event("Study", "Libary", new Time(12,15,0), new Time(12,45,0)));
        //events.add(new Event("Dinner", "Blaze Pizza", new Time(5,20,0), new Time(6,0,0)));
        //events.get(0).setStartLat(3);
        //upDateStringList(events, eventsAsStrings);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            // fall back to network if GPS is not available
            //loc = locationManager.getLastKnownLocation(
              //      LocationManager.NETWORK_PROVIDER);
        } else {
            curr = new LatLng(loc.getLatitude(), loc.getLongitude());

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curr = new LatLng(location.getLatitude(), location.getLongitude());
                updateTimes(events);
                upDateStringList(events, eventsAsStrings);
                arrayAdapter.notifyDataSetChanged();

            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });





        //Button startLoc = (Button) findViewById(R.id.startLocation);
        lv = (ListView) findViewById(R.id.myList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventsAsStrings);




        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Event temp = events.get(position);
                eventFragment ef = new eventFragment();

                ef.show(getFragmentManager(), Integer.toString(position));



                //((TextView) ef.getView().findViewById(R.id.location)).setText("test");

            }

        });




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventFragment ef = new eventFragment();
                ef.show(getFragmentManager(), "Fab");
            }
        });




        /*startLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                //String time = timeBetweenPlaces(currentPlace, place);
                //System.out.println(time);

            }

        });
*/
    }

    private void updateTimes(ArrayList<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            Ion.with(this)
                    .load("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" +
                            //currentPlace.getLatLng().latitude + "," + currentPlace.getLatLng().longitude +
                            curr.latitude + "," + curr.longitude +
                            "&destinations=" + events.get(i).getEventLat() + "," + events.get(i).getEventLng() + "&mode=bicycling&key=" + GOOGLEAPIKEY)
                    .asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {

                    //Log.d("Called at all", "called");

                    try {

                        JSONObject json = new JSONObject(result);
                        duration = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
                        Log.d("Called at all", "called");
                        //System.out.println(duration[0]);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }


                }

            });

            events.get(i).setEstTime(duration);



        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                currentPlace = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", currentPlace.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                //String time = timeBetweenPlaces(currentPlace, place);
                //System.out.println(time);

            }
        }
    }



    private void upDateStringList(ArrayList<Event> events, ArrayList<String> eventsAsStrings) {
        eventsAsStrings.clear();
        for (int i = 0; i < events.size(); i++) {
            eventsAsStrings.add(new String(events.get(i).getName() + " at " + events.get(i).getLocation() + "\n" +
                    "Time: " + events.get(i).getEventStart().toString() + " - " + events.get(i).getEventEnd().toString() +
                    "      Est time to there: " + events.get(i).getEstTime() ));
        }
    }



}
