package uic.hcilab.citymeter.Helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uic.hcilab.citymeter.DB.CousersDO;
import uic.hcilab.citymeter.R;
import uic.hcilab.citymeter.SettingsActivity;

public class CaretakersRecyclerViewAdapter  extends RecyclerView.Adapter<CaretakersRecyclerViewAdapter.ViewHolder> {

        private List<CousersDO> mData;
        private LayoutInflater mInflater;
        private uic.hcilab.citymeter.Helpers.CoUserRecyclerViewAdapter.ItemClickListener mClickListener;

        // data is passed into the constructor
        public CaretakersRecyclerViewAdapter(Context context, List<CousersDO> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.couser_recycler_row, parent, false);
        return new ViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String couser = mData.get(position).getUid().toString();
            holder.myTextView.setText(couser );
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.coUserName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        public String getItem(int id) {
            return mData.get(id).getUid().toString();
        }

        public CousersDO getCoUser(int id){
            return mData.get(id);
        }

        // allows clicks events to be caught
        public void setClickListener(SettingsActivity itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }
