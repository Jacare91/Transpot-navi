package jacare.transpot.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocHelper {
    public static final String TAG = "Trnspt.LocHelp";

    private Geocoder geocoder;

    public LocHelper(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public Observable<List<Address>> convertLatLngToAddress(final LatLng loc) {
        Log.i(TAG, String.format("Converting %s to address!", loc.toString()));

        return Observable.just(loc)
                .observeOn(Schedulers.io())
                .filter(latLng -> Geocoder.isPresent())
                .map(latLng -> {
                    List<Address> addresses = new ArrayList<Address>();
                    try {
                        addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 5);
                        Log.i(TAG, String.format("Geocoder found %s results", addresses.size()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Something fucked up with geocoder!");
                    }

                    return addresses;
                });
    }

    public Observable<List<Address>> convertAddressStringToLatLng(String addressString){
        Log.i(TAG, String.format("Converting %s to input!", addressString));

        return Observable.just(addressString)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(address -> {
                    List<Address> addresses = new ArrayList<Address>();
                    try {
                        addresses = geocoder.getFromLocationName(addressString, 20);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Something fucked up with geocoder!");
                    }

                    return addresses;
                });
    }
}