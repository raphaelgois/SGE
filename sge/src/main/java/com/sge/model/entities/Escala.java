package com.sge.model.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table (name="escala")
public class Escala implements Serializable{
    private static final long SerialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(name="escala_id", nullable = false)
    private Integer escala_id;
    @Column (name ="escala_name",nullable = false, length = 80)
    private String escala_name;
    @Column (name ="start_date",nullable = false, length = 80)
    private String start_date;
    @Column (name ="end_date",nullable = false, length = 80)
    private String end_date;
    @Column (name ="aprovacao",nullable = true, length = 80)
    private String aprovacao;

    public String getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(String aprovacao) {
        this.aprovacao = aprovacao;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.escala_id);
        hash = 89 * hash + Objects.hashCode(this.escala_name);
        hash = 89 * hash + Objects.hashCode(this.start_date);
        hash = 89 * hash + Objects.hashCode(this.end_date);
        hash = 89 * hash + Objects.hashCode(this.aprovacao);
        return hash;
    }

    public Escala() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Escala other = (Escala) obj;
        if (!Objects.equals(this.escala_id, other.escala_id)) {
            return false;
        }
        if (!Objects.equals(this.escala_name, other.escala_name)) {
            return false;
        }
        if (!Objects.equals(this.start_date, other.start_date)) {
            return false;
        }
        if (!Objects.equals(this.end_date, other.end_date)) {
            return false;
        }
        if (!Objects.equals(this.aprovacao, other.aprovacao)) {
            return false;
        }
        return true;
    }

    public Integer getEscala_id() {
        return escala_id;
    }

    public void setEscala_id(Integer escala_id) {
        this.escala_id = escala_id;
    }

    public String getEscala_name() {
        return escala_name;
    }

    public void setEscala_name(String escala_name) {
        this.escala_name = escala_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
