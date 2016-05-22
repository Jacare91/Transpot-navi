/**
 * Created by: Tomek Spędzia
 * Date: 4/28/2016
 * Email: tomek.milosz.spedzia@gmail.com
 */
package jacare.transpot.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    public static final String TAG = "LocHelp";

    protected Context context;
    protected LinkedHashMap<LatLng, String> addresssList;
    protected Address currLoc;

    public LocationHelper(Context context){
        this.context = context;
        addresssList = new LinkedHashMap<>();
    }

    public Location convertAddressToLocation(Address address){
        Location location = new Location("");
        location.setLatitude(address.getLatitude());
        location.setLongitude(address.getLongitude());

        return location;
    }

    public void convertAddressStringToLatLng(final String addressString, final Location stuffLocation,
                                             final LocationListener listener) {
        new AsyncTask<Void, Void, LatLng>() {
            @Override
            protected LatLng doInBackground(Void... params) {
                if(addressString.equals(""))
                    return null;
                try {
                    Geocoder gc = new Geocoder(context);

                    if (Geocoder.isPresent()) {
                        Log.d(TAG, "Obtaining address.");

                        List<Address> list = null;
                        String builtAddress = buildAddressString(addressString);

                        if(builtAddress != null)
                            list = gc.getFromLocationName(builtAddress, 20);

                        if (list == null || list.size() == 0) {
                            Log.d(TAG, "No addresses!");
                            listener.onAddressNotFound();
                            return null;
                        }
                        else {
                            Log.d(TAG, "Parsing addresses.");

                            Address address = list.get(0);
                            Location location = convertAddressToLocation(list.get(0));
                            double distanceFrom = stuffLocation.distanceTo(location);
                            for (int i = 1; i < list.size(); i++) {
                                location = convertAddressToLocation(list.get(i));
                                double tmpDist = stuffLocation.distanceTo(location);
                                if (tmpDist < distanceFrom) {
                                    distanceFrom = tmpDist;
                                    address = list.get(i);
                                }
                            }

                            Log.d(TAG, "Hit address! Obtained address is " + location.toString());
                            return new LatLng(address.getLatitude(), address.getLongitude());
                        }
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Something fucked up with geocoder!");
                }

                return null;
            }

            @Override
            protected void onPostExecute(LatLng latLng) {
                if(latLng != null)
                    listener.onConversionFinished(latLng);
                else
                    listener.onAddressNotFound();
            }
        }.execute();
    }

    public void convertLatLngToAddress(final LatLng location, final AddressListener listener) {
        if (!addresssList.containsKey(location)) {
            new AsyncTask<Void, Void, Address>() {
                @Override
                protected Address doInBackground(Void... params) {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                        if (addresses.size() > 0) {
                            Address address = addresses.get(0);
                            currLoc = address;
                            return address;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Something fucked up with geocoder!");
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Address address) {
                    if(listener != null && address != null)
                        listener.onConversionFinished(address);
                }
            }.execute();
        }
    }

    protected String buildAddressString(String address){
        if(currLoc != null) {
            if (address.contains(", " + currLoc.getLocality()))
                return address;
            else if(address.contains(", "))
                return address;
            else {
                return address + ", " + currLoc.getLocality();
            }
        }
        else
            return null;
    }

    public static Location buildLocation(LatLng latLng, String provider){
        Location location = new Location(provider);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        return location;
    }

    public interface LocationListener{
        void onConversionFinished(LatLng latLng);
        void onAddressNotFound();
    }

    public interface AddressListener{
        void onConversionFinished(Address address);
    }
}