package com.example.demo.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * Đây là filter trên Servlet => nen dung code nhu Servlet: forward, redirect, include...
 * Có thể dùng wraper response/request giống hệt filter ở Servlet.
 * 
 * Bỏ source code filter thì Springboot Security vẫn chạy tốt.
 *
 */
public class CustomFilter extends GenericFilterBean {
	 
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    	
    	String uri = ((HttpServletRequest)request).getRequestURI().toString();
    	System.out.println("***** CustomFilter.doFilter: " + uri);
    	
    	//
    	if(uri.equals("/filterRedirect")){
    		//forwardTest.html => can khai bao trong Controller
    		((HttpServletResponse)response).sendRedirect("redirectTest");
    	}else if(uri.equals("/filterForward")){
    		((HttpServletRequest)request).getRequestDispatcher("/forwardTest").forward(request, response);
    	}else{
    		//có thể dùng Wraper response/request ở đây
    		//Spring Security sẽ dùng request và response nay
    		chain.doFilter(request, response); //cho request đi qua
    	}
        
    }
}

