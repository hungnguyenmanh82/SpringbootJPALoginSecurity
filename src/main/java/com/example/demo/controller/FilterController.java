package com.example.demo.controller;


import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import com.example.demo.utils.WebUtils;

/**
 * Springboot security sử dụng HttpSession của Servlet => trên các @controller thì SessionId lấy từ cookies đều giống nhau (đã test)
 *
 */
@Controller
public class FilterController {
  

    @RequestMapping(path = "/filterRedirect", method = RequestMethod.GET)
        //de tra ve kieu String
    public @ResponseBody String testFilter1(HttpSession session){    	

    	//source code here is not reached because filter redirect to "redirectTest.html"
        return "filter redirect";
    }
    
    @RequestMapping(value = "/redirectTest", method = RequestMethod.GET)
    public String testRedirect(Model model) {
  
        return "filter/redirectTest";
    }
    
    //============================= forward
    @RequestMapping(value = "/filterForward", method = RequestMethod.GET)
    public @ResponseBody String testFilter2(Model model) {
          
    	//source code here is not reached because filter forward to "forwardTest.html"
    	return "filter forward";
    }
    
    @RequestMapping(value = "/forwardTest", method = RequestMethod.GET)
    public String testForward(Model model) {
  
        return "filter/forwardTest";
    }
    

}
