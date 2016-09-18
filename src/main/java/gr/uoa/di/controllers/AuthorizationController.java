package gr.uoa.di.controllers;

import gr.uoa.di.entities.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Angelos on 9/18/2016.
 */
@RestController
public class AuthorizationController {

    @ApiOperation(value = "Registers user with give username,name,surname & password", tags = "Authorization")
    @RequestMapping(value = "/register", method = RequestMethod.GET )
    public String register(@RequestParam(value="username") String username, @RequestParam(value="name") String name, @RequestParam(value="surname") String surname, @RequestParam(value="password") String password) {
        User user = new User(username,name,surname,password);
        //check if username exists in database
        // if not save in database and send success message
        return "User with details" + user.toString() + " is saved in database";
    }

    @ApiOperation(value = "Login user with given username and password", tags = "Authorization")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        //check if username exists in database with given password
        // if true send success message
        return username +" is found in database";
    }
}
