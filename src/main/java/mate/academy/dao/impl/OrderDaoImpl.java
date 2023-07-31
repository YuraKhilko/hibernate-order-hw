package mate.academy.dao.impl;

import java.util.List;
import mate.academy.dao.OrderDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Order;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
            return order;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert a order: " + order, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Order> getByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession();) {
            Query<Order> getOrdersByUserQuery = session.createQuery("FROM Order o "
                    + "INNER JOIN FETCH o.user u "
                    + "INNER JOIN FETCH o.tickets t "
                    + "WHERE u.id = :id", Order.class);
            getOrdersByUserQuery.setParameter("id", user.getId());
            return getOrdersByUserQuery.getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get orders for user " + user, e);
        }
    }
}