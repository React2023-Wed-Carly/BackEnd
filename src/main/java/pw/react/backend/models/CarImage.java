package pw.react.backend.models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "car_image")
public class CarImage {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String fileName;
    private String fileType;
    private long carId;
    @Lob
    @Column(name = "data", length = 1000000)
    private byte[] data;

    public CarImage() {
    }

    public CarImage(String fileName, String fileType, long carId, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.carId = carId;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getCompanyId() {
        return carId;
    }

    public void setCompanyId(long companyId) {
        this.carId = companyId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarImage that = (CarImage) o;
        return carId == that.carId && id.equals(that.id) && fileName.equals(that.fileName) && fileType.equals(that.fileType) && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fileName, fileType, carId);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "CarImage{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", carId=" + carId +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}