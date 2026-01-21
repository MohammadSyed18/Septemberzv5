package dev.septemberz;
import java.time.*;
public final class PublishingCalendar {public LocalDate recommendedPublishDate(Announcement a,int leadDays){if(leadDays<0)throw new IllegalArgumentException("lead days cannot be negative");LocalDate date=a.deadline().minusDays(leadDays);while(date.getDayOfWeek()==DayOfWeek.SATURDAY||date.getDayOfWeek()==DayOfWeek.SUNDAY)date=date.minusDays(1);return date;}}
