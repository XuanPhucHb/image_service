package com.nxp.api.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LogConfig {
	static Logger logger;
	static String log4JPropertyFile = "D:/log4j.properties";
//	static String log4JPropertyFile = "/home/dvdd/api-dvdd/log4j.properties";
	static Properties p = new Properties();

	public static Logger getLogger(Object obj) {
		try {
			p.load(new FileInputStream(log4JPropertyFile));
			logger = Logger.getLogger(obj.getClass());
			PropertyConfigurator.configure(p);
			return logger;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
