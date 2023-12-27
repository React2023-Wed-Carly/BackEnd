package pw.react.backend.web;
import pw.react.backend.models.FavoriteCars;
public record FavoriteCarsDto(Long id,Long UserId,Long CarId){
    public static FavoriteCarsDto ValueFrom(FavoriteCars fc)
    {
        return new FavoriteCarsDto(fc.getId(), fc.getUserId(), fc.getCarId());
    }
    public static FavoriteCars ConvertToFavoriteCars(FavoriteCarsDto fcd)
    {
        FavoriteCars fc=new FavoriteCars();
        fc.setId(fcd.id);
        fc.setUserId(fcd.UserId);
        fc.setCarId(fcd.CarId);
        return fc;
    }
}
