package org.admi.dataPreparation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Work {
    private Statement statement;
    private List<Tafassir> tafassirList = new ArrayList<>();
    private Map<Integer,String> tafassir = new HashMap<>();
    private int startOfNumberOfSourah;
    private int endOfNumberOfSourah;
    private int lemmaCenturyStart;
    private int lemmaCenturyEnd;


    public Work(int startOfNumberOfSourah, int endOfNumberOfSourah,int lemmaCenturyStart,int lemmaCenturyEnd) throws SQLException, IOException {
        this.endOfNumberOfSourah = endOfNumberOfSourah;
        this.startOfNumberOfSourah = startOfNumberOfSourah;
        this.lemmaCenturyStart = lemmaCenturyStart;
        this.lemmaCenturyEnd = lemmaCenturyEnd;

        makeSQLConnection();
        selectTafassir();
//        work();
        startLemmitization();
    }

    private void startLemmitization() {
        AlKhalilLemmitization khalilLemmatizer = new AlKhalilLemmitization();
        khalilLemmatizer.Lemmatizer("src\\dataset",lemmaCenturyStart,lemmaCenturyEnd);

    }

    private void selectTafassir() throws SQLException {
        ResultSet mofassir = statement.executeQuery("select * from Tafassir");
        while(mofassir.next())
            tafassirList.add(new Tafassir(Integer.parseInt(mofassir.getString("NTafsir")),mofassir.getString("An")));
    }

    private void makeSQLConnection() throws SQLException {
        String connection = "jdbc:sqlite:main_bayan.db";
        Connection connection1 = DriverManager.getConnection(connection);
        statement = connection1.createStatement();
    }

    private void work() throws SQLException, IOException {
        System.out.println("inside thread : "+Thread.currentThread().getName());
        for (int soura = startOfNumberOfSourah; soura <= endOfNumberOfSourah; soura++) {
            String souraNumber = getFullSourahTableName(soura);
            ResultSet tafssir = statement.executeQuery("select * from \""+souraNumber+"\"");
            while (tafssir.next()){
                String tafssirContent = tafssir.getString("Texte");
                int taffsirId = tafssir.getInt("NTafsir");
                String taffsirYear = getTafssirYear(taffsirId);
                int taffsirCentury = getCentury(taffsirYear);
                writeTafssir(taffsirCentury,tafssirContent);
            }
        }
        System.out.println("outside thread : "+Thread.currentThread().getName());
        writeIntoFiles();
    }
    private void writeIntoFiles() throws IOException {
        System.out.println("writing into the file : ");
        tafassir.forEach((key,value)->{
            //LemmesSiecle_i
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\dataset\\LemmesSiecle_"+key+".txt",true))){
                writer.append(value.replaceAll("EOL","\n").replaceAll("[<>]", ""));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("ending the write into the file!!");

    }
    private void writeTafssir(int taffsirCentury, String tafssirContent){
        tafassir.merge(taffsirCentury,tafssirContent,(oldValue,newValue)->oldValue+"\n"+newValue);
    }
    private String getTafssirYear(int taffsirId) {
        final Tafassir tafassir = new Tafassir();
        tafassirList.forEach((tafssir)->{
            if(tafssir.getTafssirId() == taffsirId){
                tafassir.setTafssirYear(tafssir.getTafssirYear());
                return;
            }
        });
        return tafassir.getTafssirYear();
    }
    private int getCentury(String year){
        return (Integer.parseInt(year)+99)/100;
    }
    private String getFullSourahTableName(int soura) {
        String finalForm;
        if(soura<10)
            return "00"+soura;
        if(soura <100)
            return "0"+soura;

        return String.valueOf(soura);
    }
}
