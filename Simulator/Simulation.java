/*
This controls the simulation and the general flow of the program

*/


import java.io.Serializable;
import java.util.*;

class Simulation implements Serializable {

    private User user;
    private Exchange exchange;
    private Order order;
    private Trade trade;

    public void start(){
		user = new User("ash");
		user.deposit(10000);
    	exchange = new Exchange(user);
		exchange.establish(); //creates the companies.

		order = new Order(exchange, user);
		trade = new Trade(exchange, user);

/*
		print("Welcome to this Stock Market Simulation");
		print("Please provide your name");
		String name = readInput();
		print("Hello " + name + " right now your balance is 0. How much would you like to deposit?");
		int deposit = readIntInput();
		test = new User(name);
		test.deposit(deposit);
		order = new Order(exchange, test);
		trade = new Trade(exchange, test);
		market();
		//perhaps create the users in an array and pass that to the method


 */


    }
/*
    public void market(){
	while(true){
		System.out.println("Day" + exchange.getDay());
	    print("Would you like to [buy] or [sell] or [trade] [terminate] or proceed to the [next] day");
	    String response = readInput();
	    if (response.equals("buy")){
		buy();
	    }
	    if (response.equals("sell")){
		sell();
	    }
	    if (response.equals("terminate")){
		terminate();
	    }
	    if(response.equals("trade")){
	    	trade();
		}
	    exchange.nextDay(); //todo code market orders executing

		}
    }
    public void trade(){		//todo make this whole method a lot cleaner.
    	print("which companies stock to view");			//some kind of illegal argument would be good
    	String response = readInput();
		if(isListed(response)){
			Company company = getCompany(response);
			exchange.displayOrderBook(company);
			print("Would you like to make a [market] order or [limit] order or [cancel] an order");
			String answer = readInput();
			if(answer.equals("limit")) {
				print("what price?");
				double price = Double.parseDouble(readInput());
				print("how many shares");
				int quantity = readIntInput();
				try {
					trade.placeLimitOrder(company, price, quantity);
				}catch(Exception e){}
				System.out.println("inaccessible : funds= " + test.getPortfolio().getLockedFunds() + "shares= " + test.getPortfolio().getLockedStock().toString());
				System.out.println("balance= " + test.getBalance());
				//todo TEST WHETHER ASK ORDERS CHECKS FUNDS AVAILBLE
				exchange.displayOrderBook(company);
				//TODO
				// Cancel limit orders, lock funds when limit order placed/ otherwise throw an exception if not availvable
			}
			if(answer.equals("cancel")){
				test.printActiveOrders();
				cancelOrder();
			} if(answer.equals("market")){				//market order
				//print current price of the company
				marketOrder(company);
			}
		}
    }

    public void marketOrder(Company company){
		print("would like to place a [buy] or [sell] market order");
		String input = readInput();
		boolean isBuy;
		int value;
		if(input.equals("buy")){
			print("how much in monetary value would you like to buy (integers)?");
			value = readIntInput();
			isBuy = true;
		} else{
			print("How many shares to sell?");
			value = readIntInput();
			isBuy =false;
		}
		try {
			trade.placeMarketOrder(company, isBuy, value);
		}
		catch(Exception w){
		}
	}

	public void cancelOrder(){
		if(test.getActiveLimitOrders().isEmpty()){
			print("you don't have any limit orders in place");
			return;
		}
		print("What companies stock (provide ticker) would you like to cancel");
		String ticker = readInput();
		if(test.getActiveLimitOrders().containsKey(getCompany(ticker))){
			Company company = getCompany(ticker);
			if(test.getActiveLimitOrders().size() ==1 && test.getActiveLimitOrders().get(company).size() ==1){
				trade.executeOrderCancellation(getCompany(ticker), -1, -1); //TODO MAYBE OVERLOAD INSTEAD OF THIS..
				return;
			}
			print("what price limit order would you like to cancel");
			double price = Double.parseDouble(readInput());

			if(test.getActiveLimitOrders().get(company).size() ==1){	         //i.e user has 1 limit order from the relevant company			//todo check if price matches the keys..
				trade.executeOrderCancellation(company, price,-1);
			} else{
				print("You have more than one limit order for this company at the price, which quantity do you want to remove");
				int quantity = readIntInput();
				trade.executeOrderCancellation(company, price, quantity);
			}
			test.printActiveOrders();	//prints updated active orders;
		}

		else{
			System.out.println("you don't have a limit order in place with a company with that ticker");
		}

	}



		//if limit then place order and add the order as a hash map to their portfolio
		//


/*
    public void buy(){
		print("The companies and their share prices are as follows");
		exchange.printCompanies();
		print("Which  company's stock would you like to buy?");
		String companyTicker = readInput();
		if(isListed(companyTicker)){						//maybe combine line 53 and 54 using exceptions
	    Company company = getCompany(companyTicker);
	    print("What share would you like to buy, [Pre]ferred or [Ord]inary or [Un]listed");
	    String type = readInput();
	    print("how many shares would you like to buy");
	    int quantity = readIntInput();
	    print("Would you like to buy " + quantity + " shares of " + company.getName() +  " Type Yes to confirm");
	    String ans = readInput();
	    if (ans.equals("Yes")) {
		order.buyOrder(company,type,quantity);
	    }
	    print("Your updated portfolio is now: ");
		test.printPortfolio();
	}


	
    }

    public void sell() {
		print("Your current portfolio is as follows");
		test.printPortfolio();
		print("what company' share would you like to sell, provide the ticker");
		String companyTicker = readInput();
		if(isListed(companyTicker)) {
			Company comp = getCompany(companyTicker);
			print("Would type of share would you like to sell,[Pre]ferred or [Ord]inary or [Un]listed ");
			String type = readInput();
			print("How many shares would you like to sell");
			int quantity = readIntInput();
			order.sellOrder(comp, type,quantity);
		}
		print("Your updated portfolio is now: ");
		test.printPortfolio();
    }

    public void terminate(){
		//this is your file i/o TODO
		/*
		Things to save:
		company: so the different share prices
		entire orderbook
		user stuff e.g. the protfolio, locked and unlocked stocks, active limit  orders

    }
 */


    public boolean isListed(String tick){
	for (Company s :exchange.getCompanyList()){
	    if (s.getTicker().equals(tick)){
		return true;
	    }	
	} return false;
    }

    public boolean isInteger(String input){
    	try{
			Integer.parseInt(input);
			return true;
		}
    	catch(NumberFormatException e){
			return false;
		}
	}
	//this method makes sure the price is to the nearest 0.5 e.g. 100, 100.0, 100.5
	public boolean isCorrectPriceForm(String input){
		try{
			Integer.parseInt(input);
			return true;
		} catch(NumberFormatException e){
			try{
			Double.parseDouble(input);
			if(input.endsWith(".5") || input.endsWith(".0")){
				return true;
			} else{
				return false;
			}
			} catch(NumberFormatException d){
				return false;
			}
		}
	}


    public Company getCompany(String tick){
	for (Company s: exchange.getCompanyList()){
	    if (s.getTicker().equals(tick)){
		return s;
	    }
	} return null; //maybe throw an exception here?
	
    }

	public Exchange getExchange() {
		return exchange;
	}

	public User getUser() {
		return user;
	}

	public Order getOrder() {
		return order;
	}

	public Trade getTrade() {
		return trade;
	}

	public void print(String message){
	System.out.println(message);
    }
    public String readInput(){
	Scanner reader = new Scanner(System.in);
    	return reader.nextLine();
    }
    public int readIntInput() throws NumberFormatException{
	return Integer.parseInt(readInput());
    }
    


}
