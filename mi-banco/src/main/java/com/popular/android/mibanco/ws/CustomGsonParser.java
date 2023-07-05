package com.popular.android.mibanco.ws;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.popular.android.mibanco.model.BaseFormResponse;

public class CustomGsonParser {

    private final Gson gson;

    public CustomGsonParser() {
        gson = new Gson();
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        T result = gson.fromJson(json, classOfT);
        if (result != null && result instanceof BaseFormResponse) {
            ((BaseFormResponse) result).setResponseBody(json);
        }
        return result;
    }
}
