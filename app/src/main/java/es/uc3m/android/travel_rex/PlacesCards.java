package es.uc3m.android.travel_rex;

public class PlacesCards {
    private String placeName;
    private String placeDescription;
    private Integer placeRating;

    // constructor
    public PlacesCards (String placeName, String placeDescription, Integer placeRating) {
    this.placeName = placeName;
    this.placeDescription = placeDescription;
    this.placeRating = placeRating;
    }

    public String getPlaceName() {
        return placeName;
    }
    public String getPlaceDescription() {
        return placeDescription;
    }
    public Integer getPlaceRating() {
        return placeRating;
    }

}
