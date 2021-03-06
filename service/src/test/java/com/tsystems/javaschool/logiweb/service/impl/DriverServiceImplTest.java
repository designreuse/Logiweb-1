package com.tsystems.javaschool.logiweb.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.tsystems.javaschool.logiweb.dao.CityDao;
import com.tsystems.javaschool.logiweb.dao.DriverDao;
import com.tsystems.javaschool.logiweb.dao.DriverShiftJournaDao;
import com.tsystems.javaschool.logiweb.dao.TruckDao;
import com.tsystems.javaschool.logiweb.dao.UserDao;
import com.tsystems.javaschool.logiweb.dao.exceptions.DaoException;
import com.tsystems.javaschool.logiweb.entities.City;
import com.tsystems.javaschool.logiweb.entities.Driver;
import com.tsystems.javaschool.logiweb.entities.DriverShiftJournal;
import com.tsystems.javaschool.logiweb.entities.LogiwebUser;
import com.tsystems.javaschool.logiweb.entities.Truck;
import com.tsystems.javaschool.logiweb.entities.status.DriverStatus;
import com.tsystems.javaschool.logiweb.model.DriverModel;
import com.tsystems.javaschool.logiweb.service.DriverService;
import com.tsystems.javaschool.logiweb.service.UserService;
import com.tsystems.javaschool.logiweb.service.exceptions.LogiwebServiceException;
import com.tsystems.javaschool.logiweb.service.exceptions.RecordNotFoundServiceException;
import com.tsystems.javaschool.logiweb.service.exceptions.ServiceValidationException;

public class DriverServiceImplTest {

    private DriverDao driverDaoMock;
    private TruckDao truckDaoMock;
    private DriverShiftJournaDao shiftDaoMock;
    private UserService userServiceMock;
    private UserDao userDaoMock;
    private CityDao cityDaoMock;
    
    private DriverService driverService;

    /**
     * Populate mocks.
     * @throws DaoException 
     */
    @Before
    public void setupMocks() throws DaoException {
        driverDaoMock = mock(DriverDao.class);
        truckDaoMock = mock(TruckDao.class);
        shiftDaoMock = mock(DriverShiftJournaDao.class);
        userServiceMock = mock(UserService.class);
        userDaoMock = mock(UserDao.class);
        cityDaoMock = mock(CityDao.class);    
        
        when(cityDaoMock.find(0))
        .thenReturn(new City());
        
        driverService = new DriverServiceImpl(driverDaoMock,
                truckDaoMock, shiftDaoMock, userServiceMock, userDaoMock, cityDaoMock);        
    }

