package edu.eci.arsw.myrestaurant.services;


import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.beans.BillCalculator;
import edu.eci.arsw.myrestaurant.model.ProductType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("Stub")
public class RestaurantOrderServicesStub implements RestaurantOrderServices {

    @Autowired
    @Qualifier("WithTaxes")
    BillCalculator calc ;

    public RestaurantOrderServicesStub() {
    }

    public void setBillCalculator(BillCalculator calc) {
        this.calc = calc;
    }

    @Override
    public Order getTableOrder(int tableNumber) {
        if (!tableOrders.containsKey(tableNumber)) {
            return null;
        } else {
            return tableOrders.get(tableNumber);
        }
    }

    @Override
    public Set<String> getAvailableProductNames() {
        return productsMap.keySet();
    }
    @Override
    public void addOrderToOrder(Order newOrder){
        Map<String, Integer> nuevosproductos = newOrder.getOrderAmountsMap();
	int num= newOrder.getTableNumber();
        Order ordenAntigua=getTableOrder(num);
        Set<String> productos = nuevosproductos.keySet();
        for (String key : productos) {
            ordenAntigua.addDish(key, nuevosproductos.get(key));
            }
     }
    @Override 
    public void deleteOrderFromOrder(Order newOrder){
        Map<String, Integer> nuevosproductos = newOrder.getOrderAmountsMap();
        Order ordenAntigua=getTableOrder(newOrder.getTableNumber());
        Set<String> productos = nuevosproductos.keySet();
        for (String key : productos) {
            ordenAntigua.deleteProduct(key);
            }
        if (ordenAntigua.getOrderAmountsMap().size()==0){
            try {
                releaseTable(newOrder.getTableNumber());
            } catch (OrderServicesException ex) {
                Logger.getLogger(RestaurantOrderServicesStub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    @Override
    public RestaurantProduct getProductByName(String product) throws OrderServicesException {
        if (!productsMap.containsKey(product)) {
            throw new OrderServicesException("Producto no existente:" + product);
        } else {
            return productsMap.get(product);
        }
    }

    @Override
    public Set<Integer> getTablesWithOrders() {
        return tableOrders.keySet();
    }

    @Override
    public void addNewOrderToTable(Order o) throws OrderServicesException {
        if (tableOrders.containsKey(o.getTableNumber())) {
            throw new OrderServicesException("La mesa tiene una orden abierta. Debe "
                    + "cerrarse la cuenta antes de crear una nueva.:" + o.getTableNumber());
        } else {
            tableOrders.put(o.getTableNumber(), o);
        }

    }

    @Override
    public void releaseTable(int tableNumber) throws OrderServicesException {
        if (!tableOrders.containsKey(tableNumber)) {
            throw new OrderServicesException("Mesa inexistente o ya liberada:" + tableNumber);
        } else {
            tableOrders.remove(tableNumber);
        }

    }

    @Override
    public int calculateTableBill(int tableNumber) throws OrderServicesException {
        if (!tableOrders.containsKey(tableNumber)) {
            throw new OrderServicesException("Mesa inexistente o ya liberada:" + tableNumber);
        } else {
            return calc.calculateBill(tableOrders.get(tableNumber), productsMap);
        }
    }
    @Override
    public Map<Integer, Order> getAllTable(){
        return tableOrders;
        
    }
    @Override
    public Map<String, RestaurantProduct> getAllProducts(){
        return productsMap;
        
    }
    

    private static final Map<String, RestaurantProduct> productsMap;

    private static final Map<Integer, Order> tableOrders;
    

    static {
        productsMap = new ConcurrentHashMap<>();
        tableOrders = new ConcurrentHashMap<>();        
        productsMap.put("PIZZA", new RestaurantProduct("PIZZA", 10000, ProductType.DISH));
        productsMap.put("HOTDOG", new RestaurantProduct("HOTDOG", 3000, ProductType.DISH));
        productsMap.put("COKE", new RestaurantProduct("COKE", 1300, ProductType.DRINK));
        productsMap.put("HAMBURGER", new RestaurantProduct("HAMBURGER", 12300, ProductType.DISH));
        productsMap.put("BEER", new RestaurantProduct("BEER", 2500, ProductType.DRINK));

        Order o = new Order(1);
        o.addDish("PIZZA", 3);
        o.addDish("HOTDOG", 1);
        o.addDish("COKE", 4);

        tableOrders.put(1, o);

        Order o2 = new Order(3);
        o2.addDish("HAMBURGER", 2);
        o2.addDish("COKE", 2);

        tableOrders.put(3, o2);
    }

}
