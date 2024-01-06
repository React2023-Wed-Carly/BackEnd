package pw.react.backend.models;

import jakarta.persistence.*;

@Entity
@Table
public class FavoriteCars {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userId;
    @Column
    private Long carId;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}
