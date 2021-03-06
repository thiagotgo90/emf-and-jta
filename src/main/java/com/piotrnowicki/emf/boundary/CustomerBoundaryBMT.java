package com.piotrnowicki.emf.boundary;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import com.piotrnowicki.emf.control.CustomerControlBMT;
import com.piotrnowicki.emf.control.CustomerControlCMT;
import com.piotrnowicki.emf.entity.Customer;

/**
 * This bean shows how to use JTA resource manager ({@code EntityManager})
 * created by EntityManagerFactory. It shows different scenarios to show when
 * the created EntityManager is a part of the transaction or how to make it a
 * part of it.
 * 
 * <p>
 * I'm using the Bean Managed Transactions (BMT) to be able to start / stop JTA
 * transactions. CMT could be used as well but would require to use more than
 * one bean and passing of the created EntityManager. That being said - for the
 * simplicity, BMT has been used.
 * </p>
 * 
 * @author Piotr Nowicki
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CustomerBoundaryBMT {

    @Inject
    private Logger logger;

    @EJB
    private CustomerControlBMT customerControlBMTBean;
    
    @EJB
    private CustomerControlCMT customerControlCMTBean;

    @PersistenceContext
    EntityManager em;

    /**
     * We're injecting the JTA Resource (take a look at {@code persistence.xml}
     * - there is only one, default, PersistenceUnit and it's
     * {@code transaction-type=JTA} (by default). It means that we'll be using
     * JTA transactions and not {@link EntityManager#getTransaction()}.
     */
    @PersistenceUnit
    private EntityManagerFactory emf;

    /**
     * Used for manually beginning / commiting JTA transactions.
     */
    @Resource
    private UserTransaction utx;

    // -------------------------------------------------------------------------------||
    // Business methods
    // --------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * This method shows what happens if we're simply creating an EntityManager
     * and invoke some operation on it. It all happens <strong>without</strong>
     * JTA transaction. There is no exception but the data is lost.
     */
    public void executeWithoutTx(String firstName, String lastName) {
        EntityManager em = emf.createEntityManager();

        em.persist(new Customer(firstName, lastName));
    }

    /**
     * This method is like the
     * {@link CustomerBoundaryBMT#executeWithoutTx(String, String)} but
     * EntityManager operations are invoked within running JTA transaction.
     */
    public void executeWithTx(String firstName, String lastName)
            throws Exception {
        utx.begin();

        Customer customer = new Customer(firstName, lastName);
        em.persist(customer);
        utx.setRollbackOnly();
        logger.info("Status da transacao: " + utx.getStatus());

        utx.commit();
    }

    public void executeWithTxAndMakeTransactionRollback(String firstName,
            String lastName) throws Exception {

        Customer customer = new Customer(firstName, lastName);
        utx.begin();
        em.persist(customer);
        utx.commit();

        utx.begin();
        customer = em.find(Customer.class, 555555L);
        em.persist(customer);
        utx.commit();
    }

    /**
     * CRIEI
     * 
     * 
     * FlushModeType.COMMIT
     * 
     */
    public void executeWithTxButNoRecoveryEntityOnQuery(String firstName,
            String lastName) throws Exception {
        Customer customer = new Customer(firstName, lastName);
        em.persist(customer);

        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.id = :id", Customer.class);
        query.setFlushMode(FlushModeType.COMMIT);
        query.setParameter("id", customer.getId());

        List<Customer> resultList = query.getResultList();

        System.out.println(resultList);

    }

    /**
     * Iniciar uma transação em um BMT, persistir um objeto, chamar um metodo de outro
     * EJB que tenha como transação REQUIRED_NEW e verificar se a entidade
     * persistido antes vai estar nesse persistence context
     * 
     */
    public void executePersistenceContextPropagation(String firstName, String lastName) throws Exception {
        Customer customer = new Customer(firstName, lastName);
        utx.begin();
        em.persist(customer);
        customerControlBMTBean.executeMethodThatStartsAnotherTransaction();
        utx.commit();

    }
    
    public void executePropagateUserTranscation(String firstName, String lastName) throws Exception {
        Customer customer = new Customer(firstName, lastName);
        utx.begin();
        em.persist(customer);
        customerControlBMTBean.executeMethodThatDontStartsAnotherTransaction(em);
        utx.commit();

    }

    public void executeWithTxStaredBeforeEntityManager(String firstName, String lastName) throws Exception {
        EntityManager em = emf.createEntityManager();

        utx.begin();
        em.persist(new Customer(firstName, lastName));
        utx.commit();
    }

    /**
     * This method fixes the problem of {@link CustomerBoundaryBMT#executeWithTxStaredBeforeEntityManager(String, String)}
     * . If we create an JTA EntityManager <strong>before</strong> the JTA
     * transaction is running, we need to manually say it to
     * <strong>join</strong> the surrounding transaction.
     */
    public void executeWithTxStaredBeforeEntityManagerWithJoin(String firstName, String lastName) throws Exception {
        EntityManager em = emf.createEntityManager();

        utx.begin();

        em.joinTransaction();
        em.persist(new Customer(firstName, lastName));

        utx.commit();
    }

    /**
     * Every application-managed EntityManager works on extended Persistence
     * Context which means its operations might span across multiple
     * transactions. In this case we're using two transactions of the same
     * EntityMangaer. Both changes will be persisted in the database.
     */
    public void executeWithTxMultipleTransactions(String firstName, String lastName) throws Exception {
        EntityManager em = emf.createEntityManager();

        utx.begin();

        em.joinTransaction();
        em.persist(new Customer(firstName, lastName));

        utx.commit();

        utx.begin();

        em.joinTransaction();
        em.persist(new Customer(firstName, lastName));

        utx.commit();
    }
}
