/**
 * Created by: Tomek Spędzia
 * Date: 5/19/2016
 * Email: tomek.milosz.spedzia@gmail.com
 */

package jacare.transpot.utility;

import android.location.Address;
import android.support.v7.widget.RecyclerView;
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
    private List<Address> addresses;

    public AddressSuggestionAdapter(List<Address> addresses){
        this.addresses = addresses;
    }

    public void setAddresses(List<Address> addresses){
        this.addresses = addresses;
    }

    @Override
    public AddressSuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_address, parent, false);

        return new AddressSuggestionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddressSuggestionHolder holder, int pos) {
        Address address = addresses.get(pos);
        holder.addressName.setText(address.getAddressLine(0));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class AddressSuggestionHolder extends RecyclerView.ViewHolder{
        TextView addressName;
        public AddressSuggestionHolder(View itemView) {
            super(itemView);
            addressName = (TextView)itemView.findViewById(R.id.card_address_name);
        }
    }
}
