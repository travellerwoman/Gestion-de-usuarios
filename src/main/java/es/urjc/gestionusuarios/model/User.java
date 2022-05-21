package es.urjc.gestionusuarios.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String login;
    private String password;
    private String nombre;
    private Date alta;
    private boolean active;
    private float saldo;
    private float saldoRetenido;


    public User(){

    }

    public User(String login, String password, String nombre, Date alta, boolean active, long saldo) {
        super();
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.alta = alta;
        this.active = active;
        this.saldo = saldo;
        this.saldoRetenido = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getAlta() {
        return alta;
    }

    public void setAlta(Date alta) {
        this.alta = alta;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public float getSaldoRetenido() {
        return saldoRetenido;
    }

    public void setSaldoRetenido(float saldoRetenido) {
        this.saldoRetenido = saldoRetenido;
    }

    @Override
    public String toString() {
        return "Bici{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", alta=" + alta +
                ", active=" + active +
                ", saldo=" + saldo +
                '}';
    }
}
