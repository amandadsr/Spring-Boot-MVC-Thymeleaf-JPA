package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override 
	protected void configure(HttpSecurity http) throws Exception {
	//configura as solicitações de acesso por Http
	
		http.csrf()
		.disable() //desativa as configurações padrão da memória
		.authorizeRequests() //Permiti restringir acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() //qualquer usuário a página inicial
		.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN") 
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuário
		.loginPage("/login") //redireciona para a pagina de login criada
		.defaultSuccessUrl("/cadastropessoa") //se logar com sucesso manda para a pag. cadastropessoa
		.failureUrl("/login?error=true") //se não logar
		.and().logout().logoutSuccessUrl("/login") //mapeia URL de logout e invalida usuário autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		//cria autenticação do usuário com banco de dados ou em memória
		
		//autenticação no banco de dados 
		auth.userDetailsService(implementacaoUserDetailsService) //chama automaticamente o loadUserByUsername
		.passwordEncoder(new BCryptPasswordEncoder());
		
		//autenticação em memória
		//(NoOpPasswordEncoder.getInstance() - sem criptografia | (new BCryptPasswordEncoder()) - com criptografia
		/*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("alex")
		.password("$2a$10$MHTmBF1ATlY2D/7xj5eXH.2Rykw2G3APNSpKKjlCg.QOQWeti2Y8K")
		.roles("ADMIN");*/
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception{
		//ignora URL especificas
		
		web.ignoring().antMatchers("/materialize/**");
	}
}
