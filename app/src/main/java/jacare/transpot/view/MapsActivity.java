package jacare.transpot.view;

import android.location.Address;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jacare.transpot.R;
import jacare.transpot.model.LocHelper;
import jacare.transpot.model.LocTracker;
import jacare.transpot.utility.AddressSuggestionAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MapsActivity extends BaseActivity implements GoogleMap.OnMapClickListener {
    public static final String TAG = "Trnspt.Maps";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        locTracker = startTracker();
        locHelper = new LocHelper(this);
        if(map == null)
            map = startMap();

        onInputLeft(destInput).subscribe();
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
                .flatMap(this::convertLatLngToAddress)
                .map(addresses -> addresses.get(0))
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
        Observable.just(destInput.hasFocus())
                .filter(hasFocus -> hasFocus)
                .flatMap(hasFocus -> Observable.just(destInput)
                        .flatMap(this::onInputLeft))
                .map(hasFocus -> latLng)
                .map(this::updateDestPos)
                .flatMap(this::convertLatLngToAddress)
                .map(addresses -> addresses.get(0).getAddressLine(0))
                .subscribe(destInput::setText);
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

    private Observable<List<Address>> convertLatLngToAddress(LatLng latLng){
        return Observable.just(latLng)
                .flatMap(loc -> locHelper.convertLatLngToAddress(loc)
                .filter(addresses -> (addresses != null && addresses.size() > 0))
                .observeOn(AndroidSchedulers.mainThread()));
    }
}
