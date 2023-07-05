package com.popular.android.mibanco.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MediaScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection connection;

    private final Context context;

    private final String mimeType;

    private final String path;

    public MediaScannerClient(final String path, final String mimeType, final Context context) {
        this.path = path;
        this.mimeType = mimeType;
        this.context = context;
    }

    @Override
    public void onMediaScannerConnected() {
        connection.scanFile(path, mimeType);
    }

    @Override
    public void onScanCompleted(final String path, final Uri uri) {
        connection.disconnect();
    }

    public MediaScannerConnection getConnection() {
        return connection;
    }

    public void setConnection(MediaScannerConnection connection) {
        this.connection = connection;
    }
}
