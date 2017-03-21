package com.icc.cardiograph.http.cookie;

import android.util.Log;

import com.icc.cardiograph.http.cookie.store.CookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {

    private String FINMOBILE2_IN_URL = "finmobile2";
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null.");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        try {
            String tempUrl = url + "";
            Log.d("yypt", "[Cookie:saveFromResponse]==>url:" + tempUrl);
            String responseUrl = tempUrl.substring(0, tempUrl.indexOf(FINMOBILE2_IN_URL) + FINMOBILE2_IN_URL.length());
            HttpUrl httpUrl = HttpUrl.parse(responseUrl);
            Log.d("yypt", "[Cookie:saveFromResponse]==>cookies:" + cookies + " url:" + responseUrl);
            cookieStore.add(httpUrl, cookies);
        } catch (Exception e) {
            Log.d("yypt", "[Cookie:saveFromResponse:Exception]==>cookies:" + cookies + " url:" + url);
            cookieStore.add(url, cookies);
        }
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        try {
            String tempUrl = url + "";
            Log.d("yypt", "[Cookie:loadForRequest]==>url:" + tempUrl);
            String responseUrl = tempUrl.substring(0, tempUrl.indexOf(FINMOBILE2_IN_URL) + FINMOBILE2_IN_URL.length());
            HttpUrl httpUrl = HttpUrl.parse(responseUrl);
            Log.d("yypt", "[Cookie:loadForRequest]==>cookies:" + cookieStore.get(httpUrl) + " url:" + responseUrl);
            return cookieStore.get(httpUrl);
        } catch (Exception e) {
            Log.d("yypt", "[Cookie:loadForRequest:Exception]==>cookies:" + cookieStore.get(url) + " url:" + url);
            return  cookieStore.get(url);
        }
    }

    public void removeAll() {
        cookieStore.removeAll();
    }

}
