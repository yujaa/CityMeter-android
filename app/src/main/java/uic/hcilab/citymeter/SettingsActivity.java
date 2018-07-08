package uic.hcilab.citymeter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

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
            }
        });
        coUserDBHelper = new CoUserDBHelper(this);
        coUserDBHelper.getAllCoUsers("1");
        while (!coUserDBHelper.isDone){
        }
        coUserDBHelper.isDone = false;
        List<CousersDO> coUsers = coUserDBHelper.coUsers;


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.couserRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoUserRecyclerViewAdapter(this, coUsers);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
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
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
