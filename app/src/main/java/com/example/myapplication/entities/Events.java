package com.example.myapplication.entities;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Events {

    private int id;
    private String name;
    private int kind;
    private String date;
    private int loop;
    private int diff;

    private int img;

    public Events(int id, String name, int kind, String date
            , int loop, int img) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.date = date;
        this.loop = loop;
        this.img = img;
        try {
            this.diff = getDiffDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public int getDiff() {
        return diff;
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

    public int getDiffDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        int diffDays;
        Date dateOfEvent = sdf.parse(date);
        Date today = new Date();
        String dateOfLoop= sdf.format(dateOfEvent);
        if (loop == 1) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateOfEvent);
            while (today.after(c.getTime())) {
                c.add(Calendar.YEAR, 1);
            }

            long diff = c.getTime().getTime() - today.getTime();
            diffDays = (int) (diff / (60 * 60 * 1000 * 24));
        } else {
            long diff = dateOfEvent.getTime() - today.getTime();
            diffDays = (int) (diff / (60 * 60 * 1000 * 24));
        }
        if ((diffDays == 0 && !dateOfLoop.equals(sdf.format(today))) || (diffDays > 0)) {
            diffDays++;
        }
        return diffDays;
    }
}
