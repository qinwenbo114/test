package com.acs.demo;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class MyHttp  
{  
    public String httpGet(String url)  
    {  
        String response = null;  
        HttpClient httpClient = new DefaultHttpClient();  
        //创建HttpGet对象   
        HttpGet httpGet = new HttpGet(url);  
        HttpResponse httpResponse;  
        try  
        {  
            //使用execute方法发送 HttpGet请求，并返回httRresponse对象   
            httpResponse = httpClient.execute(httpGet);  
            int statusCode = httpResponse.getStatusLine().getStatusCode();  
            if(statusCode==HttpStatus.SC_OK)  
            {  
                //获得返回结果   
                response=EntityUtils.toString(httpResponse.getEntity());  
            }  
              
        } catch (ClientProtocolException e)  
        {  
              
            e.printStackTrace();  
        } catch (IOException e)  
        {  
              
            e.printStackTrace();  
        }  
          
        return response;  
    }  
}  

