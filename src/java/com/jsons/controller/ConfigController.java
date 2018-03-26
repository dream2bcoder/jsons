package com.jsons.controller;

import com.jsons.data.Response;
import com.jsons.wrappers.RequestWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigController extends HttpServlet {

    private static final String SAVE_CONFIG = "save_config";
    private static final String FETCH_CONFIG = "fetch_config";
    private static final String CONFIG_UID = "config_uid";

    String uid = "";
    RequestWrapper request;
    Gson json = new GsonBuilder().setPrettyPrinting().create();
    JsonObject jsonObject;
    Response dataResponse;

    protected void processRequest(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        request = new RequestWrapper(req);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            switch (request.getParameter("m")) {
                case SAVE_CONFIG:
                    if (!(uid = request.getParameter(CONFIG_UID)).isEmpty()) {
                        if (request.isJsonRequest()) {
                            dataResponse = new Response();
                            try {
                                JsonWriter writer = json.newJsonWriter(getWriter("/config/config.json"));
                                json.toJson(request.getJsonObject().getAsJsonArray("layout"), writer);
                                writer.flush();
                                writer.close();
                                dataResponse.data = "success";
                            } catch (JsonIOException | IOException ex) {
                                dataResponse.setError(-100, ex.getLocalizedMessage());
                            }
                            out.print(dataResponse);
                            out.flush();
                            return;
                        }
                    }
                    break;
                case FETCH_CONFIG:
                    if (!(uid = request.getParameter(CONFIG_UID)).isEmpty()) {
                        dataResponse = new Response();
                        try {
                            dataResponse.data = json.fromJson(readUrl(getHost(request) + request.getContextPath() + "/config/config.json"), new TypeToken<List<Map>>(){}.getType());
                        } catch (Exception ex) {
                            dataResponse.setError(-100, ex.getLocalizedMessage());
                        }
                        out.print(dataResponse);
                        out.flush();
                        return;
                    }
                    break;
                default:
            }
            nullify();
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    private void nullify() {
        request = null;
        jsonObject = null;
        dataResponse = null;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private PrintWriter getWriter(String urlString) throws MalformedURLException, IOException {
            return new PrintWriter(new File(getServletContext().getRealPath(urlString)));
    }
    
    private String getHost(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url.substring(0, url.indexOf(request.getContextPath()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
