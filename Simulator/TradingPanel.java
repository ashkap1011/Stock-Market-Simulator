import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
//this panel allows the user to trade and place market/limit orders for the ordinary shares
public class TradingPanel extends JPanel {
    private JTabbedPane tradingPane;
    private isBuyPanel buyPanel;
    private isBuyPanel sellPanel;

    //private Simulation StockExchange;

    public TradingPanel(Simulation exchange,GUI guiWindow){
        tradingPane = new JTabbedPane();
        buyPanel = new isBuyPanel(true, exchange, guiWindow);
        sellPanel = new isBuyPanel(false,exchange, guiWindow);
        tradingPane.add(buyPanel, "Buy");
        tradingPane.add(sellPanel, "Sell");
        add(tradingPane);
    }

    private class isBuyPanel extends JPanel {
        private boolean isBuyPanel;
        private JPanel orderType;
        private JPanel userInput;       //todo make this one have cardLayout
        private JTextField tickerTextField = new JTextField();;
        private JTextField priceTextField = new JTextField();;
        private JTextField quantityInputField = new JTextField();;
        private JButton placeOrderButton = new JButton();
        private JLabel markLimitQuantityLabel = new JLabel();
        private ButtonGroup markLimitButtonGroup = new ButtonGroup();
        private Simulation stockExchange;
        private GUI GUIWindow;

        private isBuyPanel(boolean isBuy, Simulation exchange, GUI GGUIWindow){
            isBuyPanel = isBuy;
            stockExchange = exchange;
            GUIWindow = GGUIWindow;
            setLayout(new GridLayout(1,2));
            orderType = new JPanel();
            createOrderTypePanel();
            add(orderType);

            userInput = new JPanel();
            createUserInputPanel();
            add(userInput);
        }

        private void createOrderTypePanel(){
            orderType.setLayout(new GridLayout(2,1));
            JRadioButton limitRbutton = new JRadioButton("Limit",true);
            JRadioButton marketRbutton = new JRadioButton("Market",false);
            marketRbutton.setActionCommand("market");
            limitRbutton.setActionCommand("limit");

            markLimitButtonGroup.add(marketRbutton);
            markLimitButtonGroup.add(limitRbutton);
            orderType.add(limitRbutton);
            orderType.add(marketRbutton);
            //since buy market orders take a value (£) but everything else takes shares as an amount this actione event takes care of that
            ActionListener orderTypeListener = e -> {
                if(markLimitButtonGroup.getSelection().getActionCommand().equals("market")){
                    priceTextField.setText("Current Price");
                    priceTextField.setEditable(false);
                    if(this.isBuyPanel){
                    markLimitQuantityLabel.setText("Value (£)");
                    }
                } else {
                    markLimitQuantityLabel.setText("Quantity (Shares)");
                    priceTextField.setText("");
                    priceTextField.setEditable(true);
                }
            };

            marketRbutton.addActionListener(orderTypeListener);
            limitRbutton.addActionListener(orderTypeListener);

        }
        //this method creates the fields where the userinputs ticker, price and share/value, it also places the market/limit orders through the action event.
        private void createUserInputPanel(){
            userInput.setLayout(new GridLayout(7,1));
            placeOrderButton = new JButton(isBuyPanel? "Place Buy": "Place Sell");

            userInput.add(new JLabel("Company Ticker"));
            userInput.add(tickerTextField);
            userInput.add(new JLabel("Price"));
            userInput.add(priceTextField);
            markLimitQuantityLabel.setText("Quantity (Shares)");
            userInput.add(markLimitQuantityLabel);
            userInput.add(quantityInputField);
            userInput.add(placeOrderButton);

            ActionListener placeOrderListener = e -> {
                if(stockExchange.isListed(tickerTextField.getText())){
                    if(stockExchange.isInteger(quantityInputField.getText())) {
                        if(markLimitButtonGroup.getSelection().getActionCommand().equals("market")){    //is Market ORDER
                            try{
                                stockExchange.getTrade().placeMarketOrder(stockExchange.getCompany(tickerTextField.getText()),isBuyPanel,Integer.parseInt(quantityInputField.getText()));
                                GUIWindow.updateAllPanels();
                            }
                            catch(NotEnoughFunds z){
                                JOptionPane.showMessageDialog(null,z.getMessage());
                            }
                            //otherwise is limitOrder
                        } else if(stockExchange.isCorrectPriceForm(priceTextField.getText()) && markLimitButtonGroup.getSelection().getActionCommand().equals("limit")){
                            double currentSharePrice = stockExchange.getCompany(tickerTextField.getText()).getOrdinaryShare().getPrice();
                            if(isBuyPanel && Double.parseDouble(priceTextField.getText()) >= currentSharePrice){
                                JOptionPane.showMessageDialog(null, "please enter a Buy (Bid) price less than the current share price of " + currentSharePrice);
                            } else if(!isBuyPanel && Double.parseDouble(priceTextField.getText()) <= currentSharePrice){
                                JOptionPane.showMessageDialog(null, "please enter a Sell (Ask) price more than the current share price of " + currentSharePrice);
                            } else{
                                try{
                                    stockExchange.getTrade().placeLimitOrder(stockExchange.getCompany(tickerTextField.getText()),Double.parseDouble(priceTextField.getText()),Integer.parseInt(quantityInputField.getText()));
                                    GUIWindow.updateAllPanels();
                                }catch(NotEnoughFunds i){
                                    JOptionPane.showMessageDialog(null,i.getMessage());
                                }
                            }
                        } else{
                            JOptionPane.showMessageDialog(null,"Please provide a value to the nearest 0.5 e.g. 100.5 or 100.0 or 100");
                        }
                    } else{//TODO INVALID INPUT NEED AN INTEGER maybe use boolean for int/double
                        JOptionPane.showMessageDialog(null,"Please input an integer quantity");
                    }
                } else{
                    JOptionPane.showMessageDialog(null, "The ticker does not exist");    //todo Print the ticker doesn't exist
                }

            };
            placeOrderButton.addActionListener(placeOrderListener);


        }


    }

}
