package com.piotrnowicki.emf.control;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.piotrnowicki.emf.entity.Customer;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CustomerControlCMT {

    @Inject
    private Logger logger;

    /*@Inject*/
    @PersistenceContext
    EntityManager em;

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void executeMethod() {
        
        List<Customer> resultList = em.createNamedQuery(Customer.FIND_ALL, Customer.class).getResultList();
        em.persist(new Customer("ttt1222", "ttt2111"));
        
        logger.info("Inside method!");
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executeMethodWithTransactionNew() {
        
        List<Customer> resultList = em.createNamedQuery(Customer.FIND_ALL, Customer.class).getResultList();
        em.persist(new Customer("ttt1222", "ttt2111"));
        
        logger.info(resultList.toString());
    }
}
