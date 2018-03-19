package com.fastweb.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;



@EnableCaching
@EnableScheduling
@SpringBootApplication
@ServletComponentScan
@EnableAspectJAutoProxy
@ComponentScan(basePackages={"com.fastweb.core","com.fastweb.web"})
public class BootApplication extends SpringBootServletInitializer {

    //private Logger logger = LoggerFactory.getLogger(BootApplication.class);

    /**
     * 程序入口
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootApplication.class);
    }
}
