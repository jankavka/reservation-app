package cz.reservation.service.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Component("filesUtils")
public class MyFilesUtils {


    /**
     * Helper method for saving file to suggested destination represented by
     * photo object
     *
     * @param file  MultipartFile instance with data of file
     * @param photo File.class instance which represents the exact place in file system
     *              for saving the photo
     */
    public void savePhotoFile(MultipartFile file, File photo) {
        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(photo)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);

            }
            outputStream.flush();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * Helper method which tries to delete file on given url. If IOException is thrown than
     * NullPointerException is rethrown and later caught by specific method in GlobalExceptionHandler
     *
     * @param url String which represents url of file to delete
     */
    public void deleteFile(String url) {
        try {
            Files.deleteIfExists(Path.of(url));

        } catch (IOException e) {
            throw new NullPointerException("Soubor nenalezen");
        }
    }


    /**
     * Extracts file suffix from given MultipartFile instance
     *
     * @param file MultipartFile instance for extracting suffix
     * @return file suffix
     */
    public String getSuffix(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
    }

    public String getFileName(String path){
        return Objects.requireNonNull(path.split("/"))[2];
    }


}
