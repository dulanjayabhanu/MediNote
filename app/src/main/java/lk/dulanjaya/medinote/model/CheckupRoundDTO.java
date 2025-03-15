package lk.dulanjaya.medinote.model;

import java.io.Serializable;

public class CheckupRoundDTO implements Serializable {
    private String date;
    private int count;
    private String recordTimes;

    public CheckupRoundDTO(){

    }

    public CheckupRoundDTO(String date, int count, String recordTimes){
        this.date = date;
        this.count = count;
        this.recordTimes = recordTimes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRecordTimes() {
        return recordTimes;
    }

    public void setRecordTimes(String recordTimes) {
        this.recordTimes = recordTimes;
    }
}
