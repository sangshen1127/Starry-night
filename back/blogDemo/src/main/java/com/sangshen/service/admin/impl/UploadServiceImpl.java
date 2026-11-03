package com.sangshen.service.admin.impl;
import com.sangshen.service.admin.UploadService;
import com.sangshen.entity.utils.UploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    private static final long MAX_SIZE = 5L * 1024 * 1024;   // 5MB
    private static final DateTimeFormatter MONTH_DIR =
            DateTimeFormatter.ofPattern("yyyy/MM");

    @Value("${blog.upload.dir}")
    private String uploadDir;   //./uploads

    @Value("${blog.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public UploadResult upload(MultipartFile file) {
        // 1. 基础校验
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("文件不能超过 5MB");
        }

        // 2. 按文件头判断真实类型
        String ext = detectExtension(file);
        if (ext == null) {
            throw new IllegalArgumentException("只支持 jpg、png、webp、gif 格式");
        }

        // 3. 生成路径：uploads/2026/10/uuid.webp
        String subDir = LocalDate.now().format(MONTH_DIR);
        // 随机生成一个UUID作为文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        Path dir = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Path target = dir.resolve(fileName);    // 目标文件路径

        // 4. 落盘
        try {
            Files.createDirectories(dir);   // 创建目录
            file.transferTo(target);    //保存文件
        } catch (IOException e) {
            throw new IllegalStateException("保存文件失败", e);
        }

        // 5. 拼对外 URL（只存相对路径）
        String url = urlPrefix + "/" + subDir + "/" + fileName;
        return new UploadResult(url, fileName, file.getSize());
    }

    /**
     * 读前 12 个字节，按文件头判断类型。返回 null 表示不支持。
     * 若不符合任何一种下述的任何四种类型，则直接返回一个空
     */
    private String detectExtension(MultipartFile file) {
        byte[] header = new byte[12];
        try (InputStream in = file.getInputStream()) {
            int read = in.read(header);
            if (read < 12) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        // JPEG: FF D8 FF
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        // PNG: 89 50 4E 47
        if ((header[0] & 0xFF) == 0x89 && header[1] == 'P'
                && header[2] == 'N' && header[3] == 'G') {
            return "png";
        }
        // GIF: 47 49 46 38
        if (header[0] == 'G' && header[1] == 'I'
                && header[2] == 'F' && header[3] == '8') {
            return "gif";
        }
        // WEBP: RIFF....WEBP
        if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return "webp";
        }
        return null;
    }
}