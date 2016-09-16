package com.educapp;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Clase configuradora de JPA.
 * 
 * 
 * 
 * @author Harold Hormaechea
 *
 */
@Configuration
@EnableTransactionManagement
public class JPAConfiguration {

		/**
		 * Bean configurador del gestor de entidades
		 * JPA para Spring.
		 * 
		 * @return
		 */
		@Bean
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
			em.setDataSource(dataSource());
	
			//Define la ubicación de los datos a persistir
			//como entidades.
			em.setPackagesToScan(new String[] { "com.educapp.model" });
	
			JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
			em.setJpaVendorAdapter(vendorAdapter);
			em.setJpaProperties(additionalProperties());
	
			return em;
		}
	
		/**
		 * Metodo utilitario para definir la fuente de datos
		 * (dataSource) a emplear por nuestro sistema de persistencia.
		 * @return
		 */
		@Bean
		public DataSource dataSource(){
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://localhost:45055/educapp");
			dataSource.setUsername( "educapp_admin" );
			dataSource.setPassword( "educapp_admin" );
			return dataSource;
		}
	
		@Bean
		public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(emf);
			return transactionManager;
		}
	
		@Bean
		public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
			return new PersistenceExceptionTranslationPostProcessor();
		}
	
		/**
		 * Método utilidad para definir las propiedades adicionales que
		 * deseamos para nuestra gestión de persistencia.
		 * 
		 * @return
		 */
		Properties additionalProperties() {
			Properties properties = new Properties();
			//Usar cuando haga falta crear en arranque
			//y hacer drop en cierre de aplicacion
			//Comentar para "producción".
			properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
			return properties;
		}
}
