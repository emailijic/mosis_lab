package ema.mosis.elfak.rs;

public class MyPlace {
    String name;
    String description;
    String longitude;
    String latitude;
    int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public MyPlace(String name,String description) {
        this.name=name;
        this.description=description;

    }
    public MyPlace(String name){
        this.name=name;

    }
    public String getName() {
        return this.name;
    }
    public String getDesc(){
        return this.description;
    }
    public void setName(String name){
        this.name=name;

    }
    public void setDesc(String description){
        this.description=description;
    }
    @Override
    public String toString()
    {
        return name;
    }
}
