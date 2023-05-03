package org.admi.dataPreparation;
public class Tafassir {
    private int tafssirId;
    private String tafssirYear;

    public Tafassir(int tafssirId, String tafssirYear) {
        this.tafssirId = tafssirId;
        this.tafssirYear = tafssirYear;
    }
    public Tafassir(){}


    public long getTafssirId() {
        return tafssirId;
    }

    public void setTafssirId(int tafssirId) {
        this.tafssirId = tafssirId;
    }

    public String getTafssirYear() {
        return tafssirYear;
    }

    public void setTafssirYear(String tafssirYear) {
        this.tafssirYear = tafssirYear;
    }

    @Override
    public String toString() {
        return "Tafassir{" +
                "tafssirId=" + tafssirId +
                ", tafssirYear='" + tafssirYear + '\'' +
                '}';
    }
}
