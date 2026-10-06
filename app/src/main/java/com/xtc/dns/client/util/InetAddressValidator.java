package com.xtc.dns.client.util;

import java.util.regex.Pattern;

/** Validates IPv4 / IPv6 address literals. */
public class InetAddressValidator {

    private static final String IPV4_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    private static final InetAddressValidator INSTANCE = new InetAddressValidator();

    public static InetAddressValidator getInstance() {
        return INSTANCE;
    }

    /** @return true when the string is a valid IPv4 literal. */
    public boolean isValid(String address) {
        return address != null && IPV4_PATTERN.matcher(address).matches();
    }
}