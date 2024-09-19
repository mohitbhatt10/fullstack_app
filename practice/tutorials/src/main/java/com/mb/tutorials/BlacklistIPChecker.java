package com.mb.tutorials;
import java.util.*;
import java.util.regex.Pattern;

public class BlacklistIPChecker {

    // Convert wildcard pattern to regex pattern
    public static String convertToRegex(String pattern) {
        // Escape dots and replace '*' with '.*'
        return pattern.replace(".", "\\.").replace("*", ".*");
    }

    // Function to check if an IP is blacklisted by patterns
    public static boolean isIPBlacklisted(String ip, List<String> blacklistPatterns) {
        for (String pattern : blacklistPatterns) {
            String regex = convertToRegex(pattern);
            if (Pattern.matches(regex, ip)) {
                return true;  // IP matches the blacklisted pattern
            }
        }
        return false;  // No match found
    }

    // Function to process the incoming IPs and check if they should be blocked
    public static void processIPs(String[] incomingIPs, List<String> blacklistPatterns, int repeatThreshold) {
        Map<String, Integer> ipCountMap = new HashMap<>();

        for (String ip : incomingIPs) {
            // Increment count for this IP
            ipCountMap.put(ip, ipCountMap.getOrDefault(ip, 0) + 1);

            // Check if the IP should be blocked due to blacklisting pattern or repetition
            if (isIPBlacklisted(ip, blacklistPatterns) || ipCountMap.get(ip) > repeatThreshold) {
                System.out.println("Blocked: " + ip);
            } else {
                System.out.println("Allowed: " + ip);
            }
        }
    }

    public static void main(String[] args) {
        // Example blacklisted patterns
        List<String> blacklistedPatterns = new ArrayList<>();
        blacklistedPatterns.add("123.*");
        blacklistedPatterns.add("*.123.*");
        blacklistedPatterns.add("10.0.*");

        // Example IPs to check
        String[] incomingIPs = {
            "123.45.67.89",  // matches "123.*"
            "192.168.123.1", // matches "*.123.*"
            "10.0.1.2",      // matches "10.0.*"
            "10.0.0.5",      // matches "10.0.*"
            "127.0.0.1",     // no match
            "192.168.1.1",   // no match
            "127.0.0.1",     // no match, but repeats
            "127.0.0.1",     // repeat again, exceeds threshold
            "192.168.1.1"    // repeat, but threshold not exceeded
        };

        int repeatThreshold = 2;  // Threshold for repeating IPs

        // Process the incoming IPs
        processIPs(incomingIPs, blacklistedPatterns, repeatThreshold);
    }
}
