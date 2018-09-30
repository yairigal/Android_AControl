package yairigal.com.homeac;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Yair Yigal on 2018-09-25.
 */

public class DateTimeContainer {
    public int selectedYear;
    public int selectedMonth;
    public int selectedDay;
    public int selectedHour;
    public int selectedMin;
    private String dayOfweek;
    private static String dayNames[] = {"Sun","Mon","Tues","Wed","Thurs","Fri","Sat"};

    public DateTimeContainer(int selectedYear, int selectedMonth, int selectedDay, int selectedHour, int selectedMin) {
        this.selectedYear = selectedYear;
        this.selectedMonth = selectedMonth;
        this.selectedDay = selectedDay;
        this.selectedHour = selectedHour;
        this.selectedMin = selectedMin;


    }

    public DateTimeContainer() {
    }


    @Override
    public String toString() {
        return this.selectedDay + "/" + this.selectedMonth + "/" + this.selectedYear + " " + this.selectedHour + ":" + selectedMin;
    }

    public static DateTimeContainer now() {
        // Get Current Date
        TimeZone utc = TimeZone.getTimeZone("GMT+3");
        final Calendar c = Calendar.getInstance(utc);
        DateTimeContainer n = new DateTimeContainer();
        n.selectedYear = c.get(Calendar.YEAR);
        n.selectedMonth = c.get(Calendar.MONTH) + 1;
        n.selectedDay = c.get(Calendar.DAY_OF_MONTH);
        n.selectedHour = c.get(Calendar.HOUR_OF_DAY);
        n.selectedMin = c.get(Calendar.MINUTE);
        //n.dayOfweek = dayNames[c.get(Calendar.DAY_OF_WEEK)];
        return n;
    }

    public boolean after(DateTimeContainer next) {
        if (this.selectedYear > next.selectedYear)
            return true;
        else if (next.selectedYear > this.selectedYear)
            return false;
        else { // same year
            if (this.selectedMonth > next.selectedMonth)
                return true;
            else if (next.selectedMonth > this.selectedMonth)
                return false;
            else { // same month
                if (this.selectedDay > next.selectedDay)
                    return true;
                else if (next.selectedDay > this.selectedDay)
                    return false;
                else { // same day
                    if (this.selectedHour > next.selectedHour)
                        return true;
                    else if (this.selectedHour < next.selectedHour)
                        return false;
                    else {
                        return this.selectedMin > next.selectedMin;
                    }

                }
            }
        }

    }

    public boolean before(DateTimeContainer next) {
        return !this.equals(next) && !this.after(next);
    }

    @Override
    public boolean equals(Object obj) {
        DateTimeContainer next = (DateTimeContainer) obj;
        return this.selectedYear == next.selectedYear && this.selectedMonth == next.selectedMonth && this.selectedDay == next.selectedDay && this.selectedHour == next.selectedHour && this.selectedMin == next.selectedMin;
    }
}
