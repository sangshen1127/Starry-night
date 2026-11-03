package com.sangshen.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${blog.upload.dir}")
    private String uploadDir;   //  上传目录的物理路径（磁盘上的绝对/相对路径）
    @Value("${blog.upload.url-prefix}")
    private String urlPrefix;   // 访问上传资源的 URL 前缀

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 把配置里的上传目录变成一个绝对、规范、无歧义的真实基准路径
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);   //创建上传目录
        } catch (IOException e) {
            throw new IllegalStateException("无法创建上传目录: " + dir, e);
        }
        // 把路径转换成 Spring ResourceHandler 可以识别的格式
        String location = dir.toUri().toString();
        //若location没有以/结尾，则添加一个/
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler(urlPrefix + "/**")  // 配置访问上传资源的 URL 前缀
                .addResourceLocations(location)  // 配置上传资源的物理路径
                .setCachePeriod(3600);  //配置缓存周期为3600秒
    }
}
