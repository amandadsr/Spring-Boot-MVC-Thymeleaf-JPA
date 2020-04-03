package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;
//import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	//@Autowired
	//private ProfissaoRepository profissaoRepository;
	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "**/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")))); //pagerequest serve para diminuir a quantidade de itens apresentados na página
		//modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}

	//PAGINAÇÃO
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5) Pageable pageable,
						ModelAndView model, @RequestParam("nomepesquisa") String nomepesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("nomepesquisa", nomepesquisa);
		model.setViewName("cadastro/cadastropessoa");
		
		return model;
	}
	
	// método salvar sem listar na tela
	/*
	 * @RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa") public
	 * String salvar(Pessoa pessoa) {
	 * 
	 * pessoaRepository.save(pessoa); return "cadastro/cadastropessoa";
	 * 
	 * }
	 */

	// método salvar listando na tela
	@RequestMapping(method = RequestMethod.POST, 
			value = "**/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException {

		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		//validações 
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));//procurar na paginação
			modelAndView.addObject("pessoaobj", pessoa);
			//modelAndView.addObject("profissoes", profissaoRepository.findAll());
			
			//lista de erros
			List<String> msg = new ArrayList<String>();
			for(ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); //vem das anotações @NotEmpty e outras 
			}
			
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}
		
		//Cadastrando um arquivo - curriculo
		if(file.getSize() > 0) { //se tiver arquivo
			pessoa.setCurriculo(file.getBytes()); //seta em curriculo
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		}else {
			if(pessoa.getId() != null && pessoa.getId() > 0) { //editando
				
				Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
				
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
			}
		}
		
		pessoaRepository.save(pessoa);

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));//procurar na paginação
		
		andView.addObject("pessoaobj", new Pessoa());
		//andView.addObject("profissoes", profissaoRepository.findAll());
		
		return andView;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		
		andView.addObject("pessoaobj", new Pessoa());
		//andView.addObject("profissoes", profissaoRepository.findAll());
		
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa,
			@PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		String nomepesquisa = pessoa.get().getNome();
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable));
		modelAndView.addObject("pessoaobj", pessoa.get());
		//modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		
		return modelAndView;
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		
		modelAndView.addObject("pessoaobj", new Pessoa());
		//modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}
	
	//método de donwload não tem retorno, é sempre void - somente seta o arquivo na resposta que o navegador entende e baixa
	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		
		//Consultar o objeto pessoa no banco de dados
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		
		if(pessoa.getCurriculo() != null) {
			
			//Setar tamanho da resposta
			response.setContentLength(pessoa.getCurriculo().length);
			
			//Tipo do arquivo para download ou pode ser generica usando application/octet-stream
			response.setContentType(pessoa.getTipoFileCurriculo());
			
			//Define o cabeçalho da resposta
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			
			//Finaliza a resposta passando o arquivo
			response.getOutputStream().write(pessoa.getCurriculo());
			
		}
		
	}
	
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa, 
			@RequestParam("pesqsexo") String pesqsexo,
			@PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas = null;
		
		
		if((nomepesquisa != "") && pesqsexo=="") {
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}else if (pesqsexo != "" && (nomepesquisa.isEmpty() || nomepesquisa == null)){
			pessoas = pessoaRepository.findPessoaBySexoPage(pesqsexo, pageable);
		}else if (pesqsexo != "" && !nomepesquisa.isEmpty()){
			pessoas = pessoaRepository.findPessoaByNameSexoPage(nomepesquisa, pesqsexo, pageable);
		}
	
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		return modelAndView;
	}
	
	@GetMapping("**/pesquisarpessoa") //fazer o download em pdf da lista de pessoas
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa, 
			@RequestParam("pesqsexo") String pesqsexo, 
			HttpServletRequest request, HttpServletResponse response) throws Exception{
	
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesqsexo != null && !pesqsexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
		
		}else if(nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
			
		}else if(pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexo(pesqsexo);
		
		}else { //Busca todos
			Iterable<Pessoa> iterator = pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")));
			for(Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		
		//chamar o sreviço que faz a geração do relatório
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		//tamanho da resposta para o navegador
		response.setContentLength(pdf.length);

		//definir na resposta o tipo de arquivo
		response.setContentType("application/octet-stream");
		
		//definir o cabeçalho da resposta
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment: filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		//finaliza a resposta para o navegador
		response.getOutputStream().write(pdf);
		
	}
}
