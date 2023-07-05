package com.popular.android.mibanco.util;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class MediaScannerClientUT {

    private static final String PATH = "test.mp3";
    private static final String MIME_TYPE = "audio/mp3";

    @Mock
    private MediaScannerConnection mediaScannerConnection;

    @Mock
    private Context context;

    @Mock
    private Uri uri;

    @InjectMocks
    private MediaScannerClient mediaScannerClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mediaScannerClient = new MediaScannerClient(PATH, MIME_TYPE, context);
        mediaScannerClient.setConnection(mediaScannerConnection);
    }

    @Test
    public void whenCallMediaScannerConnected_GivenMediaScannerClient_ThenScanFile() {
        mediaScannerClient.onMediaScannerConnected();
        verify(mediaScannerConnection, times(1)).scanFile(PATH, MIME_TYPE);
    }

    @Test
    public void whenCallScanCompleted_GivenMediaScannerClient_ThenDisconnect() {
        mediaScannerClient.onScanCompleted(PATH, uri);
        verify(mediaScannerConnection, times(1)).disconnect();
    }

    @Test
    public void whenGetConnection_GivenMediaScannerConnection_ThenReturnConnection() {
        MediaScannerConnection connection = mediaScannerClient.getConnection();
        assert(connection != null);
        assert(connection == mediaScannerConnection);
    }

}