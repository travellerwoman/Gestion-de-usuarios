package es.urjc.gestionusuarios.service;

import es.urjc.gestionusuarios.model.Payment;
import es.urjc.gestionusuarios.model.User;
import es.urjc.gestionusuarios.model.UserCreationDTO;
import es.urjc.gestionusuarios.model.UserDTO;
import es.urjc.gestionusuarios.repositories.UsersRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private UsersRepository userRepository;

    public UserService(){

    }

    public User save(User user) {
        if(user.getId() == null || user.getId() == 0) {
            return userRepository.save(user);
        } throw new ObjectNotFoundException("User id not found", "id");
    }

    public User save(UserCreationDTO user) {
        User user1 = new User(user, new Date());
        return userRepository.save(user1);
    }

    public void saveAll(List<User> users){
        for (User user : users) {
            save(user);
        }
    }

    public Date formatDate(int day, int month, int year) throws ParseException {
        String myDate = day+"/"+month+"/"+year;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse(myDate);
        long millis = date.getTime();
        return new Date(millis);
    }

    public Collection<UserDTO> findAll(){
        Collection<UserDTO> userDTOS = new HashSet<>();
        for (User user : userRepository.findAll()) {
            userDTOS.add(new UserDTO(user));
        }
        return userDTOS;
    }

    public User findById(Long id) {
        if (userRepository.existsById(id)){
            return userRepository.findById(id).get();
        }
        throw new ObjectNotFoundException(id, "User");
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public User update(Long id, UserCreationDTO userCreationDTO) {
        if (userRepository.existsById(id)) {
            User originalUser = userRepository.getById(id);
            User user1 = new User(userCreationDTO, originalUser.getAlta());
            user1.setId(originalUser.getId());
            user1.setActive(originalUser.isActive());
            return userRepository.save(user1);
        } throw new ObjectNotFoundException("Could not find user ID", "id");
    }

    public boolean bookBike(User user, Payment payment){
        long paymentAmount = payment.getAmount();
        float paymentFee = paymentAmount*3;
        float saldo = user.getSaldo();
        if (user.isActive() && saldo>paymentFee) {
            user.setSaldo(saldo - paymentFee);
            user.setSaldoRetenido(paymentAmount * 2);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void returnBike(User user){
        float saldoRetenido = user.getSaldoRetenido();
        user.setSaldo(user.getSaldo()+saldoRetenido);
        user.setSaldoRetenido(0);
        userRepository.save(user);
    }

    public void bajaUsuario(User user) {
        user.setActive(false);
        userRepository.save(user);
    }
}
