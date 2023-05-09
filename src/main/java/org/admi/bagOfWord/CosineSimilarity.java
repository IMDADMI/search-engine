package org.admi.bagOfWord;

public class CosineSimilarity {
    public double calculateSimilarity(double[][] tafassirs, int numberOfRow, int numberOfColumn){
        normalizeMatrix(tafassirs,numberOfRow,numberOfColumn);
        double taffsir1[] = tafassirs[0];
        double taffsir2[] = tafassirs[1];

        double multiProduct = 0.0;
        double taffsirNorm1 = 0.0;
        double taffsirNorm2 = 0.0;
        for (int i = 0; i < taffsir1.length; i++) {
            multiProduct += taffsir1[i] * taffsir2[i];
            taffsirNorm1 += Math.pow(taffsir1[i], 2);
            taffsirNorm2 += Math.pow(taffsir2[i], 2);
        }
            return multiProduct / (Math.sqrt(taffsirNorm1) * Math.sqrt(taffsirNorm2));
    }
    public void normalizeMatrix(double [][] matrix, int n, int m){
        for(int i=0;i<n;i++)
            for(int j=0;j<m;j++){
                matrix[i][j]*=100;
                matrix[i][j]++;

            }
    }
}
