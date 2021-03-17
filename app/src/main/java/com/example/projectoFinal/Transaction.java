package com.example.projectoFinal;

import java.io.Serializable;
import java.util.HashMap;

public class Transaction implements Serializable {


    public double moviment;
    public String type;
    public String description;
    public String date;



    public Transaction(double movimentInput, String typeInput, String descriptionInput, String dateInput) {


        moviment = movimentInput;
        type =  typeInput;
        description = descriptionInput;
        date = dateInput;

    }

    public double getMoviment() {
        return moviment;
    }

    public void setMoviment(double moviment) {
        this.moviment = moviment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String type) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @Override
    public String toString() {


        return type + ":" + moviment + " | " + date;
    }

}
