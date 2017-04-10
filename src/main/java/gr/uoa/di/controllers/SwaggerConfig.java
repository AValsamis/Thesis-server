package gr.uoa.di.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Angelos on 9/18/2016.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(new ApiInfo("GrandaddyCool Server",
                "GrandaddyCool Server is a RESTful web service which can be used alongside GrandaddyCool Android application and acts as a middleman between guardian and elderly, both Users of GrandaddyCool.",
                "1.0.1",
                null,new Contact("Angelos Valsamis & Lefteris Skandallelis",null,"angval@di.uoa.gr"),null,null))
                .select()
                .apis(RequestHandlerSelectors.basePackage("gr.uoa.di.controllers"))
                .paths(PathSelectors.any())
                .build();
    }
}
