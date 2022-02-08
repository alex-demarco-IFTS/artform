package it.artform.pojos;

public class Utente {
	
	//private static int nextUtente = 1;
	private int id;
	private String nome, cognome, username, email, numeroTelefono, password;
	private int punteggio;

	// public Utente(int ID, String nome, String cognome, String username, String numeroTelefono, String password) {
	public Utente() {
		
	}
	public Utente(int id, String nome, String cognome, String username, String email, String numeroTelefono, String password, int punteggio) {
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.numeroTelefono = numeroTelefono;
		this.password = password;
		this.punteggio = punteggio;
		//nextUtente++;
	}
/*
	public static int getNextUtente() {
		return nextUtente;
	}
*/
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumeroTelefono() {
		return numeroTelefono;
	}

	public void setNumeroTelefono(String numeroTelefono) {
		this.numeroTelefono = numeroTelefono;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getPunteggio() {
		return punteggio;
	}
	
	public void setPunteggio(int punteggio) {
		this.punteggio = punteggio;
	}
	
	public void incrementaPunteggio(int punteggio) {
		this.punteggio += punteggio;
	}

	public int getID() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Utente [ID=" + id + ", nome=" + nome + ", cognome=" + cognome + ", username=" + username + ", email="
				+ email + ", numeroTelefono=" + numeroTelefono + ", password=" + password + "]";
	}

}