package com.sge.model.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;

@Entity
@Table (name="user_permissions")
public class user_permissions implements Serializable{
    private static final long SerialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column (name="id", unique = true, nullable = false)
    private Integer id;
    @Column (name="user_permission", nullable = false, length = 20)
    private String user_permission;

    @OneToMany (mappedBy = "user_permissions", fetch = FetchType.LAZY)
    @ForeignKey(name = "PessoaPermission")
    private List <Pessoa> pessoas;

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }
    
    public user_permissions() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser_permission() {
        return user_permission;
    }

    public void setUser_permission(String user_permission) {
        this.user_permission = user_permission;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        hash = 59 * hash + Objects.hashCode(this.user_permission);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final user_permissions other = (user_permissions) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.user_permission, other.user_permission)) {
            return false;
        }
        return true;
    }
    
    
}
