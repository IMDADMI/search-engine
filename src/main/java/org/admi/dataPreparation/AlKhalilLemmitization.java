package org.admi.dataPreparation;

import net.oujda_nlp_team.ADATAnalyzer;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlKhalilLemmitization {
    public  void Lemmatizer(String path,int startOfCentury,int endOfCentury) {
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory()){
            File files[] = dir.listFiles();
            int filesLength = files.length;
            //calculer la periode de procession
            double start = new Date().getTime();

            for(int i=0;i<filesLength;i++){
                String fileName = files[i].getName();
                int numericalValue = Integer.parseInt(fileName.substring(fileName.lastIndexOf('_')+1,fileName.indexOf('.')));
                if(!(numericalValue >= startOfCentury && numericalValue <= endOfCentury))
                    continue;

                String newFile = files[i].getName().replace(".txt",".xml");
                if(numericalValue == 14)
                    splitFile(newFile,fileName);
                else
                    startLemmitization(newFile,fileName);

            }
            double end = new Date().getTime();
            System.out.println(Thread.currentThread().getName()+" - al-khalil processing time is : "+(end-start));
        }
    }

    private void splitFile(String newFile,String fileName) {
        System.out.println("spliting the file ...");
        String file1 = "_1"+fileName;
        String file2 = "_2"+fileName;
        String file3 = "_3"+fileName;


        int numberOfLine = 0;
        String line;
        Map<Integer,String> lines = new HashMap<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("src\\dataset\\"+fileName))){
            while((line = bufferedReader.readLine())!=null){
                lines.put(numberOfLine,line);
                numberOfLine++;
            }
            System.out.println("file read successfully ..");
            System.out.println("number of line is : "+(numberOfLine-1));
            writeIntoSubFiles(lines,numberOfLine,newFile,file1,file2,file3);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void writeIntoSubFiles(Map<Integer, String> lines,int numberOfLine,String xmlFile, String file1,String file2,String file3) throws InterruptedException {

        System.out.println("start writing into the sub files ...");
        Thread t1 = new Thread(()->{
            try(BufferedWriter subFile1 = new BufferedWriter(new FileWriter("src\\dataset\\"+file1)))
            {
                for(int i = 0; i < numberOfLine / 3; i++)
                    subFile1.append(lines.get(i)+"\n");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t1.setName("sub file 1 thread");
        Thread t2 = new Thread(()-> {

            try (BufferedWriter subFile2 = new BufferedWriter(new FileWriter("src\\dataset\\"+file2))) {
                for (int i = numberOfLine / 3; i < (numberOfLine*2)/3; i++)
                    subFile2.append(lines.get(i));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t2.setName("sub file 2 thread");
        Thread t3 = new Thread(()-> {

            try (BufferedWriter subFile3 = new BufferedWriter(new FileWriter("src\\dataset\\"+file3))) {
                for (int i = (numberOfLine*2)/3; i < numberOfLine; i++)
                    subFile3.append(lines.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t3.setName("sub file 3 thread");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        String newFile1 = "_1"+xmlFile;
        String newFile2 = "_2"+xmlFile;
        String newFile3 = "_3"+xmlFile;
        startLemmitization(newFile1,file1);
        startLemmitization(newFile2,file2);
        startLemmitization(newFile3,file3);




    }

    public void startLemmitization(String newFile,String fileName){
        synchronized (ADATAnalyzer.class) {
            System.out.println("Thread : "+Thread.currentThread().getName()+" start lemmitizing ...");
            ADATAnalyzer
                    .getInstance().processLemmatization(
                            "src\\dataset\\"+fileName,
                            "utf-8",
                            "src\\lemme\\"+newFile,
                            "utf-8");
        }
        System.out.println("done");
    }
}
