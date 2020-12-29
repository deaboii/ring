package com.deeaboi.ring.Model;

public class messages
{
    private  String record;
    private  String type;

    public messages()
    {


    }

    public messages(String record, String type) {
        this.record = record;
        this.type = type;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
