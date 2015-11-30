package com.sge.suport;

import com.dhtmlx.planner.DHXPlanner;
import com.dhtmlx.planner.DHXSkin;
import com.dhtmlx.planner.data.DHXDataFormat;
import com.sge.model.entities.Pessoa;
import com.sge.util.FacesContextUtil;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;

import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@ManagedBean(name="BbUsuarioLogado")
@SessionScoped
public class BbUsuarioLogado implements Serializable{
    private static final long SerialVersionUID = 1L;
    private Pessoa usuario;
    public BbUsuarioLogado() {
        usuario = new Pessoa();
        SecurityContext context = SecurityContextHolder.getContext(); 
        if (context instanceof SecurityContext){
            Authentication authentication = context.getAuthentication();
            if (authentication instanceof Authentication){
                usuario.setLogin(((User) authentication.getPrincipal()).getUsername());
            }
        }
    }
    public Pessoa  procuraPessoa(){
        String login = getLoginUsuarioLogado();
        Session session = FacesContextUtil.getRequestSession();
        Query query = session.createQuery("from Pessoa user where user.login like ?");
        query.setString(0, login);
        return (Pessoa) query.uniqueResult();
    }

    private String getLoginUsuarioLogado() {
       return usuario.getLogin();
    }
    public void logout(){
        
    }
     String getPlanner(HttpServletRequest request) throws Exception{
		DHXPlanner s = new DHXPlanner("../codebase/", DHXSkin.TERRACE);
		s.setWidth(900);
		s.setInitialDate(2015,0,21);
		s.load("events.jsp", DHXDataFormat.JSON);
		s.data.dataprocessor.setURL("events.jsp");
		return s.render();
}
}
