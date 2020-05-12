package com.sidev.stancasa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SeekBar changeLightSeekBar;
    private TextView displayAmountOfLight;
    private Button setLightButton;

    private int desiredAmountOfLight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        setAmountOfLight();
    }


    public void setAmountOfLight(){
        changeLightSeekBar=(SeekBar)findViewById(R.id.lightSeekBar);
        displayAmountOfLight=(TextView)findViewById(R.id.DisplayAmountOfLight);
        setLightButton=(Button)findViewById(R.id.setLightButton);

        changeLightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desiredAmountOfLight=progress;
                displayAmountOfLight.setText(""+desiredAmountOfLight+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Lights set successfully!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
