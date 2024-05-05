package es.uc3m.android.travel_rex;

public class PlacesCards {
    private String placeName;
    private String placeDescription;
    private Integer placeRating;
    private String visitedImage;

    // constructor
    public PlacesCards (String placeName, String placeDescription, Integer placeRating, String visitedImage) {
    this.placeName = placeName;
    this.placeDescription = placeDescription;
    this.placeRating = placeRating;
    this.visitedImage = visitedImage;
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
    public String getVisitedImage() { return visitedImage;}

}
