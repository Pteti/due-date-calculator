package com.example.demo.service;

import com.example.demo.exception.InvalidSubmitTimeException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
public class DueDateCalculator {

    public LocalDateTime calculateDueDate(LocalDateTime submitDate, int turnAroundTime) throws InvalidSubmitTimeException {
        verifyNotWeekend(submitDate);
        verifyNotOffHours(submitDate.getHour());

        if (submitDate.getHour() + turnAroundTime <= 17) return submitDate.plusHours(turnAroundTime);

        var workdays = turnAroundTime / 8;
        var plusHours = turnAroundTime % 8;

        return addDaysAndPlusHours(submitDate,workdays,plusHours);
    }

    private void verifyNotWeekend(LocalDateTime submitDate) throws InvalidSubmitTimeException {
        if (submitDate.getDayOfWeek() == DayOfWeek.SATURDAY || submitDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new InvalidSubmitTimeException("Submit of an issue is prohibited on weekends!");
        }
    }

    private void verifyNotOffHours(int submitHour) throws InvalidSubmitTimeException {
        if (submitHour < 8 || submitHour > 17) {
            throw new InvalidSubmitTimeException("Submit of an issue is prohibited off hours!");
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

        if (resultHour <= 17)
            return resultDate.plusHours(plusHours);
        else
            return resultDate.plusDays(1).withHour(resultHour - 9);
    }

    private boolean notWeekend(DayOfWeek dayOfWeek) {
        return !(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }


}
