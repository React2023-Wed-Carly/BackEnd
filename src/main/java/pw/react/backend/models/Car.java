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
    private long ownerID;
    @Column
    private String name;
    @Column
    private long mileage ;
    @Column
    private int yearOfManufacture;
    public Long getId() {
        return id;
    }
    public long getOwnerId(){return this.ownerID;}

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    public String getName() {
        return name;
    }

    public long getMileage() {
        return mileage;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMileage(long mileage) {
        this.mileage = mileage;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getYearOfManufacture(){return this.yearOfManufacture;}

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }
}
