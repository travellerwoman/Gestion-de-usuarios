package es.urjc.gestionusuarios.service;

import es.urjc.gestionusuarios.model.User;
import es.urjc.gestionusuarios.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class UserService {
    private ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong(1);

    @Autowired
    private UsersRepository userRepository;

    public UserService(){

    }

    public void save(User user) {
        if(user.getId() == null || user.getId() == 0) {
            System.out.println(users);
            long id = nextId.getAndIncrement();
            user.setId(id);
            users.put(user.getId(), user);
            userRepository.save(user);
        }
    }

    public void saveAll(List<User> users){
        for (User user : users) {
            save(user);
        }
    }

    public Date formatDate(int day, int month, int year) {
        try{
            String myDate = day+"/"+month+"/"+year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(myDate);
            long millis = date.getTime();
            return new Date(millis);
        } catch (ParseException e){
            System.err.println("Cannot parse: "+day+"/"+month+"/"+year);
            throw new RuntimeException("Cannot parse: "+day+"/"+month+"/"+year);
        }
    }

    public Collection<User> findAll(){
        return users.values();
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public void deleteById(Long id){
        users.remove(id);
    }

    public void update(User user) {
        users.put(user.getId(), user);
        userRepository.save(user);
    }
}
