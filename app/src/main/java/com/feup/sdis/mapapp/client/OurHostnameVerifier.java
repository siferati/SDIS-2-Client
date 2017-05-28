package com.feup.sdis.mapapp.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Rui on 27/05/2017.
 */

public class OurHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session){
        return hostname.equals("192.168.1.70") || hostname.equals("google.com");
    }
}
