package es.urjc.gestionusuarios.utils;

import es.urjc.gestionusuarios.model.User;
import es.urjc.gestionusuarios.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.util.Arrays;

@Component
@Profile("local")
public class DatabasesPopulator {

    private Logger log = LoggerFactory.getLogger(DatabasesPopulator.class);

    @Autowired
    private UserService userService = new UserService();


    @PostConstruct
    public void populateDB() {

        userService.saveAll(
            Arrays.asList(
                new User("user1", "mypass1", "Felipe Garcia", userService.formatDate(14, 06, 2022), true, 0),
                new User("user2", "mypass2", "Miriam Perez", userService.formatDate(07, 11, 2000), true, 300),
                new User("user3", "mypass3", "Oscar Ramos", userService.formatDate(25, 10, 1995), false, 50),
                new User("user4", "mypass4", "Cristina Lopez", userService.formatDate(01, 03, 1983), true, 3093480),
                new User("user5", "mypass5", "Ane Perez", userService.formatDate(15, 03, 2014), false, 10)
            )
        );

    }
}
