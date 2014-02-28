package com.javaboard.app.web.config.root;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;


@Configuration
@PropertySources(value = @PropertySource("classpath:properties/env-${spring.profiles.active}.properties"))
public class WebConfig {

	public WebConfig() {
		super();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer webMobilePropertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
