package cn.fri.beans;

public class ScanCodeRecord {
    private String babyId;
    private String name;
    private String address;
    private long insertTime;

    public Object[] toObject() {
        return new Object[]{this.name, this.babyId, this.address, this.insertTime};
    }

    public String getBabyId() {
        return babyId;
    }

    public void setBabyId(String babyId) {
        this.babyId = babyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
