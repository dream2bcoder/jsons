package com.jsons.controller;

import com.jsons.data.Response;
import com.jsons.wrappers.RequestWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
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
    Gson json = new Gson();
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
                                writeToUrl("http://localhost:8084" + request.getContextPath() + "/config/config.json", request.getJsonObject().toString());
                                dataResponse.data = "success";
                            } catch (Exception ex) {
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
                            dataResponse.data = json.fromJson(readUrl("http://localhost:8084" + request.getContextPath() + "/config/config.json"), new TypeToken<List<Map>>(){}.getType());
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

    private static boolean writeToUrl(String urlString, String data) throws Exception {
        BufferedWriter w = null;
        try {
            URL url = new URL(urlString);
            w = new BufferedWriter(new OutputStreamWriter(url.openConnection().getOutputStream()));
            w.write(data, 0, data.length());
            return true;
        } finally {
            if (w != null) {
                w.close();
            }
        }
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
