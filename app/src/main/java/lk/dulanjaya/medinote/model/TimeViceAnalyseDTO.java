package lk.dulanjaya.medinote.model;

import java.io.Serializable;

public class TimeViceAnalyseDTO implements Serializable {
    private String date;
    private String recordTime;
    private String sysValue;
    private String pulValue;
    private String diaValue;

    public TimeViceAnalyseDTO(){

    }

    public TimeViceAnalyseDTO(String date, String recordTime, String sysValue, String pulValue, String diaValue){
        this.date = date;
        this.recordTime = recordTime;
        this.sysValue = sysValue;
        this.pulValue = pulValue;
        this.diaValue = diaValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getSysValue() {
        return sysValue;
    }

    public void setSysValue(String sysValue) {
        this.sysValue = sysValue;
    }

    public String getPulValue() {
        return pulValue;
    }

    public void setPulValue(String pulValue) {
        this.pulValue = pulValue;
    }

    public String getDiaValue() {
        return diaValue;
    }

    public void setDiaValue(String diaValue) {
        this.diaValue = diaValue;
    }
}
