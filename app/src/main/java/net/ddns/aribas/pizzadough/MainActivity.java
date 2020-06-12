package net.ddns.aribas.pizzadough;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private final float yeast_percent = 2.5f;
    private final float salt_percent = 2.0f;
    private final float oil_percent = 2.5f;
    private int hydration, yeastSB, saltSB, oilSB;

    private AdView mAdView;

    private Button calcButton;

    private EditText editPortions, editWeightPortion;
    private EditText editFlour;
    private EditText editWater;
    private EditText editYeast;
    private EditText editSalt;
    private EditText editOil;

    private TextView hydrationPercentTextView, yeastPercentTextView, saltPercentTextView, oilPercentTextView;

    private SeekBar hydrationSeekBar, yeastSeekBar, saltSeekBar, oilSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        /*
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(Arrays.asList(AdRequest.DEVICE_ID_EMULATOR))
                        .build());
        */
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        editPortions = findViewById(R.id.editPortions);
        editWeightPortion = findViewById(R.id.editWeightPortion);
        editFlour = findViewById(R.id.editFlour);
        editWater = findViewById(R.id.editWater);
        editYeast = findViewById(R.id.editYeast);

        editSalt = findViewById(R.id.editSalt);
        editOil = findViewById(R.id.editOil);

        hydrationPercentTextView = findViewById(R.id.hydrationPercentTextView);
        hydrationSeekBar = findViewById(R.id.hydrationSeekBar);
        yeastPercentTextView = findViewById(R.id.yeastPercentTextView);
        yeastSeekBar = findViewById(R.id.yeastSeekBar);
        saltSeekBar = findViewById(R.id.saltSeekBar);
        saltPercentTextView = findViewById(R.id.saltPercentTextView);
        oilSeekBar = findViewById(R.id.oilSeekBar);
        oilPercentTextView = findViewById(R.id.oilPercentTextView);

        // Disabling Inputs
        editFlour.setEnabled(false);
        editFlour.setInputType(InputType.TYPE_NULL);
        editWater.setEnabled(false);
        editWater.setInputType(InputType.TYPE_NULL);
        editYeast.setEnabled(false);
        editYeast.setInputType(InputType.TYPE_NULL);
        editSalt.setEnabled(false);
        editSalt.setInputType(InputType.TYPE_NULL);
        editOil.setEnabled(false);
        editOil.setInputType(InputType.TYPE_NULL);

        // Hydration seek bar change listener
        hydrationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hydration = i;
                hydrationPercentTextView.setText(String.format("%d%%", i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                hydration = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_hydration_is,hydration);
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Yeast seek bar change listener
        yeastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                yeastSB = i;
                yeastPercentTextView.setText(String.format("%.1f%%", getConvertedValue(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                yeastSB = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_yeast_is,getConvertedValue(yeastSB));
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Salt seek bar change listener
        saltSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                saltSB = i;
                saltPercentTextView.setText(String.format("%.1f%%", getConvertedValue(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                saltSB = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_salt_is,getConvertedValue(saltSB));
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Oil seek bar change listener
        oilSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                oilSB = i;
                oilPercentTextView.setText(String.format("%.1f%%", getConvertedValue(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                oilSB = 0;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_oil_is,getConvertedValue(oilSB));
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        calcButton = findViewById(R.id.buttonCalculate);
        calcButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int portions, portion_weight, total_weight;
                float flour, water, yeast_per, salt_per, oil_per;
                float yeast, salt, oil;

                portions =  (editPortions.getText().toString().equals("")) ? 0 : Integer.parseInt(editPortions.getText().toString());
                portion_weight =  (editWeightPortion.getText().toString().equals("")) ? 0 : Integer.parseInt(editWeightPortion.getText().toString());

                yeast_per = getConvertedValue(yeastSB);
                salt_per = getConvertedValue(saltSB);
                oil_per = getConvertedValue(oilSB);

                total_weight = portions * portion_weight;

               if (hydration > 0 && hydration <= 100) {
                   flour = total_weight / (1 + hydration / (float) 100);
                   water = total_weight - flour;

                   // Calculate percentages
                   yeast = flour * (yeast_per / 100);
                   salt = flour * (salt_per / 100);
                   oil = flour * (oil_per/100);

                   // Print outputs
                   editFlour.setText(String.valueOf(Math.round(flour)));
                   editWater.setText(String.valueOf(Math.round(water)));
                   editYeast.setText(String.valueOf(Math.round(yeast)));
                   editSalt.setText(String.valueOf(Math.round(salt)));
                   editOil.setText(String.valueOf(Math.round(oil)));

                } else {
                   String toastMessage = getString(R.string.toast_hydration_warning);
                   Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    // Helper function to convert value from int to float
    public float getConvertedValue(int intVal){
        float floatVal;
        floatVal = .1f * intVal;
        return floatVal;
    }
}
