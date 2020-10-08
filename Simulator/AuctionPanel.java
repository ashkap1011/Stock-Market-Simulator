import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
//this panel allows the user to place buy/sell orders for preferred and ordinary shares.
public class AuctionPanel extends JPanel {
    private JPanel orderType;
    private JPanel orderDetails;
    private Simulation stockExchange;
    private JComboBox tickerList;
    private ButtonGroup shareTypeButtonGroup = new ButtonGroup();
    ButtonGroup BuySellButtonGroup = new ButtonGroup();
    private JTextField priceTextField = new JTextField();
    private JTextField quantityInputField = new JTextField();
    private JButton placeOrderButton = new JButton("Place Order");
    private GUI guiWindow;
    //todo doesn't show a price iniitally in the curreprice
    public AuctionPanel(Simulation exchange, GUI guiWindow){
        setLayout(new GridLayout(1,2));
        stockExchange = exchange;
        orderType = new JPanel();
        orderDetails = new JPanel();
        this.guiWindow = guiWindow;
        tickerList = new JComboBox(exchange.getExchange().getCompanyListAsTickerStringArray());
        createOrderTypePanel();
        createOrderDetailsPanel();

        add(orderType);
        add(orderDetails);

    }

    private void createOrderTypePanel(){
        orderType.setLayout(new GridLayout(2,1));
        JRadioButton buyRButton = new JRadioButton("Buy",true);
        buyRButton.setActionCommand("Buy");
        JRadioButton sellRButton = new JRadioButton("Sell",false);
        sellRButton.setActionCommand("Sell");
        BuySellButtonGroup.add(buyRButton);
        BuySellButtonGroup.add(sellRButton);
        orderType.add(buyRButton);
        orderType.add(sellRButton);

    }
    private void createOrderDetailsPanel(){
        JPanel shareTypePanel = new JPanel();
        shareTypePanel.setLayout(new GridLayout(1,2));
        JRadioButton preferredShareRButton = new JRadioButton("Preferred", true);//todo maybe action command
        preferredShareRButton.setActionCommand("Preferred");
        JRadioButton unlistedShareRButton = new JRadioButton("Unlisted");
        unlistedShareRButton.setActionCommand("Unlisted");
        unlistedShareRButton.setToolTipText("Unlisted Shares don't have a price");
        //this panel chnages the price depending on the ticker selected.
        ItemListener tickerSelectedListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    if(shareTypeButtonGroup.getSelection().getActionCommand().equals("Preferred")){
                        try{ priceTextField.setText(Double.toString(stockExchange.getCompany(tickerList.getSelectedItem().toString())
                                .getCompanyStockSpecs().getPreferredSharePrice()));
                        } catch(NotAvailable z){
                            priceTextField.setText(z.getMessage());
                        }
                    } else {
                        priceTextField.setText("No Price");
                    }
                }
        }
        };
        tickerList.addItemListener(tickerSelectedListener);


        try {
            priceTextField.setText(Double.toString(stockExchange.getExchange().getCompanyList().get(0).getCompanyStockSpecs().getPreferredSharePrice()));
        }catch (Exception e){}
        //this action listener sets the different price based on if a preferred share was selected or a unlisted share( which doesnn't have a price)
        ActionListener orderTypeListener = e -> {
            if(shareTypeButtonGroup.getSelection().getActionCommand().equals("Preferred")){
               try{ priceTextField.setText(Double.toString(stockExchange.getCompany(tickerList.getSelectedItem().toString())
                       .getCompanyStockSpecs().getPreferredSharePrice()));
            } catch(NotAvailable z){
                   priceTextField.setText(z.getMessage());
               }
            } else {
                priceTextField.setText("No Price");
            }
        };
        preferredShareRButton.addActionListener(orderTypeListener);
        unlistedShareRButton.addActionListener(orderTypeListener);
        shareTypeButtonGroup.add(preferredShareRButton);
        shareTypeButtonGroup.add(unlistedShareRButton);
        shareTypePanel.add(preferredShareRButton);
        shareTypePanel.add(unlistedShareRButton);

        priceTextField.setEditable(false);

        orderDetails.setLayout(new GridLayout(11,1));
        orderDetails.add(new JLabel("Select Company Ticker"));
        orderDetails.add(tickerList);
        orderDetails.add(new JLabel("Select Share Type"));
        orderDetails.add(shareTypePanel);
        orderDetails.add(new JLabel("Current Price"));
        orderDetails.add(priceTextField);
        orderDetails.add(new JLabel("Quantity"));
        orderDetails.add(quantityInputField);
        orderDetails.add(placeOrderButton);

        ActionListener orderPlacementListener = e -> {
            Company company = stockExchange.getCompany(tickerList.getSelectedItem().toString());
            String typeOfShare = shareTypeButtonGroup.getSelection().getActionCommand();
            if (stockExchange.isInteger(quantityInputField.getText())){
               try{
                    if(BuySellButtonGroup.getSelection().getActionCommand().equals("Buy")){
                        stockExchange.getOrder().buyOrder(company, typeOfShare, Integer.parseInt(quantityInputField.getText()));
                        guiWindow.updateAllPanels();
                    } else{
                        stockExchange.getOrder().sellOrder(company, typeOfShare, Integer.parseInt(quantityInputField.getText()));
                        guiWindow.updateAllPanels();
                    }
               }
                catch(NotEnoughFunds z){
                        JOptionPane.showMessageDialog(null, z.getMessage());
                    }
            } else{
                JOptionPane.showMessageDialog(null, "Please enter an integer quantity");
            }
        };

        placeOrderButton.addActionListener(orderPlacementListener);

    }



}
