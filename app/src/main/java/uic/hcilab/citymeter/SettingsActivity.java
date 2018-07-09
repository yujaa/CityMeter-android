package uic.hcilab.citymeter;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uic.hcilab.citymeter.DB.CoUserDBHelper;
import uic.hcilab.citymeter.DB.CousersDO;
import uic.hcilab.citymeter.Helpers.CoUserRecyclerViewAdapter;

//TODO: update user ID
public class SettingsActivity extends TabHost implements CoUserRecyclerViewAdapter.ItemClickListener {
    CoUserRecyclerViewAdapter adapter;
    CoUserDBHelper coUserDBHelper;
    ImageButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.settingstoolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.ic_back);  //your icon
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
        add = (ImageButton) findViewById(R.id.addCoUserButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddCoUserActivity.class);
                startActivity(intent);
                SettingsActivity.this.finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            coUserDBHelper = new CoUserDBHelper(this);
            coUserDBHelper.connect();
            coUserDBHelper.getAllCoUsers("1");
            List<CousersDO> coUsers = coUserDBHelper.coUsers;

            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.couserRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CoUserRecyclerViewAdapter(this, coUsers);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        else
        {
            Toast.makeText(this,"Internet is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SettingsActivity.this.finish();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_home;
    }
    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), DetailCoUserActivity.class);
        intent.putExtra("COUSER_ID", adapter.getItem(position));
        startActivity(intent);
        SettingsActivity.this.finish();
    }

    public boolean isOnline() {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
