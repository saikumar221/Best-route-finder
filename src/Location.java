import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Location {

  private String country;
  private String capital;
  private Double lat;
  private Double lon;
  private List<Route> routes;
  private boolean exists;
  private Location previous;
  private float lengthFromStart;

  // Default constructor
  public Location() {
    this.country = "";
    this.capital = "";
    this.lat = (double) 0;
    this.lon = (double) 0;
    this.lengthFromStart = Float.MAX_VALUE; // Use Float.MAX_VALUE as a high value
    this.exists = true;
    this.previous = null;
    this.routes = new ArrayList<>();
  }

  // Constructor with country and capital
  public Location(String country, String capital) {
    this.country = country;
    this.capital = capital;
    this.lat = (double) 0;
    this.lon = (double) 0;
    this.lengthFromStart = Float.MAX_VALUE; // Use Float.MAX_VALUE as a high value
    this.exists = true;
    this.previous = null;
    this.routes = new ArrayList<>();
  }

  // Constructor with country, capital, latitude, and longitude
  public Location(String country, String capital, Double lat, Double lon) {
    this.country = country;
    this.capital = capital;
    this.lat = lat;
    this.lon = lon;
    this.lengthFromStart = Float.MAX_VALUE; // Use Float.MAX_VALUE as a high value
    this.exists = true;
    this.previous = null;
    this.routes = new ArrayList<>();
  }

  // Method to get the weight (distance) from this location to another location
  public float getWeight(Location start, Location end) {
    // This is a placeholder method; the actual implementation depends on the Route class
    return 0; // Replace with actual logic
  }

  // Override equals method
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Location location = (Location) obj;
    return capital.equals(location.capital);
  }

  // Override hashCode method
  @Override
  public int hashCode() {
    return capital.hashCode();
  }

  // Comparator for comparing locations based on lengthFromStart
  public static class CompareLocation implements Comparator<Location> {
    @Override
    public int compare(Location l1, Location l2) {
      return Float.compare(l1.lengthFromStart, l2.lengthFromStart);
    }
  }

  // Getters and setters
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCapital() {
    return capital;
  }

  public void setCapital(String capital) {
    this.capital = capital;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(float lat) {
    this.lat = (double) lat;
  }

  public Double getLon() {
    return lon;
  }

  public void setLon(float lon) {
    this.lon = (double) lon;
  }

  public List<Route> getRoutes() {
    return routes;
  }

  public void setRoutes(List<Route> routes) {
    this.routes = routes;
  }

  public boolean isExists() {
    return exists;
  }

  public void setExists(boolean exists) {
    this.exists = exists;
  }

  public Location getPrevious() {
    return previous;
  }

  public void setPrevious(Location previous) {
    this.previous = previous;
  }

  public float getLengthFromStart() {
    return lengthFromStart;
  }

  public void setLengthFromStart(float lengthFromStart) {
    this.lengthFromStart = lengthFromStart;
  }
}


