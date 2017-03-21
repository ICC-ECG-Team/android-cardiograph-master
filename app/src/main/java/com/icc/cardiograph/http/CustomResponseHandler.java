package com.icc.cardiograph.http;

import rx.functions.Func1;

/**
 * Description: RxJava中map操作符接收参数Func1<I,O>，其中，I为输入数据，O为输出数据
 * 实现Func1的call方法对I类型数据进行处理后返回O类型数据，即Subscriber需要的数据类型
 *
 * @param <T> - 响应中data部分的数据类型
 *            Author: liguosun
 *            Produced: 16/7/21 下午7:20
 */
public class CustomResponseHandler<T> implements Func1<CustomHttpResponse<T>, T> {

    @Override
    public T call(CustomHttpResponse<T> httpResponse) {
        // 请求成功返回data部分数据
        return httpResponse.getData();
    }
}
