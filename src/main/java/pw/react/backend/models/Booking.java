package pw.react.backend.models;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long carId;
    @Column
    private Long userId;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    private Boolean completed;
    @Column
    private Double latitude;
    @Column
    private Double longitude;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCarId() {
        return carId;
    }
    public void setCarId(Long carId) {
        this.carId = carId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
