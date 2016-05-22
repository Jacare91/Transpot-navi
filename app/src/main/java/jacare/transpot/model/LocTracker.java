/**
 * Created by: Tomek Spędzia
 * Date: 5/10/2016
 * Email: tomek.milosz.spedzia@gmail.com
 */

package jacare.transpot.model;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import rx.subjects.PublishSubject;

public class LocTracker implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String TAG = "Trnspt.LocTracker";

    protected GoogleApiClient apiClient;
    protected Context context;

    protected PublishSubject<Boolean> startedLocUpdatesObs;
    protected PublishSubject<LatLng> updatedLocObs;

    public LocTracker(Context context) {
        this.context = context;

        apiClient = startApi();
        updatedLocObs = PublishSubject.create();
        startedLocUpdatesObs = PublishSubject.create();

        startLocUpdates();
    }

    public PublishSubject<LatLng> getUpdatedLocObs() {
        return updatedLocObs;
    }

    public PublishSubject<Boolean> getStartedLocUpdatesObs() {
        return startedLocUpdatesObs;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocUpdates();
        startedLocUpdatesObs.onNext(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location loc) {
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        Log.d(TAG, "Updating location with value " + latLng.toString());
        updatedLocObs.onNext(latLng);
    }

    protected LocationRequest buildLocationRequest() {
        return new LocationRequest().setInterval(10000).setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected GoogleApiClient startApi() {
        GoogleApiClient apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        apiClient.registerConnectionCallbacks(this);
        apiClient.registerConnectionFailedListener(this);
        apiClient.connect();
        return apiClient;
    }

    protected void startLocUpdates() {
        LocationRequest request = buildLocationRequest();
        if(apiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
    }
}
