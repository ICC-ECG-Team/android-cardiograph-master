package com.icc.cardiograph.http.interceptor;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Http请求/响应日志处理
 */
public final class HttpLoggerInterceptor implements Interceptor {

    private final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p/>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p/>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p/>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END GET
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p/>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Hi?
         * --> END GET
         *
         * <-- 200 OK (22ms)
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY_SIMPLE
    }

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpLoggerInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        StringBuffer reqSb = new StringBuffer();
        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = (level == Level.BODY || level == Level.BODY_SIMPLE);
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
//        logger.log(requestStartMessage);
        reqSb.append(requestStartMessage + "\n");
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    reqSb.append("Content-Type: " + requestBody.contentType() + "\n");
                }
                if (requestBody.contentLength() != -1) {
                    reqSb.append("Content-Length: " + requestBody.contentLength() + "\n");
                }
            }

            Headers headers = request.headers();
            if (level != Level.BODY_SIMPLE) {
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                        reqSb.append(name + ": " + headers.value(i) + "\n");
                    }
                }
            }

            if (!logBody || !hasRequestBody) {
                reqSb.append("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                reqSb.append("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) {
                    reqSb.append(buffer.readString(charset) + "\n");
                    reqSb.append("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    reqSb.append("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }
        Log.d("EasyRepo", reqSb.toString());
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
//        logger.log("<-- " + response.code() + ' ' + response.message() + ' '
//                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
//                + bodySize + " body" : "") + ')');

        StringBuffer resSb = new StringBuffer();
        resSb.append("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')' + "\n");

        if (logHeaders) {
            Headers headers = response.headers();
            if (level != Level.BODY_SIMPLE) {
                for (int i = 0, count = headers.size(); i < count; i++) {
                    resSb.append(headers.name(i) + ": " + headers.value(i) + "\n");
                }
            }

            if (!logBody || !HttpEngine.hasBody(response)) {
                resSb.append("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                resSb.append("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        resSb.append("Couldn't decode the response body; charset is likely malformed." + "\n");
                        resSb.append("<-- END HTTP");
                        Log.e("EasyRepo", resSb.toString());
                        return response;
                    }
                }

//                if (!isPlaintext(buffer)) {
//                    resSb.append("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
//                    com.orhanobut.logger.Logger.e(resSb.toString());
//                    return response;
//                }

                if (contentLength != 0) {
//                    logger.log(buffer.clone().readString(charset));
                    resSb.append(buffer.clone().readString(charset) + "\n");
                }

                resSb.append("<-- END HTTP (" + buffer.size() + "-byte body)");
                Log.d("EasyRepo", resSb.toString());
            }
        }

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                if (Character.isISOControl(prefix.readUtf8CodePoint())) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}