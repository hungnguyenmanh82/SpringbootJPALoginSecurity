package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.example.demo.service.UserDetailsServiceImpl;

/**
 * đây chỉ là config thôi. Phần kiểm xoat security thực sự là do HttpSecurity
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService; //lấy thông tin user từ Database
	
	@Autowired
	private DataSource dataSource;


	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		System.out.println(" ************** step1: WebSecurityConfig " );
		return bCryptPasswordEncoder;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		// step1: find user in userDetailsService
		// step2: passwordEncoder sẽ cung cấp function để kiểm tra 2 password có giống nhau ko
		//        BCryptPasswordEncoder.matches(CharSequence rawPassword, String encodedPassword) 
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
		System.out.println(" ************** step2: WebSecurityConfig " );
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		System.out.println(" ************** step3: WebSecurityConfig " );
		
		/**
		 * HttpSecurity  sẽ đóng vai trò bộ lọc và xử lý các vấn để security
		 */
		http.csrf().disable();

		// The pages does not require login
		http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();

		// /userInfo page requires login as ROLE_USER or ROLE_ADMIN.
		// If no login, it will redirect to /login page.
		http.authorizeRequests().antMatchers("/userInfo").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");

		// For ADMIN only.
		http.authorizeRequests().antMatchers("/admin").access("hasRole('ROLE_ADMIN')");

		// When the user has logged in as XX.
		// But access a page that requires role YY,
		// AccessDeniedException will be thrown.
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		// Springboot Security nhớ cả URL trc khi gọi login để quay trở lại nó khi login thành công:  /userInfo và /admin
		// mỗi khi có /j_spring_security_check thì Springboot lại gọi userDetailsService để check security
		// Config for Login Form
		// .and()  là bắt đầu 1 filter mới
		http.authorizeRequests().and().formLogin()//
		// Submit URL of login page
		.loginProcessingUrl("/j_spring_security_check") // POST html request from HTML form in LoginPage.html
		.loginPage("/login")              // nếu "/userInfo", "/admin" mà chưa đăng nhập thì nhảy vào page này
		.defaultSuccessUrl("/userInfo")  // khi thành công nó sẽ nhảy vào URL trc khi gọi /login là: /userInfo và /admin
		.failureUrl("/login?error=true") // nếu "/j_spring_security_check" fail thì sẽ nhảy vào page này
		.usernameParameter("username")//
		.passwordParameter("password")
		// Config for Logout Page
		.and().logout().logoutUrl("/logout").logoutSuccessUrl("/logoutSuccessful");

		// Config Remember Me.
		http.authorizeRequests().and() //
		.rememberMe().tokenRepository(this.persistentTokenRepository()) //
		.tokenValiditySeconds(1 * 24 * 60 * 60); // 24h

	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}

}
