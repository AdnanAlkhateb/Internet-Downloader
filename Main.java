package DownloadProject;


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class Main
{
    public static void main (String[] args) {

        // PDF
        HttpDownload httpDownload=new HttpDownload("http://www.r-5.org/files/books/computers/algo-list/realtime-3d/Christer_Ericson-Real-Time_Collision_Detection-EN.pdf",8);

        // Image
        //HttpDownload httpDownload=new HttpDownload("https://cdn.shopify.com/s/files/1/0747/3829/products/mQ0326_1024x1024.jpeg?v=1485014085");

        Thread thread = new Thread(httpDownload);
        thread.start();

    }

}
