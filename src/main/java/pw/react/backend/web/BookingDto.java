package pw.react.backend.web;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pw.react.backend.models.Booking;
import pw.react.backend.utils.JsonDateDeserializer;
import pw.react.backend.utils.JsonDateSerializer;

import java.time.LocalDateTime;

public record BookingDto(Long id, Long carId, Long userId,
                         @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
                         LocalDateTime startDate,
                         @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
                         LocalDateTime endDate,
                         Boolean completed, Double latitude, Double longitude,Long integratedSystemId) {
    public static BookingDto EMPTY=new BookingDto(-1L,-1L,-1L,null,
            null,false,0.0,0.0,-1L);
    public static BookingDto valueFrom(Booking b){
        return new BookingDto(b.getId(), b.getCarId(), b.getUserId(), b.getStartDate(), b.getEndDate(),
                b.getCompleted(), b.getLatitude(), b.getLongitude(),b.getIntegratedSystemId());
    }
    public static Booking ConvertToBooking(BookingDto bd)
    {
        Booking booking=new Booking();
        booking.setId(bd.id);
        booking.setCarId(bd.carId);
        booking.setUserId(bd.userId);
        booking.setStartDate(bd.startDate);
        booking.setEndDate(bd.endDate);
        booking.setCompleted(bd.completed);
        booking.setLatitude(bd.latitude);
        booking.setLongitude(bd.longitude);
        booking.setIntegratedSystemId(bd.integratedSystemId);
        return booking;
    }
}
