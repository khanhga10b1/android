package com.example.myapplication.entities;

import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




public class Events implements Serializable {
    private int id;
    private String name;
    private int kind;
    private String date;
    private String dateOfLoop;
    private int diff;
    private int loop;
    private int img;
    private int state;
    private String idSync;
    private int deleted;

    public Events(int id, String name, int kind, String date
            , int loop, int img, int state, String idSync, int deleted) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.date = date;
        this.loop = loop;
        this.img = img;
        this.state = state;
        this.idSync = idSync;
        this.deleted = deleted;
        try {
            this.diff = getDiffDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDiffDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        int diffDays;
        Date dateOfEvent = sdf.parse(date);
        Date today = new Date();
        if (loop == 1) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateOfEvent);
            while (today.after(c.getTime())) {
                c.add(Calendar.YEAR, 1);
            }
            this.dateOfLoop = sdf.format(c.getTime());
            long diff = c.getTime().getTime() - today.getTime();
            diffDays = (int) (diff / (60 * 60 * 1000 * 24));
        } else {
            this.dateOfLoop = sdf.format(dateOfEvent);
            long diff = dateOfEvent.getTime() - today.getTime();
            diffDays = (int) (diff / (60 * 60 * 1000 * 24));
        }
        if ((diffDays == 0 && !dateOfLoop.equals(sdf.format(today))) || (diffDays > 0)) {
            diffDays++;
        }
        return diffDays;
    }

    public int getState() {
        return state;
    }

    public String getIdSync() {
        return idSync;
    }

    public int getDeleted() {
        return deleted;
    }

    public int getDiff() {
        return diff;
    }

    public String getName() {
        return name;
    }

    public int getKind() {
        return kind;
    }


    public String getDate() {
        return date;
    }

    public int getImg() {
        return img;
    }

    public int getLoop() {
        return loop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDateOfLoop(String dateOfLoop) {
        this.dateOfLoop = dateOfLoop;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public SpannableString getDiffString(int display) throws Exception {
        String displayYear = "Y";
        String displayMonth = "M";
        String displayDay = "D";
        if (display == 1) {
            displayYear = " years ";
            displayMonth = " months ";
            displayDay = " days ";
        }
        SpannableString spannableString;
        String string = "";
        int sizeDate = MainActivity.context.getResources()
                .getDimensionPixelSize(R.dimen.size_date_diff);
        if (display == 0) sizeDate += 70;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        Date event;
        if (loop == 0)
            event = sdf.parse(date);
        else
            event = sdf.parse(dateOfLoop);
        DateTime then = new DateTime(event);
        DateTime now = new DateTime();
        Period period = new Period(then, now);
        int year = period.getYears();
        int month = period.getMonths();
        int day = period.getDays() + period.getWeeks() * 7;
        if (year < 0 || month < 0 || day < 0) {
            year = -year;
            month = -month;
            day = -day;
            day++;//fix bug 160701
            if (display == 1)
                string += "> ";
        } else if (year == 0 && month == 0 && day == 0) {
            if (sdf.format(event).equals(now.toString(fmt))) {
                string = "TODAY";
                spannableString = new SpannableString(string);
                spannableString.setSpan(new AbsoluteSizeSpan(sizeDate), 0, string.length(), 0);
                return spannableString;
            } else {
                string += "> ";
                day++;
            }
        } else if (display == 1)
            string += "> ";

        String yearx = String.valueOf(year);
        String monthx = String.valueOf(month);
        String dayx = String.valueOf(day);
        boolean haveYear = false, haveMonth = false, haveDay = false;
        int startYear = yearx.length();
        int startMonth = monthx.length();
        int startDay =dayx.length();
        if (display == 1) {
            startYear += 2;
            startMonth += 2;
            startDay += 2;
        }
        if (year != 0) {
            haveYear = true;
            string += yearx + displayYear;
        }
        if (month != 0) {
            haveMonth = true;
            startMonth = string.length() + monthx.length();
            string += monthx + displayMonth;
        }
        if (day != 0) {
            haveDay = true;
            startDay = string.length() + dayx.length();
            string += dayx + displayDay;
        }
        spannableString = new SpannableString(string);
        if (haveYear)
            spannableString.setSpan(new AbsoluteSizeSpan(sizeDate), startYear, startYear + displayYear.length(), 0);
        if (haveMonth)
            spannableString.setSpan(new AbsoluteSizeSpan(sizeDate), startMonth, startMonth + displayMonth.length(), 0);
        if (haveDay)
            spannableString.setSpan(new AbsoluteSizeSpan(sizeDate), startDay, startDay + displayDay.length(), 0);
        return spannableString;
    }
}
