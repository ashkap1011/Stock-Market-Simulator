import java.io.Serializable;
import java.math.*;
import java.util.Random;
//this type of share is used for trading
public class OrdinaryShare extends Share implements Serializable {
    private boolean votingRight;
    private double dividend;
    private String typeOfShare;
    private double oldPrice =1;

    public OrdinaryShare(Company comp, double sharePrice, int multiplier) {
        super(comp, sharePrice, multiplier);
        dividend = 3; initialDividend(multiplier);
        typeOfShare = "Ordinary Share";
        votingRight = true;
    }
    public String getShareInfo(){
        return "Company: " + getCompany().getName() +" Type: " + typeOfShare
                + "Current Price: "+ getPrice();
    }
    @Override
    public String toString () {
        String o = typeOfShare +": Current Price: "+ getPrice()+ " with a *current* dividend percentage of " + getDividend() + "with " + getVotingRight() + " voting rights";
        return o;
    }

    @Override
    public void setVolatility() {
        super.volatility = randomNumber(dividend);

        if(this.oldPrice!=1){
            dividend = Math.abs(this.getPrice()/oldPrice)*randomNumber(getMultiplier());
        }
        oldPrice= this.getPrice();
       // System.out.println("this is the dividend for ordinary share");
    }

    public double getVolatility(){
        return volatility;
    }

    public String getTypeofShare() {
        return typeOfShare;
    }

    public static double initialDividend(int multi){
         return randomNumber(multi);
    }

    public boolean getVotingRight() {
        return votingRight;
    }

    public double getDividend() {
        return dividend;
    }

    public static double randomNumber (double multiplier){
        Random random = new Random();
        return multiplier * random.nextInt(4)+1;
    }


}


