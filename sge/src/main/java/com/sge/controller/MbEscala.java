package com.sge.controller;

import com.sge.conversores.ConverterSHA1;
import com.sge.model.dao.HibernateDAO;
import com.sge.model.dao.InterfaceDAO;
import com.sge.model.entities.Escala;
import com.sge.model.entities.Pessoa;
import com.sge.util.FacesContextUtil;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@ManagedBean(name="mbEscala")
@SessionScoped
public class MbEscala implements Serializable{
    private static final long serialVersionUID = 1L;
    private Escala escala = new Escala();
    private List<Escala> escalas    ;

    public MbEscala() {
    }
    
    private InterfaceDAO<Escala> escalaDAO(){
        InterfaceDAO<Escala> escalaDAO = new HibernateDAO<Escala>(Escala.class, FacesContextUtil.getRequestSession());
        return escalaDAO;
    }
    
    
    public Escala getEscala() {
        return escala;
    }

    public void setEscala(Escala escala) {
        this.escala = escala;
    }

    public List<Escala> getEscalas() {
        escalas = escalaDAO().getEntities();
        return escalas;
    }

    public void setEscalas(List<Escala> escalas) {
        this.escalas = escalas;
    }

    public void deleteEscala(){
        escalaDAO().remove(escala);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Remoção efetuada com sucesso", ""));
    }
    public void aprovarEscala(){
        escala.setAprovacao("Aprovado");
    }
    public void reprovarEscala(){
        escala.setAprovacao("Reprovado");
    }

}
