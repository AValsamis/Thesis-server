package gr.uoa.di;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = {"gr.uoa.di.controllers"})
public class DipServerApplication  {
	public static void main(String[] args) {
		SpringApplication.run(DipServerApplication.class, args);
	}
}
