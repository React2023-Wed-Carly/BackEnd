package pw.react.backend.services;
import org.springframework.web.multipart.MultipartFile;
import pw.react.backend.models.CarImage;
public interface ImageService {
    CarImage sotreImage(long carId, MultipartFile file);
    CarImage getCarImage(long carId);
    void deleteCarImage(long carId);
}
