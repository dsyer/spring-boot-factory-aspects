package com.example;

import java.util.Properties;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

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

	@After("execution(org.springframework.boot.SpringApplication+.new(..)) "
			+ "&& target(application) && args(sources)")
	public void run(SpringApplication application, Class<?>[] sources) throws Throwable {
		new SpringApplicationCustomizerAdapter(application, sources).customize();
	}

}
