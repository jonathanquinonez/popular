package com.popular.android.mibanco.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.popular.android.mibanco.R;
import com.popular.android.mibanco.base.BaseSessionActivity;

/**
 * Review Checks Activity class.
 */
public class RDCHistoryImageZoom extends BaseSessionActivity {

    public static final String IMAGE_PATH = "imagePath";

    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rdc_history_check_image_zoom);

        ImageView imageView = (ImageView) findViewById(R.id.check_image);
        final Intent intent = getIntent();
        String imagePath = intent.getStringExtra(IMAGE_PATH);
        if (imagePath != null) {
            imageUri = Uri.parse(imagePath);
            imageView.setImageURI(imageUri);
        }

        // Close if we click the image
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
