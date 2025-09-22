package com.aribasadmetlla.pizzadough;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewException;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SaveFileDialog.SaveFileDialogListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static String PACKAGE_NAME;
    private AdView mAdView;
    private DrawerLayout drawerLayout;
    private Preferences preferences;
    private BillingClient billingClient;
    private EditText editPortions, editWeightPortion;
    private EditText editFlour, editWater, editYeast, editSalt, editOil;
    private TextView hydrationPercentTextView, yeastPercentTextView, saltPercentTextView, oilPercentTextView;

    private int portions = 0, portion_weight = 0, total_weight = 0;
    private int hydration = 65, yeastSB = 10, saltSB = 25, oilSB = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        // Set up the toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> drawerLayout.open());
        topAppBar.setOnMenuItemClickListener(item -> {
            String toastMessage;

            int itemId = item.getItemId();
            if (itemId == R.id.save) {
                if (portions > 0
                        && portion_weight > 0
                        && total_weight > 0) {
                    openSaveFileDialog();
                } else {
                    toastMessage = getString(R.string.toast_saving_warning);
                    Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (itemId == R.id.send) {
                if (portions > 0
                        && portion_weight > 0
                        && total_weight > 0) {
                    Intent sendIntent = new Intent();
                    StringBuilder message = writeRecipe();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, null));
                } else {
                    toastMessage = getString(R.string.toast_send_warning);
                    Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });


        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigationView);
        // Set a listener for when an item in the NavigationView is selected
        // Called when an item in the NavigationView is selected.
        navigationView.setNavigationItemSelectedListener(manuItem -> {
            int itemId = manuItem.getItemId();
            if (itemId == R.id.nav_saved_recipes) {
                Intent savedRecipesListIntent = new Intent(MainActivity.this, ListSavedRecipesActivity.class);
                startActivity(savedRecipesListIntent);
            } else if (itemId == R.id.nav_rate) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PACKAGE_NAME)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + PACKAGE_NAME)));
                }
            } else if (itemId == R.id.nav_share) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=net.ddns.aribas.pizzadough");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.app_name));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } else if (itemId == R.id.nav_remove_ads) {
                connectGooglePlayBilling();
            } else if (itemId == R.id.nav_about) {
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
            drawerLayout.close();
            return true;
        });

        preferences = new Preferences(getApplicationContext());
        if (preferences.getRemoveAd() == 0) {
            buildAdView();
        }

        Log.i(LOG_TAG, "Check purchased products");
        checkProducts();

        Log.i(LOG_TAG, "Building BillingClient");
        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                        purchases != null) {
                    for (Purchase purchase : purchases) {
                        verifyPayment(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    Log.i(LOG_TAG, "Item already owned");
                }
            }
        };

        PendingPurchasesParams pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build();

        billingClient = BillingClient.newBuilder(getApplicationContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases(pendingPurchasesParams)
                .enableAutoServiceReconnection()
                .build();

        editPortions = findViewById(R.id.editPortions);
        editWeightPortion = findViewById(R.id.editWeightPortion);
        editFlour = findViewById(R.id.editFlour);
        editWater = findViewById(R.id.editWater);
        editYeast = findViewById(R.id.editYeast);

        editSalt = findViewById(R.id.editSalt);
        editOil = findViewById(R.id.editOil);

        hydrationPercentTextView = findViewById(R.id.hydrationPercentTextView);
        TextView hydrationMinusTextView = findViewById(R.id.hydrationMinusTextView);
        TextView hydrationPlusTextView = findViewById(R.id.hydrationPlusTextView);
        SeekBar hydrationSeekBar = findViewById(R.id.hydrationSeekBar);
        yeastPercentTextView = findViewById(R.id.yeastPercentTextView);
        TextView yeastMinusTextView = findViewById(R.id.yeastMinusTextView);
        TextView yeastPlusTextView = findViewById(R.id.yeastPlusTextView);
        SeekBar yeastSeekBar = findViewById(R.id.yeastSeekBar);
        saltPercentTextView = findViewById(R.id.saltPercentTextView);
        TextView saltMinusTextView = findViewById(R.id.saltMinusTextView);
        TextView saltPlusTextView = findViewById(R.id.saltPlusTextView);
        SeekBar saltSeekBar = findViewById(R.id.saltSeekBar);
        oilPercentTextView = findViewById(R.id.oilPercentTextView);
        TextView oilMinusTextView = findViewById(R.id.oilMinusTextView);
        TextView oilPlusTextView = findViewById(R.id.oilPlusTextView);
        SeekBar oilSeekBar = findViewById(R.id.oilSeekBar);

        // Disabling Inputs
        disableInputs();

        // Set default values
        editWeightPortion.setText(String.valueOf(280)); // standard weight for a single dough ball

        /* HYDRATION PLUS AND MINUS CLICKABLE */
        hydrationMinusTextView.setOnClickListener(v -> {
            if (hydration > 0) {
                hydration--;
                hydrationPercentTextView.setText(String.format(Locale.getDefault(), "%d%%", hydration));
                hydrationSeekBar.setProgress(hydration);
                Log.i(LOG_TAG, "Hydration set to " + hydration);
            }
        });
        hydrationPlusTextView.setOnClickListener(v -> {
            if (hydration < 100) {
                hydration++;
                hydrationPercentTextView.setText(String.format(Locale.getDefault(), "%d%%", hydration));
                hydrationSeekBar.setProgress(hydration);
                Log.i(LOG_TAG, "Hydration set to " + hydration);
            }
        });

        /* HYDRATION SEEK BAR */
        // Sets default
        hydrationSeekBar.setProgress(hydration);
        hydrationPercentTextView.setText(String.format(Locale.getDefault(), "%d%%", hydration));
        // Sets change listener
        hydrationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                Log.i(LOG_TAG, "Hydration set to " + hydration);
            }
        });

        /* YEAST PLUS AND MINUS CLICKABLE */
        yeastMinusTextView.setOnClickListener(v -> {
            if (yeastSB > 0) {
                yeastSB--;
                yeastPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(yeastSB)));
                yeastSeekBar.setProgress(yeastSB);
                Log.i(LOG_TAG, "Yeast set to " + yeastSB);
            }
        });
        yeastPlusTextView.setOnClickListener(v -> {
            if (yeastSB < 50) {
                yeastSB++;
                yeastPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(yeastSB)));
                yeastSeekBar.setProgress(yeastSB);
                Log.i(LOG_TAG, "Yeast set to " + yeastSB);
            }
        });

        /* YEAST SEEK BAR */
        // Sets default
        yeastSeekBar.setProgress(yeastSB);
        yeastPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(yeastSB)));
        // Sets change listener
        yeastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                Log.i(LOG_TAG, "Yeast set to " + yeastSB);
            }
        });

        /* SALT PLUS AND MINUS CLICKABLE */
        saltMinusTextView.setOnClickListener(v -> {
            if (saltSB > 0) {
                saltSB--;
                saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(saltSB)));
                saltSeekBar.setProgress(saltSB);
                Log.i(LOG_TAG, "Salt set to " + saltSB);
            }
        });
        saltPlusTextView.setOnClickListener(v -> {
            if (saltSB < 50) {
                saltSB++;
                saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(saltSB)));
                saltSeekBar.setProgress(saltSB);
                Log.i(LOG_TAG, "Salt set to " + saltSB);
            }
        });

        /* SALT SEEK BAR */
        // Sets default
        saltSeekBar.setProgress(saltSB);
        saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(saltSB)));
        // Sets change listener
        saltSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                Log.i(LOG_TAG, "Salt set to " + saltSB);
            }
        });

        /* OIL PLUS AND MINUS CLICKABLE */
        oilMinusTextView.setOnClickListener(v -> {
            if (oilSB > 0) {
                oilSB--;
                oilPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(oilSB)));
                oilSeekBar.setProgress(oilSB);
                Log.i(LOG_TAG, "Oil set to " + oilSB);
            }
        });
        oilPlusTextView.setOnClickListener(v -> {
            if (oilSB < 50) {
                oilSB++;
                oilPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(oilSB)));
                oilSeekBar.setProgress(oilSB);
                Log.i(LOG_TAG, "Oil set to " + oilSB);
            }
        });

        /* SALT PLUS AND MINUS CLICKABLE */
        saltMinusTextView.setOnClickListener(v -> {
            if (saltSB > 0) {
                saltSB--;
                saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(saltSB)));
                saltSeekBar.setProgress(saltSB);
            }
        });
        saltPlusTextView.setOnClickListener(v -> {
            if (saltSB < 50) {
                saltSB++;
                saltPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(saltSB)));
                saltSeekBar.setProgress(saltSB);
            }
        });

        /* OIL SEEK BAR */
        // Sets default
        oilSeekBar.setProgress(oilSB);
        oilPercentTextView.setText(String.format(Locale.getDefault(), "%.1f%%", getConvertedValue(oilSB)));
        // Sets change listener
        oilSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                Log.i(LOG_TAG, "Oil set to " + oilSB);
            }
        });

        Button mCalcButton = findViewById(R.id.buttonCalculate);
        mCalcButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                float flour, water, yeast_per, salt_per, oil_per;
                float yeast, salt, oil;
                String toastMessage;

                portions = (editPortions.getText().toString().isEmpty()) ? 0 : Integer.parseInt(editPortions.getText().toString());
                portion_weight = (editWeightPortion.getText().toString().isEmpty()) ? 0 : Integer.parseInt(editWeightPortion.getText().toString());
                if (portions <= 0 || portion_weight <= 0) {
                    toastMessage = getString(R.string.toast_portions_warning);
                    Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                yeast_per = getConvertedValue(yeastSB);
                salt_per = getConvertedValue(saltSB);
                oil_per = getConvertedValue(oilSB);

                total_weight = portions * portion_weight;

                if (hydration > 0) {
                    flour = total_weight / (1 + hydration / (float) 100);
                    water = total_weight - flour;

                    // Calculate percentages
                    yeast = flour * (yeast_per / 100);
                    salt = flour * (salt_per / 100);
                    oil = flour * (oil_per / 100);

                    // Print outputs
                    editFlour.setText(String.valueOf(Math.round(flour)));
                    editWater.setText(String.valueOf(Math.round(water)));
                    editYeast.setText(String.valueOf(Math.round(yeast)));
                    editSalt.setText(String.valueOf(Math.round(salt)));
                    editOil.setText(String.valueOf(Math.round(oil)));

                    Log.i(LOG_TAG, "Flour: " + flour + " | Water: " + water + " | Yeast: " + yeast + " | Salt: " + salt + " | Oil: " + oil);
                } else {
                    toastMessage = getString(R.string.toast_hydration_warning);
                    Toast.makeText(MainActivity.this, toastMessage,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check if the drawer is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    launchInAppRating();
                    // Back is pressed... Finishing the activity
                    finish();
                }
            }
        });
    } // onCreate

    // Helper function to convert value from int to float
    public float getConvertedValue(int intVal) {
        float floatVal;
        floatVal = .1f * intVal;
        return floatVal;
    }

    // Helper function to create the recipe
    public StringBuilder writeRecipe() {
        StringBuilder sb = new StringBuilder();

        sb.append(getResources().getText(R.string.portions)).append(": ");
        sb.append(editPortions.getText()).append(System.lineSeparator());
        sb.append(getResources().getText(R.string.portion_weight)).append(": ");
        sb.append(editWeightPortion.getText()).append(System.lineSeparator());
        sb.append(getResources().getText(R.string.hydration)).append(": ");
        sb.append(hydration).append('%').append(System.lineSeparator());
        sb.append(getResources().getText(R.string.flour)).append(": ");
        sb.append(editFlour.getText()).append(System.lineSeparator());
        sb.append(getResources().getText(R.string.water)).append(": ");
        sb.append(editWater.getText()).append(System.lineSeparator());
        sb.append(getResources().getText(R.string.yeast_grams)).append(": ");
        sb.append(editYeast.getText()).append(System.lineSeparator());
        sb.append(getResources().getText(R.string.salt_grams)).append(": ");
        sb.append(editSalt.getText()).append(System.lineSeparator());
        sb.append(getResources().getText(R.string.oil_grams)).append(": ");
        sb.append(editOil.getText());

        return sb;
    }

    public void buildAdView() {
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
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
    }

    // Helper function to disable inputs
    public void disableInputs() {
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
    public void saveFile(File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            Log.i(LOG_TAG, "Saving file " + file.getAbsolutePath());

            StringBuilder sb = writeRecipe();
            outputStream.write(sb.toString().getBytes());
            outputStream.close();

            Log.i(LOG_TAG, "File saved successfully");
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error saving file: " + e.getMessage());
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper function to call In App Rating
    public void launchInAppRating() {
        // Replace FakeReviewManager for testing
        // ReviewManager manager = new FakeReviewManager(this);
        ReviewManager manager = ReviewManagerFactory.create(MainActivity.this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(LOG_TAG, "requestReviewFlow: Success");
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.i(LOG_TAG, "launchReviewFlow: Success");
                    }
                });
            } else {
                @ReviewErrorCode int reviewErrorCode = ((ReviewException) task.getException()).getErrorCode();
                Log.e(LOG_TAG, "requestReviewFlow: Failed. Error Code: " + reviewErrorCode);
            }
        });
    }

    void connectGooglePlayBilling() {
        Log.i(LOG_TAG, "Starting connection to Google Play Billing service");
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(LOG_TAG, "Connected");
                    getProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i(LOG_TAG, "Reconnecting....");
                connectGooglePlayBilling();
            }
        });
    }

    void getProducts() {
        Log.i(LOG_TAG, "Querying for product details");
        BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS);
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.i(LOG_TAG, String.format("Feature not supported. %s", billingResult));
        } else {
            Log.i(LOG_TAG, String.format("Feature is supported. %s", billingResult));

            QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                    .setProductList(
                            ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("remove_ads_id")
                                            .setProductType(BillingClient.ProductType.INAPP)
                                            .build()))
                    .build();

            billingClient.queryProductDetailsAsync(
                    queryProductDetailsParams,
                    new ProductDetailsResponseListener() {
                        public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull QueryProductDetailsResult queryProductDetailsResult) {
                            // Process the result
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                for (ProductDetails productDetails : queryProductDetailsResult.getProductDetailsList()) {
                                    if (productDetails.getProductId().equals("remove_ads_id")) {
                                        launchPurchaseFlow(productDetails);
                                    }
                                }
                            }
                        }
                    }
            );
        }
    }
    void checkProducts() {

        PendingPurchasesParams pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build();

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .setListener((billingResult, list) -> {
        }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build(),
                            new PurchasesResponseListener() {
                                @Override
                                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                                    Log.i(LOG_TAG, "" + list);
                                    if (list.isEmpty()) {
                                        Log.i(LOG_TAG, "No product");
                                        preferences.setRemoveAd(0);
                                    } else {
                                        for (Purchase purchase : list) {
                                            if (purchase.getProducts().get(0).equals("remove_ads_id")) {
                                                Log.i(LOG_TAG, purchase.getProducts() + " Product found");
                                                preferences.setRemoveAd(1);
                                            }
                                        }
                                    }
                                }
                            }
                    );
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Empty
            }
        });
    }

    void launchPurchaseFlow(ProductDetails productDetails) {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
    }

    void verifyPayment(Purchase purchase) {
        Log.i(LOG_TAG, "Starting payment verification flow");
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // 1 - True - Remove ad
                            // 0 - False - Show ad
                            Log.i(LOG_TAG, "Marking item as purchased");
                            preferences.setRemoveAd(1);
                        }
                    }
                });
            }
        }
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (Purchase purchase : list) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                    verifyPayment(purchase);
                                }
                            }
                        }
                    }
                }
        );
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
