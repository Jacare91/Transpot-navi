/**
 * Created by: Tomek Spędzia
 * Date: 5/19/2016
 * Email: tomek.milosz.spedzia@gmail.com
 */

package jacare.transpot.utility;

import android.location.Address;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jacare.transpot.R;

public class AddressSuggestionAdapter extends RecyclerView.Adapter<AddressSuggestionAdapter.AddressSuggestionHolder>{
    protected static final String TAG = "Maps.Suggestions";
    private List<Address> addresses;

    public AddressSuggestionAdapter(List<Address> addresses){
        this.addresses = addresses;
    }

    public List<Address> getAddresses(){
        return addresses;
    }

    @Override
    public AddressSuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, String.format("createViewHolder. Is current thread main: %b",
                Looper.myLooper() == Looper.getMainLooper()));
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_address, parent, false);

        return new AddressSuggestionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressSuggestionHolder holder, int pos) {
        Log.i(TAG, String.format("bindViewHolder. Is current thread main: %b",
                Looper.myLooper() == Looper.getMainLooper()));
        Address address = addresses.get(pos);
        holder.addressName.setText(address.getAddressLine(0));
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, String.format("Size: %s. Is current thread main: %b",
                addresses.size(), Looper.myLooper() == Looper.getMainLooper()));
        return addresses.size();
    }

    public class AddressSuggestionHolder extends RecyclerView.ViewHolder{
        TextView addressName;
        public AddressSuggestionHolder(View itemView) {
            super(itemView);
            Log.i(TAG, String.format("Building item. Is current thread main: %b",
                    Looper.myLooper() == Looper.getMainLooper()));
            addressName = (TextView)itemView.findViewById(R.id.card_address_name);
        }
    }
}
