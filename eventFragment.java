package edu.ucsb.cs.cs190i.jsegovia.getmethere;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.app.Activity.RESULT_OK;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.GOOGLEAPIKEY;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.PLACE_PICKER_REQUEST;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.arrayAdapter;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.curr;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.currentPlace;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.events;
import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.eventsAsStrings;
//import static edu.ucsb.cs.cs190i.jsegovia.getmethere.MainActivity.place;


public class eventFragment extends DialogFragment {
    public static String duration;
    public String mode;
    public static Place place;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.event_fragment, container, false);



        Button add = (Button) view.findViewById(R.id.ADD);

        final EditText activity = (EditText) view.findViewById(R.id.EventName);
        final EditText eventLocation = (EditText) view.findViewById(R.id.eventLocation);
        final TimePicker startTime = (TimePicker) view.findViewById(R.id.StartTime);
        final TimePicker endTime = (TimePicker) view.findViewById(R.id.EndTime);


        if (getTag() != "Fab") {
            add.setText("Update");
            int index = Integer.parseInt(getTag());
            activity.setText(events.get(index).getName());
            eventLocation.setText(events.get(index).getLocation());
            startTime.setHour(events.get(index).getEventStart().getHours());
            startTime.setMinute(events.get(index).getEventStart().getMinutes());
            endTime.setHour(events.get(index).getEventEnd().getHours());
            endTime.setMinute(events.get(index).getEventEnd().getMinutes());
            activity.setTextColor(Color.BLACK);
        }

        Button location = (Button) view.findViewById(R.id.location);



        location.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });




        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePicker starter = (TimePicker) view.findViewById(R.id.StartTime);
                TimePicker ender = (TimePicker) view.findViewById(R.id.EndTime);

                Time startTime = new Time(starter.getHour(),starter.getMinute(),0);
                Time endTime = new Time(ender.getHour(),ender.getMinute(),0);

                if (activity.getText().toString().length() == 0| eventLocation.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill in the text box(s)", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    place.getLatLng();
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "Please pick a location", Toast.LENGTH_SHORT).show();
                    return;
                }



                //if (timeBetweenPlaces(currentPlace, place) == null) {
                if(duration == null) {
                    System.out.println("Null");
                    return;


                }

                //temp = timeBetweenPlaces(currentPlace, place);

                if (getTag() == "Fab") {
                    Event e = new Event(activity.getText().toString(), eventLocation.getText().toString(), startTime, endTime);
                    e.setEventLat(place.getLatLng().latitude);
                    e.setEventLng(place.getLatLng().longitude);
                    e.setEstTime(duration);
                    //e.setEstTime(duration);
                    events.add(e);

                } else {
                    int index = Integer.parseInt(getTag());
                    events.get(index).setName(activity.getText().toString());
                    events.get(index).setLocation(eventLocation.getText().toString());
                    events.get(index).setEventStart(startTime);
                    events.get(index).setEventEnd(endTime);
                    events.get(index).setEventLat(place.getLatLng().latitude);
                    events.get(index).setStartLng(place.getLatLng().longitude);
                    events.get(index).setEstTime(duration);
                    //events.get(index).setEstTime(duration);
                }

                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event o1, Event o2) {
                        if(o1.getEventEnd().before(o2.getEventStart())){
                            return -1;
                        }
                        else if(o1.getEventEnd().after(o2.getEventStart())) {
                            return 1;
                        }
                        else{
                            return 0;
                        }
                    }
                });

                upDateStringsList(events, eventsAsStrings);
                arrayAdapter.notifyDataSetChanged();
                //arrayAdapter.notifyDataSetChanged();
                //upDateStringsList(events, eventsAsStrings);
                getActivity().getFragmentManager().beginTransaction().remove(eventFragment.this).commit();



            }



            private void upDateStringsList(ArrayList<Event> events, ArrayList<String> eventsAsStrings) {
                eventsAsStrings.clear();
                for (int i = 0; i < events.size(); i++) {
                    eventsAsStrings.add(new String(events.get(i).getName() + " at " + events.get(i).getLocation() + "\n" +
                            "Time: " + events.get(i).getEventStart().toString() + " - " + events.get(i).getEventEnd().toString() +
                            "      Est time to there: " + events.get(i).getEstTime() ));
                }

            }




        });

        return view;

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                timeBetweenPlaces(currentPlace, place);
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void timeBetweenPlaces(Place currentPlace, Place place) {
        System.out.println("yolo");

        Ion.with(eventFragment.this)
                .load("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" +
                        //currentPlace.getLatLng().latitude + "," + currentPlace.getLatLng().longitude +
                        curr.latitude + "," + curr.longitude +
                        "&destinations=" + place.getLatLng().latitude + "," + place.getLatLng().longitude + "&mode=bicycling&key=" + GOOGLEAPIKEY)
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

        //return duration;
    }

}
