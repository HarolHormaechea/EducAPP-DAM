package com.educapp;

import java.io.File;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.educapp.auth.OAuth2SecurityConfiguration;
import com.educapp.repositories.Repository;
import com.educapp.utilities.CustomDateDeserializerSerializer.CustomDateSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


//Auto-config flag para Spring (permite inyecciones y autodetección
//de dependencias).
@EnableAutoConfiguration

//Define la ubicación donde se buscarán los repositorios
//JPA para ser configurados: En este caso, el paquete que
//contiene la clase "marcadora" Repository.class.
@EnableJpaRepositories(basePackageClasses = Repository.class)

//Configura el escaneo de dependencias y componentes.
//Al no tener parámetro, inicia una búsqueda recursiva
//desde este paquete.
@ComponentScan

//Importa l clase de configuracion de OAuth2 para permitir
//seguridad.
@Import(OAuth2SecurityConfiguration.class)

//Importa WebMVC para garantizar que Spring permitirá
//enlazar solicitudes con nuestros controladores definidos.
@EnableWebMvc

//Nos permite emplear directivas de seguridad en metodos
//mediante anotaciones (p.ej. PreAuthorize)
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)


/**
 * 
 *  
 * @author Harold Hormaechea
 *
 */
@Configuration
public class Application {
	
	/**
	 * Main application launcher, through Spring.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	
	/**
	 * We have to register the modules to serialize and
	 * deserialize calendar objects to the jackson
	 * object mapper.
	 * @return
	 */
	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
	    simpleModule.addSerializer(new CustomDateSerializer());
	    objectMapper.registerModule(simpleModule);
		return objectMapper;
	}
	
	@Bean
	EmbeddedServletContainerCustomizer containerCustomizer(
			@Value("${keystore.file}") String keystoreFile,
			@Value("${keystore.pass}") final String keystorePass)
			throws Exception {

		
		// Code which sets up HTTPS in Tomcat
		
		final String absoluteKeystoreFile = new File(keystoreFile)
				.getAbsolutePath();

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
				tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {

					@Override
					public void customize(Connector connector) {
						connector.setPort(8443);
						connector.setSecure(true);
						connector.setScheme("https");

						Http11NioProtocol proto = (Http11NioProtocol) connector
								.getProtocolHandler();
						proto.setSSLEnabled(true);
						
						// If you update the keystore, you need to change
						// these parameters to match the keystore that you generate
						proto.setKeystoreFile(absoluteKeystoreFile);
						proto.setKeystorePass(keystorePass);
						proto.setKeystoreType("JKS");
						proto.setKeyAlias("tomcat");

					}
				});
			}

		};
	}
}
