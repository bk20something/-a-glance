package com.example.jeffery.aglace;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //An asyncTask used to call all web API need for this app.
    //API data is collected and parsed in the do in background method
    //The results are sent to the on post execute which updates the UI
    private class callAPI extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            BufferedInputStream input = null;
            String JSON1 = "";
            String JSON2 = "";
            String result =  null;

            String apiRequest1 = new String("http://svc.metrotransit.org/NexTrip/");
            String apiRequest2 = new String("http://svc.metrotransit.org/NexTrip/");
            String station = params[0];

            if(station == "eastCreek"){
                apiRequest1 = apiRequest1 + "695/2/EAST?format=JSON";
                apiRequest2 = apiRequest2 + "698/2/EAST?format=JSON";
                Log.d("Jeffery", "doInBackground:apiRequest1: "+ apiRequest1);
                Log.d("Jeffery", "doInBackground:apiRequest2: "+ apiRequest2);
            }

            if(station == "coffman"){
                apiRequest1 = apiRequest1 + "695/3/COFF?format=JSON";
                apiRequest2 = apiRequest2 + "698/3/COFF?format=JSON";
            }

            //for each request get the JSON data and read it in to a buffer and convert into a string
            //parse the JSON string and put the result in a string
            try {
                URL apiRequest1URL = new URL(apiRequest1);
                URL apiRequest2URL = new URL(apiRequest2);
                byte[] contents = new byte[1024];
                int bytesRead=0;

                //turn html input in to a string
                HttpURLConnection connection1 = (HttpURLConnection) apiRequest1URL.openConnection();
                input = new BufferedInputStream(connection1.getInputStream());
                while( (bytesRead = input.read(contents)) != -1){
                    JSON1 = JSON1 + new String(contents, 0, bytesRead);
                }
                Log.d("Jeffery", "doInBackground:JSON1: "+ JSON1);

                HttpURLConnection connection2 = (HttpURLConnection) apiRequest2URL.openConnection();
                input = new BufferedInputStream(connection2.getInputStream());
                while( (bytesRead = input.read(contents)) != -1){
                    JSON2 = JSON2 + new String(contents, 0, bytesRead);
                }

                Log.d("Jeffery", "doInBackground:JSON2: "+ JSON2);

                JSONArray deptureArray = new JSONArray(JSON1);

                if(deptureArray.length() != 0){
                    for(int i=0; i<deptureArray.length(); i++){
                        JSONObject departure = deptureArray.getJSONObject(i);
                        String departureText = departure.getString("DepartureText");
                        String route = departure.getString("Route");
                        if (result == null)
                            result = departureText + " -- " + route + "\n";
                        else
                            result = result + departureText + " -- " + route + "\n";
                    }
                    result = result + "\n";
                }

                deptureArray = new JSONArray(JSON2);

                if(deptureArray.length() != 0){
                    for(int i=0; i<deptureArray.length(); i++){
                        JSONObject departure = deptureArray.getJSONObject(i);
                        String departureText = departure.getString("DepartureText");
                        String route = departure.getString("Route");
                        if (result == null)
                            result = departureText + " -- " + route + "\n";
                        else
                            result = result + departureText + " -- " + route + "\n";
                    }
                }

                if(result == null)
                    result = "There are no buses at this time\n";

                /* TEST JSON PARSE DATA

                String testData = new String("[{\"Actual\":false,\"BlockNumber\":36228,\"DepartureText\":\"4:14\",\"DepartureTime\":\"\\/Date(1440710040000-0500)\\/\",\"Description\":\"U of M Direct\\/SW Sta\\/SW Vill\\/E Crk\",\"Gate\":\"\",\"Route\":\"695\",\"RouteDirection\":\"WESTBOUND\",\"Terminal\":\"\",\"VehicleHeading\":0,\"VehicleLatitude\":0,\"VehicleLongitude\":0},{\"Actual\":false,\"BlockNumber\":36214,\"DepartureText\":\"4:51\",\"DepartureTime\":\"\\/Date(1440712260000-0500)\\/\",\"Description\":\"U of M Direct\\/SW Sta\\/SW Vill\\/E Crk\",\"Gate\":\"\",\"Route\":\"695\",\"RouteDirection\":\"WESTBOUND\",\"Terminal\":\"\",\"VehicleHeading\":0,\"VehicleLatitude\":44.976909,\"VehicleLongitude\":-93.269676},{\"Actual\":false,\"BlockNumber\":36247,\"DepartureText\":\"5:15\",\"DepartureTime\":\"\\/Date(1440713700000-0500)\\/\",\"Description\":\"U of M Direct\\/SW Sta\\/SW Vill\\/E Crk\",\"Gate\":\"\",\"Route\":\"695\",\"RouteDirection\":\"WESTBOUND\",\"Terminal\":\"\",\"VehicleHeading\":0,\"VehicleLatitude\":0,\"VehicleLongitude\":0}]");
                //String testData = new String("[]");
                JSONArray testArray = new JSONArray(testData);

                if(testArray.length() != 0){
                    for(int i=0; i<testArray.length(); i++){
                        JSONObject departure = testArray.getJSONObject(i);
                        String departureText = departure.getString("DepartureText");
                        String route = departure.getString("Route");
                        if (result == null)
                            result = departureText + " -- " + route + "\n";
                        else
                            result = result + departureText + " -- " + route + "\n";
                    }
                }
                else{
                    result = "There are no buses at this time\n";
                }
                 *****/

            } catch (Exception e) {
                Log.e("Jeffery", "doInBackground: " + e.getMessage());
                return e.getMessage();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            TextView editText = (TextView) findViewById(R.id.busInfoText);
            editText.append(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button eastCreekButton = (Button) findViewById(R.id.eastCreekButton);
        Button coffmanButton = (Button) findViewById(R.id.coffmanButton);
        Button clearButton = (Button) findViewById(R.id.clearButton);

        //setting up the listener for the east creek button
        eastCreekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView editText = (TextView) findViewById(R.id.busInfoText);
                editText.setText("");
                new callAPI().execute("eastCreek");
                Log.d("Jeffery", "onClick: east creek was pressed");
            }
        });

        //setting up the listener for the coffman button
        coffmanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView editText = (TextView) findViewById(R.id.busInfoText);
                editText.setText("");
                new callAPI().execute("coffman");
                Log.d("Jeffery", "onClick: coffman was pressed");
            }
        });

        //setting up the listener for the clear button
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView editText = (TextView) findViewById(R.id.busInfoText);
                editText.setText("");
                Log.d("Jeffery", "onClick: clear pressed");
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
