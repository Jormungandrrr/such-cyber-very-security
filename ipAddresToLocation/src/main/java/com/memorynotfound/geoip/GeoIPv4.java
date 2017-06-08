package com.memorynotfound.geoip;

import com.maxmind.geoip.LookupService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.lang.ClassLoader;

public class GeoIPv4 {
 
    private static LookupService lookUp;
    static {
        try {

            lookUp = new LookupService(
                           GeoIPv4.class.getClassLoader().getResource("GeoLiteCity.dat").getFile(),
                    LookupService.GEOIP_MEMORY_CACHE);

            System.out.println("GeoIP Database loaded: " + lookUp.getDatabaseInfo());
        } catch (IOException e) {
            System.out.println("Could not load geo ip database: " + e.getMessage());
        }
    }
   
    public static GeoLocation getLocation(String ipAddress) {
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }


    public static GeoLocation getLocation(long ipAddress) {
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }

    public static GeoLocation getLocation(InetAddress ipAddress) {
        return GeoLocation.map(lookUp.getLocation(ipAddress));
    }
}
