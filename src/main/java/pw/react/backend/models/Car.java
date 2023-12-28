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
    private Long ownerID;
    @Column
    private String name; //moze split na marka i model oddzielnie (?)
    @Column
    private Long mileage;
    @Column
    private Integer yearOfManufacture;
    @Column
    private Long pricePerDay;
    @Column
    private String pictureUrl; //string z urlem do zdjecia (?)
    @Column
    private String description;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOwnerID() {
        return ownerID;
    }
    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getMileage() {
        return mileage;
    }
    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }
    public Integer getYearOfManufacture(){return this.yearOfManufacture;}
    public void setYearOfManufacture(Integer yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }
    public Long getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Long pricePerDay) { this.pricePerDay = pricePerDay; }
    public String getPictureUrl() { return pictureUrl; }
    public void setPictureUrl(String pictureUrl) { this.pictureUrl = pictureUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
