package com.popular.android.mibanco.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.content.Context;

import com.popular.android.mibanco.App;
import com.popular.android.mibanco.listener.ResponderListener;
import com.popular.android.mibanco.task.AsyncTasks;
import com.popular.android.mibanco.util.BPAnalytics;
import com.popular.android.mibanco.util.CryptoUtils;
import com.popular.android.mibanco.util.PushUtils;
import com.popular.android.mibanco.util.RSACollectUtils;
import com.popular.android.mibanco.util.Utils;
import com.popular.android.mibanco.ws.response.BannerResponse;
import com.popular.android.mibanco.ws.response.Carousel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, AsyncTasks.class, Context.class, ResponderListener.class, RSACollectUtils.class, Utils.class, Activity.class, CryptoUtils.class, Cipher.class, KeyGenerator.class,
        PushUtils.class, BPAnalytics.class})
public class BannerResponseTest {

    private BannerResponse bannerResponse;

    @Test
    public void testGettersAndSetters() {
        BannerResponse bannerResponse = new BannerResponse();
        List<Carousel> carouselList = new ArrayList<>();
        carouselList.add(new Carousel("Image1", "ActionUrl", "ImageRetinaUrl"));
        carouselList.add(new Carousel("Image2", "ActionUrl", "ImageRetinaUrl"));

        bannerResponse.setImage_retina_url("ImageRetinaUrl");
        bannerResponse.setAction_url("ActionUrl");
        bannerResponse.setImage_url("ImageUrl");
        bannerResponse.setCarousel(carouselList);

        assertEquals("ImageRetinaUrl", bannerResponse.getImage_retina_url());
        assertEquals("ActionUrl", bannerResponse.getAction_url());
        assertEquals("ImageUrl", bannerResponse.getImage_url());
        assertEquals(carouselList, bannerResponse.getCarousel());
    }

    @Test
    public void testSetTitle() {
        BannerResponse bannerResponse = new BannerResponse();
        List<Carousel> carouselList = new ArrayList<>();
        carouselList.add(new Carousel("Image1", "ActionUrl1", "ImageRetinaUrl"));
        carouselList.add(new Carousel("Image2", "ActionUrl2", "ImageRetinaUrl"));
        bannerResponse.setCarousel(carouselList);

        for (Carousel carousel : bannerResponse.getCarousel()) {
            carousel.setAction_url("ActionUrl");
        }

        assertEquals("ActionUrl", bannerResponse.getCarousel().get(0).getAction_url());
        assertEquals("ActionUrl", bannerResponse.getCarousel().get(1).getAction_url());
    }

}


