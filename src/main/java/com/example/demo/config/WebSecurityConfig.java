package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.example.demo.service.UserDetailsServiceImpl;

/**
 * trong vd này: Springboot security vẫn dùng sessionId  lấy của Servlet.
 * Tuy nhiên có thể config dùng sessionId độc lập với Servlet. Phần config session là độc lập với phần Springboot Security dưới đây.
 *  https://docs.spring.io/spring-session/docs/current/reference/html5/
 * 
 *  Tóm lại: dùng AuthenticationManagerBuilder singleton và hàm configure() của WebSecurityConfigurerAdapter để thiết lập Security
 */
@Configuration    //thay cho file bean.xml => class này chứa Bean cần khởi tạo.
@EnableWebSecurity  //Springboot Security sẽ gọi singleton nay
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	//============================================================================
	//
	// Các Bean dưới đây để cung câp thong tin cho Springboot Security thôi
	//
	//============================================================================
	@Autowired  //refer to singleton
	private UserDetailsServiceImpl userDetailsService; //lấy thông tin user từ Database

	@Autowired  //refer to Bean singleton
	private DataSource dataSource;


	@Bean    //Mặc định nếu @bean ko khai báo @scope thì là singleton.
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		System.out.println(" ************** step1: WebSecurityConfig " );
		return bCryptPasswordEncoder;
	}



	/**
	 * gắn cố định token với user.
	 * Phần này ko nên dùng
	 */
	@Bean //Mặc định nếu @bean ko khai báo @scope thì là singleton.
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}

	/*=======================================================================================
	 * @Autowired: hàm này đc gọi lúc khởi tạo instance của Class này => vì thế gọi hàm này trc hàm config()
	 * AuthenticationManagerBuilder bean là singleton.
	 * dùng AuthenticationManagerBuilder với Dependency Injection parameter để truyền tham số vào hệ thống với Interface chuẩn của Springboot Security
	 * 
	 * Tóm lại: dùng AuthenticationManagerBuilder singleton và hàm configure() của WebSecurityConfigurerAdapter để thiết lập Security
	 *=========================================================================================
	 */
	@Autowired //refer to Bean singleton của Springboot Security để Overide
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		// step1: find user in userDetailsService
		// step2: passwordEncoder sẽ cung cấp function để kiểm tra 2 password có giống nhau ko
		//        BCryptPasswordEncoder.matches(CharSequence rawPassword, String encodedPassword) 
		auth.userDetailsService(userDetailsService)
		    .passwordEncoder(passwordEncoder());

		System.out.println(" ************** step2: WebSecurityConfig " );
	}

/*	// for testing
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		//thông tin authen đc lưu ở memory
		auth.inMemoryAuthentication()
		     .withUser(User
				.withDefaultPasswordEncoder()
			     .username("user")
			     .password("password")
				 .roles("ADMIN")
				.build());  //User.class là builder để build UserDetails
	}
*/
	
	/*=======================================================================================
	 * 
	 * Tất cả config là ở hàm @Overide này. 
	 * Các @Bean ở trên là để cung cấp thông tin cho phần config trong function này
	 *=========================================================================================
	 */
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
		.usernameParameter("username")  	// "username" field trong url = /login
		.passwordParameter("password")		// "password" field trong url = /login
		// Config for Logout Page
		.and().logout().logoutUrl("/logout").logoutSuccessUrl("/logoutSuccessful");

		// Config Remember Me.
		http.authorizeRequests().and() //
		.rememberMe().tokenRepository(this.persistentTokenRepository()) //
		.tokenValiditySeconds(1 * 24 * 60 * 60); // 24h

	}



}
