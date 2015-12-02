package com.sge.controller;

import com.sge.conversores.ConverterSHA1;
import com.sge.model.dao.HibernateDAO;
import com.sge.model.dao.InterfaceDAO;
import com.sge.model.entities.Pessoa;
import com.sge.util.FacesContextUtil;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean(name="mbPessoa")
@SessionScoped
public class MbPessoa implements Serializable{
    private static final long serialVersionUID = 1L;
    private String confereSenha;
    
    
    public String getConfereSenha() {
        return confereSenha;
    }

    public void setConfereSenha(String confereSenha) {
        this.confereSenha = confereSenha;
    }
    private Pessoa pessoa = new Pessoa();
    private List<Pessoa> pessoas;

    public MbPessoa() {
    }
    
    private InterfaceDAO<Pessoa> pessoaDAO(){
        InterfaceDAO<Pessoa> pesssoaDAO = new HibernateDAO<Pessoa>(Pessoa.class, FacesContextUtil.getRequestSession());
        return pesssoaDAO;
    }
    public String limpaPessoa(){
        return "/rh/usuariosCadastrados.xhtml";
    }
    public String editPessoa(){
        return "/rh/cadastrarUsuario.xhtml";
    }
    
    public String addPessoa(){
        if (pessoa.getIdPessoa()== null || pessoa.getIdPessoa()==0){
            insertPessoa();
        }else{
            updatePessoa();
        }
        limpaPessoa();
        return null;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getPessoas() {
        pessoas = pessoaDAO().getEntities();
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    private void insertPessoa() {
        pessoa.setSenha(ConverterSHA1.cipher(pessoa.getSenha()));
        if (pessoa.getSenha() == null ? confereSenha == null : pessoa.getSenha().equals(ConverterSHA1.cipher(confereSenha))) {
            if (pessoa.getTipo().equals("Gestor")){
                pessoa.setPermissao("ROLE_ADMIN");
            } else if (pessoa.getTipo().equals("RH")){
                pessoa.setPermissao("ROLE_RH");
            } else {
                pessoa.setPermissao("ROLE_USER");
            }
            pessoaDAO().save(pessoa);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Gravação efetuada com sucesso", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "As senhas não conferem.", ""));
        }
    }

    private void updatePessoa() {
        pessoaDAO().update(pessoa);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Atualização efetuada com sucesso", ""));
    }
    public void deletePessoa(){
        pessoaDAO().remove(pessoa);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Remoção efetuada com sucesso", ""));
    }

}
