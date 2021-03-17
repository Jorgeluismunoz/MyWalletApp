package com.example.projectoFinal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MonthAccounting implements Serializable {

    public int month;
    public int year;
    public String prueba;
    public ArrayList<Transaction> transtactions;
    public double balance;
    public double totalExpense;
    public double totalIncome;


    public MonthAccounting(int monthInput, int yearInput) {
        month = monthInput;
        year = yearInput;
        prueba = "fixo";
        transtactions = new ArrayList<Transaction>();
        balance = 0;
        totalExpense = 0;
        totalIncome = 0;
    }

    public String getMonth() {
        return MontLetter(month);
    }
    public int getMonthInt() { return month; }

    public int getYear() {
        return year;
    }

    public String MontLetter(int month) {
        switch (month) {
            case 1:  return "January";
            case 2:  return "February";
            case 3:  return "March";
            case 4:  return "April";
            case 5:  return "May";
            case 6:  return "June";
            case 7:  return "July";
            case 8:  return "August";
            case 9:  return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "Invalid month";
        }
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String pruebaInput) {
        prueba = pruebaInput;
    }

    public void setItem(MonthAccounting itemUdate) {
        this.month = itemUdate.month;
        this.year = itemUdate.year;
        this.prueba = itemUdate.prueba;
        this.transtactions = itemUdate.transtactions;
        this.balance = itemUdate.balance;
        this.totalExpense = itemUdate.totalExpense;
    }

    public double getBalance() {
        calculate();
        return balance;
    }
    public double getTotalExpense() {
        calculate();
        return totalExpense;
    }
    public double getTotalIncome() {
        calculate();
        return totalIncome;
    }

    public void calculate() {
        balance = 0;
        totalIncome = 0;
        totalExpense = 0;
        for(int i =0; i < transtactions.size(); i++ )
        {
            double amount = transtactions.get(i).getMoviment();
            balance += amount ;
            if (amount < 0) totalExpense += amount;
            else totalIncome += amount;
        }

    }


    public void addTransaction(double moviment, String type, String description, String date) {
        if (type == "Expense" && moviment > 0) {
            totalExpense += moviment;
            moviment = moviment * -1;
        }
        transtactions.add(new Transaction(moviment, type, description, date));

    }
    @Override
    public String toString()
    {
        return  year + " - " + month ;
    }


    public HashMap<String, Double> distributeTransactionByDescription() {

        HashMap<String, Double> map=new HashMap<String, Double>();
        for(int i = 0; i < transtactions.size(); i++ ) {
            String descTem = transtactions.get(i).getDescription();
            double moviTem = (double) transtactions.get(i).getMoviment();
            if (!map.containsKey(descTem))
                map.put( descTem, (double) moviTem);
            else
                map.put(descTem, (double) map.get(descTem) + moviTem);
        }
        return map;
    }

}
