package com.example.eht18_masterprojekt;

public class MainActivityController {

    private MainActivityModel model;
    private MainActivityView view;

    public MainActivityController(MainActivityModel m, MainActivityView v){
        model = m;
        view = v;
    }

    public void updateView(){
        view.printServiceList(model.getServiceList());
    }

}
