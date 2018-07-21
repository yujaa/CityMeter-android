package uic.hcilab.citymeter.Helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.models.nosql.CognitiveTestDO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import uic.hcilab.citymeter.DB.CousersDO;
import uic.hcilab.citymeter.DetailCareTakerActivity;
import uic.hcilab.citymeter.R;
import uic.hcilab.citymeter.SettingsActivity;

public class CognitiveTestRecyclerViewAdapter  extends RecyclerView.Adapter<CognitiveTestRecyclerViewAdapter.ViewHolder> {

        private List<com.amazonaws.models.nosql.CognitiveTestDO> mData;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;

        // data is passed into the constructor
        public CognitiveTestRecyclerViewAdapter(Context context, List<com.amazonaws.models.nosql.CognitiveTestDO> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public CognitiveTestRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.couser_recycler_row, parent, false);
            return new CognitiveTestRecyclerViewAdapter.ViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String timestamp = mData.get(position).getTimestamp();
            timestamp = getDateFromTimestamp(Long.parseLong(timestamp), "YYYY-dd-MM HH:mm:ss");
            holder.myTextView.setText(timestamp );
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

        public CognitiveTestDO getCoUser(int id){
            return mData.get(id);
        }

        // allows clicks events to be caught
        public void setClickListener(DetailCareTakerActivity itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }

    public static String getDateFromTimestamp(long timestamp, String dateFormat) {
        // precaution
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }

        // Using SimpleDateFormat to format date
        String date;
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("CDT")); // setting timezone to GMT
        date = formatter.format(new Date(timestamp));
        return date;
    }


}
