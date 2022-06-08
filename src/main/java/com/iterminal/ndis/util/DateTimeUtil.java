/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.util;

import com.iterminal.exception.InvalidInputException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 *
 * @author Suranga
 */
public class DateTimeUtil {

    public static final long EPOCH_YEAR_DIFF = 31536000;

    public static long getCurrentTime() {
        long currentTime = Instant.now().getEpochSecond();
        return currentTime;
    }

    public static long getAge(long value) throws InvalidInputException {

        long year = 0;
        long currentTime = Instant.now().getEpochSecond();
        long diff = currentTime - value;
        if (diff > 0) {
            year = (diff / EPOCH_YEAR_DIFF);
        } else {
            throw new InvalidInputException("Invalid Date of Birth.");
        }

        return year;
    }

    public static LocalDate getLocalDate(long epochTime) {

        TimeZone timeZone = TimeZone.getDefault();
        ZoneId timeZoneId = timeZone.toZoneId();
        LocalDate localDate = Instant.ofEpochSecond(epochTime).atZone(timeZoneId).toLocalDate();
        return localDate;

    }

    public static String getFormatDateTime(long epochTime) {

        TimeZone timeZone = TimeZone.getDefault();
        ZoneId timeZoneId = timeZone.toZoneId();
        LocalDateTime dateTime = Instant.ofEpochMilli(epochTime * 1000).atZone(timeZoneId).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh.mm a");
        String formatDateTime = dateTime.format(formatter);
        return formatDateTime;
    }

    public static String getFormatDate(long epochTime) throws InvalidInputException {

        try {
            TimeZone timeZone = TimeZone.getDefault();
            ZoneId timeZoneId = timeZone.toZoneId();
            LocalDate localDate = Instant.ofEpochSecond(epochTime).atZone(timeZoneId).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formatDateTime = localDate.format(formatter);
            return formatDateTime;
        } catch (Exception ex) {
            throw new InvalidInputException("Invalid date or date pattern");
        }
    }

    public static String getFormatDate(LocalDate dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formatDateTime = dateTime.format(formatter);
        return formatDateTime;
    }

    public static LocalDate getLocalDate(String date) throws InvalidInputException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(date, formatter);
            return localDate;
        } catch (Exception ex) {
            throw new InvalidInputException("Invalid date or date pattern");
        }
    }

    public static String getStringDate(LocalDate localDate) throws InvalidInputException {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formatDateTime = localDate.format(formatter);
            return formatDateTime;
        } catch (Exception ex) {
            throw new InvalidInputException("Invalid date or date pattern");
        }
    }

}
