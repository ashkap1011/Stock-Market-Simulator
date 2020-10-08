import java.io.Serializable;
//has three subclass, namely preferred, unlisted and ordinary share.
public abstract class Share implements Serializable{
    protected double volatility;
    private Company company; //company the share is of
    private double price;       //current price of the stock
    private int multiplier;    // used to randomly make a dividend.
    // private boolean cashordivi; // does the company pay cash or dividend 

    public Share(Company comp, double pprice,int multi) {
	company = comp;
	price = pprice;
	multiplier = multi;
    }

    public abstract String toString();
    public abstract void setVolatility();
    public abstract double getVolatility();
    public abstract String getTypeofShare();
    public abstract String getShareInfo();
    public abstract double getDividend();

    public Company getCompany() {
	return company;
    }

    public void setCompany(Company company) {
	this.company = company;
    }

    public double getPrice(){
	return this.price;
    }

    public void setPrice(double price) {
	this.price = price;
    }

    public int getMultiplier() {
	return multiplier;
    }

    public void setMultiplier(int multiplier) {
	this.multiplier = multiplier;
    }



	
}

