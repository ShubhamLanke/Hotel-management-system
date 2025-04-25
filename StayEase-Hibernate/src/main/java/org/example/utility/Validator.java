package org.example.utility;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]+( [A-Za-z]+)*$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{4,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@(?:gmail\\.com|yahoo\\.com|hotmail\\.com|outlook\\.com|protonmail\\.com|icloud\\.com|direction\\.biz|dss\\.com|dss\\.biz)$");

    public static boolean isValidMobile(String mobile) {
        return Strings.isNotBlank(mobile) && MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 70;
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return Objects.nonNull(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isNotNullAnd(String str) {
        return str != null && !str.isBlank();
    }

    public static boolean isValidPinCode(String pinCode) {
        return pinCode != null && pinCode.matches("^[1-9][0-9]{5}$");
    }

    public static boolean isValidAadhaar(String aadhaar) {
        return aadhaar != null && aadhaar.matches("^\\d{12}$");
    }
}