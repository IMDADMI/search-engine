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

       CenteryTreatment treatment = new CenteryTreatment();
       treatment.listTaffsirByCentery();
       //show the result of the table
       FilterLemma filterLemma = new FilterLemma();

    

    }
}
