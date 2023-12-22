package pw.react.backend.web;
import pw.react.backend.models.Car;
public record CarDto(Long id,String name,long mileage,int yearOfManufature,long ownerID) {
    public static CarDto valueFrom(Car c){
        return new CarDto(c.getId(), c.getName(), c.getMileage(),c.getYearOfManufacture() ,c.getOwnerID());
    }
    public static Car ConvertToCar(CarDto cd)
    {
        Car car=new Car();
        car.setId(cd.id);
        car.setName(cd.name);
        car.setMileage(cd.mileage);
        car.setOwnerID(cd.ownerID);
        car.setYearOfManufacture((cd.yearOfManufature));
        return car;
    }
}
