package com.sge.model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javassist.SerialVersionUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.ForeignKey;

@Entity
@Table (name="pessoa")
public class Pessoa implements Serializable{
    private static final long SerialVersionUID = 1L;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.idPessoa);
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
        final Pessoa other = (Pessoa) obj;
        if (!Objects.equals(this.idPessoa, other.idPessoa)) {
            return false;
        }
        return true;
    }

    public Pessoa() {
    }
    
    @Id
    @GeneratedValue
    @Column(name="IdPessoa", nullable = false)
    private Integer idPessoa;
    @Column (name ="Nome",nullable = false, length = 80)
    private String nome;
    @Column (name ="Sobrenome",nullable = false, length = 200)
    private String sobrenome;
    @Column (name ="Email",nullable = false, length = 80)
    private String email;
    @Column (name ="Sexo",nullable = false, length = 20)
    private String sexo;
    @Column (name ="Data",nullable = false)
    @Temporal (javax.persistence.TemporalType.DATE)
    private Date dataNascimento;
    
    @ManyToOne(optional = false)
    @ForeignKey(name = "PessoaPermission")
    private Pessoa pessoa;
    public void setIdPessoa(Integer idPessoa) {
        this.idPessoa = idPessoa;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getIdPessoa() {
        return idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getSexo() {
        return sexo;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }
}
