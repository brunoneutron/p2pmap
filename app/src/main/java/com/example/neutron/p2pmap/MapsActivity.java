package com.example.neutron.p2pmap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.neutron.p2pmap.OSRMAPI.Geometry;
import com.example.neutron.p2pmap.OSRMAPI.Legs;
import com.example.neutron.p2pmap.OSRMAPI.Maneuver;
import com.example.neutron.p2pmap.OSRMAPI.Matchings;
import com.example.neutron.p2pmap.OSRMAPI.OSRMAPIMATCH;
import com.example.neutron.p2pmap.OSRMAPI.Steps;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {




    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient client2;


    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    // A request object to store parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(35.571589, 129.189327);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    /********************************** New globals **********************************************/
    private static final String test_str = "http://10.20.17.228:5000/match/v1/driving/129.188039,35.576254;129.188681,35.576119;129.188358,35.575573;129.187736,35.574526;129.187403,35.573313;129.186856,35.571393;129.189892,35.571201;129.191383,35.572650;129.192520,35.572859;129.194408,35.572231;129.195116,35.571952;129.197476,35.571987;129.199450,35.572214;129.200040,35.572188;129.200373,35.572179;129.200652,35.572467;129.201831,35.572888;129.202260,35.573612;129.201831,35.574982;129.200694,35.576169;129.197947,35.576811;129.196091,35.577422;129.193634,35.576846;129.191987,35.576309;129.190973,35.575755;129.190319,35.575642?timestamps=1412229071;1412229076;1412229081;1412229086;1412229091;1412229096;1412229101;1412229106;1412229111;1412229116;1412229121;1412229126;1412229131;1412229136;1412229141;1412229146;1412229151;1412229156;1412229161;1412229166;1412229171;1412229176;1412229181;1412229186;1412229191;1412229196&steps=true&geometries=geojson&overview=full";
    List<LatLng> decoded_geometry = new ArrayList<>();
    List<LatLng> instruc_loc = new ArrayList();
    List<String> instruction = new ArrayList();
    List<LatLng> rInstruc_loc = new ArrayList();
    List<String> rInstruction = new ArrayList();
    List<LatLng> rgeometry = new ArrayList();
    List<LatLng> mInstruc_loc = new ArrayList();
    List<String> mInstruction = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        //if (savedInstanceState != null) {
            //mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            //mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            /**
             * Avoid multiple updates of markers
             */
          //  savedInstanceState = null;
        //}

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        buildGoogleApiClient();
        mGoogleApiClient.connect();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();





    }

    /**
     * Get the device location and nearby places when the activity is restored after a pause.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        updateMarkers();
    }

    /**
     * Stop location updates when the activity is no longer in focus, to reduce battery consumption.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /**
         * This has been commented our to prevent multiple markers form showing up
         */

        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Gets the device's current location and builds the map
     * when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handles the callback when location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMarkers();



    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Add markers for nearby places.
        //updateMarkers();

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }



        });
        /*
         * Set the map's camera position to the current location of the device.
         * If the previous state was saved, set the position to the saved state.
         * If the current location is unknown, use a default position and zoom value.
         */
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }


        //bruno
        getOSRMData();



    }

    /**
     * Builds a GoogleApiClient.
     * Uses the addApi() method to request the Google Places API and the Fused Location Provider.
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        /*
         * Sets the desired interval for active location updates. This interval is
         * inexact. You may not receive updates at all if no location sources are available, or
         * you may receive them slower than requested. You may also receive updates faster than
         * requested if other applications are requesting location at a faster interval.
         */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        /*
         * Sets the fastest rate for active location updates. This interval is exact, and your
         * application will never receive updates faster than this value.
         */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Gets the current location of the device and starts the location update notifications.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         * Also request regular updates about the device location.
         */
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Adds markers for places nearby the device and turns the My Location feature on or off,
     * provided location permission has been granted.
     */
    private void updateMarkers() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the businesses and other points of interest located
            // nearest to the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Add a marker for each place near the device's current location, with an
                        // info window showing place information.
                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
                        String snippet = (String) placeLikelihood.getPlace().getAddress();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }

                        mMap.addMarker(new MarkerOptions()
                                .position(placeLikelihood.getPlace().getLatLng())
                                .title((String) placeLikelihood.getPlace().getName())
                                .snippet(snippet));
                    }
                    // Release the place likelihood buffer.
                    likelyPlaces.release();
                }
            });
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(mDefaultLocation)
                    .title(getString(R.string.default_info_title))
                    .snippet(getString(R.string.default_info_snippet)));
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }


    /********************************************* The code above has been tested and is working *****************************************/
    public void getOSRMData(){
        Context context = getApplication();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_LONG).show();

                try {
                    //here we make our request
                    String response = client(test_str);
                    longInfo(response);
                    MatchRequestClient(response);
                }catch (IOException e){
                    e.printStackTrace();
                }
            } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_LONG).show();

                try {
                    //here we make our request
                    MatchRequestClient(client(test_str));
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        } else{
            Toast.makeText(context,"No connection",Toast.LENGTH_LONG).show();
        }


        //MatchRequestClient(str);
    }

    public static void longInfo(String str) {
        if(!Strings.isNullOrEmpty(str)){
            if(str.length() > 4000) {
                Log.i("BRUNOOOO", str.substring(0, 4000));
                longInfo(str.substring(4000));
            } else
                Log.i("BRUNOOOO", str);
        }

    }

    public String client(String query) throws IOException{
        DatagramSocket client_socket = new DatagramSocket();
        String serverIP = "114.70.9.118";
        int Port = 8003;
        InetAddress IPAddress = InetAddress.getByName(serverIP);
        byte[] sendData = query.getBytes();
        byte[] receiveData = new byte[53000];
        DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,IPAddress,Port);
        client_socket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
        client_socket.receive(receivePacket);
        //longInfo(receivePacket.toString());

        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        //longInfo(response);

        return response;
    }

    private void  MatchRequestClient(String response){
        //When the response is a JSONObject instead of array
        //longInfo(response.toString());

        //Now we extract the DATA
        Gson gson = new Gson();
        OSRMAPIMATCH osrmapimatch = gson.fromJson(response,OSRMAPIMATCH.class);
        //longInfo(osrmapimatch.toString());
        Matchings[] matchingses = osrmapimatch.getMatchings();
        Matchings matchings = matchingses[0];
        Geometry geometry = matchings.getGeometry();

        /**
         * From now we want to extract the instructions
         */
        Legs[] legses = matchings.getLegs();
        for(Legs legs : legses){
            Steps[] stepses = legs.getSteps();
            for(Steps steps : stepses){
                Maneuver maneuver = steps.getManeuver();
                String modifier = maneuver.getModifier();
                String[] location = maneuver.getLocation();
                double y = Double.parseDouble(location[0]);
                double x = Double.parseDouble(location[1]);
                String type = maneuver.getType();
                String inst = type+" "+modifier;
                LatLng latLng = new LatLng(x,y);
                if(!Strings.isNullOrEmpty(modifier)){
                    instruc_loc.add(latLng);
                    instruction.add(inst);
                }else{
                    instruc_loc.add(latLng);
                    instruction.add(type);
                }

            }

        }

        //longInfo(instruc_loc.toString());
        //longInfo(instruction.toString());
        //Here we are going to extract the coordinates into a List<LatLng>
        String[][] coordinates = geometry.getCoordinates();
        //longInfo(coordinates.toString());
        for(int i=0; i<coordinates.length;i++){
            double y = Double.parseDouble(coordinates[i][0]);
            double x = Double.parseDouble(coordinates[i][1]);
            LatLng latLng = new LatLng(x,y);
            if(latLng != null)
                decoded_geometry.add(latLng);
        }

        new Thread(new Runnable() {
            public void run(){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Draw the geometry
                         */
                        PolylineOptions options = new PolylineOptions();
                        options.width(5);
                        options.color(Color.RED);
                        options.addAll(decoded_geometry);
                        options.width(10);
                        mMap.addPolyline(options);


                        /**
                         * Add markers to show the instructions
                         */

                        for(int i=0;i<instruction.size();i++){
                            String instruc = instruction.get(i);
                            LatLng latLng = instruc_loc.get(i);

                            //Now we add the marker with text as instruc
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(instruc));
                            //.icon(createPureTextIcon(instruc)));
                        }
                    }
                });

            }
        }).start();

    }



    private String str = "{\"code\":\"Ok\",\"matchings\":[{\"confidence\":0.441557,\"distance\":3700.2,\"duration\":859.1,\"geometry\":{\"coordinates\":[[129.188041,35.576265],[129.188307,35.57622],[129.188618,35.576157],[129.188688,35.576143],[129.188801,35.576122],[129.188871,35.575795],[129.188886,35.575701],[129.188619,35.575653],[129.188393,35.575618],[129.188345,35.575605],[129.188227,35.575574],[129.188125,35.575535],[129.18805,35.575465],[129.187974,35.575374],[129.187921,35.575273],[129.187867,35.575108],[129.187781,35.574728],[129.187729,35.574527],[129.187604,35.574039],[129.187407,35.573311],[129.187352,35.573105],[129.187293,35.572904],[129.187218,35.572725],[129.187127,35.572555],[129.186977,35.572328],[129.18688,35.572162],[129.186816,35.572014],[129.186757,35.571853],[129.186735,35.571726],[129.186767,35.571473],[129.186796,35.57138],[129.1868,35.571368],[129.186848,35.571294],[129.186918,35.571211],[129.186987,35.571146],[129.187062,35.571085],[129.187159,35.571028],[129.187255,35.570989],[129.187363,35.570949],[129.187465,35.570914],[129.187593,35.570888],[129.187792,35.570853],[129.187996,35.570836],[129.188178,35.570827],[129.188345,35.570827],[129.188516,35.570836],[129.188752,35.570853],[129.188983,35.57088],[129.189165,35.570914],[129.189358,35.570962],[129.189535,35.571015],[129.189788,35.571115],[129.18991,35.571173],[129.190029,35.571229],[129.190244,35.571359],[129.190506,35.571543],[129.190791,35.571787],[129.190995,35.571995],[129.191166,35.572223],[129.191268,35.57238],[129.1914,35.572644],[129.191413,35.572668],[129.191475,35.572853],[129.191653,35.572868],[129.191794,35.572891],[129.191987,35.572922],[129.192132,35.57293],[129.192255,35.572922],[129.192378,35.572895],[129.192519,35.572857],[129.193054,35.572716],[129.193435,35.572594],[129.193774,35.572488],[129.194235,35.572309],[129.194411,35.572236],[129.194508,35.572197],[129.194669,35.572121],[129.194804,35.572051],[129.194831,35.572005],[129.194876,35.571968],[129.194947,35.571944],[129.195003,35.571943],[129.195064,35.571959],[129.195091,35.571976],[129.195117,35.571994],[129.195145,35.57203],[129.195368,35.571993],[129.19561,35.571957],[129.195905,35.571921],[129.196225,35.571894],[129.196509,35.571892],[129.196783,35.571899],[129.19711,35.571931],[129.197432,35.57197],[129.197477,35.571974],[129.197759,35.572005],[129.198279,35.572062],[129.198854,35.572121],[129.199452,35.572198],[129.199476,35.572202],[129.19974,35.572237],[129.19997,35.572278],[129.200016,35.572194],[129.200033,35.572181],[129.200083,35.572147],[129.200151,35.572126],[129.200236,35.572126],[129.200299,35.572143],[129.200364,35.572183],[129.200401,35.572227],[129.200424,35.572291],[129.200418,35.572352],[129.20038,35.572419],[129.200641,35.572489],[129.20076,35.572522],[129.201278,35.572684],[129.201836,35.572877],[129.202415,35.573077],[129.202381,35.573162],[129.202341,35.573282],[129.20224,35.573608],[129.20211,35.574032],[129.202016,35.574407],[129.201887,35.574802],[129.201821,35.574979],[129.201745,35.575188],[129.201638,35.575592],[129.20153,35.575841],[129.201401,35.575991],[129.20028,35.576268],[129.199116,35.57651],[129.198685,35.576628],[129.198049,35.576779],[129.197596,35.576844],[129.197137,35.576853],[129.196681,35.576862],[129.196343,35.57689],[129.195927,35.576896],[129.195396,35.576905],[129.194967,35.576875],[129.194723,35.576803],[129.194353,35.576689],[129.193905,35.576524],[129.193653,35.576447],[129.192913,35.576268],[129.192529,35.576233],[129.191985,35.576239],[129.191901,35.57624],[129.191456,35.576039],[129.191091,35.575862],[129.190951,35.575779],[129.190914,35.575758],[129.190667,35.575614],[129.190485,35.575496],[129.190312,35.575637]],\"type\":\"LineString\"},\"legs\":[{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[102],\"location\":[129.188041,35.576265]}],\"geometry\":{\"coordinates\":[[129.188041,35.576265],[129.188307,35.57622],[129.188618,35.576157],[129.188688,35.576143]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":102,\"location\":[129.188041,35.576265],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":18,\"distance\":60.1,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[284],\"location\":[129.188688,35.576143]}],\"geometry\":{\"coordinates\":[[129.188688,35.576143]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.188688,35.576143],\"bearing_before\":104,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":18,\"distance\":60.1},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[103],\"location\":[129.188688,35.576143]}],\"geometry\":{\"coordinates\":[[129.188688,35.576143],[129.188801,35.576122],[129.188871,35.575795],[129.188886,35.575701]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":103,\"location\":[129.188688,35.576143],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":19.1,\"distance\":57.9,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":1,\"location\":[129.188886,35.575701],\"bearings\":[75,255,345],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.188886,35.575701],[129.188619,35.575653],[129.188393,35.575618],[129.188345,35.575605]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":257,\"type\":\"turn\",\"modifier\":\"right\",\"bearing_before\":171,\"location\":[129.188886,35.575701]},\"duration\":9.1,\"distance\":50.1,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[72],\"location\":[129.188345,35.575605]}],\"geometry\":{\"coordinates\":[[129.188345,35.575605]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.188345,35.575605],\"bearing_before\":252,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":28.2,\"distance\":108.1},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[252],\"location\":[129.188345,35.575605]}],\"geometry\":{\"coordinates\":[[129.188345,35.575605],[129.188227,35.575574],[129.188125,35.575535],[129.18805,35.575465],[129.187974,35.575374],[129.187921,35.575273],[129.187867,35.575108],[129.187781,35.574728],[129.187729,35.574527]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":252,\"location\":[129.188345,35.575605],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":25.3,\"distance\":141,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[12],\"location\":[129.187729,35.574527]}],\"geometry\":{\"coordinates\":[[129.187729,35.574527]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.187729,35.574527],\"bearing_before\":192,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":25.3,\"distance\":141},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[192],\"location\":[129.187729,35.574527]}],\"geometry\":{\"coordinates\":[[129.187729,35.574527],[129.187604,35.574039],[129.187407,35.573311]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":192,\"location\":[129.187729,35.574527],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":24.8,\"distance\":138.4,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[12],\"location\":[129.187407,35.573311]}],\"geometry\":{\"coordinates\":[[129.187407,35.573311]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.187407,35.573311],\"bearing_before\":192,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":24.8,\"distance\":138.4},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[192],\"location\":[129.187407,35.573311]}],\"geometry\":{\"coordinates\":[[129.187407,35.573311],[129.187352,35.573105],[129.187293,35.572904],[129.187218,35.572725],[129.187127,35.572555],[129.186977,35.572328],[129.18688,35.572162],[129.186816,35.572014],[129.186757,35.571853],[129.186735,35.571726],[129.186767,35.571473],[129.186796,35.57138]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":192,\"location\":[129.187407,35.573311],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":40.9,\"distance\":226.6,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[346],\"location\":[129.186796,35.57138]}],\"geometry\":{\"coordinates\":[[129.186796,35.57138]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"type\":\"arrive\",\"modifier\":\"left\",\"bearing_before\":166,\"location\":[129.186796,35.57138]},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":40.9,\"distance\":226.6},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[165],\"location\":[129.186796,35.57138]},{\"out\":0,\"location\":[129.1868,35.571368],\"bearings\":[150,240,345],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.186796,35.57138],[129.1868,35.571368],[129.186848,35.571294],[129.186918,35.571211],[129.186987,35.571146],[129.187062,35.571085],[129.187159,35.571028],[129.187255,35.570989],[129.187363,35.570949],[129.187465,35.570914],[129.187593,35.570888],[129.187792,35.570853],[129.187996,35.570836],[129.188178,35.570827],[129.188345,35.570827],[129.188516,35.570836],[129.188752,35.570853],[129.188983,35.57088],[129.189165,35.570914],[129.189358,35.570962],[129.189535,35.571015],[129.189788,35.571115],[129.18991,35.571173]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":165,\"type\":\"depart\",\"modifier\":\"left\",\"bearing_before\":0,\"location\":[129.186796,35.57138]},\"duration\":55.7,\"distance\":311.3,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[240],\"location\":[129.18991,35.571173]}],\"geometry\":{\"coordinates\":[[129.18991,35.571173]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.18991,35.571173],\"bearing_before\":60,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":55.7,\"distance\":311.3},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[60],\"location\":[129.18991,35.571173]}],\"geometry\":{\"coordinates\":[[129.18991,35.571173],[129.190029,35.571229],[129.190244,35.571359],[129.190506,35.571543],[129.190791,35.571787],[129.190995,35.571995],[129.191166,35.572223],[129.191268,35.57238],[129.1914,35.572644]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":60,\"location\":[129.18991,35.571173],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":38.8,\"distance\":216.2,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[202],\"location\":[129.1914,35.572644]}],\"geometry\":{\"coordinates\":[[129.1914,35.572644]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.1914,35.572644],\"bearing_before\":22,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":38.8,\"distance\":216.2},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[24],\"location\":[129.1914,35.572644]}],\"geometry\":{\"coordinates\":[[129.1914,35.572644],[129.191413,35.572668],[129.191475,35.572853]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":24,\"location\":[129.1914,35.572644],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":5,\"distance\":24.2,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":1,\"location\":[129.191475,35.572853],\"bearings\":[15,90,195],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.191475,35.572853],[129.191653,35.572868],[129.191794,35.572891],[129.191987,35.572922],[129.192132,35.57293],[129.192255,35.572922],[129.192378,35.572895],[129.192519,35.572857]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":82,\"type\":\"turn\",\"modifier\":\"right\",\"bearing_before\":14,\"location\":[129.191475,35.572853]},\"duration\":28.8,\"distance\":96.3,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[288],\"location\":[129.192519,35.572857]}],\"geometry\":{\"coordinates\":[[129.192519,35.572857]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.192519,35.572857],\"bearing_before\":108,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"}],\"summary\":\"유니스트길 (UNIST-gil)\",\"duration\":33.8,\"distance\":120.5},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[108],\"location\":[129.192519,35.572857]}],\"geometry\":{\"coordinates\":[[129.192519,35.572857],[129.193054,35.572716],[129.193435,35.572594],[129.193774,35.572488],[129.194235,35.572309],[129.194411,35.572236]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":108,\"location\":[129.192519,35.572857],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":55.5,\"distance\":184.9,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[297],\"location\":[129.194411,35.572236]}],\"geometry\":{\"coordinates\":[[129.194411,35.572236]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.194411,35.572236],\"bearing_before\":117,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"}],\"summary\":\"유니스트길 (UNIST-gil)\",\"duration\":55.5,\"distance\":184.9},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[116],\"location\":[129.194411,35.572236]},{\"out\":1,\"location\":[129.194804,35.572051],\"bearings\":[0,150,300],\"entry\":[true,true,false],\"in\":2},{\"out\":0,\"location\":[129.194876,35.571968],\"bearings\":[120,195,330],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.194411,35.572236],[129.194508,35.572197],[129.194669,35.572121],[129.194804,35.572051],[129.194831,35.572005],[129.194876,35.571968],[129.194947,35.571944],[129.195003,35.571943],[129.195064,35.571959],[129.195091,35.571976]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":116,\"location\":[129.194411,35.572236],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":16.3,\"distance\":73.5,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[232],\"location\":[129.195091,35.571976]}],\"geometry\":{\"coordinates\":[[129.195091,35.571976]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.195091,35.571976],\"bearing_before\":52,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"유니스트길 (UNIST-gil)\",\"duration\":16.3,\"distance\":73.5},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[50],\"location\":[129.195091,35.571976]}],\"geometry\":{\"coordinates\":[[129.195091,35.571976],[129.195117,35.571994],[129.195145,35.57203]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":50,\"location\":[129.195091,35.571976],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":1.4,\"distance\":7.8,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":1,\"location\":[129.195145,35.57203],\"bearings\":[15,105,210],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.195145,35.57203],[129.195368,35.571993],[129.19561,35.571957],[129.195905,35.571921],[129.196225,35.571894],[129.196509,35.571892],[129.196783,35.571899],[129.19711,35.571931],[129.197432,35.57197],[129.197477,35.571974]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":101,\"type\":\"turn\",\"modifier\":\"right\",\"bearing_before\":32,\"location\":[129.195145,35.57203]},\"duration\":24,\"distance\":212.8,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[264],\"location\":[129.197477,35.571974]}],\"geometry\":{\"coordinates\":[[129.197477,35.571974]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.197477,35.571974],\"bearing_before\":84,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"}],\"summary\":\"유니스트길 (UNIST-gil)\",\"duration\":25.4,\"distance\":220.6},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[82],\"location\":[129.197477,35.571974]}],\"geometry\":{\"coordinates\":[[129.197477,35.571974],[129.197759,35.572005],[129.198279,35.572062],[129.198854,35.572121],[129.199452,35.572198]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":82,\"location\":[129.197477,35.571974],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":20.3,\"distance\":180.4,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[261],\"location\":[129.199452,35.572198]}],\"geometry\":{\"coordinates\":[[129.199452,35.572198]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.199452,35.572198],\"bearing_before\":81,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"}],\"summary\":\"유니스트길 (UNIST-gil)\",\"duration\":20.3,\"distance\":180.4},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[78],\"location\":[129.199452,35.572198]}],\"geometry\":{\"coordinates\":[[129.199452,35.572198],[129.199476,35.572202],[129.19974,35.572237],[129.19997,35.572278]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":78,\"location\":[129.199452,35.572198],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":6.5,\"distance\":47.7,\"name\":\"유니스트길 (UNIST-gil)\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":1,\"location\":[129.19997,35.572278],\"bearings\":[0,150,255],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.19997,35.572278],[129.200016,35.572194],[129.200033,35.572181]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":156,\"type\":\"turn\",\"modifier\":\"right\",\"bearing_before\":77,\"location\":[129.19997,35.572278]},\"duration\":1.5,\"distance\":12.3,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[313],\"location\":[129.200033,35.572181]}],\"geometry\":{\"coordinates\":[[129.200033,35.572181]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.200033,35.572181],\"bearing_before\":133,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"유니스트길 (UNIST-gil)\",\"duration\":8,\"distance\":60.1},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[130],\"location\":[129.200033,35.572181]},{\"out\":0,\"location\":[129.200299,35.572143],\"bearings\":[60,150,255],\"entry\":[true,true,false],\"in\":2}],\"geometry\":{\"coordinates\":[[129.200033,35.572181],[129.200083,35.572147],[129.200151,35.572126],[129.200236,35.572126],[129.200299,35.572143],[129.200361,35.57218]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":130,\"location\":[129.200033,35.572181],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":3.7,\"distance\":33.6,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[234],\"location\":[129.200364,35.572183]}],\"geometry\":{\"coordinates\":[[129.200361,35.57218]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.200364,35.572183],\"bearing_before\":54,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":3.7,\"distance\":33.6},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[34],\"location\":[129.200364,35.572183]}],\"geometry\":{\"coordinates\":[[129.200364,35.572183],[129.200401,35.572227],[129.200424,35.572291],[129.200418,35.572352],[129.20038,35.572419]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":34,\"location\":[129.200364,35.572183],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":5.9,\"distance\":28.4,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":0,\"location\":[129.20038,35.572419],\"bearings\":[75,150,315],\"entry\":[true,false,true],\"in\":1}],\"geometry\":{\"coordinates\":[[129.20038,35.572419],[129.200641,35.572489]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":70,\"type\":\"turn\",\"modifier\":\"right\",\"bearing_before\":334,\"location\":[129.20038,35.572419]},\"duration\":4.4,\"distance\":24.9,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[252],\"location\":[129.200641,35.572489]}],\"geometry\":{\"coordinates\":[[129.200641,35.572489]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.200641,35.572489],\"bearing_before\":72,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":10.3,\"distance\":53.2},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[71],\"location\":[129.200641,35.572489]}],\"geometry\":{\"coordinates\":[[129.200641,35.572489],[129.20076,35.572522],[129.201278,35.572684],[129.201836,35.572877]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":71,\"location\":[129.200641,35.572489],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":20.9,\"distance\":116.4,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[247],\"location\":[129.201836,35.572877]}],\"geometry\":{\"coordinates\":[[129.201836,35.572877]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.201836,35.572877],\"bearing_before\":67,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":20.9,\"distance\":116.4},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[67],\"location\":[129.201836,35.572877]}],\"geometry\":{\"coordinates\":[[129.201836,35.572877],[129.202415,35.573077]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":67,\"location\":[129.201836,35.572877],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":15.1,\"distance\":56.9,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":2,\"location\":[129.202415,35.573077],\"bearings\":[165,240,345],\"entry\":[true,false,true],\"in\":1}],\"geometry\":{\"coordinates\":[[129.202415,35.573077],[129.202381,35.573162],[129.202341,35.573282],[129.20224,35.573608]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":340,\"type\":\"turn\",\"modifier\":\"left\",\"bearing_before\":66,\"location\":[129.202415,35.573077]},\"duration\":18.3,\"distance\":61.2,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[166],\"location\":[129.20224,35.573608]}],\"geometry\":{\"coordinates\":[[129.20224,35.573608]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.20224,35.573608],\"bearing_before\":346,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":33.4,\"distance\":118.1},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[346],\"location\":[129.20224,35.573608]}],\"geometry\":{\"coordinates\":[[129.20224,35.573608],[129.20211,35.574032],[129.202016,35.574407],[129.201887,35.574802],[129.201821,35.574979]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":346,\"location\":[129.20224,35.573608],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":47.1,\"distance\":157.2,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[163],\"location\":[129.201821,35.574979]}],\"geometry\":{\"coordinates\":[[129.201821,35.574979]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.201821,35.574979],\"bearing_before\":343,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":47.1,\"distance\":157.2},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[344],\"location\":[129.201821,35.574979]}],\"geometry\":{\"coordinates\":[[129.201821,35.574979],[129.201745,35.575188],[129.201638,35.575592],[129.20153,35.575841],[129.201401,35.575991],[129.20028,35.576268],[129.199116,35.57651],[129.198685,35.576628],[129.198049,35.576779],[129.197596,35.576844],[129.197137,35.576853],[129.196681,35.576862],[129.196343,35.57689],[129.195927,35.576896],[129.195396,35.576905],[129.194967,35.576875],[129.194723,35.576803],[129.194353,35.576689],[129.193905,35.576524],[129.193653,35.576447],[129.192913,35.576268],[129.192529,35.576233],[129.191985,35.576239]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":344,\"location\":[129.201821,35.574979],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":299.2,\"distance\":997.6,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[91],\"location\":[129.191985,35.576239]}],\"geometry\":{\"coordinates\":[[129.191985,35.576239]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"type\":\"arrive\",\"modifier\":\"right\",\"bearing_before\":271,\"location\":[129.191985,35.576239]},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":299.2,\"distance\":997.6},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[271],\"location\":[129.191985,35.576239]}],\"geometry\":{\"coordinates\":[[129.191985,35.576239],[129.191901,35.57624],[129.191456,35.576039],[129.191091,35.575862],[129.190951,35.575779]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":271,\"type\":\"depart\",\"modifier\":\"right\",\"bearing_before\":0,\"location\":[129.191985,35.576239]},\"duration\":32.2,\"distance\":107.8,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[54],\"location\":[129.190951,35.575779]}],\"geometry\":{\"coordinates\":[[129.190951,35.575779]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.190951,35.575779],\"bearing_before\":234,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":32.2,\"distance\":107.8},{\"steps\":[{\"intersections\":[{\"out\":0,\"entry\":[true],\"bearings\":[235],\"location\":[129.190951,35.575779]}],\"geometry\":{\"coordinates\":[[129.190951,35.575779],[129.190914,35.575758],[129.190667,35.575614],[129.190485,35.575496]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":235,\"location\":[129.190951,35.575779],\"bearing_before\":0,\"type\":\"depart\"},\"duration\":17.3,\"distance\":52.6,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"out\":2,\"location\":[129.190485,35.575496],\"bearings\":[45,150,315],\"entry\":[false,true,true],\"in\":0}],\"geometry\":{\"coordinates\":[[129.190485,35.575496],[129.190312,35.575637]],\"type\":\"LineString\"},\"maneuver\":{\"bearing_after\":315,\"type\":\"turn\",\"modifier\":\"right\",\"bearing_before\":230,\"location\":[129.190485,35.575496]},\"duration\":4,\"distance\":22.2,\"name\":\"\",\"mode\":\"driving\"},{\"intersections\":[{\"in\":0,\"entry\":[true],\"bearings\":[135],\"location\":[129.190312,35.575637]}],\"geometry\":{\"coordinates\":[[129.190312,35.575637]],\"type\":\"Point\"},\"maneuver\":{\"bearing_after\":0,\"location\":[129.190312,35.575637],\"bearing_before\":315,\"type\":\"arrive\"},\"duration\":0,\"distance\":0,\"name\":\"\",\"mode\":\"driving\"}],\"summary\":\"\",\"duration\":21.3,\"distance\":74.8}]}],\"tracepoints\":[{\"waypoint_index\":0,\"location\":[129.188041,35.576265],\"name\":\"\",\"hint\":\"FFcHgBlXBwAAAAAAQwAAAEoAAAAcAwAAGQEAAGFkBABFAQAAyUCzB8nZHgLHQLMHvtkeAhMAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":1,\"location\":[129.188688,35.576143],\"name\":\"\",\"hint\":\"FFcHgBlXB4AAAAAAEwAAACAAAAAABAAAjwAAAGFkBABFAQAAUEOzB0_ZHgJJQ7MHN9keAhUAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":2,\"location\":[129.188345,35.575605],\"name\":\"\",\"hint\":\"_1YHABpXB4AAAAAAEwAAAAkAAAB9AwAAUgAAAFRkBABFAQAA-UGzBzXXHgIGQrMHFdceAhIAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":3,\"location\":[129.187729,35.574527],\"name\":\"\",\"hint\":\"_1YHABpXB4AAAAAAYwAAACoAAAAwAgAALgEAAFRkBABFAQAAkT-zB__SHgKYP7MH_tIeAgsAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":4,\"location\":[129.187407,35.573311],\"name\":\"\",\"hint\":\"_1YHABpXB4AAAAAAKgAAAJUAAABxAQAAuwEAAFRkBABFAQAATz6zBz_OHgJKPrMHQc4eAgoAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":5,\"location\":[129.186796,35.57138],\"name\":\"\",\"hint\":\"_1YHgBpXB4AAAAAAAgAAABQAAAAAAAAA1QMAAFRkBABFAQAA7DuzB7TGHgIoPLMHwcYeAgAAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":6,\"location\":[129.18991,35.571173],\"name\":\"\",\"hint\":\"_lYHgBFXBwAAAAAAFgAAABcAAAAVAgAAmQEAAFNkBABFAQAAFkizB-XFHgIDSLMHAcYeAhQAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":7,\"location\":[129.1914,35.572644],\"name\":\"\",\"hint\":\"_lYHgBFXBwAAAAAAOAAAAAYAAAB3AwAAJgAAAFNkBABFAQAA6E2zB6TLHgLXTbMHqsseAhsAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":8,\"location\":[129.192519,35.572857],\"name\":\"유니스트길 (UNIST-gil)\",\"hint\":\"EFcHgP___3-AigAAKAAAAMEAAAD4AAAADgIAAF5kBABFAQAAR1KzB3nMHgJIUrMHe8weAgYAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":9,\"location\":[129.194411,35.572236],\"name\":\"유니스트길 (UNIST-gil)\",\"hint\":\"EFcHgP___3-AigAANQAAAFMAAAAWAwAAXgAAAF5kBABFAQAAq1mzBwzKHgKoWbMHB8oeAgoAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":10,\"location\":[129.195091,35.571976],\"name\":\"\",\"hint\":\"J1cHAClXB4AAAAAAAwAAAAQAAAAFAAAAFQAAAGZkBABFAQAAU1yzBwjJHgJsXLMH8MgeAgEAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":11,\"location\":[129.197477,35.571974],\"name\":\"유니스트길 (UNIST-gil)\",\"hint\":\"JFcHACVXB4CAigAAHQAAAAUAAADjAAAA6wAAAGVkBABFAQAApWWzBwbJHgKkZbMHE8keAgUAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":12,\"location\":[129.199452,35.572198],\"name\":\"유니스트길 (UNIST-gil)\",\"hint\":\"JFcHACVXB4CAigAAAgAAAD4AAAAzAAAAfQEAAGVkBABFAQAAXG2zB-bJHgJabbMH9skeAgIAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":13,\"location\":[129.200033,35.572181],\"name\":\"\",\"hint\":\"CVcHACNXB4AAAAAABgAAAAMAAAAXAAAADAAAAFtkBABFAQAAoW-zB9XJHgKob7MH3MkeAgMAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":14,\"location\":[129.200364,35.572183],\"name\":\"\",\"hint\":\"CFcHgA1XBwAAAAAAAAAAAAcAAAAIAAAAGQAAAFpkBABFAQAA7HCzB9fJHgL1cLMH08keAgEAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":15,\"location\":[129.200641,35.572489],\"name\":\"\",\"hint\":\"D1cHgBZXBwAAAAAALAAAABUAAAAAAAAAIwEAAF1kBABFAQAAAXKzBwnLHgILcrMH88oeAgAAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":16,\"location\":[129.201836,35.572877],\"name\":\"\",\"hint\":\"D1cHgBZXBwAAAAAAYgAAAGcAAACbAAAAAAAAAF1kBABFAQAArHazB43MHgKndrMHmMweAgIAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":17,\"location\":[129.20224,35.573608],\"name\":\"\",\"hint\":\"F1cHgCBXBwAAAAAAcAAAAJIAAABHAAAA1Q4AAGJkBABFAQAAQHizB2jPHgJUeLMHbM8eAgIAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":18,\"location\":[129.201821,35.574979],\"name\":\"\",\"hint\":\"F1cHgCBXBwAAAAAAPQAAAEkAAABRAgAARw0AAGJkBABFAQAAnXazB8PUHgKndrMHxtQeAgUAAQHX2jHt\",\"matchings_index\":0},null,null,null,null,{\"waypoint_index\":19,\"location\":[129.191985,35.576239],\"name\":\"\",\"hint\":\"F1cHgCBXBwAAAAAAkwAAABcAAACrDQAAyQEAAGJkBABFAQAAMVCzB6_ZHgIzULMH9dkeAhoAAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":20,\"location\":[129.190951,35.575779],\"name\":\"\",\"hint\":\"F1cHgCBXBwAAAAAALgAAAA0AAABSDwAAkQAAAGJkBABFAQAAJ0yzB-PXHgI9TLMHy9ceAh0AAQHX2jHt\",\"matchings_index\":0},{\"waypoint_index\":21,\"location\":[129.190312,35.575637],\"name\":\"\",\"hint\":\"GFcHAB9XB4AAAAAAAQAAACgAAAD5AAAAAAAAAGNkBABFAQAAqEmzB1XXHgKuSbMHWtceAgcAAQHX2jHt\",\"matchings_index\":0}]}";
}