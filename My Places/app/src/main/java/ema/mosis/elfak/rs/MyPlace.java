package ema.mosis.elfak.rs;

public class MyPlace {
    String name;
    String description;

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
