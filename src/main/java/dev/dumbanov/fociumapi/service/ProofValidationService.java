package dev.dumbanov.fociumapi.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import dev.dumbanov.fociumapi.entity.Task;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@Service
public class ProofValidationService {

    public void validateProofTimestamp(MultipartFile file, Task task) {
        LocalDateTime proofDate = extractFileDate(file);
        if (proofDate == null) {
            return;
        }
        if (proofDate.isBefore(task.getCreatedAt())) {
            throw new BadRequestException(
                    "Proof was captured before the task was created and cannot be accepted."
            );
        }
    }

    private LocalDateTime extractFileDate(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            var metadata = ImageMetadataReader.readMetadata(stream);
            var directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory == null) {
                return null;
            }
            // Pass system timezone — EXIF DateTimeOriginal has no embedded TZ,
            // so without this the library returns null for most real camera photos.
            Date date = directory.getDate(
                    ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL,
                    TimeZone.getDefault()
            );
            if (date == null) {
                // Fall back to TAG_DATETIME (file modification time baked into EXIF)
                date = directory.getDate(
                        ExifSubIFDDirectory.TAG_DATETIME,
                        TimeZone.getDefault()
                );
            }
            if (date == null) {
                return null;
            }
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}
