package source.model;
public class Route {
    private int id;
    private String place;

    public Route(int id, String place) {
        this.id = id;
        this.place = place;
    }


    public int getId() {
        return id;
    }

    public String getPlace() {
        return place;
    }
}

