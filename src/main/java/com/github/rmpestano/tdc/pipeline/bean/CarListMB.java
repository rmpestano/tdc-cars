package com.github.rmpestano.tdc.pipeline.bean;

import com.github.adminfaces.persistence.bean.CrudMB;
import com.github.adminfaces.persistence.service.CrudService;
import com.github.adminfaces.persistence.service.Service;
import com.github.adminfaces.persistence.util.Messages;
import com.github.rmpestano.tdc.pipeline.service.CarService;
import com.github.rmpestano.tdc.pipeline.model.Car;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

import static com.github.adminfaces.template.util.Assert.has;


/**
 * Created by rmpestano on 12/02/17.
 */
@Named
@ViewScoped
public class CarListMB extends CrudMB<Car> implements Serializable {

    @Inject
    CarService carService;

    @Inject
    @Service
    CrudService<Car, Integer> crudService; //generic injection

    @Inject
    public void initService() {
        setCrudService(carService);
    }

    public List<String> completeModel(String query) {
        List<String> result = carService.getModels(query);
        return result;
    }

    public void findCarById(Integer id) {
        if (id == null) {
            throw new BusinessException("Provide Car ID to load");
        }
        selectionList.add(crudService.findById(id));
        getFilter().addParam("id",id);//show only the found car in table list
    }

    public void delete() {
        int numCars = 0;
        for (Car selectedCar : selectionList) {
            numCars++;
            carService.remove(selectedCar);
        }
        selectionList.clear();
        addDetailMsg(numCars + " car(s) deleted successfully!");
    }

    public String getSearchCriteria() {
        StringBuilder sb = new StringBuilder(21);

        String nameParam = null;
        Car carFilter = filter.getEntity();
        if (filter.hasParam("name")) {
            nameParam = filter.getStringParam("name");
        } else if (has(carFilter) && carFilter.getName() != null) {
            nameParam = carFilter.getName();
        }

        if (has(nameParam)) {
            sb.append("<b>name</b>: " + nameParam + ", ");
        }

        String modelParam = null;
        if (filter.hasParam("model")) {
            modelParam = filter.getStringParam("model");
        } else if (has(carFilter) && carFilter.getModel() != null) {
            modelParam = carFilter.getModel();
        }

        if (has(modelParam)) {
            sb.append("<b>model</b>: " + modelParam + ", ");
        }

        Double priceParam = null;
        if (filter.hasParam("price")) {
            priceParam = filter.getDoubleParam("price");
        } else if (has(carFilter) && carFilter.getModel() != null) {
            priceParam = carFilter.getPrice();
        }

        if (has(priceParam)) {
            sb.append("<b>price</b>: " + priceParam + ", ");
        }

        if (filter.hasParam("minPrice")) {
            sb.append("<b>" + Messages.getMessage("label.minPrice") + "</b>: " + filter.getParam("minPrice") + ", ");
        }

        if (filter.hasParam("maxPrice")) {
            sb.append("<b>" + Messages.getMessage("label.maxPrice") + "</b>: " + filter.getParam("maxPrice") + ", ");
        }

        int commaIndex = sb.lastIndexOf(",");

        if (commaIndex != -1) {
            sb.deleteCharAt(commaIndex);
        }

        if (sb.toString().trim().isEmpty()) {
            return "No search criteria";
        }

        return sb.toString();
    }


}
