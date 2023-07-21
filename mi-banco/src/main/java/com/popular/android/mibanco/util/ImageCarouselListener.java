package com.popular.android.mibanco.util;

import com.popular.android.mibanco.ws.response.BannerResponse;

public interface ImageCarouselListener {
    default void setImageCarousel(BannerResponse response) {
    }
}
