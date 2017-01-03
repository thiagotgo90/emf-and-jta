package com.piotrnowicki.emf.control;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import com.piotrnowicki.emf.entity.Customer;

@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class CustomerControlBMT {

    @Inject
    private Logger logger;

    /*@Inject*/
    @PersistenceContext
    EntityManager em;
    
    @Resource
    UserTransaction utx;

    public void executeMethod() {
        
        List<Customer> resultList = em.createNamedQuery(Customer.FIND_ALL, Customer.class).getResultList();
        em.joinTransaction();
        em.persist(new Customer("ttt1222", "ttt2111"));
        
        logger.info("Inside method!");
    }
    
    
    public void executeMethodThatDontStartsAnotherTransaction() throws Exception {
        
        List<Customer> resultList = em.createNamedQuery(Customer.FIND_ALL, Customer.class).getResultList();
        em.joinTransaction();
        em.persist(new Customer("ttt1222", "ttt2111"));
        logger.info(resultList.toString());
    }
    
    public void executeMethodThatDontStartsAnotherTransaction(EntityManager em) throws Exception {
        
        /*em.joinTransaction();*/
        utx.begin();
        List<Customer> resultList = em.createNamedQuery(Customer.FIND_ALL, Customer.class).getResultList();
        em.persist(new Customer("ttt1222", "ttt2111"));
        logger.info(resultList.toString());
    }
    
    
    public void executeMethodThatStartsAnotherTransaction() throws Exception {
        
        List<Customer> resultList = em.createNamedQuery(Customer.FIND_ALL, Customer.class).getResultList();
        utx.begin();
        em.persist(new Customer("ttt1222", "ttt2111"));
        utx.commit();
        
        logger.info("BMT || TRANSACTION REQUIRED || "+resultList.toString());
    }
}
