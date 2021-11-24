package com.robot.pool;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpClientUtils {

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=UTF-8";

    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    public static HttpClientResult doGet( String url ) {
        return doGet(url, null);
    }

    public static HttpClientResult doGet( String url, Map<String, String> params, Header... headers ) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, Charsets.UTF_8));
            }
            HttpGet httpGet = new HttpGet(url);
            if (null != headers && headers.length > 0) {
                httpGet.setHeaders(headers);
            }
            return getHttpClientResult(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpClientResult doPost( String url ) {
        return doPost(url, null);
    }

    public static HttpClientResult doPost( String url, Map<String, String> params, Header... headers ) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (null != headers && headers.length > 0) {
                httpPost.setHeaders(headers);
            }
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, Charsets.UTF_8));
            }
            return getHttpClientResult(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static HttpClientResult doPostJson( String url,String content, Header... headers ) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            StringEntity se = new StringEntity(content,"utf-8");
            se.setContentEncoding("UTF-8");
            se.setContentType(CONTENT_TYPE_JSON);
            HttpPost httpPost = new HttpPost(url);
            if (null != headers && headers.length > 0) {
                httpPost.setHeaders(headers);
            }
            if (se != null ) {
                httpPost.setEntity(se);
            }
            return getHttpClientResult(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpClientResult doPostJson( String url,String content, Set<Header> headers ) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            StringEntity se = new StringEntity(content,"utf-8");
            se.setContentEncoding("UTF-8");
            se.setContentType(CONTENT_TYPE_JSON);
            HttpPost httpPost = new HttpPost(url);
            if (null != headers && headers.size() > 0) {
                for (Header header:headers) {
                    httpPost.setHeader(header);
                }
            }
            if (se != null ) {
                httpPost.setEntity(se);
            }
            return getHttpClientResult(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取code=200的结果
     * @param request
     * @return
     * @throws IOException
     */
    private static String submit( HttpUriRequest request ) throws IOException {
        CloseableHttpResponse response = HttpClientPool.DEFAULT.fetchHttpClient()
            .execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            request.abort();
            throw new RuntimeException("HttpClient,error status code :" + statusCode);
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            result = EntityUtils.toString(entity, Charsets.UTF_8);
        }
        EntityUtils.consume(entity);
        response.close();
        return result;
    }

    /**
     * 获取整个response
     * @param request
     * @return
     * @throws Exception
     */
    public static HttpClientResult getHttpClientResult(HttpUriRequest request) throws Exception {
        // 执行请求
        CloseableHttpResponse httpResponse = HttpClientPool.DEFAULT.fetchHttpClient()
                .execute(request);
        // 获取返回结果
        if (httpResponse != null && httpResponse.getStatusLine() != null) {
            String content = "";
            if (httpResponse.getEntity() != null) {
                content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            }
            return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(), content);
        }
        return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
