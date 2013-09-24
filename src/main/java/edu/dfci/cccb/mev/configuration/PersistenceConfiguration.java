package edu.dfci.cccb.mev.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource ({ "classpath:/configuration/database.properties", "classpath:/configuration/hibernate.properties" })
public class PersistenceConfiguration {

  @Autowired private Environment environment;

  @Bean
  public LocalSessionFactoryBean sessionFactory () {
    return new LocalSessionFactoryBean () {
      {
        setDataSource (restDataSource ());
        setPackagesToScan ("edu.dfci.cccb.mev.domain");
        setHibernateProperties (new Properties () {
          private static final long serialVersionUID = 1L;

          {
            setProperty ("hibernate.hbm2ddl.auto", environment.getProperty ("hibernate.hbm2ddl.auto"));
            setProperty ("hibernate.dialect", environment.getProperty ("hibernate.dialect"));
            setProperty ("hibernate.ejb.naming_strategy", environment.getProperty ("hibernate.ejb.naming_strategy"));
            setProperty ("hibernate.connection.charSet", environment.getProperty ("hibernate.connection.charSet"));
          }
        });
      }
    };
  }

  @Bean
  public DataSource restDataSource () {
    return new BasicDataSource () {
      {
        setDriverClassName (environment.getProperty ("database.driver.class"));
        setUrl (environment.getProperty ("database.url"));
        setUsername (environment.getProperty ("database.username"));
        setPassword (environment.getProperty ("database.password"));
      }
    };
  }

  /*@Bean
  public HibernateTransactionManager transactionManager () {
    try {
      return new HibernateTransactionManager (sessionFactory ().getObject ());
    } catch (Throwable e) {
      StringBuffer sb = new StringBuffer ("\n\n\n\n\n");
      for (; e != null; e = e.getCause ())
        sb.append ("CAUGHT EXCEPTION " + e.getClass ().getSimpleName () + ": " + e.getMessage () + "\n");
      sb.append ("\n\n\n\n\n\n");
      System.err.println (sb);
      return null;
    }
  }*/

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation () {
    return new PersistenceExceptionTranslationPostProcessor ();
  }
}