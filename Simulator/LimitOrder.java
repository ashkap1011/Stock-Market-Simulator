import java.io.Serializable;
import java.util.UUID;
//this is the limit order class which is used for trading ordinary shares
public class LimitOrder implements Serializable {
    double price;
    int quantity;
    UUID id;

    public LimitOrder(double pprice, int qquantity){
        price =pprice;
        quantity = qquantity;
        id = UUID.randomUUID();
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public UUID getId() {
        return id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Price: " + price +", Quantity: " + quantity +", ID: " + id; //TODO takeid out after testing
    }

    public double getValue(){
        return price*quantity;
    }

    public void appreciateDevianceOnPrice(double dev){
        price += dev;
    }




/*
    @Override
    public int compareTo(Object obj) {
        LimitOrder that = (LimitOrder) obj;
        if (this.getPrice() < that.getPrice()){
            return -1;
        } else if (this.getPrice() == that.getPrice()){
            return 0;
        } else {
            return 1;
        }
    }

 */




}

