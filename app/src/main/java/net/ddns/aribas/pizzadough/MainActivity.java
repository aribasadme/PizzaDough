package net.ddns.aribas.pizzadough;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SaveFileDialog.SaveFileDialogListener {
    public static String PACKAGE_NAME;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private AdView mAdView;
    private Button mCalcButton;
    private DrawerLayout mDrawerLayout;
    private EditText editPortions, editWeightPortion;
    private EditText editFlour, editWater, editYeast, editSalt, editOil;
    private ReviewManager mReviewManager;
    //private ReviewInfo reviewInfo;
    private SeekBar hydrationSeekBar, yeastSeekBar, saltSeekBar, oilSeekBar;
    private TextView hydrationPercentTextView, yeastPercentTextView, saltPercentTextView, oilPercentTextView;

    private int portions = 0, portion_weight = 0, total_weight = 0;
    private int hydration = 65, yeastSB = 10, saltSB = 25, oilSB = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_draw_open, R.string.navigation_draw_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
        disableInputs();

        /* HYDRATION SEEK BAR */
        // Sets default
        hydrationSeekBar.setProgress(hydration);
        hydrationPercentTextView.setText(String.format(Locale.getDefault(), "%d%%", hydration));
        // Sets change listener
        hydrationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hydration = progress;
                hydrationPercentTextView.setText(String.format(Locale.getDefault(), "%d%%", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_hydration_is,hydration);
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /* YEAST SEEK BAR */
        // Sets default
        yeastSeekBar.setProgress(yeastSB);
        yeastPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(yeastSB)));
        // Sets change listener
        yeastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                yeastSB = progress;
                yeastPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_yeast_is,getConvertedValue(yeastSB));
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /* SALT SEEK BAR */
        // Sets default
        saltSeekBar.setProgress(saltSB);
        saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(saltSB)));
        // Sets change listener
        saltSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                saltSB = progress;
                saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_salt_is,getConvertedValue(saltSB));
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /* OIL SEEK BAR */
        // Sets default
        oilSeekBar.setProgress(oilSB);
        oilPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(oilSB)));
        // Sets change listener
        oilSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                oilSB = progress;
                oilPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String toastMessage = getString(R.string.toast_oil_is,getConvertedValue(oilSB));
                Toast.makeText(MainActivity.this, toastMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mCalcButton = findViewById(R.id.buttonCalculate);
        mCalcButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //int portions, portion_weight, total_weight;
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
                   Toast.makeText(MainActivity.this, toastMessage,
                           Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Helper function to convert value from int to float
    public float getConvertedValue(int intVal){
        float floatVal;
        floatVal = .1f * intVal;
        return floatVal;
    }

    // Helper function to create the recipe
    public StringBuilder writeRecipe(){
        StringBuilder sb = new StringBuilder();

        sb.append(getResources().getText(R.string.portions)).append(": ");
        sb.append(editPortions.getText()).append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.portion_weight)).append(": ");
        sb.append(editWeightPortion.getText()).append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.hydration)).append(": ");
        sb.append(hydration).append('%').append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.flour)).append(": ");
        sb.append(editFlour.getText()).append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.water)).append(": ");
        sb.append(editWater.getText()).append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.yeast_grams)).append(": ");
        sb.append(editYeast.getText()).append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.salt_grams)).append(": ");
        sb.append(editSalt.getText()).append(System.getProperty("line.separator"));
        sb.append(getResources().getText(R.string.oil_grams)).append(": ");
        sb.append(editOil.getText());

        return sb;
    }

    // Helper function to disable inputs
    public void disableInputs(){
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
    }

    public void openSaveFileDialog() {
        SaveFileDialog saveFileDialog = new SaveFileDialog();
        saveFileDialog.show(getSupportFragmentManager(), "Save File Dialog");
    }

    @Override
    public void saveFile(String fileName) {
        if (!fileName.isEmpty()) {
            StringBuilder sb = writeRecipe();
            FileOutputStream fos = null;
            try {
                fos = getApplicationContext().openFileOutput(fileName.concat(".txt"), Context.MODE_PRIVATE);
                fos.write(sb.toString().getBytes());
                String toastMessage = getString(R.string.toast_saving, getFilesDir() + "/" + fileName);
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Helper function to call In App Rating
    public void launchInAppRating(){
        // Replace FakeReviewManager for testing
        mReviewManager = ReviewManagerFactory.create(MainActivity.this);
        Task<ReviewInfo> request = mReviewManager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = mReviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "In App Rating complete", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // There was some problem, continue regardless of the result.
                    Toast.makeText(MainActivity.this, "In App Rating failure: requestReviewFlow", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            launchInAppRating();
            super.onBackPressed();
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String toastMessage;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if (itemId == R.id.nav_save) {
            if (portions > 0
                    && portion_weight > 0
                    && total_weight > 0) {
                openSaveFileDialog();
            }
            else {
                toastMessage = getString(R.string.toast_saving_warning);
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        }
        else if (itemId == R.id.nav_rate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME)));
            } catch (ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME)));
            }
        }
        else if (itemId == R.id.nav_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=net.ddns.aribas.pizzadough");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.app_name));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        }
        else if (itemId == R.id.nav_send) {
            if (portions > 0
                    && portion_weight > 0
                    && total_weight > 0) {
                Intent sendIntent = new Intent();
                StringBuilder message = writeRecipe();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
            }
            else {
                toastMessage = getString(R.string.toast_send_warning);
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
