package es.urjc.gestionusuarios.repositories;

import es.urjc.gestionusuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findById(long id);
    List<User> findAllById(long id);

}
