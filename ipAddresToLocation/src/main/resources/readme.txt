# Getting geo location from IP Address
# Home website: https://www.maxmind.com/en/home
# Download: http://dev.maxmind.com/geoip/legacy/geolite/
# Maven dependency: http://mvnrepository.com/artifact/com.maxmind.geoip/geoip-api/1.2.14
# Licencing: https://www.maxmind.com/en/builder

The following options can be passed as the second parameter to the LookupService constructor.

GEOIP_STANDARD - Read database from file system. Uses the least memory.
GEOIP_MEMORY_CACHE - Load database into memory. This provides faster performance but uses more memory
GEOIP_CHECK_CACHE - Check for updated database. If database has been updated, reload file handle and/or memory cache.
GEOIP_INDEX_CACHE - Cache only the most frequently accessed index portion of the database, resulting in faster lookups than GEOIP_STANDARD, but less memory usage than GEOIP_MEMORY_CACHE. This is useful for larger databases such as GeoIP Legacy Organization and GeoIP Legacy City. Note: for GeoIP Legacy Country, Region and Netspeed databases, GEOIP_INDEX_CACHE is equivalent to GEOIP_MEMORY_CACHE.

These options may be combined. For example:

LookupService cl = new LookupService(dbfile,
    LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE);