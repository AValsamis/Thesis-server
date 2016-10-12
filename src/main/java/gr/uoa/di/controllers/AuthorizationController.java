package gr.uoa.di.controllers;

import gr.uoa.di.entities.User;
import gr.uoa.di.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthorizationController {

    @Autowired
    private UserRepository userRepository;


    @ApiOperation(value = "Registers user with give username,name,surname & password", tags = "Authorization")
    @RequestMapping(value = "/register/{username}/{name}/{surname}/{password}", method = RequestMethod.GET )
    public String register(@PathVariable(value="username") String username, @PathVariable(value="name") String name, @PathVariable(value="surname") String surname, @PathVariable(value="password") String password) {
        String userId = "";
        try {
            User user = new User(username,name,surname, password);
            userRepository.save(user);
            userId = String.valueOf(user.getId());
        }
        catch (Exception ex) {
            return "Error creating the user: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }


    @ApiOperation(value = "Login user with given username and password", tags = "Authorization")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        //check if username exists in database with given password
        // if true send success message
        return username +" is found in database";
    }
}
