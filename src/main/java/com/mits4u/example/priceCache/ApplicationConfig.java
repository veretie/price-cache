package com.mits4u.example.priceCache;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.impl.engine.DefaultShutdownStrategy;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;

@Configuration
public class ApplicationConfig {

    @Value("${activemq.broker.url}")
    private String brokerUrl;

    @Value("${priceCache.systemConfig.jtaTransactionTimeoutMillis}")
    private int jtaTimeoutMillis;

    @Value("${priceCache.systemConfig.camelGracefulShutdownLimitSeconds}")
    private int camelGracefulShutdownLimitSeconds;

    @Bean
    public BrokerService embeddedBroker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(true);
        broker.addConnector(brokerUrl);
        return broker;
    }

    // Construct Atomikos UserTransactionManager, needed to configure Spring
    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() throws SystemException {
        var tm = new UserTransactionManager();
        tm.setTransactionTimeout(jtaTimeoutMillis);
        tm.setForceShutdown(true);
        return tm;
    }

    //Also use Atomikos UserTransactionImp, needed to configure Spring
    @Bean
    public UserTransactionImp atomikosUserTransaction() throws SystemException {
        var atomikosUserTransaction = new UserTransactionImp();
        atomikosUserTransaction.setTransactionTimeout(jtaTimeoutMillis);
        return atomikosUserTransaction;
    }

    //Configure the Spring framework to use JTA transactions from Atomikos
    @Bean(name = "transactionManager")
    public JtaTransactionManager springJtaTransactionManager() throws SystemException {
        var jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(atomikosTransactionManager());
        jtaTransactionManager.setUserTransaction(atomikosTransactionManager());
        return jtaTransactionManager;
    }


    /*
        JMS CONFIG
     */

    //The underlying JMS vendor's XA connection factory. XA is required for transactional correctness.
    @Bean
    public ActiveMQXAConnectionFactory actimeMqXaFactory() {
        var xaFactory = new ActiveMQXAConnectionFactory();
        xaFactory.setBrokerURL(brokerUrl);
        return xaFactory;
    }

    //The Atomikos JTA-enabled JmsConnectionFactory, configured with the vendor's XA factory.
    @Bean(initMethod = "init", destroyMethod = "close")
    @DependsOn("embeddedBroker")
    public AtomikosConnectionFactoryBean atomikosConnectionFactory() {
        var factoryBean = new AtomikosConnectionFactoryBean();
        factoryBean.setUniqueResourceName("ACTIVEMQ_BROKER");
        factoryBean.setXaConnectionFactory(actimeMqXaFactory());
        return factoryBean;
    }

    // JMS producer using Atomikos
    @Bean
    public JmsTemplate jmsTemplate() {
        var jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(atomikosConnectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

     /*
        DB CONFIG - uses Spring "transactionManager" bean which is configured as JtaTransactionManager above
     */

    /*
       CAMEL transactions using Spring framework
     */

    @Bean("PROPAGATION_REQUIRED")
    public SpringTransactionPolicy propagationRequired() throws SystemException {
        var policy = new SpringTransactionPolicy();
        policy.setTransactionManager(springJtaTransactionManager());
        policy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return policy;
    }

    @Bean
    public DefaultShutdownStrategy camelShutdownStrategy() {
        var shutdownStrategy = new DefaultShutdownStrategy();
        shutdownStrategy.setTimeout(camelGracefulShutdownLimitSeconds);
        return shutdownStrategy;
    }

}