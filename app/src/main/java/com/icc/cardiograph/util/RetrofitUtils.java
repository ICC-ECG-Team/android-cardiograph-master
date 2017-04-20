package com.icc.cardiograph.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.icc.cardiograph.BuildConfig;
import com.icc.cardiograph.base.ApplicationEx;
import com.icc.cardiograph.http.NoSSLv3SocketFactory;
import com.icc.cardiograph.http.cookie.CookieJarImpl;
import com.icc.cardiograph.http.cookie.store.MemoryCookieStore;
import com.icc.cardiograph.http.interceptor.HeaderInterceptor;
import com.icc.cardiograph.http.interceptor.HttpLoggerInterceptor;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Retrofit请求工具类
 */
public class RetrofitUtils {

    public final String TAG = RetrofitUtils.class.getName();
    public final HashMap<String, String> cookieLocal = new HashMap<>();
    private final int DEFAULT_TIMEOUT = 20;

    private OkHttpClient mHttpClient;
    private OkHttpClient.Builder httpClientBuilder;

    /**
     * 访问RetrofitUtils时创建单例
     */
    private static class SingletonHolder {
        private static final RetrofitUtils INSTANCE = new RetrofitUtils();
    }

    /**
     * 获取单例
     * @return RetrofitUtils单例
     */
    public static RetrofitUtils getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     * 构造方法私有
     */
    private RetrofitUtils() {
        initHttpClient();
    }

    /**
     * 初始化OKHttpClient
     * 手动创建一个OkHttpClient并设置超时时间
     */
    private void initHttpClient() {
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.readTimeout(DEFAULT_TIMEOUT * 2, TimeUnit.SECONDS);
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        // Cookie支持（其中，persCookieJar保存至本地，memCookieJar存储于内存）
        // 鉴于cookie中包含session等敏感信息，项目中选用内存存储
        // CookieJarImpl persCookieJar = new CookieJarImpl(new PersistentCookieStore(GlobalApplicationContext.instances.getApplicationContext()));
        CookieJarImpl memCookieJar = new CookieJarImpl(new MemoryCookieStore());
        httpClientBuilder.cookieJar(memCookieJar);

        // 添加header拦截器
        httpClientBuilder.addInterceptor(new HeaderInterceptor());
        // 添加log拦截器
        if (BuildConfig.DEBUG_OKHTTP) {
            HttpLoggerInterceptor loggerInterceptor = new HttpLoggerInterceptor();
            loggerInterceptor.setLevel(HttpLoggerInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggerInterceptor);
        }

        if (!BuildConfig.PROD_FLAG) {
            if (BuildConfig.URL_SERVER.startsWith("https")) {
                trustAllHosts();
            }
        } else {
            if (BuildConfig.URL_SERVER.startsWith("https")) {
                fixAndroidSSLv3SysBug();
            }
        }
        mHttpClient = httpClientBuilder.build();
    }

    /**
     * 内网：忽略Https安全认证
     */
    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            httpClientBuilder.sslSocketFactory(sslSocketFactory);
            httpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e("qdzg", TAG, e);
        }
    }

    /**
     * 公网：支持https
     */
    private void fixAndroidSSLv3SysBug() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, null, null);
            SSLSocketFactory socketFactory = new NoSSLv3SocketFactory(sslContext.getSocketFactory());
            httpClientBuilder.sslSocketFactory(socketFactory);
        } catch (Exception e) {
            Log.e("qdzg", TAG, e);
        }
    }

    public Retrofit createRetrofit() {
        RetrofitUtils.getInstance().syncCookieFromWebView(ApplicationEx.getInstance(), BuildConfig.URL_SERVER);
        return new Retrofit.Builder()
                .client(mHttpClient)
                .baseUrl(BuildConfig.URL_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

//    public Retrofit createCustomRetrofit() {
//        RetrofitUtils.getInstance().syncCookieFromWebView(ApplicationEx.getInstance(), BuildConfig.URL_SERVER);
//        return new Retrofit.Builder()
//                .client(mHttpClient)
//                .baseUrl(BuildConfig.URL_SERVER)
//                .addConverterFactory(new CustomConverterFactory())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//    }

    public <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }

    /**
     * 新建线程进行网络请求，UI线程刷新页面
     */
    private final Observable.Transformer schedulersTransformer = new Observable.Transformer() {

        @Override
        public Object call(Object observable) {
            return ((Observable)observable).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * 从WebView同步Cookie到当前
     *
     * @param context 上下文环境
     * @param baseUrl 地址
     */
    public void syncCookieFromWebView(Context context, String baseUrl) {
        CookieJar cookieJar = mHttpClient.cookieJar();
        HttpUrl httpUrl = HttpUrl.parse(baseUrl);
        List<Cookie> cookies = getCookieFromWebView(context, baseUrl, httpUrl);
        cookieJar.saveFromResponse(httpUrl, cookies);
    }

    private List<Cookie> getCookieFromWebView(Context context, String url, HttpUrl httpUrl) {
        List<Cookie> cookies = new ArrayList<>();
        CookieSyncManager csm = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        csm.startSync();
        String allCookies = cookieManager.getCookie(url);
        Log.d("yypt", url + ";allCookies:val:" + allCookies);
        if (allCookies == null || allCookies.length() == 0) {
            return cookies;
        }
        String[] keypairs = allCookies.split(";");
        for (String keypair : keypairs) {
//            Log.d("yypt", "getCookieFromWebView:url" + url);
//            Log.d("yypt", "getCookieFromWebView:" + keypair);
            Cookie cookie = Cookie.parse(httpUrl, keypair);
            cookies.add(cookie);
        }
        csm.sync();
        return cookies;
    }

    /**
     * 同步http请求的cookie到webview
     *
     * @param url     地址
     */
    public void syncCookieToWebView(Context context, String url) {
        CookieJar cookieJar = mHttpClient.cookieJar();
        HttpUrl httpUrl = HttpUrl.parse(url);
        List<Cookie> cookies = cookieJar.loadForRequest(httpUrl);
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for (Cookie cookie : cookies) {
            String strCookies = cookie.toString();
            Log.d("yypt", "syncCookieToWebView:url" + url);
            Log.d("yypt", "syncCookieToWebView:" + strCookies);
            cookieManager.setCookie(url, strCookies);
        }
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
    }

    /**
     * 清除浏览器的Cookie缓存
     *
     * @param context 上下文
     */
    public void clearCookie(Context context) {
        CookieSyncManager.createInstance(context.getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
        CookieJarImpl cookieJar = (CookieJarImpl) mHttpClient.cookieJar();
        cookieJar.removeAll();
    }

    public void setTokenCookie(Context context, String token) {
        if (token == null || "".equals(token)) {
            clearCookie(context);
            return;
        }
        CookieSyncManager csm = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        csm.startSync();
        String val = "token=" + token + ";path=" + "/icc";
        Log.d("qdzg4", ">>>setTokenCookie:" + val);
        cookieManager.setCookie(BuildConfig.URL_SERVER, val);
        csm.sync();
    }

    /**
     * trust>>HttpsURLConnection>>Host
     */
    public void trustHttpsURLConnectionHosts() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(RetrofitUtils.DO_NOT_VERIFY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

}
