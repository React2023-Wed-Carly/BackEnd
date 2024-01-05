package pw.react.backend.models;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long ownerId;
    @Column
    private String brand;
    @Column
    private String model;
    @Column
    private Long mileage;
    @Column
    private Integer year;
    @Column
    private Long dailyPrice;
    @Column
    private String photo;
    @Column
    private String description;
    @Column
    private Double latitude;
    @Column
    private Double longitude;
    @Column
    private Integer seatNumber;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public Long getMileage() {
        return mileage;
    }
    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }
    public Integer getYear(){return this.year;}
    public void setYear(Integer year) {
        this.year = year;
    }
    public Long getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(Long dailyPrice) { this.dailyPrice = dailyPrice; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }
}
