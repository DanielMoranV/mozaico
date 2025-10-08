package com.djasoft.mozaico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
	"com.djasoft.mozaico.web.controllers",
	"com.djasoft.mozaico.infrastructure.controllers",
	"com.djasoft.mozaico.services",
	"com.djasoft.mozaico.application.services",
	"com.djasoft.mozaico.domain.repositories",
	"com.djasoft.mozaico.config",
	"com.djasoft.mozaico.security.aspects"
})
public class MozaicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MozaicoApplication.class, args);
	}

}
