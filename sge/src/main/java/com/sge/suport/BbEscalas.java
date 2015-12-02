package com.sge.suport;

import com.sge.model.entities.Escala;
import com.sge.model.entities.Pessoa;
import com.sge.util.FacesContextUtil;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import org.hibernate.Query;
import org.hibernate.Session;

@ManagedBean(name = "BbEscalas")
@SessionScoped
public class BbEscalas implements Serializable{
    private static final long SerialVersionUID = 1L;

    public Escala procuraEscala() {
        Session session = FacesContextUtil.getRequestSession();
        Query query = session.createQuery("from Escala esc where esc.escala_name like ?");
        return (Escala) query.uniqueResult();
    }

}
