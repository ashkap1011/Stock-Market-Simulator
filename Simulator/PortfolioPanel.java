import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serializable;

public class PortfolioPanel extends JPanel {
    private User user;
    private JTextField addBalanceinput;
    private JPanel thisPanel;
    public PortfolioPanel(User uuser){
        thisPanel = new JPanel();
        user = uuser;
        createPanel(thisPanel);
    }

    public void createPanel(JPanel thisPanell){
        thisPanell.setLayout(new BorderLayout());
        //GridBagConstraints gbc = new GridBagConstraints();

        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new GridLayout(1,5));

        JLabel balanceLabel = new JLabel("Available Balance: ");
        JTextField balanceField = new JTextField(Double.toString(user.getAvailableBalance()));
        balanceField.setEditable(false);
        JLabel addBalanceLabel = new JLabel("Add Balance:");
        addBalanceinput = new JTextField("");
        JButton addBalanceButton = new JButton("Deposit");
        ActionListener depositListener = e -> {
            try{
                user.deposit(Integer.parseInt(addBalanceinput.getText()));
                portfolioPanelUpdate();

            } catch(NumberFormatException d){
                JOptionPane.showMessageDialog(null, "Please input an Integer value");
            } catch(IllegalArgumentException c){
                JOptionPane.showMessageDialog(null, c.getMessage());
            }
        };
        addBalanceButton.addActionListener(depositListener);                                        //todo  actionevent listener
        balancePanel.add(balanceLabel);
        balancePanel.add(balanceField);
        balancePanel.add(addBalanceLabel);
        balancePanel.add(addBalanceinput);
        balancePanel.add(addBalanceButton);

        //centre panel for active limit orders and stock portfolio
        JPanel stockAndOrders = new JPanel();
        stockAndOrders.setLayout(new GridLayout(1,2));
        //todo make the text area certain size only maybe change the horizontal scrollbar
        JTextArea activeLimitOrdersText = new JTextArea(user.getActiveLimitOrdersAsString());
        activeLimitOrdersText.setEditable(false);
        activeLimitOrdersText.setBorder(BorderFactory.createTitledBorder("Active Limit Orders"));
        JScrollPane limitOrderScrp = new JScrollPane(activeLimitOrdersText);
        limitOrderScrp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        limitOrderScrp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JTextArea stockPortfolioText = new JTextArea(user.getPortfolio().getPortfolioAsString());
        stockPortfolioText.setEditable(false);
        stockPortfolioText.setBorder(BorderFactory.createTitledBorder("Stock Portfolio"));
        JScrollPane portfolioScrp = new JScrollPane(stockPortfolioText);
        portfolioScrp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        portfolioScrp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        portfolioScrp.setMaximumSize(new Dimension(300,400));

        stockAndOrders.add(limitOrderScrp);
        stockAndOrders.add(portfolioScrp);

        //panel for user's portfolio valuation      //TODO THE MATH ISN'T CORRECT!!!!! also adding depsoit increases returns
        JPanel userPortfolioValuation = new JPanel();
        userPortfolioValuation.setLayout(new FlowLayout());
        JLabel totalValuationLabel = new JLabel("Portfolio Total Valuation: ");
        JTextField totalValuationText = new JTextField(Double.toString(user.getTotalUserPortfolioValuation()));
        totalValuationText.setEditable(false);
        JLabel rOI = new JLabel("Total Returns On Initital Deposit");
        String ROIString = user.getReturnsOnInvestmentAsString();
        JTextField rOITEXT = new JTextField(ROIString);
        if(ROIString.contains("+")){
            rOITEXT.setForeground(Color.blue);
        }
        if(ROIString.contains("-")){
            rOITEXT.setForeground(Color.RED);
        }
        rOITEXT.setEditable(false);
        userPortfolioValuation.add(totalValuationLabel);
        userPortfolioValuation.add(totalValuationText);
        userPortfolioValuation.add(rOI);
        userPortfolioValuation.add(rOITEXT);

        thisPanell.add(balancePanel, BorderLayout.NORTH);
        thisPanell.add(stockAndOrders,BorderLayout.CENTER);
        thisPanell.add(userPortfolioValuation,BorderLayout.SOUTH);
        thisPanell.setBorder(BorderFactory.createTitledBorder("Porfolio"));
        add(thisPanell);
    }

    public void portfolioPanelUpdate(){
        remove(0);
        thisPanel = new JPanel();
        createPanel(thisPanel);
        revalidate();
        repaint();
    }



}