    /**
     * Sets up test data that will be returned by driverDaoMock and
     * shiftDaoMock. Drivers for
     * driverDaoMock.findByCityWhereNotAssignedToTruck(...) will be created with
     * names: test0..test4 and IDs 0..4
     *  Journals for
     * shiftDaoMock.findThisMonthJournalsForDrivers(...) will be created with
     * working hours: 
     * test0 -- 15 working hours in two shifts 
     * test1 -- 12 working hours in one shift 
     * test2 -- 0 hours 
     * test3 -- 5 hours this month, 10 in previous (one shift)
     * test4 -- have unknown number of hours in unfinished shift
     * 
     * @return list of this driver in that exact order
     * @throws DaoException
     */
    private List<Driver> setupDriverAndJournalsTestData() throws DaoException {
        List<Driver> freeDriversAsList = new ArrayList<Driver>();
        // list is used to keep order for 'return'
        Set<Driver> freeDrivers;
        Driver d0 = new Driver(); // have 15 working hours in two shifts
        Driver d1 = new Driver(); // have 12 working hours in one shift
        Driver d2 = new Driver(); // have 0 hours
        Driver d3 = new Driver(); // 5 this month, 10 in previous (one shift)
        Driver d4 = new Driver(); // unknown number of hours in unfinished shift
        d0.setId(0);
        d1.setId(1);
        d2.setId(2);
        d3.setId(3);
        d4.setId(4);
        d0.setName("test0");
        d1.setName("test1");
        d2.setName("test2");
        d3.setName("test3");
        d4.setName("test4");
        freeDriversAsList.add(d0);
        freeDriversAsList.add(d1);
        freeDriversAsList.add(d2);
        freeDriversAsList.add(d3);
        freeDriversAsList.add(d4);
        freeDrivers = new HashSet<Driver>(freeDriversAsList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // today

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        DriverShiftJournal j0 = new DriverShiftJournal();
        j0.setDriverForThisRecord(d0);
        j0.setId(1);
        j0.setShiftBeggined(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        j0.setShiftEnded(calendar.getTime());

        DriverShiftJournal j1 = new DriverShiftJournal();
        j1.setDriverForThisRecord(d0);
        j1.setId(1);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        j1.setShiftBeggined(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        j1.setShiftEnded(calendar.getTime());
        // Driver 0 have 15 hours in total

        DriverShiftJournal j2 = new DriverShiftJournal();
        j2.setDriverForThisRecord(d1);
        j2.setId(1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        j2.setShiftBeggined(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        j2.setShiftEnded(calendar.getTime());
        // Driver 1 have 12 hours in total

        DriverShiftJournal j3 = new DriverShiftJournal();
        j3.setDriverForThisRecord(d3);
        j3.setId(1);
        calendar.set(Calendar.HOUR_OF_DAY, 5); // ended
        j3.setShiftEnded(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.HOUR, -10); // 10 H in prev. month
        j3.setShiftBeggined(calendar.getTime());
        // driver 3 have 5 hours in this month, and 10 in prev.
        
        DriverShiftJournal j4 = new DriverShiftJournal();
        j4.setDriverForThisRecord(d4);
        j4.setId(1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        j4.setShiftBeggined(calendar.getTime());
        // driver 4 have unknown number of hours in unfinished shift

        Set<DriverShiftJournal> journals = new HashSet<DriverShiftJournal>();
        journals.add(j0);
        journals.add(j1);
        journals.add(j2);
        journals.add(j4);

        when(driverDaoMock.findByCityWhereNotAssignedToTruck(Mockito
                        .any(City.class))).thenReturn(freeDrivers);
        when(shiftDaoMock.findThisMonthJournalsForDrivers(freeDrivers))
                .thenReturn(journals);

        return freeDriversAsList;
    }

    /**
     * Examine Driver Models and return true if they contain all drivers with ids specified in 
     * driverIdsToLookFor.
     * @param driverIdsToLookFor
     * @param driverModels
     * @return
     */
    private boolean isDriverModelsCollectionContainAllDriverIds(List<Integer> driverIdsToLookFor, Collection<DriverModel> driverModels) {
        List<Integer> driverIds = new ArrayList<Integer>();
        for (DriverModel dm : driverModels) {
            driverIds.add(dm.getId());
        }
        
        driverIdsToLookFor.removeAll(driverIds);
        return driverIdsToLookFor.isEmpty();
    }

    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: Driver shift started in last month, ended in this.
     */
    @Test
    public void testFindByWorkHoursWhenShiftStartedInLastMonth()
            throws DaoException, LogiwebServiceException {
        setupDriverAndJournalsTestData();

        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0, 10);
        
        List<Integer> expectedDriverIds = new ArrayList<Integer>();
        expectedDriverIds.add(3);
        Assert.assertTrue(isDriverModelsCollectionContainAllDriverIds(
                expectedDriverIds, result));
    }
    
    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: Driver doesn't have any shifts yet.
     */
    @Test
    public void testFindByWorkHoursWhenThereIsNoShifts() throws DaoException,
            LogiwebServiceException {
        setupDriverAndJournalsTestData();

        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0, 10);
        
        List<Integer> expectedDriverIds = new ArrayList<Integer>();
        expectedDriverIds.add(2);
        Assert.assertTrue(isDriverModelsCollectionContainAllDriverIds(
                expectedDriverIds, result));
    }

    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: '0' hours is argument for method
     */
    @Test
    public void testFindByWorkHoursWhenArgumentIsZeroHours()
            throws DaoException, LogiwebServiceException {
        setupDriverAndJournalsTestData();

        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0, 0);
        
        List<Integer> expectedDriverIds = new ArrayList<Integer>();
        expectedDriverIds.add(2);
        Assert.assertTrue(isDriverModelsCollectionContainAllDriverIds(
                expectedDriverIds, result));
    }
    
    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: negative number is argument for method
     */
    @Test
    public void testFindByWorkHoursWhenArgumentIsNegativeHours()
            throws DaoException, LogiwebServiceException {
        setupDriverAndJournalsTestData();

        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0, -1);
        Assert.assertTrue(result.isEmpty());
    }
    
    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: expect more than one result
     */
    @Test
    public void testFindByWorkHoursExpectMultipleResuts()
            throws DaoException, LogiwebServiceException {setupDriverAndJournalsTestData();

        //Expected: drv.test1 (12h), drv.test2 (0 hours) and
        // drv.test3(5 hours)
        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0,
                        13);
        
        List<Integer> expectedDriverIds = new ArrayList<Integer>();
        expectedDriverIds.add(1);
        expectedDriverIds.add(2);
        expectedDriverIds.add(3);
        Assert.assertTrue(isDriverModelsCollectionContainAllDriverIds(
                expectedDriverIds, result));
    }
    
    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: driver have more working hours than was requested
     */
    @Test
    public void testFindByWorkHoursWhenDriverHaveMoreHoursThanArgument()
            throws DaoException, LogiwebServiceException {
        setupDriverAndJournalsTestData();

        //Expected: drv.test1 (12h), drv.test2 (0 hours) and
        // drv.test3(5 hours), and not drv.test0 (15 hours)
        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0,
                        13);
        
        List<Integer> expectedDriverIds = new ArrayList<Integer>();
        expectedDriverIds.add(0);
        Assert.assertFalse(isDriverModelsCollectionContainAllDriverIds(
                expectedDriverIds, result));
    }
    
