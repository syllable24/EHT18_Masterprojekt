package com.example.eht18_masterprojekt;

public class MainActivityController {

    private MainActivityModel model;
    private MainActivity view;

    public MainActivityController(MainActivityModel m, MainActivity v){
        model = m;
        view = v;
    }

    public void updateView(){
        view.printServiceList(model.getServiceList());
    }

}
