package jacare.transpot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import jacare.transpot.model.LocHelper;
import jacare.transpot.model.LocTracker;
import jacare.transpot.utility.AddressSuggestionAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener {
    public static final String TAG = "Trnspt.Maps";

    private final static int START = 0;
    private final static int DEST = 1;

    @Bind(R.id.maps_output_start)protected TextView currPosOutput;
    @Bind(R.id.maps_input_dest)protected EditText destInput;
    @Bind(R.id.maps_input_container)protected CardView inputContainer;
    @Bind(R.id.maps_btn_action)protected FloatingActionButton btnAction;
    @Bind(R.id.maps_btn_switch)protected ImageView btnSwitch;
    @Bind(R.id.maps_photo_profile)protected ImageView phtProfile;
    @Bind(R.id.maps_address_list_suggestions) protected RecyclerView addressSuggestions;

    private AddressSuggestionAdapter suggestionAdapter;
    private LocTracker locTracker;
    private LocHelper locHelper;
    private GoogleMap map;

    private Marker destPosMarker;
    private Marker currPosMarker;

    protected boolean started;
    protected boolean addressTyped;
    protected boolean addresInputLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        locTracker = startTracker();
        locHelper = new LocHelper(this);
        if(map == null)
            map = startMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMap();
    }

    private LocTracker startTracker(){
        LocTracker tracker = new LocTracker(this);
        tracker.getStartedLocUpdatesObs().filter(bool -> !started).subscribe();
        tracker.getUpdatedLocObs()
                .flatMap(latLng -> locHelper.convertLatLngToAddress(latLng)
                .filter(addresses -> (addresses != null && addresses.size() > 0))
                .map(addresses -> addresses.get(0)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> {
                    currPosOutput.setText(address.getAddressLine(0));
                    updateCurrPos(new LatLng(address.getLatitude(), address.getLongitude()));
                });
        return tracker;
    }

    private GoogleMap startMap() {
        GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.setOnMapClickListener(this);
        return map;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.i(TAG, "OnMapClick");
        Observable.just(latLng).map(loc -> updateDestPos(latLng))
                .flatMap(loc -> locHelper.convertLatLngToAddress(latLng))
                .filter(addresses -> addresses != null || addresses.size() > 0)
                .map(addresses -> addresses.get(0).getAddressLine(0))
                .doOnError(e -> Log.d(TAG, "Fuckup!"))
                .subscribe(address -> destInput.setText(address));
    }

    protected LatLng updateDestPos(LatLng latLng){
        if(destPosMarker != null)
            destPosMarker.remove();
        destPosMarker = map.addMarker(new MarkerOptions().position(latLng));
        return latLng;
    }

    private LatLng updateCurrPos(LatLng latLng){
        Log.i(TAG, "Refreshing current pos marker");

        if(currPosMarker != null)
            currPosMarker.remove();
        currPosMarker = map.addMarker(new MarkerOptions().position(latLng));
        return latLng;
    }
}