    /**
     * Test: findUnassignedToTrucksDriversByMaxWorkingHoursAndCity 
     * Case: driver have unfinished shift
     */
    @Test
    public void testFindByWorkHoursWhenDriverHasUnfinishedShift()
            throws DaoException, LogiwebServiceException {
        setupDriverAndJournalsTestData();

        //Expected: drv.test1 (12h), drv.test2 (0 hours) and
        // drv.test3(5 hours), and not drv.test0 (15 hours)
        Set<DriverModel> result = driverService
                .findUnassignedToTrucksDriversByMaxWorkingHoursAndCity(0,
                        744); //744 - max hours in month
        
        List<Integer> expectedDriverIds = new ArrayList<Integer>();
        expectedDriverIds.add(4);
        Assert.assertTrue(isDriverModelsCollectionContainAllDriverIds(
                expectedDriverIds, result));
    }
    
    /**
     * Test: assignDriverToTruck
     * Case: driver not exist
     */
    @Test(expected = ServiceValidationException.class)  
    public void testAssignDriverToTruckWhenDriverNotExist() throws DaoException, LogiwebServiceException {
        setupMocks();
        when(driverDaoMock.find(1))
            .thenReturn(null);
        when(truckDaoMock.find(1))
        .thenReturn(new Truck());
        
        driverService.assignDriverToTruck(1, 1);
    }
    
    /**
     * Test: assignDriverToTruck
     * Case: truck not exist
     */
    @Test(expected = ServiceValidationException.class)  
    public void testAssignDriverToTruckWhenTruckNotExist() throws DaoException, LogiwebServiceException {
        setupMocks();
        when(driverDaoMock.find(1))
        .thenReturn(new Driver());
        when(truckDaoMock.find(1))
        .thenReturn(null);
        
        driverService.assignDriverToTruck(1, 1);
    }
    
    /**
     * Test: assignDriverToTruck
     * Case: check that truck is added to driver as well as 
     * driver to truck
     * @throws LogiwebServiceException 
     */
    @Test  
    public void testAssignDriverToTruckThatTruckIsAddedToDriver() throws DaoException, LogiwebServiceException {
        setupMocks();
        
        Driver d = new Driver();
        Truck t = new Truck();
        t.setCrewSize(1);
        
        when(driverDaoMock.find(1))
        .thenReturn(d);
        when(truckDaoMock.find(1))
        .thenReturn(t);
        
        driverService.assignDriverToTruck(1, 1);
        
        Assert.assertTrue(t.getDrivers().contains(d));
        Assert.assertTrue(d.getCurrentTruck() == t);
    }
    
    /**
     * Test: assignDriverToTruck
     * Case: no one in crew
     * @throws LogiwebServiceException 
     */
    @Test  
    public void testAssignDriverToTruckWhenTruckHaveNoCrew() throws DaoException, LogiwebServiceException {
        setupMocks();
        
        Driver d = new Driver();
        Truck t = new Truck();
        t.setCrewSize(1);
        
        when(driverDaoMock.find(1))
        .thenReturn(d);
        when(truckDaoMock.find(1))
        .thenReturn(t);
        
        driverService.assignDriverToTruck(1, 1);
        
        Assert.assertTrue(t.getDrivers().contains(d));
    }
    
