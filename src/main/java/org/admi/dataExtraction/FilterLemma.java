package org.admi.dataExtraction;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class FilterLemma {

    private Map<Integer, String> lemmasPerCentury = new HashMap<>();
    private Map<Integer, Integer> listOfWords = new HashMap<>();
    private Map<Integer, Integer> numberOfLemmasExcludedFromCoran = new HashMap<>();
    private Set<String> words = new HashSet<>();
    private Map<Integer, List<String>> lemmaListByCentury = new HashMap<>();
    private List<String> coranLemmaList = new ArrayList<>();
    private List<String> lemmaList = new ArrayList<>();
    private String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <Lemmas>\n";

    public FilterLemma() throws ParserConfigurationException, IOException, SAXException {
        saveLemma();
        saveNonCoranLemma();
        getTheNumberOfWordsAndLemmas();
    }

    public void saveLemma() throws ParserConfigurationException, IOException, SAXException {

        File directory = new File("src\\lemme");
        if (directory.exists() && directory.isDirectory()) {
            File files[] = directory.listFiles();
            int filesLength = files.length;

            for (int i = 0; i < filesLength; i++) {
                String fileName = files[i].getName();
                int numericalValue = Integer.parseInt(fileName.substring(fileName.lastIndexOf('_') + 1, fileName.indexOf('.')));
                lemmasPerCentury.merge(numericalValue, getContentFromXML(fileName, numericalValue), (oldValue, newValue) -> oldValue + newValue);
            }

        }
        listLemmaIntoLemmaFiles();
    }


    public void saveNonCoranLemma() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\coranLemma\\Quran-Lemmas-uniq.txt"))) {
            String line;
            while ((line = reader.readLine()) != null)
                coranLemmaList.add(line.trim());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filterLemma();
    }

    //QUESTION 2
    private void filterLemma() {
        this.lemmaListByCentury.forEach((century, listOfLemma) -> {
            List<String> lemmaExcludedFromCoranLemma = listOfLemma.stream().filter((lemma) -> !coranLemmaList.contains(lemma)).toList();
            numberOfLemmasExcludedFromCoran.put(century, lemmaExcludedFromCoranLemma.size());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\lemmesSiecleSansLemmesSaintCoran\\LemmesSiecleSansLemmesSaintCoran_" + century + ".xml", true))) {
                writer.append(xmlHeader);
                lemmaExcludedFromCoranLemma.forEach((lemma) -> {
                    try {
                        writer.append("<lemma>").append(lemma).append("<lemma/>\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                writer.append("<Lemmas>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void getTheNumberOfWordsAndLemmas() {
        System.out.println("number of lemma not in coran by century : \n");
        numberOfLemmasExcludedFromCoran.forEach((cen, numberOfLemma) -> System.out.printf("century : %d, number of lemma : %d\n",cen,numberOfLemma));

        System.out.println("number of word by century");
        listOfWords.forEach((century, numberOfWord) -> System.out.printf("century : %d, number of word : %d\n", century, numberOfWord));

        System.out.println("number of lemma by century : ");
        lemmaListByCentury.forEach((century, listOfLemma) -> System.out.printf("century %d, number of lemma %d\n", century, listOfLemma.size()));

    }

    private void listLemmaIntoLemmaFiles() {
        //Question 1
        lemmasPerCentury.forEach((century, lemma) -> {
            String fileName = "LemmesSiecle_" + century + ".xml";
            StringBuilder result = new StringBuilder();
            result
                    .append(xmlHeader)
                    .append(lemma)
                    .append("</Lemmas>");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\lemmesSiecle\\" + fileName, true))) {
                writer.append(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static NodeList getItems(String path) throws ParserConfigurationException, IOException, SAXException {
        File inputFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        return  doc.getElementsByTagName("res");
    }
    private String getContentFromXML(String fileName, int century) throws ParserConfigurationException, IOException, SAXException {
        NodeList nList = getItems("src\\lemme\\" + fileName);
        StringBuilder lemmaBuilder = new StringBuilder();
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            Element element = (Element) nNode;
            String lemma = element.getElementsByTagName("lemma").item(0).getTextContent();
            lemma = removeSpecialCharacters(lemma);
            String word = element.getElementsByTagName("word").item(0).getTextContent();
            words.add(word);
            if (lemma != "" && lemmaBuilder.indexOf(lemma) == -1) {
                lemmaBuilder.append("<lemma>").append(lemma).append("<lemma/>\n");
                lemmaList.add(lemma);
            }
        }

        listOfWords.merge(century, words.size(), (oldV, newV) -> newV + oldV);
        lemmaListByCentury.merge(century, lemmaList, (oldV, newV) -> {

            newV.addAll(oldV);
            return newV;
        });
        lemmaList = new ArrayList<>();
        words = new HashSet<>();
        return lemmaBuilder.toString();
    }

    public static String removeSpecialCharacters(String lemma) {
        return (lemma.replaceAll("[،؟.\\[\\]{}()’`]", " ")).replaceAll("EOL","");
    }

    public static void main(String[] args) {
        //        return lemma.replaceAll("[ -=+_$#@!,.<>:;{}\\[\\]()؟’`]", "");
//        return lemma.replaceAll("[/[^\\w\\u0621-\\u064A\\s]/gi]", "");
        String lemma = "aklsd /, Ad.,wd /.";
        System.out.println();

    }
}