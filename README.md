# IP-API PlaceholderAPI Expansion

A PlaceholderAPI expansion that provides placeholders for IP geolocation data using the [ip-api.com](https://ip-api.com) service.

## Features

- Get detailed IP information about your players
- Cache system to stay within API rate limits
- No additional dependencies required
- Free to use (uses ip-api.com free tier)

## Available Placeholders

All placeholders are prefixed with `%ipapi_`:

### Basic Information
- `%ipapi_query%` - The IP address that was queried
- `%ipapi_status%` - Status of the query (success/fail)

### Geographical Information
- `%ipapi_continent%` - Continent name
- `%ipapi_continentCode%` - Two-letter continent code
- `%ipapi_country%` - Country name
- `%ipapi_countryCode%` - Two-letter country code
- `%ipapi_region%` - Region code
- `%ipapi_regionName%` - Region name
- `%ipapi_city%` - City name
- `%ipapi_district%` - District name (if available)
- `%ipapi_zip%` - ZIP code
- `%ipapi_lat%` - Latitude
- `%ipapi_lon%` - Longitude

### Time and Currency
- `%ipapi_timezone%` - Timezone
- `%ipapi_offset%` - UTC offset in seconds
- `%ipapi_currency%` - Currency code

### Network Information
- `%ipapi_isp%` - Internet Service Provider
- `%ipapi_org%` - Organization
- `%ipapi_as%` - AS number and organization
- `%ipapi_asname%` - AS name

### Connection Type
- `%ipapi_mobile%` - Whether the IP is from a mobile network (true/false)
- `%ipapi_proxy%` - Whether the IP is a proxy (true/false)
- `%ipapi_hosting%` - Whether the IP belongs to a hosting company (true/false)

### DNS Information (Unavailable in Free Tier)
- `%ipapi_dns_geo%` - DNS geo information
- `%ipapi_dns_ip%` - DNS IP

⚠️ **Important Note on DNS Placeholders:** The DNS-related placeholders (`%ipapi_dns_geo%` and `%ipapi_dns_ip%`) are part of ip-api.com's Pro subscription and will not return data with the free tier. The code still attempts to fetch this data but will return empty values unless you have a Pro subscription.

## Installation

1. Download the latest release from the [Releases](https://github.com/GigaZelensky/ipapi-expansion/releases) page
2. Place the JAR file in the `plugins/PlaceholderAPI/expansions/` folder on your server
3. Restart your server or reload PlaceholderAPI with `/papi reload`

## Building from Source

1. Clone the repository
2. Build using Maven: `mvn clean package`
3. The compiled JAR will be in the `target` folder

## Usage Examples

Here are some examples of how you can use these placeholders:

### Welcome Message
```
&aWelcome to the server!
&eYou are connecting from %ipapi_city% 
&eTime in your area: %ipapi_timezone%
```

### Server Rules Message
```
&cPlease note: VPNs and proxies are not allowed (Detection: %ipapi_proxy%)
&eAll connections are logged with %ipapi_countryCode% geolocation
```

## Rate Limits

The free tier of ip-api.com has a limit of 45 requests per minute. The expansion includes a caching system that stores IP data for 1 hour to help stay within this limit.

If you run a large server, consider upgrading to the Pro version of ip-api.com for higher rate limits.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

Created by GigaZelensky