    /**
     * Test: assignDriverToTruck
     * Case: some drivers are in crew
     * @throws LogiwebServiceException 
     */
    @Test  
    public void testAssignDriverToTruckWhenTruckNotHaveFullCrew() throws DaoException, LogiwebServiceException {
        setupMocks();
        
        Driver d = new Driver();
        Truck t = new Truck();
        t.setCrewSize(2);
        Set<Driver> crew = new HashSet<Driver>();
        crew.add(new Driver());
        t.setDrivers(crew);
        
        when(driverDaoMock.find(1))
        .thenReturn(d);
        when(truckDaoMock.find(1))
        .thenReturn(t);
        
        driverService.assignDriverToTruck(1, 1);
        
        Assert.assertTrue(t.getDrivers().contains(d));
    }
    
    /**
     * Test: assignDriverToTruck
     * Case: crew is full
     * @throws LogiwebServiceException 
     */
    @Test(expected = ServiceValidationException.class)  
    public void testAssignDriverToTruckWhenTruckCrewIsFull() throws DaoException, LogiwebServiceException {
        setupMocks();
        
        Driver d = new Driver();
        Truck t = new Truck();
        t.setCrewSize(1);
        Set<Driver> crew = new HashSet<Driver>();
        crew.add(new Driver());
        t.setDrivers(crew);
        
        when(driverDaoMock.find(1))
        .thenReturn(d);
        when(truckDaoMock.find(1))
        .thenReturn(t);
        
        driverService.assignDriverToTruck(1, 1);
    }
    
    /**
     * Test: addDriverWithAccount
     * Case: driver with same employee id already exists
     */
    @Test(expected = ServiceValidationException.class)  
    public void testAddDriverWhenDriverWithSameEmpIdExists()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        DriverModel driverModel = new DriverModel();
        driverModel.setEmployeeId(1);
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(new Driver());

