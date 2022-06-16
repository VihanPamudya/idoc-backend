
package com.iterminal.ndis.util;

import com.iterminal.exception.CustomException;
import com.iterminal.exception.InvalidInputException;
import java.time.Instant;
import java.util.regex.Pattern;


public class InputValidatorUtil {

    public static String validateStringProperty(String message, String propertyValue) throws InvalidInputException {

        if (propertyValue == null) {
            throw new InvalidInputException(message);
        }
        propertyValue = propertyValue.trim();
        if (propertyValue.isEmpty()) {
            throw new InvalidInputException(message);
        }
        return propertyValue;
    }
    
    public static String validateStringProperty(String message, String propertyValue, String property, int maxLen) throws InvalidInputException {

        if (propertyValue == null) {
            throw new InvalidInputException(message+" null");
        }
        propertyValue = propertyValue.trim();
        if (propertyValue.isEmpty()) {
            throw new InvalidInputException(message+" empty");
        }

        if (propertyValue.length() > maxLen) {
            throw new InvalidInputException("You have reached your maximum limit of characters allowed. Property name : " + property + " , maximum limit : " + maxLen);
        }
        return propertyValue;
    }

    public static String validateMaximumLimit(String propertyValue, String property, int maxLen) throws InvalidInputException {
        if (propertyValue != null) {
            propertyValue = propertyValue.trim();
            if (!propertyValue.isEmpty() && propertyValue.length() > maxLen) {
                throw new InvalidInputException("You have reached your maximum limit of characters allowed. Property name : " + property + ", maximum limit : " + maxLen);
            }
        }
        return propertyValue;
    }

    public static String fillDefaulePropertyValuesWithMaximumLimit(String propertyValue, String property, int maxLen) throws CustomException {
        if (propertyValue == null) {
            propertyValue = "";
        } else {
            propertyValue = propertyValue.trim();
        }

        if (!propertyValue.isEmpty() && propertyValue.length() > maxLen) {
            throw new InvalidInputException("You have reached your maximum limit of characters allowed. Property name : " + property + ", maximum limit : " + maxLen);
        }
        return propertyValue;
    }

    public static String fillDefaulePropertyValues(String propertyValue) throws CustomException {
        if (propertyValue == null) {
            propertyValue = "";
        } else {
            propertyValue = propertyValue.trim();
        }
        return propertyValue;
    }

    public static boolean isValidName(String w) {
        return w.matches("^[a-zA-Z0-9_.-]*$");
    }

    public static boolean validTimestamp(long ts) {
        long currentTime = Instant.now().getEpochSecond();
        long MIN_TIMESTAMP = Instant.now().getEpochSecond();
        return ts >= MIN_TIMESTAMP && ts <= currentTime;
    }

    /**
     * Validate Email RFC 5322 Official Standard
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {

        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).find();

    }

    public static boolean isValidPassword(String password) {

        String regexTester = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})";
        Pattern pattern = Pattern.compile(regexTester);
        return pattern.matcher(password).find();

    }

}
