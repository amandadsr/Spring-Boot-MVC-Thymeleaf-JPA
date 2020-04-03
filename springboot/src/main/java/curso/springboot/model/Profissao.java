package curso.springboot.model;


public enum Profissao {

	ADVOGADO("Advogado"),
	ENGENHEIRO("Engenheiro"),
	MEDICO("Médico"),
	PROFESSOR("Professor"),
	PROGRAMADOR("Programador"),
	OUTRO("Outro");
	
	private String nome;

	private Profissao(String nome) {
		this.nome = nome;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
