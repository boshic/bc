package barcode.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        private DataSource dataSource;

        public WebSecurityConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Autowired
        public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication().dataSource(dataSource)
                    .passwordEncoder(passwordEncoder())
                    .usersByUsernameQuery("select name, password, active from user where name=?")
                    .authoritiesByUsernameQuery("select name, role from user where name=?");
        }

        @Bean
        public PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
            http
                .authorizeRequests()
                .antMatchers("/findComingItemByFilter",
                        "/tools/getHashedPass",
                        "/tools/pdfreport",
                        "/makeAutoMovings",
                        "/public/**",
                        "/webjars/fe/0.1.0/css/public**/**",
                        "/",
                        "/addAutoSellings",
                        "/addComings",
                        "/home",
                        "/getInvoicesFor1c").permitAll()
//                .antMatchers("/addComings").hasRole("ADMIN")
//                    .access("hasRole('ADMIN')")
                .anyRequest().authenticated()
                    .and().formLogin().loginPage("/login")
//                    .successHandler(new SecuritySuccessHandler())
                    .failureHandler(new SecurityErrorHandler()).permitAll()
                    .and().logout().permitAll()
                    .and().sessionManagement()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                    .sessionRegistry(sessionRegistry());
        }

        @Bean(name = "sessionRegistry")
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

    }
