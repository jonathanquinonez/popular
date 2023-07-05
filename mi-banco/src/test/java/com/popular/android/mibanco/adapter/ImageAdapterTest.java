package com.popular.android.mibanco.adapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import com.popular.android.mibanco.activity.ImageAdapter;

@RunWith(MockitoJUnitRunner.class)
public class ImageAdapterTest {

    @Mock
    List<String> mUrlsForOnClickMock;

    @Test
    public void givenImageUrlList_whenAdapterCreated_thenListSet() {
        // Given
        List<String> expectedUrls = new ArrayList<>();
        expectedUrls.add("https://www.example1.com");
        expectedUrls.add("https://www.example2.com");
        expectedUrls.add("https://www.example3.com");

        // When
        ImageAdapter adapter = new ImageAdapter(expectedUrls, mUrlsForOnClickMock);
        int actualSize = adapter.getItemCount();

        // Then
        assertEquals(expectedUrls.size(), actualSize);
        assertEquals(expectedUrls.get(0), adapter.getImageUrl(0));
        assertEquals(expectedUrls.get(1), adapter.getImageUrl(1));
        assertEquals(expectedUrls.get(2), adapter.getImageUrl(2));
    }

    @Test
    public void givenImageUrlList_whenAdapterCreatedWithEmptyList_thenListEmpty() {
        // Given
        List<String> expectedUrls = new ArrayList<>();

        // When
        ImageAdapter adapter = new ImageAdapter(expectedUrls, mUrlsForOnClickMock);
        int actualSize = adapter.getItemCount();

        // Then
        assertEquals(expectedUrls.size(), actualSize);
    }
}






