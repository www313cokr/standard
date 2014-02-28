package com.javaboard.app.web.config.servlet;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.util.ArrayUtils;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.javaboard.app.web", excludeFilters=@Filter(Configuration.class))
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	@Value("#{systemProperties['spring.profiles.active']}")
	private String springProfilesActive;

	@Autowired
	private ApplicationContext applicationContext;


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/public-resources/");
		registry.addResourceHandler("/css/**").addResourceLocations("/public-resources/css/");
		registry.addResourceHandler("/img/**").addResourceLocations("/public-resources/img/");
		registry.addResourceHandler("/js/**").addResourceLocations("/public-resources/js/");
		registry.addResourceHandler("/html/**").addResourceLocations("/public-resources/html/");
		super.addResourceHandlers(registry);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
//		registry.addInterceptor(new XSSBlockInterceptor());
	}

	@Bean
	public ContentNegotiatingViewResolver contentNegotiationgViewResolver() {
		ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
		viewResolver.setContentNegotiationManager(contentNegotiationManager());

		List<ViewResolver> viewResolvers = new ArrayList<>();
		viewResolvers.add(thymeleafViewResolver());
		viewResolvers.add(internalResourceViewResolver());
		viewResolver.setViewResolvers(viewResolvers);

		List<View> defaultViews = new ArrayList<>();
		MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
		mappingJackson2JsonView.setExtractValueFromSingleKeyModel(true);
		mappingJackson2JsonView.setModelKey("result");
		defaultViews.add(mappingJackson2JsonView);

		viewResolver.setDefaultViews(defaultViews);
		return viewResolver;
	}

	@Bean
	public ContentNegotiationManager contentNegotiationManager() {
		Map<String, MediaType> mediaTypes = new HashMap<>();
		mediaTypes.put("html", MediaType.TEXT_HTML);
		mediaTypes.put("json", MediaType.APPLICATION_JSON);
		ContentNegotiationStrategy contentNegotiationStrategy = new PathExtensionContentNegotiationStrategy(mediaTypes);
		return new ContentNegotiationManager(contentNegotiationStrategy);
	}
	
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setViewClass(JstlView.class);
        internalResourceViewResolver.setPrefix("/WEB-INF/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }
    
	@Bean
	public ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		if (!ArrayUtils.contains(applicationContext.getEnvironment().getActiveProfiles(), "live")) {
			templateResolver.setCacheable(false);
		}
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.setMessageSource(messageSource());
		// thymeleaf-layout-dialect 사용 설정
		engine.addDialect(new LayoutDialect());
		return engine;
	}
    
	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		if (!ArrayUtils.contains(applicationContext.getEnvironment().getActiveProfiles(), "live")) {
			thymeleafViewResolver.setCache(false);
		}
		return thymeleafViewResolver;
	}
    
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("public-resources/message/message", MessageFormat.format("public-resources/message/clientenv-{0}", springProfilesActive));
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		if (!Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains("live")) {
			messageSource.setCacheSeconds(0);
		}
		return messageSource;
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//(s)custom view
		registry.addViewController("/customers").setViewName("customers");
		registry.addViewController("/users").setViewName("/common/users");
		//(e)custom view
		
		
		//(s)common view
		registry.addViewController("/index").setViewName("/index");
		registry.addViewController("/common/login").setViewName("/common/login");
		registry.addViewController("/common/logout").setViewName("/common/logout");
		registry.addViewController("/common/accessDenied").setViewName("/common/accessDenied");
		//(e)common view
			
	}

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
