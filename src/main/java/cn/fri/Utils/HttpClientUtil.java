package cn.fri.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String doGet(String url, Map<String, String> param) {
        return doGet(url, param , null);
    }

    public static String doGet(String url, Map<String, String> param , Map<String , String> headers) {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            //增加header
            if (headers != null) {
                for (String key : headers.keySet()) {
                    httpGet.addHeader(key, headers.get(key));
                }
            }

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("[doGet]url=" + url + " , param:\n" +param.toString() + "\nresultString=" + resultString);
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }


    public static String doPost(String url, Map<String, String> param, Map<String, String> headerParam){
        return doPost(url,param,headerParam,null) ;
    }

    public static String doPost(String url, Map<String, String> param, Map<String, String> headerParam,RequestConfig requestConfig){
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            httpPost.addHeader( new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));

            if(headerParam != null && headerParam.size()>0){
                for (String key : headerParam.keySet()) {
                    httpPost.addHeader(key, headerParam.get(key));
                }
            }


            if(requestConfig != null){
                httpPost.setConfig(requestConfig);
            }


            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            if(response.getStatusLine()  != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY){
                Header location = response.getFirstHeader("Location");
                if(location != null){
                    logger.warn("location=" + location.getValue());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if(response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("[POST]url=" + url + " , param:\n" + param.toString() + "\nresult=" + resultString);
        return resultString;
    }
    public static String doPost(String url, Map<String, String> param) {
        return doPost(url, param, null);
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json) {
        return doPostJson(url, json, null);
    }

    public static String doPostJson(String url, String json, Map<String, String> headerParam) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            if(headerParam != null && headerParam.size()>0){
                for (String key : headerParam.keySet()) {
                    httpPost.addHeader(key, headerParam.get(key));
                }
            }
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }


    public static  <T> T  httpPostXmlObject(String url, Map<String, String> headMap,
                                            String jsonStr,Class<T> classes) {
        CloseableHttpResponse response = null;
        T result = null;
        try {
            String responseContent =  httpPostJson(url,headMap,jsonStr,MediaType.APPLICATION_XML_VALUE);
            result  =  JSONObject.parseObject(responseContent, classes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static  <T> T  httpPostJsonObject(String url, Map<String, String> headMap,
                                             Object params,Class<T> classes) {
        CloseableHttpResponse response = null;
        T result = null;
        try {
            String jsonStr = JSON.toJSONString(params);
            String responseContent =  httpPostJson(url,headMap,jsonStr,MediaType.APPLICATION_JSON_VALUE);
            result  =  JSONObject.parseObject(responseContent, classes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * http的post请求
     *
     * @param url
     * @param headMap
     * @param jsonStr
     * @return
     */
    public static String httpPostJson(String url, Map<String, String> headMap,
                                      String jsonStr,String contentType) {
        String responseContent = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            setPostHead(httpPost, headMap);
            StringEntity entity = new StringEntity(jsonStr, ("UTF-8"));
            entity.setContentType(contentType);
            httpPost.setEntity(entity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
            responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    /**
     * 设置http的HEAD
     *
     * @param httpPost
     * @param headMap
     */
    private  static void setPostHead(HttpPost httpPost, Map<String, String> headMap) {
        if (headMap != null && headMap.size() > 0) {
            Set<String> keySet = headMap.keySet();
            for (String key : keySet) {
                httpPost.addHeader(key, headMap.get(key));
            }
        }
    }


    /**
     * 原生字符串发送put请求
     *
     * @param url
     * @param jsonStr
     * @return
     * @throws IOException
     */
    public static String doPutJson(String url, String jsonStr) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000).setConnectionRequestTimeout(35000).setSocketTimeout(60000).build();
        httpPut.setConfig(requestConfig);
        httpPut.setHeader("Content-type", "application/json;charset=utf-8");
        httpPut.setHeader("DataEncoding", "UTF-8");

        CloseableHttpResponse httpResponse = null;
        try {
            httpPut.setEntity(new StringEntity(jsonStr, ("UTF-8")));
            httpResponse = httpClient.execute(httpPut);
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity,"UTF-8");
            return result;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 发送delete请求
     *
     * @param url
     * @param jsonStr
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doDeleteJson(String url, String jsonStr) {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000).setConnectionRequestTimeout(35000).setSocketTimeout(60000).build();
        httpDelete.setConfig(requestConfig);
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("DataEncoding", "UTF-8");

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpDelete);
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity,"UTF-8");
            return result;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}