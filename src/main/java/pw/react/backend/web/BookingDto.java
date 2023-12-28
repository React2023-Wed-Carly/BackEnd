package pw.react.backend.web;
import pw.react.backend.models.Booking;
import pw.react.backend.models.Car;

public record BookingDto(Long id, Long carId, Long userId, String startDate, String endDate,
                         Boolean completed, Double latitude, Double longitude) {
    public static BookingDto valueFrom(Booking b){
        return new BookingDto(b.getId(), b.getCarId(), b.getUserId(), b.getStartDate(), b.getEndDate(),
                b.getCompleted(), b.getLatitude(), b.getLongitude());
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
        return booking;
    }
}
