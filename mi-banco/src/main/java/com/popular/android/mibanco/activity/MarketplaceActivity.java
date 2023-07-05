package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.popular.android.mibanco.App;
import com.popular.android.mibanco.MiBancoConstants;
import com.popular.android.mibanco.R;
import com.popular.android.mibanco.adapter.MarketplaceAdapter;
import com.popular.android.mibanco.base.BaseActivity;
import com.popular.android.mibanco.model.MarketPlaceEnum;
import com.popular.android.mibanco.model.MarketplaceCard;
import com.popular.android.mibanco.model.MarketplaceImages;
import com.popular.android.mibanco.util.MarketplaceUtils;
import com.popular.android.mibanco.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.popular.android.mibanco.MiBancoConstants.POPULAR_MARKETPLACE_IMAGES_API;
import static com.popular.android.mibanco.model.MarketPlaceEnum.Products.marketplace_credit_card_pr;

/**
 * Activity that manages the selection of a card
 */
public class MarketplaceActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    MarketplaceAdapter marketplaceAdapter;
    List<MarketplaceCard> productCards;
    private boolean hasCreditCard;
    private boolean hasEAccount;
    List<MarketplaceImages> list;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_layout);
        getIntentExtras();
        listView = findViewById(R.id.marketplacelistViewCards);

        productCards = new ArrayList<MarketplaceCard>();

        setMarketplaceProducts();

        marketplaceAdapter = new MarketplaceAdapter(this, productCards);
        listView.setAdapter(marketplaceAdapter);
        listView.setOnItemClickListener(this);

        setProductImages();
    }

    /**
     * setMarketplaceProducts
     *
     */
    private void setMarketplaceProducts() {
        if (hasCreditCard) { //Marketplace
            if (application.getLoggedInUser().getHasVIAccount()) {
                MarketplaceCard item = new MarketplaceCard(MarketPlaceEnum.Products.marketplace_credit_card_usvi.name(), getString(R.string.marketplace_credit_card_title)
                        , getString(R.string.marketplace_credit_card_subtitle), getString(R.string.marketplace_credit_card_button),"",null);
                productCards.add(item);
            } else {

                MarketplaceCard item = new MarketplaceCard(marketplace_credit_card_pr.name(), getString(R.string.marketplace_credit_card_title)
                        , getString(R.string.marketplace_credit_card_subtitle), getString(R.string.marketplace_credit_card_button),"",null);
                productCards.add(item);
            }
        }

        if (hasEAccount) { //e-aacount
            if (application.getLoggedInUser().getHasVIAccount()) {// isVi
                MarketplaceCard item = new MarketplaceCard(MarketPlaceEnum.Products.marketplace_eaccount_usvi.name(), getString(R.string.marketplace_eaccount_title)
                        , getString(R.string.marketplace_eaccount_usvi_subtitle), getString(R.string.marketplace_eaccount_button),"",null);
                productCards.add(item);
            } else {
                MarketplaceCard item = new MarketplaceCard(MarketPlaceEnum.Products.marketplace_eaccount_pr.name(), getString(R.string.marketplace_eaccount_title)
                        , getString(R.string.marketplace_eaccount_subtitle), getString(R.string.marketplace_eaccount_button),"",null);
                productCards.add(item);
            }
        }
    }

    void setProductImages(){

        boolean willUpdateImages = false;
        File file;

        for (MarketplaceCard card  : productCards) {
            final String identifier = card.getType();
            file = new File(getImageCachePath(identifier));

            if (hasImageDateExpired(file)){
                willUpdateImages = true;
            } else {
                Bitmap fileBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                card.setImageBitmap(fileBitmap);
            }
        }

        if(willUpdateImages){
            new getMarketplaceImages().execute(POPULAR_MARKETPLACE_IMAGES_API);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getAdapter().getItem(position) instanceof MarketplaceCard) {
            MarketplaceCard item = (MarketplaceCard) parent.getAdapter().getItem(position);

            startProductActivity(item.getType());
        }
    }

    private void startProductActivity(String productName) {
        if (marketplace_credit_card_pr.name().equals(productName) || MarketPlaceEnum.Products.marketplace_credit_card_usvi.name().equals(productName) ) {
            final Intent intentWebView = new Intent(this, WebViewActivity.class); //Setup new Intent for WebViewActivity

            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_KEY, application.getLoggedInUser().getHasVIAccount() ?
                    getString(R.string.marketplace_vi_url) : getString(R.string.marketplace_pr_url));

            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_URL_BLACKLIST_KEY, Utils.urlBlacklist(MarketplaceActivity.this));
            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_HIDE_NAVIGATION_KEY, true);
            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_SYNC_COOKIES_KEY, true);
            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_CAN_BACK, true);
            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE, true);
            intentWebView.putExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_CCA, true);
            startActivityForResult(intentWebView, MiBancoConstants.MARKETPLACE_WEBVIEW_REQUEST_CODE);
        } else if (MarketPlaceEnum.Products.marketplace_eaccount_pr.name().equals(productName) ||  MarketPlaceEnum.Products.marketplace_eaccount_usvi.name().equals(productName)) {
            startActivity(new Intent(this, OpenAccount.class));
        }
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        hasCreditCard = intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_CCA, Boolean.FALSE);
        hasEAccount = intent.getBooleanExtra(MiBancoConstants.WEB_VIEW_MARKETPLACE_EACCOUNT, Boolean.FALSE);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_locator).setVisible(false);
        menu.findItem(R.id.menu_contact).setVisible(false);
        return true;
    }

    private void fetchDefaultImage(String url, String identifier) {
        fetchWelcomeImageAsync(Utils.stripUrlQueryParameters(url), identifier);
    }

    private boolean hasImageDateExpired(File file){
        final Date currDate = Calendar.getInstance().getTime();
        return !file.exists() || file.lastModified() < currDate.getTime() - MiBancoConstants.WEEK_MILLIS;//30000
    }

    private void fetchWelcomeImageAsync(final String imageUrl, final String identifier) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final File imageFile = new File(getImageCachePath(identifier));
                if (hasImageDateExpired(imageFile)) {
                    //Reset image when expired
                    if (imageFile.exists()){
                        imageFile.delete();
                    }
                    ImageLoader.getInstance().loadImage(imageUrl, App.getApplicationInstance().getDefaultOptionsNoDiskCache(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            for(int y = 0; y < productCards.size(); y++){
                                MarketplaceCard card = productCards.get(y);
                                if (card.getType().equals(identifier)){
                                    card.setImageBitmap(loadedImage);
                                    //Save for local cache
                                    App.getApplicationInstance().getAsyncTasksManager().new SaveImageTask().execute(loadedImage, getImageCachePath(identifier));
                                    break;
                                }
                            }
                            marketplaceAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            //Do nothing
                        }
                    });
                }
            }
        });
    }

    private class getMarketplaceImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream;
            try {
                java.net.URL url = new URL(params[0]);
                HttpURLConnection connection = null;

                if( url.openConnection() instanceof  HttpURLConnection){
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                }
                inputStream = connection.getInputStream();
                String result = convertInputStreamToString(inputStream);

                try {
                    JSONObject jsonObject;
                    jsonObject = new JSONObject(result);
                    JSONArray products = jsonObject.getJSONArray("products");
                    String stringProducts = String.valueOf(products);
                    final GsonBuilder gsonBuilder = new GsonBuilder();
                    final Gson gson = gsonBuilder.create();

                    list = Arrays.asList(gson.fromJson(stringProducts, MarketplaceImages[].class));
                    addUrlImageToObjectCards();

                } catch (JSONException e) {
                    Log.w("MarketplaceActivity", e);
                }
            } catch (IOException e) {
                Log.w("MarketplaceActivity", e);
            }
            return "";
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        inputStream.close();
        return result.toString();
    }

    private void addUrlImageToObjectCards(){

        for(int x = 0; x < list.size();x++){
            for(int y = 0; y < productCards.size(); y++){
                MarketplaceCard card = productCards.get(y);
                if (card.getType().equals(list.get(x).getId())){
                    card.setUrlImage(list.get(x).getUrlImage());
                    fetchDefaultImage(card.getUrlImage(), card.getType());
                }
            }
        }
    }

    /**
     * Get Cache path by Image ID
     * @param identifier
     * @return String with cache path
     */
    private String getImageCachePath(String identifier) {
        return StringUtils.isEmpty(MarketplaceUtils.getIdImageCache(identifier)) ? StringUtils.EMPTY :
                application.getImageCachePath().getPath() + MarketplaceUtils.getIdImageCache(identifier);
    }

}


