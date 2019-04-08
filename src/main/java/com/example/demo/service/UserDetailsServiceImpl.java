package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AppRoleDAO;
import com.example.demo.dao.AppUserDAO;
import com.example.demo.entity.AppUser;

/**
 * *  Tóm lại: dùng AuthenticationManagerBuilder singleton và hàm @override configure() của WebSecurityConfigurerAdapter để thiết lập Security.
 * 
 *  UserDetailsService singleton là đầu vào của AuthenticationManagerBuilder singleton  (Dependency Injection)
 *
 */
@Service  //singleton
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired  //refer to singleton
    private AppUserDAO appUserDAO;
 
    @Autowired //refer to singleton
    private AppRoleDAO appRoleDAO;
 
    /**
     * Lấy thông tin user từ database => cung cấp cho SpringBoot ở đây
     * @userName: là thông tin ở "username" field trong url = "\login" POST request trả về cho Springboot Security
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    	//step1: userName trả về từ "\login" form of POST request
    	
    	//step2: lấy thông tin user từ data base (or từ đâu đó tùy ý. vd: hard code ở đây vẫn ok)
        AppUser appUser = this.appUserDAO.findUserAccount(userName);
 
        //validate User
        if (appUser == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
 
        System.out.println("Found User: " + appUser);
 
        //step3: get Roles of User từ Database (or từ đâu đó tùy ý. vd: hard code ở đây vẫn ok)
        // [ROLE_USER, ROLE_ADMIN,..]
        List<String> roleNames = this.appRoleDAO.getRoleNames(appUser.getUserId());
 
        //step4: convert User and Role of User to Springboot format Which is  UserDetails
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (roleNames != null) {
            for (String role : roleNames) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }
 
        // nếu để encrypted pass thì String sẽ dài => ????
        UserDetails userDetails = (UserDetails) new User(appUser.getUserName(), //
                appUser.getEncrytedPassword(), grantList);
 
        return userDetails;
    }
 
}
