package com.codenotfound.primefaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ViewScoped
public class CarsView implements Serializable {

    private static final long serialVersionUID = 1L;

//  @Inject
//  private CarRepository carRepository;

    private List<Car> cars;

    @PostConstruct
    public void init() {
        cars = new ArrayList<>(5);
        cars.add(new Car(1L, "Audi", 1992, "Red"));
        cars.add(new Car(2L, "Fiat", 2001, "Red"));
        cars.add(new Car(3L, "Mercedes", 1991, "Brown"));
        cars.add(new Car(4L, "Fiat", 1962, "Black"));
        cars.add(new Car(5L, "Renault", 1997, "Brown"));
        cars.add(new Car(6L, "Renault", 1967, "Maroon"));
        cars.add(new Car(7L, "Renault", 1986, "Yellow"));
        cars.add(new Car(8L, "BMW", 1970, "Maroon"));
        cars.add(new Car(9L, "Fiat", 1990, "Silver"));
        cars.add(new Car(10L, "Renault", 1972, "Black"));
    }

    public List<Car> getCars() {
        return cars;
    }
}
