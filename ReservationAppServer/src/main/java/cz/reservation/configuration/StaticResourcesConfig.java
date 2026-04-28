package cz.reservation.configuration;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class StaticResourcesConfig implements WebMvcConfigurer {

    @Value("${file.directory}")
    private String fileDirectory;


    /**
     * Adds resource handlers to application. Method addResourceHandler() determines a URL where we find
     * our resource. Method addResourceLocation is exact location of resource on server
     *
     * @param registry object where resource infos are set
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileDirectory + "/");
    }


    @Override
    public void addCorsMappings(@Nonnull CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOriginPatterns("**")
                .allowedMethods("GET", "POST", "PUT","DELETE","HEAD", "OPTIONS")
                .allowedOrigins("**")
                .allowedOrigins("http://127.0.0.1:5173")
                .allowedOrigins("http://localhost:5173");
    }




}
