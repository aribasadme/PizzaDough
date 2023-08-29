package net.ddns.aribas.pizzadough;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    BillingClient billingClient;
    Preferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        preferences = new Preferences(this);

        checkProducts();

        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }

    void checkProducts() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // Empty
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> {
                        Log.d("TestRed", "" + list);
                        if (list.size() == 0) {
                            Log.d("TestRed", "No product");
                            preferences.setRemoveAd(0);
                        } else {
                            for (Purchase purchase : list) {
                                if (purchase.getSkus().get(0).equals("remove_ads_id")) {
                                    Log.d("TestRed", "" + purchase.getSkus() + " Product found");
                                    preferences.setRemoveAd(1);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
