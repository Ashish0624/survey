package com.ncert.survey.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ncert.survey.businessMgr.RedisMgrImpl;
import com.ncert.survey.businessMgr.SurveyMgrImpl;

@Configuration
public class SecurityConfig
    extends
    WebSecurityConfigurerAdapter
{
    @Value("${redis.IP}")
    private String redisIP;
    @Value("${redis.password}")
    private String redisPassword;
    @Override
    protected void configure( HttpSecurity http )
        throws Exception
    {
        http.csrf().ignoringAntMatchers( "/custom/**", "/api/**" );
        http.csrf().requireCsrfProtectionMatcher( new AntPathRequestMatcher( "**/login" ) ).and().authorizeRequests()
                .antMatchers( "/dashboard" ).hasRole( "USER" ).and().formLogin().defaultSuccessUrl( "/dashboard" )
                .loginPage( "/login" ).and().logout().permitAll();
        http.headers().contentTypeOptions().and().xssProtection().and().cacheControl().and().frameOptions().and()
                .addHeaderWriter( new StaticHeadersWriter( "X-Content-Security-Policy", "script-src 'self'" ) );
        http.headers().httpStrictTransportSecurity().includeSubDomains( true ).maxAgeInSeconds( 31536000 );

    }
    /*
     * @Bean public PasswordEncoder passwordEncoder() { return new
     * BCryptPasswordEncoder(); }
     * 
     * @Bean public APPMgrImpl appMgr() { return new APPMgrImpl(); }
     */

    @Bean
    public SurveyMgrImpl surveyMgrImpl()
    {
        return new SurveyMgrImpl();
    }

    @Bean
    RedissonClient redisson()
    {
        Config config = new Config();
        // use single Redis server
        config.useSingleServer().setAddress( "redis://" + redisIP ).setPassword( redisPassword );
        RedissonClient redisson = Redisson.create( config );
        return redisson;
    }

    @Bean
    public RedisMgrImpl redisMgrImpl()
    {
        return new RedisMgrImpl( redisson() );
    }

    @Override
    public void configure( WebSecurity web )
        throws Exception
    {
        web.ignoring().antMatchers( "/*.css" );
        web.ignoring().antMatchers( "/*.js" );
    }

    @Bean
    public CorsFilter corsFilter()
    {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //        config.setAllowCredentials( true );
        config.addAllowedOrigin( "*" );
        config.addAllowedHeader( "*" );
        config.addAllowedMethod( "OPTIONS" );
        config.addAllowedMethod( "GET" );
        config.addAllowedMethod( "POST" );
        config.addAllowedMethod( "PUT" );
        config.addAllowedMethod( "DELETE" );
        source.registerCorsConfiguration( "/**", config );
        return new CorsFilter( source );
    }
}