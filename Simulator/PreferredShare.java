import java.io.Serializable;
import java.util.*;
import java.math.*;

class PreferredShare extends Share implements Serializable {
    private double fixedDividend;
    private String typeOfShare;

    public PreferredShare(Company comp, double sharePrice, int multiplier){
	super(comp,sharePrice,multiplier);
	fixedDividend = randomNumber(multiplier) +2;
	typeOfShare = "Preferred Share";
    }

    @Override
    public String toString() {
        String s = typeOfShare + "Current Price: "+ getPrice()+" with a set dividend percentage of " + fixedDividend;
        return s;
    }
    @Override
    public String getShareInfo(){
        return "Company: " + getCompany().getName() +" Type: " + typeOfShare
                + "Current Price: "+ getPrice();
    }

    @Override
    public void setVolatility() {
        super.volatility = fixedDividend*3;
    }


    public double getVolatility(){
        return volatility;
    }

    @Override
    public String getTypeofShare() {
        return typeOfShare;
    }

    public double getDividend() {
        return fixedDividend;
    }

    public static double randomNumber(double multiplier){
	Random random = new Random();
	return multiplier*random.nextDouble();
    }






    
}