        driverService.addDriverWithAccount(driverModel, "irrelevant",
                "irrelevant");
    }
    
    /**
     * Test: addDriverWithAccount
     * Case: driver successfully added
     */
    @Test 
    public void testAddDriverWhenDriverWhenEverythingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        DriverModel driverModel = new DriverModel();
        driverModel.setEmployeeId(1);
        
        driverService.addDriverWithAccount(driverModel, "irrelevant",
                "irrelevant");
        
        Mockito.verify(driverDaoMock, times(1)).create(any(Driver.class));
    }
    
    /**
     * Test: removeDriverAndAccount
     * Case: driver not exist
     */
    @Test(expected = ServiceValidationException.class)  
    public void testRemoveDriverAndAccountWhenDriverNotExist()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        Driver d = new Driver();
        d.setCurrentTruck(new Truck());
        when(driverDaoMock.find(1)).thenReturn(null);

        driverService.removeDriverAndAccount(1);
    }
    
    /**
     * Test: removeDriverAndAccount
     * Case: try to remove driver who is still assigned to truck
     */
    @Test(expected = ServiceValidationException.class)  
    public void testRemoveDriverAndAccountWhenDriverIsAssignedToTruck()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        Driver d = new Driver();
        d.setCurrentTruck(new Truck());
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        
        driverService.removeDriverAndAccount(1);
    }
    
    /**
     * Test: addDriverWithAccount
     * Case: driver successfully added
     */
    @Test 
    public void testAddDriverWhenEverythingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        Driver d = new Driver();
        d.setLogiwebAccount(new LogiwebUser());
        
        when(driverDaoMock.find(1)).thenReturn(d);
        
        driverService.removeDriverAndAccount(1);
        
        Mockito.verify(driverDaoMock, times(1)).delete(d);
    }
    
    /**
     * Test: calculateWorkingHoursForDriver
     * Case: check that method shows accurate results
     */
    @Test 
    public void testCalculateWorkingHoursForDriver()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        List<Driver> drivers = setupDriverAndJournalsTestData();

        // use previously created test data but only for driver0
        // who have 15 hours in 2 shifts
        Set<Driver> driversSet = driverDaoMock
                .findByCityWhereNotAssignedToTruck(new City());
        Set<DriverShiftJournal> journals = shiftDaoMock
                .findThisMonthJournalsForDrivers(driversSet);

        Iterator<DriverShiftJournal> it = journals.iterator();
        while (it.hasNext()) {
            DriverShiftJournal j = it.next();
            if (j.getDriverForThisRecord() != drivers.get(0)) {
                it.remove();
            }
        }

        when(shiftDaoMock.findThisMonthJournalsForDrivers(drivers.get(0)))
                .thenReturn(journals);

        when(driverDaoMock.find(1)).thenReturn(drivers.get(0));
        float wHours = driverService.calculateWorkingHoursForDriver(1);
        Assert.assertEquals(15.0, wHours, 0.01);
    }
    
    /**
     * Test: calculateWorkingHoursForDriver
     * Case: driver don't have any shifts yet
     */
    @Test 
    public void testCalculateWorkingHoursForDriverWithoutShifts()
            throws LogiwebServiceException, DaoException {
        setupMocks();

        when(driverDaoMock.find(1)).thenReturn(new Driver());
        float wHours = driverService.calculateWorkingHoursForDriver(1);
        Assert.assertEquals(0, wHours, 0);
    }
    
    /**
     * Test: setDriverStatusToResting
     * Case: driver not exist
     */
    @Test(expected = RecordNotFoundServiceException.class)  
    public void TestSetDriverStatusToRestingWhenDriverNotExist()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(null);
        
        driverService.setDriverStatusToResting(1);
    }
    
    /**
     * Test: setDriverStatusToResting
     * Case: everything OK
     */
    @Test 
    public void TestSetDriverStatusToRestingWhenEverythingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        Driver d = new Driver();
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        driverService.setDriverStatusToResting(1);
        
        Mockito.verify(driverDaoMock, times(1)).update(d);
    }
    
    /**
     * Test: setDriverStatusToDriving
     * Case: driver not exist
     */
    @Test(expected = RecordNotFoundServiceException.class)  
    public void TestSetDriverStatusToDrivingWhenDriverNotExist()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(null);
        
        driverService.setDriverStatusToDriving(1);
    }
    
    /**
     * Test: setDriverStatusToResting
     * Case: everything OK
     */
    @Test 
    public void testSetDriverStatusToDrivingWhenEverythingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        Driver d = new Driver();
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        driverService.setDriverStatusToDriving(1);
        
        Mockito.verify(driverDaoMock, times(1)).update(d);
    }
    
    /**
     * Test: editDriverAndAccountName
     * Case: driver id is not set
     */
    @Test(expected = ServiceValidationException.class)
    public void testEditDriverAndAccountNameWhenDriverIdMissing() throws LogiwebServiceException,
            DaoException {
        DriverModel dm = new DriverModel();
        
        driverService.editDriverAndAccountName(dm, null);
    }
    
    /**
     * Test: editDriverAndAccountName
     * Case: driver with same employee id already exists
     */
    @Test(expected = ServiceValidationException.class)  
    public void testEditDriverAndAccountNameWhenEmployeeIdOccupied()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        DriverModel driverModel = new DriverModel();
        driverModel.setId(1);
        driverModel.setEmployeeId(1);
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(new Driver());
        
        driverService.editDriverAndAccountName(driverModel, "irrelevant");
    }
    
    /**
     * Test: editDriverAndAccountName
     * Case: driver didn't change his employee id
     */
    @Test 
    public void testEditDriverAndAccountNameWhenEmployeeIdDidntChange()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        DriverModel driverModel = new DriverModel();
        driverModel.setId(1);
        driverModel.setEmployeeId(1);
        
        Driver sameDriver = new Driver();
        sameDriver.setId(1);
        sameDriver.setEmployeeId(1);
        sameDriver.setLogiwebAccount(new LogiwebUser());
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(sameDriver);
        when(driverDaoMock.find(1)).thenReturn(sameDriver);

        driverService.editDriverAndAccountName(driverModel, "irrelevant");
        
        Mockito.verify(driverDaoMock, times(1)).update(sameDriver);
    }
    
    /**
     * Test: editDriverAndAccountName
     * Case: driver does not exist
     */
    @Test(expected = ServiceValidationException.class)  
    public void testEditDriverAndAccountNameWhenDriverNotExist()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        DriverModel driverModel = new DriverModel();
        driverModel.setId(1);
        driverModel.setEmployeeId(1);
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(null);
        when(driverDaoMock.find(1)).thenReturn(null);

        driverService.editDriverAndAccountName(driverModel, "irrelevant");
    }
    
    /**
     * Test: editDriverAndAccountName
     * Case: driver didn't change his employee id
     */
    @Test 
    public void testEditDriverAndAccountNameWhenEvyrithingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        DriverModel driverModel = new DriverModel();
        driverModel.setId(1);
        driverModel.setEmployeeId(1);
        
        Driver sameDriver = new Driver();
        sameDriver.setLogiwebAccount(new LogiwebUser());
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(null);
        when(driverDaoMock.find(1)).thenReturn(sameDriver);

        driverService.editDriverAndAccountName(driverModel, "irrelevant");
        
        Mockito.verify(driverDaoMock, times(1)).update(sameDriver);
    }
    
    /**
     * Test: startShiftForDriver 
     * Case: driver does not exist
     */
    @Test(expected = ServiceValidationException.class)  
    public void testStartShiftForDriverWhenDriverDoesNotExist()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(null);

        driverService.startShiftForDriverAndSetRestingEnRouteStatus(1);
    }
    
    /**
     * Test: startShiftForDriver 
     * Case: driver have unfinished shift
     */
    @Test(expected = ServiceValidationException.class)  
    public void testStartShiftForDriverWhenDriverHaveUnfinieshedShift()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        Driver d = new Driver();
        d.setStatus(DriverStatus.FREE);
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        when(shiftDaoMock.findUnfinishedShiftForDriver(d)).thenReturn(
                new DriverShiftJournal());

        driverService.startShiftForDriverAndSetRestingEnRouteStatus(1);
    }
    
    /**
     * Test: startShiftForDriver 
     * Case: everything Ok
     */
    @Test
    public void testStartShiftForDriverWhenEverythingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        Driver d = new Driver();
        d.setStatus(DriverStatus.FREE);
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);

        driverService.startShiftForDriverAndSetRestingEnRouteStatus(1);
        
        Mockito.verify(shiftDaoMock, times(1)).create(
                any(DriverShiftJournal.class));
    }
    
    /**
     * Test: startShiftForDriver 
     * Case: Driver not free
     */
    @Test(expected = ServiceValidationException.class)  
    public void testStartShiftForDriverWhenDriverNotFree()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        Driver d = new Driver();
        d.setStatus(DriverStatus.RESTING_EN_ROUT);
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);

        driverService.startShiftForDriverAndSetRestingEnRouteStatus(1);
    }
    

    /**
     * Test: endShiftForDriver 
     * Case: driver does not exist
     */
    @Test(expected = ServiceValidationException.class)  
    public void testEndShiftForDriverWhenDriverDoesNotExist()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(null);

        driverService.endShiftForDriverAndSetFreeStatus(1);
    }
    
    /**
     * Test: endShiftForDriver
     * Case: driver doesn't have unfinished shift
     */
    @Test(expected = ServiceValidationException.class)  
    public void testEndShiftForDriverWhenDriverDoesntHaveUnfinieshedShift()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        Driver d = new Driver();
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        when(shiftDaoMock.findUnfinishedShiftForDriver(d)).thenReturn(
                null);

        driverService.endShiftForDriverAndSetFreeStatus(1);
    }
    
    /**
     * Test: endShiftForDriver
     * Case: everything Ok
     */
    @Test
    public void testEndShiftForDriverWhenEverythingOk()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        Driver d = new Driver();
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        when(shiftDaoMock.findUnfinishedShiftForDriver(d)).thenReturn(
                new DriverShiftJournal());

        driverService.endShiftForDriverAndSetFreeStatus(1);
        
        Mockito.verify(shiftDaoMock, times(1)).update(
                any(DriverShiftJournal.class));
    }
    
    /**
     * Test: endShiftForDriver
     * Case: test that driver status is changed to free
     */
    @Test
    public void testEndShiftForDriverAndCheckStatusCorrectness()
            throws LogiwebServiceException, DaoException {
        setupMocks();
        
        Driver d = new Driver();
        d.setStatus(DriverStatus.DRIVING);
        
        when(driverDaoMock.findByEmployeeId(1)).thenReturn(d);
        when(shiftDaoMock.findUnfinishedShiftForDriver(d)).thenReturn(
                new DriverShiftJournal());

        driverService.endShiftForDriverAndSetFreeStatus(1);
        
        Assert.assertEquals(d.getStatus(), DriverStatus.FREE);
    }

}
