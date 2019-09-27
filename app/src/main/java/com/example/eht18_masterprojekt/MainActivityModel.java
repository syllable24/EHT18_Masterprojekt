package com.example.eht18_masterprojekt;

import java.util.List;

class MainActivityModel {
    List<String> services;

    public MainActivityModel(List<String> services){
        this.services = services;
    }


    public List<String> getServiceList(){
        return this.services;
    }

    public void addService(String service){
        this.services.add(service);
        //update View
    }
}
