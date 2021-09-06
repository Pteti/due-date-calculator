package com.example.demo.service;

import com.example.demo.exception.InvalidSubmitTimeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DueDateCalculatorTest {

    private static DueDateCalculator underTest;

    @BeforeAll
    public static void setUp() {
        underTest = new DueDateCalculator();
    }

    @Test
    public void submit_on_weekends_is_prohibited() {

        var dateTime = LocalDateTime.parse("2021-09-05T11:30:00");
        var turnaroundTime = 1;

        Exception exception = assertThrows(InvalidSubmitTimeException.class,
                () -> underTest.calculateDueDate(dateTime,turnaroundTime));

        String expectedMessage = "Submit of an issue is prohibited on weekends!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void submit_off_hours_is_prohibited() {
        var dateTime = LocalDateTime.parse("2021-09-06T06:30:00");
        var turnaroundTime = 1;

        Exception exception = assertThrows(InvalidSubmitTimeException.class,
                () -> underTest.calculateDueDate(dateTime,turnaroundTime));

        String expectedMessage = "Submit of an issue is prohibited off hours!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void submit_on_Monday_9am_with_8_hours_turnaround_is_resolved_at_same_day_5pm() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2021-08-02T09:00:00");
        var turnaroundTime = 8;
        var expectedDateTime = LocalDateTime.parse("2021-08-02T17:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

    @Test
    public void submit_on_Monday_with_16_hours_of_turnaround_is_resolved_on_Wednesday() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2021-08-02T12:00:00");
        var turnaroundTime = 16;
        var expectedDateTime = LocalDateTime.parse("2021-08-04T12:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

    @Test
    public void submit_on_Monday_5pm_with_17_hours_of_turnaround_is_resolved_on_Thursday_9am() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2021-08-02T17:00:00");
        var turnaroundTime = 17;
        var expectedDateTime = LocalDateTime.parse("2021-08-05T09:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

    @Test
    public void submit_on_Monday_3_pm_with_4_hours_turnaround_is_resolved_at_Tuesday_10_am() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2021-08-02T15:00:00");
        var turnaroundTime = 4;
        var expectedDateTime = LocalDateTime.parse("2021-08-03T10:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

    @Test
    public void submit_on_Thursday_with_24_hour_turnaround_is_resolved_on_Tuesday() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2021-08-05T12:00:00");
        var turnaroundTime = 24;
        var expectedDateTime = LocalDateTime.parse("2021-08-10T12:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

    @Test
    public void submit_on_the_last_day_of_month_is_resolved_next_month() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2021-08-31T13:00:00");
        var turnaroundTime = 8;
        var expectedDateTime = LocalDateTime.parse("2021-09-01T13:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

    @Test
    public void submit_on_the_last_day_of_the_year_is_resolved_next_year() throws InvalidSubmitTimeException {
        var dateTime = LocalDateTime.parse("2020-12-31T16:00:00");
        var turnaroundTime = 16;
        var expectedDateTime = LocalDateTime.parse("2021-01-04T16:00:00");

        var response = underTest.calculateDueDate(dateTime, turnaroundTime);

        assertEquals(expectedDateTime, response);
    }

}
