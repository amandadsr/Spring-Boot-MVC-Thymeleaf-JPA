package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import curso.springboot.model.Usuario;
import curso.springboot.repository.UsuarioRepository;

@Service
@Transactional //para conseguir carregar os acessos na transação
public class ImplementacaoUserDetailsService implements UserDetailsService{
//tem que fazer a injeção de dependência do Repository
	
	@Autowired
	private UsuarioRepository usuarioRepository;	
	
	@Override //esse método é chamado automaticamente pq está implementando a UserDetailsService
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado!");
		}
		
		return new User( usuario.getUsername(), usuario.getPassword(), 
				true, true,
				true, true,
				usuario.getAuthorities());
	}

}
