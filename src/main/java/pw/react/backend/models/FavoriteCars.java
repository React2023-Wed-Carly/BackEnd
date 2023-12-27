package pw.react.backend.models;

import jakarta.persistence.*;

@Entity
@Table
public class FavoriteCars {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long UserId;
    @Column
    private Long CarId;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return UserId;
    }

    public Long getCarId() {
        return CarId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public void setCarId(Long carId) {
        CarId = carId;
    }
}
