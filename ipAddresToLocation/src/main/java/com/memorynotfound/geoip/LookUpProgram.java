package com.memorynotfound.geoip;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LookUpProgram {

    public static void main(String... args) throws UnknownHostException {

        long ipAddress = new BigInteger(InetAddress.getByName("145.93.177.75").getAddress()).longValue();
    
        System.out.println("By String IP address: \n" +
                GeoIPv4.getLocation("145.93.177.75"));

        System.out.println("By long IP address: \n" +
                GeoIPv4.getLocation(ipAddress));

        System.out.println("By InetAddress IP address: \n" +
                GeoIPv4.getLocation(InetAddress.getByName("145.93.177.75")));

    }
}
