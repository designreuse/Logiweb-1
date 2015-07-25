package com.tsystems.javaschool.logiweb.model.ext;

import com.tsystems.javaschool.logiweb.entities.City;
import com.tsystems.javaschool.logiweb.entities.Driver;
import com.tsystems.javaschool.logiweb.entities.Truck;
import com.tsystems.javaschool.logiweb.model.DriverModel;
import com.tsystems.javaschool.logiweb.model.TruckModel;

public class ModelToEntityConverter {

    private ModelToEntityConverter() {
    }
    
    public static Driver convertToEntity(DriverModel driverModel) {
        Driver entity = new Driver();
        
        City city = new City();
        city.setId(driverModel.getCurrentCityId());
        
        entity.setCurrentCity(city);
        entity.setName(driverModel.getName());
        entity.setSurname(driverModel.getSurname());
        entity.setEmployeeId(driverModel.getEmployeeId());
        entity.setStatus(driverModel.getStatus());
        
        return entity;
    }
    
    public static DriverModel convertToModel(Driver entity) {
        DriverModel model = new DriverModel();
        
        model.setCurrentCityId(entity.getCurrentCity() == null ? 0 : entity
                .getCurrentCity().getId());

        model.setCurrentTruckLicensePlate(entity.getCurrentTruck() == null ? null : entity.getCurrentTruck().getLicencePlate());
        model.setEmployeeId(entity.getEmployeeId());
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setOrderId(null);
        model.setRouteInfo(null);
        model.setStatus(entity.getStatus());
        model.setSurname(entity.getSurname());
        model.setWorkingHoursThisMonth(0);
        
        return model;
    }
    
    public static Truck convertToEntity(TruckModel model) {
        Truck truck = new Truck();
        
        City city = new City();
        city.setId(model.getCurrentCityId());
        
        truck.setId(model.getId());
        truck.setLicencePlate(model.getLicencePlate());
        truck.setAssignedDeliveryOrder(model.getAssignedDeliveryOrder());
        truck.setCurrentCity(city);
        truck.setCrewSize(model.getCrewSize());
        truck.setCargoCapacity(model.getCargoCapacity());
        truck.setDrivers(model.getDrivers());
        truck.setStatus(model.getStatus());
        
        return truck;
    }

    public static TruckModel convertToModel(Truck entity) {
        TruckModel model = new TruckModel();
        
        model.setCurrentCityId(entity.getCurrentCity() == null ? 0 : entity
                .getCurrentCity().getId());
        
        model.setId(entity.getId());
        model.setLicencePlate(entity.getLicencePlate());
        model.setAssignedDeliveryOrder(entity.getAssignedDeliveryOrder());
        model.setCargoCapacity(entity.getCargoCapacity());
        model.setCrewSize(entity.getCrewSize());
        model.setDrivers(entity.getDrivers());
        model.setStatus(entity.getStatus());
        
        return model;
    }
}
