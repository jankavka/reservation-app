package cz.reservation.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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


}
