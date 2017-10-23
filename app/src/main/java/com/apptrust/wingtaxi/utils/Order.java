package com.apptrust.wingtaxi.utils;

import java.util.ArrayList;

/**
 * @author RareScrap
 */
public class Order {
    public ArrayList<Adres> adresses;
    int preferredTimeH;
    int preferredTimeM;

    public Order(ArrayList<Adres> adresses, int preferredTimeH, int preferredTimeM){
        this.adresses = adresses;
        this.preferredTimeH = preferredTimeH;
        this.preferredTimeM = preferredTimeM;
    }
}
