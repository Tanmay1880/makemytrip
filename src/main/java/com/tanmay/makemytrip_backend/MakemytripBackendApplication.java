package com.tanmay.makemytrip_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MakemytripBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MakemytripBackendApplication.class, args);
	}
}