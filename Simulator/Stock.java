import java.io.Serializable;
import java.util.Objects;
//stock is simply an instance of this class and has a share and quantity
class Stock implements Serializable {
    private int quantity;
    private Share share;

    public Stock(Share sshare, int qquantity) {
        share = sshare;
        quantity = qquantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    @Override
    public String toString() {
        return share.getShareInfo() +
                "Quantity=" + quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return quantity == stock.quantity &&
                share.getCompany().equals(stock.getShare().getCompany()) &&
                share.getPrice() == stock.getShare().getPrice() && share.getTypeofShare().equals(stock.getShare().getTypeofShare());
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, share);
    }
}
