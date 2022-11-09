package DownloadProject;

import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class HttpDownload implements Runnable,Serializable
{

    private MetaData fileInfo = null ;
    private String link ;
    private ArrayList <PartsDownload> partsOfDownload ;
    private ArrayList <Thread> threadOfParts ;
    private boolean limited = false ;
    private int valueOfLimit ;
    boolean RESUME = false;

    public HttpDownload () { }

    public HttpDownload (String link)
    {
        this.link=link;
        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpDownload (String link , String path)
    {
        this.link = link ;

        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileInfo.setUserPath(path);
    }

    public HttpDownload (String link , int parts)
    {
        this.link = link ;

        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!fileInfo.isRangeAllowed())
        {
            fileInfo.setNumOfParts(1);
            System.out.println("The Range download is not allowed");
        }

        else
            fileInfo.setNumOfParts(parts) ;

    }

    public HttpDownload (String link , int parts , String path)
    {
        this.link = link ;

        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileInfo.setUserPath(path) ;

        if (!fileInfo.isRangeAllowed())
        {
            fileInfo.setNumOfParts(1);
            System.out.println("The Range download is not allowed");
        }

        else
            fileInfo.setNumOfParts(parts) ;

    }

    public HttpDownload (int limitInKB , String link )
    {
        this.link=link;
        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
        limited = true ;
        valueOfLimit = limitInKB * 1024 ;
    }

    public HttpDownload (String link , String path , int limitInKB )
    {
        this.link = link ;

        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileInfo.setUserPath(path);

        limited = true ;
        valueOfLimit = limitInKB * 1024 ;
    }

    public HttpDownload (String link , int parts , String path , int limitInKB)
    {
        this.link = link ;

        fileInfo = new MetaData(link);
        try {
            fileInfo.loadFileMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!fileInfo.isRangeAllowed())
        {
            fileInfo.setNumOfParts(1);
            System.out.println("The Range download is not allowed");
        }

        else
            fileInfo.setNumOfParts(parts) ;

        fileInfo.setUserPath(path) ;

        limited = true ;
        valueOfLimit = limitInKB * 1024 ;
    }

    /// inner class to download parts od any download
    public class PartsDownload implements Runnable,Serializable
    {
        private PartsMetaData partFileInfo ;

        public PartsDownload ()
        {
            partFileInfo = new PartsMetaData();
        }

        @Override
        public void run()
        {
            try {
                StartDownloadPart();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        public synchronized void StartDownloadPart() throws IOException
        {
            //if (sizeOfFileInPath(partFileInfo.getFilePath()) > partFileInfo.getCompleted())
            //partFileInfo.setCompleted(sizeOfFileInPath(partFileInfo.getFilePath()));

            URL url=new URL (link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            String byteRange = partFileInfo.getStartRange() + "-" + partFileInfo.getEndRange();
            httpURLConnection.setRequestProperty("Range", "bytes=" + byteRange);
            System.out.println(" Bytes-Range= " + byteRange);

            httpURLConnection.connect();

            if ( (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    && (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL))
            {
                System.out.println("There is an Error occurred.");
                System.out.println(httpURLConnection.getResponseCode() + "  " + httpURLConnection.getResponseMessage());
                partFileInfo.setStatus(Status.ERROR);
                fileInfo.setStatus(Status.ERROR);
                System.exit(-1);
            }

            if (partFileInfo.getStatus() != Status.COMPLETED)
            {
                partFileInfo.setStatus(Status.DOWNLOADING);
                //partFileInfo.setFileSize(httpURLConnection.getContentLengthLong());
            }

            BufferedInputStream in  = new BufferedInputStream (httpURLConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream(partFileInfo.getFilePath(), true);
            byte[] buffer   =   new byte[4096];
            int read;

            ////////////////
            long startTimeDownload = System.currentTimeMillis();
            long startTimeSpeed = System.currentTimeMillis();
            int totalbyte = 0;
            fileInfo.setSpeedInByte(0);
            //int speedByte = 0;
            /////////////////

            while((partFileInfo.getStatus() == Status.DOWNLOADING ) && ( read = in.read(buffer,0,4096) ) != -1)
            {
                ///////////
                totalbyte += read;
                fileInfo.addToSpeed(read);
                //speedByte += read;


                if (System.currentTimeMillis() >= (startTimeSpeed + 1000))
                {
                    System.out.println(">>>>>>>>>>>>>>>>> "+ (fileInfo.getSpeedInByte() / 1024) + " KB / sec");
                    //speedByte = 0 ;
                    fileInfo.setSpeedInByte(0);

                    ///
                    //System.out.println((">>>>>>>>>>****>>>>>>> "+ fileInfo.getSpeedInByte() / 1024) + " KB / sec");


                    startTimeSpeed = System.currentTimeMillis();
                }

                if (limited)
                {
                    long sleepTime = (startTimeDownload + 1000) - System.currentTimeMillis();
                    if ( ( totalbyte >= (valueOfLimit * 1024) ) && ( sleepTime > 0 ) )
                    {
                        try
                        {
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        totalbyte = 0 ;
                    }
                    startTimeDownload = System.currentTimeMillis();
                }
                ////////////

                fos.write(buffer ,0 , read);
                this.partFileInfo.addToStartRange(read);
                //increase downloaded file
                this.partFileInfo.addToCompleted(read);
                fileInfo.addToCompleted(read);
                fileInfo.setDownloadPercent( (double) ( fileInfo.getCompleted() * 100 ) / fileInfo.getFileSize() );

                DecimalFormat formatter = new DecimalFormat("#0.00");
                System.out.println( formatter.format(fileInfo.getDownloadPercent()) + " % ");
                //System.out.println(" Downloaded " + download_percent + " of file size");

                /////// save data
                DownloadData ob = new DownloadData();
                ob.setAll(fileInfo,link,partsOfDownload,limited,valueOfLimit);
                saveDownlaodsData(ob);
                /////////

            }

            if( (partFileInfo.getCompleted() == partFileInfo.getFileSize()) && (partFileInfo.getStatus() == Status.DOWNLOADING) )
            {
                partFileInfo.setStatus(Status.COMPLETED);
                //System.out.println(" The part " + partFileInfo.getId() + " is [COMPLETE] ");

                /////// save data after completed this part
                DownloadData ob = new DownloadData();
                ob.setAll(fileInfo,link,partsOfDownload,limited,valueOfLimit);
                saveDownlaodsData(ob);
                System.out.println("Save complete part");
                ///////

                /// check to rebuild the file
                checkToRebuild();

            }

        }

        public PartsMetaData getPartFileInfo ()
        {
            return partFileInfo;
        }

        public void setPartFileInfo(PartsMetaData partFileInfo) {
            this.partFileInfo = partFileInfo;
        }
    }
    ///

    @Override
    public void run()
    {
        try
        {
            startDownload ();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startDownload () throws IOException
    {
        PartsDownload obj = null;
        partsOfDownload = new ArrayList<>();
        threadOfParts = new ArrayList<>();

        long sizeOfpart = fileInfo.getFileSize() / fileInfo.getNumOfParts();
        long startRng, endRng;

        for (int i = 0; i < fileInfo.getNumOfParts(); i++)
        {
            obj = new PartsDownload();
            startRng = sizeOfpart * i;
            endRng = startRng + sizeOfpart - 1;

            String nameOfPart = fileInfo.getFileName() + i;
            String tempPathOfParts = fileInfo.getTempPath() + "\\" + nameOfPart;
            int idOfPart = i;

            obj.getPartFileInfo().setPartInfo(idOfPart, sizeOfpart, startRng, endRng, tempPathOfParts, nameOfPart, link);

            partsOfDownload.add(obj);
        }

        Resume();
        // create the temp directory for the file is downloading
        new File(fileInfo.getTempPath()).mkdirs();

        if (!RESUME)
        {
            // create file to save data
            new File(fileInfo.getPathOfDataFile() + "\\" + fileInfo.getFileName() + fileInfo.getId() + ".DataID").createNewFile();
        }

        // set time of the start download
        Date date = new Date();
        fileInfo.setStartTime(date.getTime());


        fileInfo.setStatus(Status.DOWNLOADING);
        for (int i=0 ; i < fileInfo.getNumOfParts() ; i++ )
        {
            Thread thread=new Thread(partsOfDownload.get(i));
            threadOfParts.add(thread);
            threadOfParts.get(i).start();
        }
    }

    public void Resume()
    {
        File file = new File(fileInfo.getPathOfDataFile());

        if (file.exists())
        {
            DownloadData downloadData = searchInData();

            if (downloadData != null && downloadData.checkAllRightToResume())
            {
                System.out.println("resume");
                RESUME = true ;
                setFromAnother(downloadData);
            }

            if (RESUME)
            {
                for (int i=0;i<partsOfDownload.size();i++)
                {
                    if (partsOfDownload.get(i).getPartFileInfo().getStatus().equals(Status.PAUSE))
                        partsOfDownload.get(i).getPartFileInfo().setStatus(Status.DOWNLOADING);
                }
                fileInfo.setStatus(Status.DOWNLOADING);
            }

        }

    }

    public void checkToRebuild () throws IOException
    {
        /// check to rebuild the file
        if (fileInfo.getStatus() != Status.COMPLETED)
        {
            int rebuild = 0;
            for (int i=0; i<fileInfo.getNumOfParts() ;i++)
            {
                if (partsOfDownload.get(i).getPartFileInfo().getStatus() == Status.COMPLETED)
                {
                    rebuild ++;
                    //System.out.println("******++++********** "+rebuild);
                }
            }
            if (rebuild == fileInfo.getNumOfParts())
            {
                Rebuild();
                fileInfo.setStatus(Status.COMPLETED);
                System.out.println(" The download is completed ");

                // save data after completed the all parts
                DownloadData ob1 = new DownloadData();
                ob1.setAll(fileInfo,link,partsOfDownload,limited,valueOfLimit);
                saveDownlaodsData(ob1);
                //
            }
            ///
        }
    }

    public void Rebuild () throws IOException
    {
        fileInfo.setStatus(Status.REBUILDING);

        int bytesRead;
        byte[] buffer = new byte [4096] ;

        for (int i=0 ; i < fileInfo.getNumOfParts() ; i++)
        {
            FileInputStream file_input = new FileInputStream(fileInfo.getTempPath() + "\\" + fileInfo.getFileName() + i);
            FileOutputStream file_output = new FileOutputStream(fileInfo.getUserPath(),true);
            while ( ( bytesRead = file_input.read(buffer,0,4096) ) != -1)
            {
                file_output.write(buffer ,0 , bytesRead);
            }

            if ( i == (fileInfo.getNumOfParts() - 1) )
            {
                file_input.close();
                file_output.close();
            }
        }

        // set time of the end download
        Date date = new Date();
        fileInfo.setEndTime(date.getTime());

    }

    // all functions for save downloads data
    public void saveDownlaodsData(DownloadData object)
    {
        try
        {
            FileOutputStream file_output = new FileOutputStream(fileInfo.getPathOfDataFile() + "\\" + fileInfo.getFileName() + fileInfo.getId() + ".DataID");
            ObjectOutputStream object_output = new ObjectOutputStream(file_output);
            object_output.writeObject(object);

            file_output.close();
            object_output.close();
            System.out.println("----> Saved");
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public DownloadData searchInData ()
    {
        DownloadData downloadData ;
        File file1=new File(fileInfo.getPathOfDataFile());
        File[] allContents = file1.listFiles();
        if (allContents != null)
        {
            for (File file : allContents)
            {
                downloadData = loadObject(file.getAbsolutePath());

                if (downloadData != null)
                {
                    if (downloadData.getInfo().getId() == fileInfo.getId())
                    {
                        return downloadData;
                    }
                }
            }
        }
        return null;
    }

    public DownloadData loadObject(String path)
    {
        DownloadData ob = null;
        try
        {
            FileInputStream file_input = new FileInputStream(path);
            ObjectInputStream object_input = new ObjectInputStream(file_input);
            ob = (DownloadData) object_input.readObject();

            file_input.close();
            object_input.close();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
        return ob;
    }
    // end of functions

    public boolean isLimited() {
        return limited;
    }

    public void setLimited(boolean limited) {
        this.limited = limited;
    }

    public int getValueOfLimit() {
        return valueOfLimit;
    }

    public void setValueOfLimit(int valueOfLimit) {
        this.valueOfLimit = valueOfLimit;
    }

    public String getLink() {
        return link;
    }

    public void setFileInfo(MetaData fileInfo) {
        this.fileInfo = fileInfo;
    }

    public ArrayList<PartsDownload> getPartsOfDownload() {
        return partsOfDownload;
    }

    public MetaData getFileInfo() {
        return fileInfo;
    }

    public ArrayList<Thread> getThreadOfParts() {
        return threadOfParts;
    }

    public boolean deleteDirectory(File directoryToDeleted)
    {
        System.out.println("hellooooooo");

        File[] allContents = directoryToDeleted.listFiles();
        if (allContents != null)
        {
            for (File file : allContents)
            {
                deleteDirectory(file);
            }
        }
        return directoryToDeleted.delete();
    }

    public void deleteDirectoryStream(Path path) throws IOException
    {
        System.out.println("hello");

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        System.out.println("goodbye");

        //Path path= Paths.get(fileInfo.getTempPath());
        //deleteDirectoryStream(path);

    }

    public void setFromAnother(DownloadData downloadData)
    {
        link=downloadData.getUrl();
        fileInfo=downloadData.getInfo();

        for (int i=0;i<downloadData.getPartsMeta().size();i++)
        {
            partsOfDownload.get(i).setPartFileInfo(downloadData.getPartsMeta().get(i));
        }

        limited=downloadData.isLimit();
        valueOfLimit=downloadData.getValueLimit();
    }

    public void setPartsOfDownload(ArrayList<PartsDownload> partsOfDownload) {
        this.partsOfDownload = partsOfDownload;
    }

}
