package org.admi.bagOfWord;

import Jama.Matrix;
import net.oujda_nlp_team.ADATAnalyzer;
import org.admi.dataExtraction.FilterLemma;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static org.admi.dataExtraction.FilterLemma.removeSpecialCharacters;

public class PrepareData {
    private Statement statement;
    Map<String,Integer> tokenInSentece = new HashMap<>();

    private int interpretationNumber = 0;
    private List<Thread> threads = new ArrayList<>();
    private static final String QUERY = "SELECT S.TEXTE FROM \"114\" S,Tafassir T WHERE S.NTafsir = T.NTafsir AND T.An BETWEEN 600 AND 699";
    private String interpretations;
    private boolean token;
    private String targetName = "al-nass-lemmas.xml";




    private Map<Integer,List<String>> tokensPerInterpretation = new HashMap<>();
    private Set<String> tokens = new HashSet<>();

    /**
     * <p>This is a <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">boolean value</a>
     *, if the value is true then the similarity will be based on <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">
     *word</a>, else it will be based on <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">lemmas</a>
     *</p>
     */
    PrepareData(boolean token){
        this.token = token;
    }
    PrepareData(){
    }


    public void getSimilarity() throws SQLException, IOException, ParserConfigurationException, SAXException {
        makeSQLConnection();
        getInterpretation();
    }
    private void makeSQLConnection() throws SQLException {
        String connection = "jdbc:sqlite:main_bayan.db";
        Connection connection1 = DriverManager.getConnection(connection);
        statement = connection1.createStatement();
    }

    public void getInterpretation() throws SQLException, IOException, ParserConfigurationException, SAXException {
        ResultSet tafssir = statement.executeQuery(QUERY);
        while (tafssir.next()){
            interpretations = removeSpecialCharacters(tafssir.getString("TEXTE").replace("EOF","\n"));
            lemmitizeInterpretation(interpretations.trim(),++interpretationNumber);
        }
        getTokens();
//        System.out.println(interpretations);
    }

    private  void lemmitizeInterpretation(String interpretations,int interpretationNumber) {
        String srcFileName = interpretationNumber+"_al-nass-interpretations.txt";
        String targetFileName = interpretationNumber+targetName;
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("src\\bow\\interpretation\\"+srcFileName))){
            writer.append(interpretations);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

                ADATAnalyzer
                        .getInstance().processLemmatization(
                                "src\\bow\\interpretation\\" + srcFileName,
                                "utf-8",
                                "src\\bow\\lemmas\\" + targetFileName,
                                "utf-8");
    }

    private void getTokens() throws ParserConfigurationException, IOException, SAXException {
        System.out.println("interpretation number is : "+interpretationNumber);
        for(int i=1;i<=this.interpretationNumber;i++){
            NodeList list = FilterLemma.getItems("src\\bow\\lemmas\\"+i+targetName);
            System.out.println(i+targetName);
            List<String> tokenList = new ArrayList<>();
            for (int k = 0; k < list.getLength(); k++) {
                Node nNode = list.item(k);
                Element element = (Element) nNode;
                String token = element.getElementsByTagName("word").item(0).getTextContent();
                token = removeSpecialCharacters(token);
                if(!token.trim().equals("")){
                    tokens.add(token);
                    tokenList.add(token);
                }
            }
            this.tokensPerInterpretation.put(i,tokenList);
        }
        fill_TF_IDF();

    }

    private void fill_TF_IDF() {
        int line = tokensPerInterpretation.keySet().size();
        int column = tokens.size();
        double TF [][] = new double[line][column];
        for(int i = 0; i< line; i++){
            int k = 0;
            for (String token : tokens) {
                double frequencyOfTokenInEachInterpretation = Collections.frequency(tokensPerInterpretation.get(i+1), token);
//                System.out.println("token per inter : "+tokensPerInterpretation.get(i+1).size());
//                System.out.println("freq : "+frequencyOfTokenInEachInterpretation);
                TF[i][k++] = (frequencyOfTokenInEachInterpretation / (double)(tokensPerInterpretation.get(i+1).size()));
            }
        }
//        tokenInSentece //string integer
        tokens.forEach((token)->{
            final int numberOfSentenceContainingTheWord=1;
            tokensPerInterpretation.forEach((numberOfInterception, interceptionList)->{
                if(interceptionList.contains(token))
                    tokenInSentece.merge(token,numberOfSentenceContainingTheWord, Integer::sum);
            });
        });

        double tokenIDF[] = new double[tokens.size()];
        int index = 0;
        for(String fromTokenSet : tokens){
            double log = Math.log(tokenInSentece.get(fromTokenSet));
            double numberOfToken = tokensPerInterpretation.keySet().size();
//            System.out.println("the log is : "+log);
//            System.out.println("the number of token is : "+numberOfToken);

            tokenIDF[index] = (log/numberOfToken);

        }

        for (int i = 0; i < column; i++) {
            for (int j = 0; j <line; j++) {
                TF[j][i]+=tokenIDF[i];
            }
        }
        new Matrix(TF).print(line,column);
        RealVector realVector = new ArrayRealVector();

        CosineSimilarity similarity = new CosineSimilarity();
        double similarityBetweenTafassir = similarity.calculateSimilarity(TF,line,column);
        System.out.println("the similarity between tafassir is : "+similarityBetweenTafassir);


    }

    public static void main(String[] args) throws SQLException, IOException, ParserConfigurationException, SAXException {
        new PrepareData().getSimilarity();
//        double one = 1;
//        double one3 = 78;
//
//        System.out.println(one/one3);
    }
}
