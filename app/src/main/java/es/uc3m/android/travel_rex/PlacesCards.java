package es.uc3m.android.travel_rex;

public class PlacesCards {
    private String placeName;
    private String placeDescription;
    private Integer placeRating;
    private String visitedImage;
    private String profilePic;

    // constructor
    public PlacesCards (String placeName, String placeDescription, Integer placeRating, String visitedImage, String profilePic) {
    this.placeName = placeName;
    this.placeDescription = placeDescription;
    this.placeRating = placeRating;
    this.visitedImage = visitedImage;
    this.profilePic = profilePic;
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
    public String getProfilePic() { return profilePic; }

}
