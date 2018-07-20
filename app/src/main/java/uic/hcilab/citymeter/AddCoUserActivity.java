package uic.hcilab.citymeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import uic.hcilab.citymeter.DB.CoUserDBHelper;
import uic.hcilab.citymeter.Helpers.LogInHelper;

public class AddCoUserActivity extends AppCompatActivity {
    CoUserDBHelper coUserDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_co_user);
        coUserDBHelper = new CoUserDBHelper(this);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.addCoUserToolbar);
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
                AddCoUserActivity.this.finish();
            }
        });
        final CheckBox locCheck = (CheckBox) findViewById(R.id.locationCheck);
        final CheckBox activitiesCheck = (CheckBox) findViewById(R.id.activityCheck);
        final CheckBox cogCheck = (CheckBox) findViewById(R.id.cogCheck);

        final EditText cuid = (EditText) findViewById(R.id.usernameEditText);
        cuid.setSelection(cuid.getText().toString().length());
        cuid.setSelectAllOnFocus(true);

        Button cancel = (Button) findViewById(R.id.cancelAddCoUserButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(intent);
                AddCoUserActivity.this.finish();
            }
        });

        Button confirm = (Button) findViewById(R.id.confirmAddCoUserButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cuid.getText().toString() != "") {
                    coUserDBHelper.createCoEntry(LogInHelper.getCurrUser(), cuid.getText().toString(), boolTodouble(locCheck.isChecked()), boolTodouble(activitiesCheck.isChecked()), boolTodouble(cogCheck.isChecked()));
                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);
                    AddCoUserActivity.this.finish();
                }
            }
        });
    }

    Double boolTodouble(Boolean b) {
        if (b == true)
            return 1.0;
        else
            return 0.0;

    }
}