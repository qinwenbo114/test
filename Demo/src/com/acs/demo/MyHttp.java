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
        //����HttpGet����   
        HttpGet httpGet = new HttpGet(url);  
        HttpResponse httpResponse;  
        try  
        {  
            //ʹ��execute�������� HttpGet���󣬲�����httRresponse����   
            httpResponse = httpClient.execute(httpGet);  
            int statusCode = httpResponse.getStatusLine().getStatusCode();  
            if(statusCode==HttpStatus.SC_OK)  
            {  
                //��÷��ؽ��   
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

