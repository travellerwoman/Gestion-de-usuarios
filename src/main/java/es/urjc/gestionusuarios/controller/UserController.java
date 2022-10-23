package es.urjc.gestionusuarios.controller;

import es.urjc.gestionusuarios.model.Payment;
import es.urjc.gestionusuarios.model.User;
import es.urjc.gestionusuarios.model.UserCreationDTO;
import es.urjc.gestionusuarios.model.UserDTO;
import es.urjc.gestionusuarios.service.UserService;
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
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid url supplied",
                    content = @Content)})
    public Collection<UserDTO> getUsers(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Devuelve el Usuario con el ID adecuado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "Id del usuario")@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null) {
            return ResponseEntity.ok(new UserDTO(user));
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
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid body supplied",
                    content = @Content) })
    public ResponseEntity<UserDTO> createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User with new parameters to be modified (exclcluding the id)") @RequestBody UserCreationDTO user){
        User newUser = userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(location).body(new UserDTO(newUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Pasa el estado del usuario a inactivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> deleteUser(@Parameter(description = "Id del usuario a dar de baja")@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null){
            // Solo queremos que pase a estado inactivo no borrarlo de verdad
            userService.bajaUsuario(user);
            return ResponseEntity.ok(new UserDTO(user));
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
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> replaceUser(@Parameter(description = "Id del usuario") @PathVariable Long id, @RequestBody UserCreationDTO newUser){
        User user = userService.findById(id);

        if (user != null){
            // Guardamos solo la id del user vieja, no nos interesa la nueva
            return ResponseEntity.ok(new UserDTO(userService.update(id, newUser)));
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
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied, not text body that can be converted to float or not enough money",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> payBooking(
            @Parameter(description = "id del usuario") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Amount of money that renting the bike costs (without fee)") @RequestBody Payment paymentQuantity){
        User user = userService.findById(id);
        if (user != null){
            if (userService.bookBike(user, paymentQuantity)){
                return ResponseEntity.ok(new UserDTO(user));
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/{id}/deposit")
    @Operation(summary = "Devuelve una bicicleta (Todo el dinero retenido vuelve al saldo del usuario)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    public ResponseEntity<UserDTO> payMoneyBack(
            @Parameter(description = "id del usuario")@PathVariable Long id){
        User user = userService.findById(id);

        if (user != null){
            userService.returnBike(user);
            return ResponseEntity.ok(new UserDTO(user));
        }
        return ResponseEntity.notFound().build();
    }
}
