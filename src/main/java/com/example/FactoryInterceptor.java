package com.example;

import java.util.Properties;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;

@Aspect
public class FactoryInterceptor {

	private static final String[] EXCLUDES = { ApplicationListener.class.getName(),
			ApplicationContextInitializer.class.getName() };

	@Around("execution(java.util.Properties org.springframework.core.io.support.PropertiesLoaderUtils.loadProperties(..))")
	public Object stack(ProceedingJoinPoint joinPoint) throws Throwable {
		Properties proceed = (Properties) joinPoint.proceed();
		Properties result = new Properties();
		result.putAll(proceed);
		for (String name : EXCLUDES) {
			if (proceed.containsKey(name)) {
				result.remove(name);
			}
		}
		return result;
	}

	@Before("execution(org.springframework.context.ConfigurableApplicationContext org.springframework.boot.SpringApplication+.run(..)) "
			+ "&& this(application) && args(args)")
	public void run(ProceedingJoinPoint joinPoint, SpringApplication application,
			String[] args) throws Throwable {
		new SpringApplicationCustomizerAdapter(application).customize(args);
	}

}
