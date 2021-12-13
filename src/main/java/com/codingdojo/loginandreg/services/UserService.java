package com.codingdojo.loginandreg.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.codingdojo.loginandreg.models.LoginUser;
import com.codingdojo.loginandreg.models.User;
import com.codingdojo.loginandreg.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;
    
    
	public User oneUser(Long id) {
		Optional<User> optionalUser = userRepo.findById(id);
		if(optionalUser.isPresent()) {
			return optionalUser.get();
		}else {
			return null;
		}
		
	}
    
    //This is for Registration
    public User register(User newUser, BindingResult result) {
    	//If check to see if the new User's email isPresent() if it exists
        if(userRepo.findByEmail(newUser.getEmail()).isPresent()) {
        	//Send Error message if we fidn their email in the DB
            result.rejectValue("email", "Unique", "This email is already in use!");
        }
        // If check to see is the password matches the confirm password
        if(!newUser.getPassword().equals(newUser.getConfirm())) {
        	//if they do not match send error message to the front end 
            result.rejectValue("confirm", "Matches", "The Confirm Password must match Password!");
        }
        //If there are any errors at all, they get nothing 
        if(result.hasErrors()) {
            return null;
        } else {
        	//If everything is all good, hash Pw, save the password and save hash password
            String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
            newUser.setPassword(hashed);
            return userRepo.save(newUser);
        }
    }
    
    
    //This is for logging in 
    public User login(LoginUser newLogin, BindingResult result) { 	
        if(result.hasErrors()) {
            return null;
        }
        //
        
        Optional<User> potentialUser = userRepo.findByEmail(newLogin.getEmail());
        if(!potentialUser.isPresent()) {
        	//If there is no email matching in the Db - Send the customer error message 
            result.rejectValue("email", "Unique", "Unknown email!");
            return null;
        }
        // Grabs the instance of the user based on the email that we found a match
        /// for in the DB
        User user = potentialUser.get();
        // Check to see if passwords match
        if(!BCrypt.checkpw(newLogin.getPassword(), user.getPassword())) {
        	//If they do not -Send message foward
            result.rejectValue("password", "Matches", "Invalid Password!");
        }
        // If errors at all they get nothing 
        if(result.hasErrors()) {
            return null;
        } else {
        	// IF no errors they get their information
            return user;
        }
    }



    
}
