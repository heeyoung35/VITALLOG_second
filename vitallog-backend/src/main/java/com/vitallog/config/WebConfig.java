package com.vitallog.config;

import com.vitallog.config.security.filter.HeaderFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private static final String[] CLASSPATH_RESOURCE_LOCATION = {
            "classpath:/static/", "classpath:/public/", "classpath:/", "classpath:/resources/",
            "classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/"
    };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("**").addResourceLocations(CLASSPATH_RESOURCE_LOCATION);
    }

    @Bean
    public FilterRegistrationBean<HeaderFilter> getFilterRegistrationBean() {

        FilterRegistrationBean<HeaderFilter> registrationBean = new FilterRegistrationBean<HeaderFilter>(createHeaderFilter());

        registrationBean.setOrder(Integer.MIN_VALUE);

        registrationBean.addUrlPatterns("/*");
        return registrationBean;

    }

    @Bean
    public HeaderFilter createHeaderFilter() {
        return new HeaderFilter();
    }
}
