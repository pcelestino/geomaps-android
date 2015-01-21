package pedro.geo.ffit.model;

/**
 * Created by pedro on 20/01/15.
 */
// Um Bean simples
public class Favorite {

    private String id;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;

    public Favorite() {
    }

    public Favorite(String id, String title, String description, Double latitude, Double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}