package com.caderneta;

import com.br.azevedo.infra.cache.EnableCache;
import com.br.azevedo.security.EnableCors;
import com.br.azevedo.utils.mensagemUtils.EnableI18N;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCors
@EnableCache
@EnableI18N
@EnableScheduling
@ComponentScan(basePackages = {"com.caderneta", "com.br.azevedo"})
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		RedisRepositoriesAutoConfiguration.class
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
