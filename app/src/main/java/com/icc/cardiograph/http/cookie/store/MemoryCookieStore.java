package com.icc.cardiograph.http.cookie.store;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 储在内存中
 */
public class MemoryCookieStore implements CookieStore {

    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    @Override
    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = allCookies.get(url.host());
        if (oldCookies == null) {
            List<Cookie> newCookie = new ArrayList<>();
            allCookies.put(url.host(), cookies);
            return;
        }
        List<Integer> delTemp = new ArrayList<>();
        for (int i = 0; i < cookies.size(); i++) {
            String va = cookies.get(i).name();
            for (int j = oldCookies.size() - 1; j >= 0; j--) {
                String v = oldCookies.get(j).name();
                if (v != null && va.equals(v)) {
                    delTemp.add(j);
                }
            }
        }
        //冒泡排序，大的左移，防止remov的时候，空指针异常
        for (int i = 0; i < delTemp.size() - 1; i++) {
            for (int j = 0; j < delTemp.size() - 1 - i; j++) {
                if (delTemp.get(j) < delTemp.get(j + 1)) {
                    int temp = delTemp.get(j);
                    delTemp.set(j, delTemp.get(j + 1));
                    delTemp.set(j + 1, temp);
                }
            }
        }
        for (int i = 0; i< delTemp.size(); i++) {
            try {
                int index = delTemp.get(i);
                oldCookies.remove(index);
            } catch (Exception e) {
                Log.e("yypt", "MemoryCookieStore", e);
            }
        }
        oldCookies.addAll(cookies);
    }

    @Override
    public List<Cookie> get(HttpUrl uri) {
        List<Cookie> cookies = allCookies.get(uri.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(uri.host(), cookies);
        }
        return cookies;
    }

    @Override
    public boolean removeAll() {
        allCookies.clear();
        return true;
    }

    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = allCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(allCookies.get(url));
        }
        return cookies;
    }


    @Override
    public boolean remove(HttpUrl uri, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(uri);
        return cookie != null && cookies.remove(cookie);
    }

}
