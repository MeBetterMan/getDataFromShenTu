package cn.fri.beans;

public class BabyInfo {
    private String name;
    private String gender;
    private long birthday;
    private String icon;
    private String height;
    private String weight;
    private String features;
    private String bloodType;
    private String drugAllergy;
    private String inbornDisease;
    private String otherInfo;
    private long insertTime;
    public Object[] toObject(){
        return new Object[]{this.name,this.gender,this.birthday,this.icon,this.height,this.weight,this.features,this.bloodType,this.drugAllergy,this.inbornDisease,this.otherInfo,this.insertTime};
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getDrugAllergy() {
        return drugAllergy;
    }

    public void setDrugAllergy(String drugAllergy) {
        this.drugAllergy = drugAllergy;
    }

    public String getInbornDisease() {
        return inbornDisease;
    }

    public void setInbornDisease(String inbornDisease) {
        this.inbornDisease = inbornDisease;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }
}
