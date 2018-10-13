package project;

import javax.swing.*;
import java.awt.*;

public class ConfigFrame extends JFrame {
    /*  Settings:
		 * 1) Floors quantity
		 * 2) Elevators capacity
		 * 3) People to generate
    */
    private JTextField numberOfFloorsField = new JTextField();
    private JTextField elevatorCapacityField = new JTextField();
    private JTextField numberOfPeopleField = new JTextField();
    private JLabel errorLabel = new JLabel("");
    private JButton startButton = new JButton("Start");

    public ConfigFrame()
    {
        init();
    }

    private void init()
    {
        setTitle("The Elevators");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4,2));
        setVisible(true);
        setTextFields();


        pack();
    }

    private void setTextFields()
    {
        elevatorCapacityField.setPreferredSize(new Dimension(60,30));
        numberOfFloorsField.setPreferredSize(new Dimension(60,30));
        numberOfPeopleField.setPreferredSize(new Dimension(60,30));
        JLabel numberOfFloorsLabel = new JLabel("Number of floors: [4;9]");
        JLabel elevatorCapacityLabel = new JLabel("Elevator capacity: (>0)");
        JLabel numberOfPeopleLabel = new JLabel("Number of people to generate: [1;50]");
        getContentPane().add(numberOfFloorsLabel);
        getContentPane().add(numberOfFloorsField);
        getContentPane().add(elevatorCapacityLabel);
        getContentPane().add(elevatorCapacityField);
        getContentPane().add(numberOfPeopleLabel);
        getContentPane().add(numberOfPeopleField);
        getContentPane().add(startButton);
        getContentPane().add(errorLabel);
        startButton.addActionListener(e -> {
            if(areEnteredValuesCorrect())
            {
                int numberOfFloors = Integer.parseInt(numberOfFloorsField.getText());
                int elevatorsCapacity = Integer.parseInt(elevatorCapacityField.getText());
                int personQuantity =  Integer.parseInt(numberOfPeopleField.getText());
                new Engine(numberOfFloors,elevatorsCapacity,personQuantity);
                dispose();
            }
        });
    }

    private boolean areEnteredValuesCorrect()
    {
        boolean result = isPeopleNumberValueCorrect();
        if(result) result = isFloorNumberValueCorrect();
        if(result) result = isElevatorCapacityValueCorrect();
        return result;
    }
    private boolean isFloorNumberValueCorrect()
    {
        boolean result = isInteger(numberOfFloorsField.getText());
        if(result)
        {
            int floorsNr = Integer.parseInt(numberOfFloorsField.getText());
            if(floorsNr > 3 && floorsNr < 10) return true;
        }
        errorLabel.setText("Wrong number of floors");
        return false;
    }

    private boolean isPeopleNumberValueCorrect()
    {
        boolean result = isInteger(numberOfPeopleField.getText());
        if(result)
        {
            int pplNr = Integer.parseInt(numberOfPeopleField.getText());
            if(pplNr > 0 && pplNr < 51) return true;
        }
        errorLabel.setText("Wrong number of people");
        return false;
    }

    private boolean isElevatorCapacityValueCorrect()
    {
        boolean result = isInteger(elevatorCapacityField.getText());
        if(result)
        {
            int capacity = Integer.parseInt(elevatorCapacityField.getText());
            if(capacity > 0) return true;
        }
        errorLabel.setText("Wrong elevator capacity");
        return false;
    }

    private boolean isInteger(String string)
    {
        try{ Integer.parseInt(string); }
        catch(NumberFormatException e) { return false; }
        return true;
    }

}
