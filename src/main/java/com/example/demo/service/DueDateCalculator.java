package com.example.demo.service;

import com.example.demo.exception.InvalidSubmitTimeException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
public class DueDateCalculator {

    private static final String WEEKEND_SUBMIT = "Submit of an issue is prohibited on weekends!";
    private static final String OFF_HOUR_SUBMIT = "Submit of an issue is prohibited off hours!";
    private static final int WORK_HOUR_END = 17;
    private static final int WORK_HOUR_START = 9;
    private static final int WORK_TIME = 8;

    public LocalDateTime calculateDueDate(LocalDateTime submitDate, int turnAroundTime) throws InvalidSubmitTimeException {
        verifyNotWeekend(submitDate);
        verifyNotOffHours(submitDate.getHour());

        if (submitDate.getHour() + turnAroundTime <= WORK_HOUR_END) return submitDate.plusHours(turnAroundTime);

        var workdays = turnAroundTime / WORK_TIME;
        var plusHours = turnAroundTime % WORK_TIME;

        return addDaysAndPlusHours(submitDate,workdays,plusHours);
    }

    private void verifyNotWeekend(LocalDateTime submitDate) throws InvalidSubmitTimeException {
        if (submitDate.getDayOfWeek() == DayOfWeek.SATURDAY || submitDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new InvalidSubmitTimeException(WEEKEND_SUBMIT);
        }
    }

    private void verifyNotOffHours(int submitHour) throws InvalidSubmitTimeException {
        if (submitHour < WORK_HOUR_START || submitHour >= WORK_HOUR_END) {
            throw new InvalidSubmitTimeException(OFF_HOUR_SUBMIT);
        }
    }

    private LocalDateTime addDaysAndPlusHours(LocalDateTime submitDate, int workdays, int plusHours) {
        var  resultDate = submitDate;

        while (workdays != 0) {
            resultDate = resultDate.plusDays(1);
            if (notWeekend(resultDate.getDayOfWeek())) {
                workdays--;
            }
        }

        var resultHour = resultDate.getHour() + plusHours;

        if (resultHour < WORK_HOUR_END)
            return resultDate.plusHours(plusHours);
        else
            return resultDate.plusDays(1).withHour(resultHour - 8);
    }

    private boolean notWeekend(DayOfWeek dayOfWeek) {
        return !(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }


}
