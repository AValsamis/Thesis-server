package gr.uoa.di.controllers;

import gr.uoa.di.entities.ElderlyResponsible;
import gr.uoa.di.entities.SimpleResponse;
import gr.uoa.di.entities.User;
import gr.uoa.di.repository.ElderlyResponsibleRepository;
import gr.uoa.di.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthorizationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElderlyResponsibleRepository elderlyResponsibleRepository;



    @ApiOperation(value = "Registers user with give username,name,surname & password", tags = "Authorization")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public SimpleResponse register(@RequestBody User user) {
        System.out.println("REGISTER " + user.toString());

            User userFromDB = userRepository.findByUsername(user.getUsername());
            if(userFromDB==null || userFromDB.getUsername()==null) {
                user = userRepository.save(user);
                if(user.getResponsibleUserName()!=null)
                {
                    User responsibleUser = userRepository.findByUsername(user.getResponsibleUserName());
                    ElderlyResponsible elderlyResponsible = new ElderlyResponsible(responsibleUser,user);
                    elderlyResponsibleRepository.save(elderlyResponsible);
                }
            }
            else
                return  new SimpleResponse("User with username "+ user.getUsername() +" already exists in database.",false);
        return new SimpleResponse("User succesfully created with id = " + user.getUserId(),true);
    }


    @ApiOperation(value = "Login user with given username and password", tags = "Authorization")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public SimpleResponse login(@RequestParam(value="username") String username, @RequestParam(value="password") String password, @RequestParam(value="token")String token) {
        System.out.println("LOGIN: "+username+ " "+ password);
        User userFromDB = userRepository.findByUsername(username);
        if(userFromDB==null)
            return new SimpleResponse("Password is incorrect or user was not found in our database",false, false);
        boolean isElderly = false;
        if (elderlyResponsibleRepository.findAssociatedGuardian(userFromDB.getUserId())!=null)
            isElderly = true;
        userFromDB.setToken(token);
        userFromDB = userRepository.save(userFromDB);
        if(userFromDB!=null && userFromDB.getUsername()!=null && userFromDB.getPassword().equals(password))
            return new SimpleResponse(username +" is found in database", true, isElderly);
        else
            return new SimpleResponse("Password is incorrect or user was not found in our database",false, isElderly);
    }
}
