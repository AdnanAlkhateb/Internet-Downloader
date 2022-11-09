package DownloadProject;

import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.net.URL ;
import java.net.URLConnection ;
import java.io.File ;

class FtpDownload
{
    private static final int BUFFER_SIZE = 4096 ;

    public void download()
    {    //this is a function
        long startTime = System.currentTimeMillis() ;
        String ftpUrl = "ftp://test.rebex.net/"; //**username**:**password**@filePath ;
        String file= "readme.txt" ; // name of the file which has to be download
        String host =  "test.rebex.net"; //ftp server
        String user = "demo" ; //user name of the ftp server
        String pass = "password" ; // password of the ftp server

        String savePath = "D:\\" + file ;
        ftpUrl = String.format(ftpUrl, user, pass, host) ;
        System.out.println("Connecting to FTP server") ;

        try{
            URL url = new URL("") ;
            URLConnection conn = url.openConnection() ;
            InputStream inputStream = conn.getInputStream() ;
            long filesize = conn.getContentLength() ;
            System.out.println("Size of the file to download in kb is:-" + filesize/1024 ) ;

            FileOutputStream outputStream = new FileOutputStream(savePath) ;

            byte[] buffer = new byte[BUFFER_SIZE] ;
            int bytesRead = -1 ;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead) ;
            }
            long endTime = System.currentTimeMillis() ;
            System.out.println("File downloaded") ;
            System.out.println("Download time in sec. is:-" + (endTime-startTime)/1000)  ;
            outputStream.close() ;
            inputStream.close() ;
        }
        catch (IOException ex){
            ex.printStackTrace() ;
        }
    }

}
