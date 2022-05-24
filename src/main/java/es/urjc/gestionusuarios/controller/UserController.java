package es.urjc.gestionusuarios.controller;

import es.urjc.gestionusuarios.model.User;
import es.urjc.gestionusuarios.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService = new UserService();

    @GetMapping("/")
    public Collection<User> getUsers(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user){
        userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null){
            // Solo queremos que pase a estado inactivo no borrarlo de verdad
            user.setActive(false);
            return ResponseEntity.ok(user);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> replaceUser(@PathVariable Long id, @RequestBody User newUser){
        User user = userService.findById(id);

        if (user != null){
            // Guardamos solo la id del user vieja, no nos interesa la nueva
            newUser.setAlta(user.getAlta());
            newUser.setId(id);
            userService.update(newUser);
            return ResponseEntity.ok(newUser);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{id}/bikes", consumes = "text/plain")
    public ResponseEntity<User> payBooking(@PathVariable Long id, @RequestBody String paymentQuantity){
        try {
            float payment = Float.parseFloat(paymentQuantity);
            User user = userService.findById(id);
            if (user != null){
                // El usuario tiene que tener el dinero para la reserva y el doble para la fianza
                float paymentFee = payment*3;
                float saldo = user.getSaldo();
                if (user.isActive() && saldo>paymentFee){
                    user.setSaldo(saldo-paymentFee);
                    user.setSaldoRetenido(payment*2);
                    return ResponseEntity.ok(user);
                }
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/{id}/bikes", consumes = {})
    public ResponseEntity<User> payMoneyBack(@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null){
            float saldoRetenido = user.getSaldoRetenido();
            user.setSaldo(user.getSaldo()+saldoRetenido);
            user.setSaldoRetenido(0);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}
