package com.tcc.smsdecrypt.requisicoes;

/**
 * Created by claudinei on 31/10/17.
 * "\"nome\"" + ":\"" + name + "\"," +
 "\"email\"" + ":\"" + email + "\"," +
 "\"celular\"" + ":\"" + mobile + "\"," +
 "\"password\"" + ":\"" + encoder.encode(password) + "\"," +
 "\"endereco\"" + ": " + "{ " +
 "\"logradouro\"" + ":\"" +  logradouro + "\"," +
 "\"numero\"" + ":\"" +  numero + "\"," +
 "\"complemento\"" + ":\"" +  complemento + "\"," +
 "\"bairro\"" + ":\"" +  bairro + "\"," +
 "\"cep\"" + ":\""  + cep + "\"," +
 "\"cidade\"" + ":\"" +  cidade + "\"," +
 "\"estado\"" + ":\"" +  estado + "\"" +
 "}," +
 "\"ativo\"" + ":\"" + "true\"" +
 "}";
 */

public class UserRegistrate {
    String codPessoa;
    String nome;
    String email;
    String celular;
    String password;
    String logradouro;
    String numero;
    String complemento;
    String bairro;
    String cep;
    String cidade;
    String estado;
    boolean ativo;

    public UserRegistrate(String codPessoa, String nome, String email, String celular, String password, String logradouro, String numero, String complemento, String bairro, String cep, String cidade, String estado, boolean ativo) {
        this.codPessoa = codPessoa;
        this.nome = nome;
        this.email = email;
        this.celular = celular;
        this.password = password;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.ativo = ativo;
    }

    public String getCodPessoa() {
        return codPessoa;
    }

    public void setCodPessoa(String codPessoa) {
        this.codPessoa = codPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
