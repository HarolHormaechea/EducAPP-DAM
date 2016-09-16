package com.educapp.auth;

import java.io.File;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;

import com.educapp.EducappAPI;
import com.educapp.repositories.UsersRepository;

/**
 *	Configure this web application to use OAuth 2.0.
 *
 * 	The resource server will be accessed by retrieving tokens from the
 *  EducappAPI.TOKEN_PATH endpoint, using the Password Grant Flow 
 *  as specified by OAuth 2.0.
 *  
 *  
 *  ResourceServer.configure(...) - Configures the resources available in the server
 *  								and what OAuth scopes can access their HTTP methods.
 *  
 *  OAuth2Config constructor - 		Creates services to handle client and user
 *  								authentication entities.
 *  
 *  OAuth2SecurityConfiguration.containerCustomizer(...) - Creates and configures the
 *  								HTTP/HTTPS container to be used. 
 *  
 */
@Configuration
public class OAuth2SecurityConfiguration {
	
	
	// This first section of the configuration just makes sure that Spring Security picks
	// up the UserDetailsService that we create below. 
	@Configuration
	@EnableWebSecurity
	protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
		
		@Autowired
		private UserDetailsService userDetailsService;
		
		@Override
		public void configure(WebSecurity webSecurity) throws Exception {
			// Spring Security will ignore this point so the user can
			// access it without being previously logged in.
		    webSecurity.ignoring().antMatchers(HttpMethod.POST, EducappAPI.SIGN_UP);
		}

		@Autowired
		protected void registerAuthentication( 
				final AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}
	}
	
	
	/**
	 *	Configures who can access what resource in the server.
	 */
	@Configuration
	@EnableResourceServer
	protected static class ResourceServer extends
			ResourceServerConfigurerAdapter {
		
		// Configures OAuth Scopes required to access
		// the server resources.
		@Override
		public void configure(HttpSecurity http) throws Exception {
			
			http.csrf().disable();
			
			http
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, EducappAPI.SIGN_UP)
				.anonymous();
			
			http
			.authorizeRequests()
				.antMatchers("/oauth/token").anonymous();
			
			// All GET requests must have read scope.
			http
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/**")
				.access("#oauth2.hasScope('read')");
			
			// Any other requests (due to lazyness) must have write scope.
			http
			.authorizeRequests()
				.antMatchers("/**")
				.access("#oauth2.hasScope('write')");
		}

	}
	
	

	/**
	 * This class is used to configure how our authorization server (the "/oauth/token" endpoint) 
	 * validates client credentials.
	 */
	@Configuration
	@EnableAuthorizationServer
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	protected static class OAuth2Config extends
			AuthorizationServerConfigurerAdapter {

		// Delegate the processing of Authentication requests to the framework
		@Autowired
		private AuthenticationManager authenticationManager;
		
		@Autowired
		private UserDetailsManager userDetailsManager;
		// A data structure used to store both a ClientDetailsService and a UserDetailsService
		private CustomizedCombinedDetailsManager combinedDetailsService;
		
		//A reference to our User JPA managed database to load existing users.. or create
		//default ones!
		@Autowired
		private UsersRepository userRepository;
		

		/**
		 * 
		 * Sets up clients and users managers which will handle authentication
		 * and access control to our service.
		 * 
		 * @param auth
		 * @throws Exception
		 */
		public OAuth2Config() throws Exception {
			
			// Create a service that has the credentials for all our clients
			ClientDetailsService csvc = new InMemoryClientDetailsServiceBuilder()
					.withClient("mobile").authorizedGrantTypes("password")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read","write").resourceIds(EducappAPI.RESOURCE_BASE)
					.and().build();

			//TODO: Understand and remove comment.
			// Since clients have to use BASIC authentication with the client's id/secret,
			// when sending a request for a password grant, we make each client a user
			// as well. When the BASIC authentication information is pulled from the
			// request, this combined UserDetailsService will authenticate that the
			// client is a valid "user". 
			combinedDetailsService = new CustomizedCombinedDetailsManager(csvc, userDetailsManager);
		}
		
		/**
		 * Returns our combined client and user details service.
		 * 
		 * @return
		 * @throws Exception
		 */
		@Bean
		public ClientDetailsService clientDetailsService() throws Exception {
			return combinedDetailsService;
		}
		
		/**
		 * Returns our combined client and user details service.
		 * 
		 * @return
		 * @throws Exception
		 */
		@Bean
		public UserDetailsService userDetailsService() {
			return combinedDetailsService;
		}

		/**
		 * This method tells our AuthorizationServerConfigurerAdapter to use the delegated AuthenticationManager
		 * to process authentication requests.
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.authenticationManager(authenticationManager);
		}

		/**
		 * Tells AuthorizationServerConfigurerAdapter to use our clientDetailsService
		 * for authentications.
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.withClientDetails(clientDetailsService());
		}

	}
	
	
    // Tomcat initialization and HTTPS configuration using a certificate. Add
	// the following to the run configuration VM parameters:
	//
	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
	//
	//    
	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("${keystore.file:src/main/resources/private/keystore}") String keystoreFile,
            @Value("${keystore.pass:changeit}") final String keystorePass) throws Exception {
        final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        return new EmbeddedServletContainerCustomizer () {

			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
		            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
		            tomcat.addConnectorCustomizers(
		                    new TomcatConnectorCustomizer() {
								@Override
								public void customize(Connector connector) {
									connector.setPort(8443);
			                        connector.setSecure(true);
			                        connector.setScheme("https");

			                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
			                        proto.setSSLEnabled(true);
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
