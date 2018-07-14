package uic.hcilab.citymeter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Typeface.BOLD;

public class TestResultActivity extends AppCompatActivity {
    String cuid;
    String _memoryProblem;
    String _blood;
    String _balance;
    String _balanceCause;
    String _majorStroke;
    String _sadDepressed;
    String _personality;
    String _difficultyActivities;
    String _todayDate;
    String _namePictureRhino;
    String _namePictureHarp;
    String _tulip;
    String _quarters;
    String _groceriesChange;
    String _copyPictureFile;
    String _drawClockFile;
    String _countries12;
    String _circleLinesFile;
    String _trianglesFile;
    String _done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        cuid = getIntent().getStringExtra("User");
        _memoryProblem = getIntent().getStringExtra("memoryProblem");
        TextView mem = (TextView) findViewById(R.id.memoryA);
        mem.setText(_memoryProblem);
        mem.setTextColor(Color.BLUE);
        _blood = getIntent().getStringExtra("blood");
        TextView blood = (TextView) findViewById(R.id.bloodA);
        blood.setText(_blood);
        blood.setTextColor(Color.BLUE);
        _balance = getIntent().getStringExtra("balance");
        _balanceCause = getIntent().getStringExtra("balanceCause");
        TextView balance = (TextView) findViewById(R.id.balanceA);
        balance.setText(_balance + " " + _balanceCause);
        balance.setTextColor(Color.BLUE);

        _majorStroke = getIntent().getStringExtra("stroke");
        TextView stroke = (TextView) findViewById(R.id.strokeA);
        stroke.setText(_majorStroke);
        stroke.setTextColor(Color.BLUE);

        _sadDepressed = getIntent().getStringExtra("sad");
        TextView sad = (TextView) findViewById(R.id.sadA);
        sad.setText(_sadDepressed);
        sad.setTextColor(Color.BLUE);

        _personality = getIntent().getStringExtra("personality");
        TextView p = (TextView) findViewById(R.id.personalityA);
        p.setText(_personality);
        p.setTextColor(Color.BLUE);

        _difficultyActivities = getIntent().getStringExtra("difficulty");
        TextView d = (TextView) findViewById(R.id.difficultiesA);
        d.setText(_difficultyActivities);
        d.setTextColor(Color.BLUE);

        _todayDate = getIntent().getStringExtra("today");
        TextView today = (TextView) findViewById(R.id.todayA);
        today.setText(_todayDate);
        today.setTextColor(Color.BLUE);

        _namePictureRhino = getIntent().getStringExtra("rhino");
        TextView rhino = (TextView) findViewById(R.id.rhinoA);
        rhino.setText(_namePictureRhino);
        rhino.setTextColor(Color.BLUE);

        _namePictureHarp = getIntent().getStringExtra("harp");
        TextView harp = (TextView) findViewById(R.id.harpA);
        harp.setText(_namePictureHarp);
        harp.setTextColor(Color.BLUE);

        _tulip = getIntent().getStringExtra("tulip");
        TextView tulip = (TextView) findViewById(R.id.tulipA);
        tulip.setText(_tulip);
        tulip.setTextColor(Color.BLUE);

        _quarters = getIntent().getStringExtra("quarters");
        TextView q = (TextView) findViewById(R.id.quartersA);
        q.setText(_quarters);
        q.setTextColor(Color.BLUE);

        _groceriesChange = getIntent().getStringExtra("change");
        TextView c = (TextView) findViewById(R.id.changeA);
        c.setText(_groceriesChange);
        c.setTextColor(Color.BLUE);

        _copyPictureFile = getIntent().getStringExtra("copy");
        ImageView copy = (ImageView)findViewById(R.id.copyAI);
        try {
            copy.setImageURI(Uri.parse(_copyPictureFile));
        }catch (Exception e){

        }

        _drawClockFile = getIntent().getStringExtra("clock");
        ImageView clock = (ImageView)findViewById(R.id.clockAI);
        try {
            clock.setImageURI(Uri.parse(_drawClockFile));
        }catch (Exception e){

        }

        _countries12 = getIntent().getStringExtra("countries");
        TextView c12 = (TextView) findViewById(R.id.countriesA);
        c12.setText(_countries12);
        c12.setTextColor(Color.BLUE);

        _circleLinesFile = getIntent().getStringExtra("circle");
        ImageView circle = (ImageView)findViewById(R.id.circleAI);
        try {
            circle.setImageURI(Uri.parse(_circleLinesFile));
        }catch (Exception e){

        }
        _trianglesFile = getIntent().getStringExtra("tri");
        ImageView tri = (ImageView)findViewById(R.id.triangleAI);
        try {
            tri.setImageURI(Uri.parse(_trianglesFile));
        }catch (Exception e){

        }

        _done = getIntent().getStringExtra("done");
        TextView done = (TextView) findViewById(R.id.doneA);
        done.setText(_done);
        done.setTextColor(Color.BLUE);

        Toolbar couserToolbar = (Toolbar) findViewById(R.id.resultToolbar);
        setSupportActionBar(couserToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(cuid);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        couserToolbar.setNavigationIcon(R.drawable.ic_back);  //your icon
        couserToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                TestResultActivity.this.finish();
            }
        });
    }
}
