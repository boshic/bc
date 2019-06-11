package barcode;



import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

//@Configuration
//@EnableTransactionManagement
public class PersistenceConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "barcode.dao.entities" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/lamp_vil?useUnicode=true&characterEncoding=utf8&characterSetResults=UTF-8");
        dataSource.setUsername( "admin" );
        dataSource.setPassword( "6012310" );
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");

        return properties;
    }

}


//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
//        LocalContainerEntityManagerFactoryBean em = builder.dataSource(viewerDataSource())
//                .packages("barcode.dao.entity")
//                .persistenceUnit("barcode")
//                .build();
//        return em;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(
//            EntityManagerFactory viewerEntityManagerFactory) {
//        return new JpaTransactionManager(viewerEntityManagerFactory);
//    }
//
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "viewer.dbo.datasource")
//    public DataSource viewerDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "viewer.auth.datasource")
//    public DataSource authDataSource() {
//        return DataSourceBuilder.create().build();
//    }



//
//import org.hibernate.SessionFactory;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.Database;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import java.beans.PropertyVetoException;
//import java.util.Properties;
//
//@Configuration
//@EnableTransactionManagement
//public class SessionFactoryConfig {
//
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource datasource() {
//        return DataSourceBuilder.create().build();
//    }
//}
//
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//        LocalContainerEntityManagerFactoryBean factoryBean =
//                new LocalContainerEntityManagerFactoryBean();
//
//        DataSource dataSource = dataSource();
//
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setPackagesToScan("barcode");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setShowSql(true);
//        //vendorAdapter.setGenerateDdl(generateDdl)
//
//        factoryBean.setJpaVendorAdapter(vendorAdapter);
//
//        Properties additionalProperties = new Properties();
//        additionalProperties.put("hibernate.hbm2ddl.auto", "update");
//
//        factoryBean.setJpaProperties(additionalProperties);
//
//
//        return factoryBean;
//    }
//
//    @Bean
//    public DataSource dataSource(){
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://localhost:3306/lamp_vil?useUnicode=true&characterEncoding=utf8&characterSetResults=UTF-8");
//        dataSource.setUsername( "admin" );
//        dataSource.setPassword( "6012310" );
//        return dataSource;
//    }
//
////    @Bean
////    public DataSource dataSource() throws Exception {
////        try {
////            Context ctx = new InitialContext();
////            return (DataSource) ctx.lookup("jdbc:mysql://localhost:3306/lamp_vil?useUnicode=true&characterEncoding=utf8&characterSetResults=UTF-8");
////        } catch (Exception e) {
////            return null;
////        }
////    }
//
////    @Bean
////    public DataSource dataSource() {
////        final ComboPooledDataSource dataSource = new ComboPooledDataSource();
////
////        try {
////            dataSource.setDriverClass(driverClass);
////        } catch (PropertyVetoException e) {
////            throw new RuntimeException(e);
////        }
////
////        dataSource.setJdbcUrl(jdbcUrl);
////        dataSource.setUser(user);
////        dataSource.setPassword(password);
////        dataSource.setMinPoolSize(3);
////        dataSource.setMaxPoolSize(15);
////        dataSource.setDebugUnreturnedConnectionStackTraces(true);
////
////        return dataSource;
////    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
//
//        return transactionManager;
//    }
//
//    @Bean
//    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
//        return new PersistenceExceptionTranslationPostProcessor();
//    }
//
//
//
//////    @Autowired
//////    private DataSource dataSource;
////
//////    @Autowired
//////    private EntityManagerFactory entityManagerFactory;
////
//////    @Bean
//////    public DataSource dataSource(){
//////        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//////        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//////        dataSource.setUrl("jdbc:mysql://localhost:3306/lamp_vil?useUnicode=true&characterEncoding=utf8&characterSetResults=UTF-8");
//////        dataSource.setUsername( "admin" );
//////        dataSource.setPassword( "6012310" );
//////        return dataSource;
//////    }
////
////    @Bean
////    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaVendorAdapter jpaVendorAdapter) {
////        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
//////        lef.setDataSource(dataSource);
////        lef.setJpaVendorAdapter(jpaVendorAdapter);
////        lef.setPackagesToScan("barcode.dao.entities");
////        return lef;
////    }
////
////    @Bean
////    public JpaVendorAdapter jpaVendorAdapter() {
////        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
////        hibernateJpaVendorAdapter.setShowSql(false);
////        hibernateJpaVendorAdapter.setGenerateDdl(true);
////        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
////        return hibernateJpaVendorAdapter;
////    }
//
//}
