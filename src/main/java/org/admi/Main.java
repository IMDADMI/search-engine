package org.admi;

import Jama.Matrix;
import org.admi.dataExtraction.FilterLemma;
import org.admi.dataPreparation.CenteryTreatment;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, ParserConfigurationException, IOException, SAXException {
//
//        CenteryTreatment treatment = new CenteryTreatment();
//        treatment.listTaffsirByCentery();
//        //show the result of the table
//        FilterLemma filterLemma = new FilterLemma();

        File dir = new File("src\\lemmesSiecle");
        if(dir.exists() && dir.isDirectory()) {
            File files[] = dir.listFiles();
            int filesLength = files.length;
            for (int i = 0; i <filesLength; i++) {
                if(files[i].getName().equals(".gitignore")){
                    System.out.println("do");
                }
            }
        }

    }
}