package com.tsystems.javaschool.logiweb;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.tsystems.javaschool.logiweb.dao.CargoDao;
import com.tsystems.javaschool.logiweb.dao.CityDao;
import com.tsystems.javaschool.logiweb.dao.DeliveryOrderDao;
import com.tsystems.javaschool.logiweb.dao.DriverDao;
import com.tsystems.javaschool.logiweb.dao.DriverShiftJournaDao;
import com.tsystems.javaschool.logiweb.dao.TruckDao;
import com.tsystems.javaschool.logiweb.dao.jpa.CargoDaoJpa;
import com.tsystems.javaschool.logiweb.dao.jpa.CityDaoJpa;
import com.tsystems.javaschool.logiweb.dao.jpa.DeliveryOrderDaoJpa;
import com.tsystems.javaschool.logiweb.dao.jpa.DriverDaoJpa;
import com.tsystems.javaschool.logiweb.dao.jpa.DriverShiftJournalDaoJpa;
import com.tsystems.javaschool.logiweb.dao.jpa.TruckDaoJpa;
import com.tsystems.javaschool.logiweb.model.Cargo;
import com.tsystems.javaschool.logiweb.model.City;
import com.tsystems.javaschool.logiweb.model.DeliveryOrder;
import com.tsystems.javaschool.logiweb.model.Driver;
import com.tsystems.javaschool.logiweb.model.DriverShiftJournal;
import com.tsystems.javaschool.logiweb.model.Truck;
import com.tsystems.javaschool.logiweb.service.DriverService;
import com.tsystems.javaschool.logiweb.service.TrucksService;
import com.tsystems.javaschool.logiweb.service.impl.DriverServiceImpl;
import com.tsystems.javaschool.logiweb.service.impl.TrucksSeviceimpl;

/**
 * Class provides to app context. 
 * 
 * @author Andrey Baliushin
 */
public final class LogiwebAppContext {
    
    public static final LogiwebAppContext INSTANCE = new LogiwebAppContext();
    
    private static final String PERSISTENCE_UNIT = "logiweb";
    
    private EntityManagerFactory emf;
    private EntityManager em;

    private CargoDao cargoDao;

    private CityDao cityDao;

    private DeliveryOrderDao deliveryOrderDao;

    private DriverDao driverDao;

    private DriverShiftJournaDao driverShiftJournalDao;

    private TruckDao truckDao;

    private TrucksService truckService;

    private DriverService driverService;    
    
    private LogiwebAppContext() { }
    
    private synchronized void createEmfInstance() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
    }
    
    /**
     * Get singleton instance of EntityManagerFactory for 'logiweb' persistence-unit.
     * 
     * @return unique instance of EntityManagerFactory
     */
    public EntityManagerFactory getEntityManagerFactory() {
        if(emf == null) {
            createEmfInstance();
        }
        return emf;
    }    
    
    public EntityManager getEntityManager() {
        if(em == null || !em.isOpen()) {
            em = getEntityManagerFactory().createEntityManager();
        }
        return em;
    }
    
    
    public CargoDao getCargoDao() {
        if (cargoDao == null) {
            cargoDao = new CargoDaoJpa(Cargo.class, getEntityManager());
        }
        return cargoDao;
    }

    
    public CityDao getCityDao() {
        if (cityDao == null) {
            cityDao = new CityDaoJpa(City.class, getEntityManager());
        }
        return cityDao;
    }

    
    public DeliveryOrderDao getDeliveryOrderDao() {
        if (deliveryOrderDao == null) {
            deliveryOrderDao = new DeliveryOrderDaoJpa(DeliveryOrder.class, getEntityManager());
        }
        return deliveryOrderDao;
    }

    
    public DriverDao getDriverDao() {
        if (driverDao == null) {
            driverDao = new DriverDaoJpa(Driver.class, getEntityManager());
        }
        return driverDao;
    }

    
    public DriverShiftJournaDao geDriverShiftJournaDao() {
        if (driverShiftJournalDao == null) {
            driverShiftJournalDao = new DriverShiftJournalDaoJpa(DriverShiftJournal.class, getEntityManager());
        }
        return driverShiftJournalDao;
    }

    
    public TruckDao getTruckDao() {
        if (truckDao == null) {
            truckDao = new TruckDaoJpa(Truck.class, getEntityManager());
        }
        return truckDao;
    }
    
    public TrucksService getTruckService() {
        if (truckService == null) {
            truckService = new TrucksSeviceimpl(getTruckDao(), getEntityManager());
        }
        return truckService;
    }
    
    public DriverService getDriverService() {
        if(driverService == null) {
            driverService = new DriverServiceImpl(getDriverDao(), getEntityManager());
        }
        return driverService;
    }
    
}