/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dayman.poiot.backend;

/**
 *
 * @author bpyou
 */
public class GraphMaker {
    
    public String[] getPoints(String[][] data, int targetX, int targetY){
        String returnArray[] = new String[data.length];

        for(int i = 0; i < returnArray.length; i++){
            returnArray[i] = data[i][targetX] + "" + data[i][targetY];
        }

        return returnArray;
    }
}
