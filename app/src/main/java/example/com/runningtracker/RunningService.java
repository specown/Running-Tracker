package example.com.runningtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RunningService extends Service implements LocationListener {

    Location previousLoc;
    double totalDistance;
    long startTime;
    LocationManager locationManager;

    public RunningService() {
    }

    @Override
    //Location manager
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, this);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }

        startTime = System.currentTimeMillis();

        return START_STICKY;
    }

    //public long getTime() {
    //return System.currentTimeMillis() - startTime; }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    //If service destroyed
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(previousLoc != null) {

            //Gets distance from last update and adds to total distance
            //Helps to prevent errors if person is running in circles (back to starting position)
            totalDistance += (double) previousLoc.distanceTo(location);
            previousLoc = location;
            Intent intent = new Intent();
            intent.setAction("RUNNING_UPDATE");
            intent.putExtra("distance", totalDistance);
            //intent.putExtra("time", getTime());
            sendBroadcast(intent);
        } else {
            previousLoc = location;
        }
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
}
