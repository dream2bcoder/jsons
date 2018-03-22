package com.jsons.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 *
 * @author Subhankar
 */
@SuppressWarnings("unused")
public class Response {

    public Object data = null;
    private Object error = null;

    private final Gson gson;
    private final Type type;

    public Response() {
        gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .serializeNulls()
                .create();
        
        type = new TypeToken<Response>() {
        }.getType();
    }
    
    public void setError(Error error) {
        this.error = error;
    }
    
    public void setError(int errno, String errMsg) {
        this.error = new Error(errno, errMsg);
    }

    @Override
    public String toString() {
        return gson.toJson(this, type);
    }
}
