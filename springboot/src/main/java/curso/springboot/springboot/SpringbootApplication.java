package curso.springboot.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = "curso.springboot.model")
@ComponentScan(basePackages = {"curso.*"})
@EnableJpaRepositories(basePackages = {"curso.springboot.repository"})
@EnableTransactionManagement
public class SpringbootApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		//SpringApplication.run(SpringbootApplication.class, args);
		
		//para criptografar a senha
		  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); 
		  String result = encoder.encode("123"); 
		  System.out.println(result);
		 
		 
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//para redirecionar para o login, pois não se pode alterar o login do spring security
		
		registry.addViewController("/login").setViewName("/login");
		//registry.setOrder(Ordered.LOWEST_PRECEDENCE);
	}

}
