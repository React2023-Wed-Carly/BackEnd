package pw.react.backend.web;
import pw.react.backend.models.Car;
public record CarDto(Long id, String name, Long mileage, Integer yearOfManufature, Long ownerID,
                     Long pricePerDay, String pictureUrl, String description) {
    public static CarDto valueFrom(Car c){
        return new CarDto(c.getId(), c.getName(), c.getMileage(), c.getYearOfManufacture(), c.getOwnerID(),
                c.getPricePerDay(), c.getPictureUrl(), c.getDescription());
    }
    public static Car ConvertToCar(CarDto cd)
    {
        Car car=new Car();
        car.setId(cd.id);
        car.setName(cd.name);
        car.setMileage(cd.mileage);
        car.setOwnerID(cd.ownerID);
        car.setYearOfManufacture((cd.yearOfManufature));
        car.setPricePerDay(cd.pricePerDay);
        car.setPictureUrl(cd.pictureUrl);
        car.setDescription(cd.description);
        return car;
    }
}
