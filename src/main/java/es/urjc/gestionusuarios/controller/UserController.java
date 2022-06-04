package es.urjc.gestionusuarios.controller;

import es.urjc.gestionusuarios.model.Payment;
import es.urjc.gestionusuarios.model.User;
import es.urjc.gestionusuarios.service.UserService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Devuelve todos los Usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Users accessed and returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid url supplied",
                    content = @Content)})
    public Collection<User> getUsers(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Devuelve el Usuario con el ID adecuado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<User> getUserById(@Parameter(description = "Id del usuario")@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    @Operation(summary = "Crea nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid body supplied",
                    content = @Content) })
    public ResponseEntity<User> createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User with new parameters to be modified (exclcluding the id)") @RequestBody User user){
        userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Pasa el estado del usuario a inactivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<User> deleteUser(@Parameter(description = "Id del usuario a dar de baja")@PathVariable Long id){
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
    @Operation(summary = "Cambia los datos del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<User> replaceUser(@Parameter(description = "Id del usuario") @PathVariable Long id, @RequestBody User newUser){
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

    @PostMapping(value = "/{id}/deposit", consumes = "application/json")
    @Operation(summary = "Reserva una bicicleta (Quita dinero al usuario)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied, not text body that can be converted to float or not enough money",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<User> payBooking(
            @Parameter(description = "id del usuario") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Amount of money that renting the bike costs (without fee)") @RequestBody Payment paymentQuantity){
        try {
            float payment = paymentQuantity.getAmount();
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

    @DeleteMapping(value = "/{id}/deposit")
    @Operation(summary = "Devuelve una bicicleta (Todo el dinero retenido vuelve al saldo del usuario)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<User> payMoneyBack(
            @Parameter(description = "id del usuario")@PathVariable Long id){
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
