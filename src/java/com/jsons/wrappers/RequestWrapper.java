package com.jsons.wrappers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Subhankar
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final static JsonParser jsonParser = new JsonParser();

    private JsonObject jsonObject;

    public RequestWrapper(ServletRequest request) {
        this((HttpServletRequest) request);
    }

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        {
            StringBuilder sb = new StringBuilder();
            BufferedReader br;
            try {
                br = request.getReader();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                jsonObject = jsonParser.parse(sb.toString()).getAsJsonObject();
            } catch (Exception ex) {
                jsonObject = null;
//                Logger.getLogger(RequestWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isJsonRequest() {
        return null != jsonObject;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public String getParameter(String prop) {
        return getParameter(prop, "");
    }

    public String getParameter(String prop, String nullValue) {
        String value;
        if (null == jsonObject || !jsonObject.has(prop)) {
            value = super.getParameter(prop) != null ? super.getParameter(prop) : nullValue;
        } else {
            value = jsonObject.get(prop) != null ? jsonObject.get(prop).getAsString() : nullValue;
        }
        return value;
    }
}
