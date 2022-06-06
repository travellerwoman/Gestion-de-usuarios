package es.urjc.gestionusuarios.model;

import java.util.Date;

public class UserDTO {

    private Long id;

    private String login;
    private String password;
    private String nombre;
    private float saldo;
    private float saldoRetenido;

    public UserDTO() {
    }

    public UserDTO(Long id, String login, String password, String nombre, float saldo, float saldoRetenido) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.saldo = saldo;
        this.saldoRetenido = saldoRetenido;
    }

    public UserDTO(User user){
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.nombre = user.getNombre();
        this.saldo = user.getSaldo();
        this.saldoRetenido = user.getSaldoRetenido();
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
}
