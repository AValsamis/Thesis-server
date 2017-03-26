package gr.uoa.di;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = {"gr.uoa.di.entities","gr.uoa.di.repository","gr.uoa.di.controllers", "gr.uoa.di.utils"})
public class DipServerApplication  {
	public static void main(String[] args) {
		SpringApplication.run(DipServerApplication.class, args);
	}
}
