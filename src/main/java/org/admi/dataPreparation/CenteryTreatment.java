package org.admi.dataPreparation;

import java.util.Date;
public class CenteryTreatment {


    public CenteryTreatment() {}


    public void listTaffsirByCentery() throws InterruptedException {

        Thread thread1;
        Thread thread2;
        Thread thread3;
        Thread thread4;
        Thread thread5;
        Thread thread6;

        thread1 = new Thread(() -> {
            try {
                double start = new Date().getTime();
                Work work = new Work(1,4,2,3);
                double end= new Date().getTime();
                System.out.println("thread "+Thread.currentThread().getName()+" : the estimation time is : "+(end-start));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread1.setName("1-4");
        thread2 = new Thread(() -> {
            try {
                double start = new Date().getTime();
                Work work = new Work(5,10,4,4);
                double end= new Date().getTime();
                System.out.println("thread "+Thread.currentThread().getName()+" : the estimation time is : "+(end-start));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread2.setName("5-10");
        thread3 = new Thread(() -> {
            try {
                double start = new Date().getTime();
                Work work = new Work(11,20,4,7);
                double end= new Date().getTime();
                System.out.println("thread "+Thread.currentThread().getName()+" : the estimation time is : "+(end-start));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread3.setName("11-20");
        thread4 = new Thread(() -> {
            try {
                double start = new Date().getTime();
                Work work = new Work(21,76,8,13);
                double end= new Date().getTime();
                System.out.println("thread "+Thread.currentThread().getName()+" : the estimation time is : "+(end-start));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread4.setName("21-76");
        thread5 = new Thread(() -> {
            try {
                double start = new Date().getTime();
                Work work = new Work(77,95,14,14);
                double end= new Date().getTime();
                System.out.println("thread "+Thread.currentThread().getName()+" : the estimation time is : "+(end-start));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread5.setName("77-95");
        thread6 = new Thread(() -> {
            try {
                double start = new Date().getTime();
                Work work = new Work(96,114,15,15);
                double end= new Date().getTime();
                System.out.println("thread "+Thread.currentThread().getName()+" : the estimation time is : "+(end-start));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread6.setName("96-114");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();


    }

}
