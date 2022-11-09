package DownloadProject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class MetaData implements Serializable
{
    private long      id ;
    private String 	  url ;
    private String    protocol ;
    private String	  filePath ;
    private String 	  fileName ;
    private String 	  fileType ;
    private long 	  fileSize ;
    private String    userPath ;
    private String    tempPath ;
    private String    pathOfDataFile ;
    private Status    status ;
    private long      completed = 0l ;
    private boolean   rangeAllowed ;
    private long      startrange = 0l ;
    private long      endrange = 0l ;
    private Date	  startTime = new Date();
    private Date	  endTime = new Date();
    private long      speedInByte ;

    private int       numOfParts = 1 ;
    private double downloadPercent ;


    public MetaData(String link)
    {
        url = link ;
    }

    public MetaData(String link , int numParts)
    {
        url = link ;
        numOfParts = numParts ;
    }

    public void loadFileMetadata() throws IOException
    {
        URL link = new URL(url);

        if ( link.getProtocol().equals("http") || link.getProtocol().equals("https"))
        {
            // to open connect with the server
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            // to be sure if you connect to url
            final int responseCode = conn.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK )
            {
                System.out.println("[SUCCESS] Connected to server. Gathering file info. ");

                // get file size from url connection
                fileSize = conn.getContentLengthLong();

                // generate id to this file
                id  = new Date().getTime();

                // get protocol from url
                protocol = link.getProtocol();
                System.out.println("---------------------------- " + protocol);

                // get the file type from url connection
                String ft = conn.getContentType();
                ft = ft.substring(ft.lastIndexOf("/") + 1);
                fileType = ft ;
                System.out.println("----------------------------- " + fileType);

                // get the file name from path after convert the url to uri
                URI uri = null;
                try
                {
                    uri = link.toURI();
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
                String fn = new File(uri.getPath()).getName();
                if (fn.contains("."))
                {
                    fn = fn.substring( 0 , fn.lastIndexOf("."));
                }
                fileName = fn ;
                System.out.println("----------------------------- " + fileName);

                // create the default directory for downloader
                String userName = System.getenv("USERNAME");
                String UP = "C:\\Users\\"+ userName +"\\Downloads\\[InternetDownloader] Downloads" ;
                new File(UP).mkdirs();
                userPath = UP + "\\" + fileName + "." + fileType ;

                // create the temp directory for the file is downloading
                tempPath = "C:\\Users\\" + userName + "\\AppData\\Roaming\\InternetDownloader\\DownloadsTempFile\\" + fileName + id;
               //**** new File(tempPath).mkdirs();

                // create the downloads data directory
                String pat ="C:\\Users\\" + userName + "\\AppData\\Roaming\\InternetDownloader\\DownloadsData";
                new File(pat).mkdirs();
                File file = new File(pat + "\\" + fileName + id + ".DataID");
                // File file = new File(pat + \\Data.ID");

                //if (!file.exists())
                    //file.createNewFile();

                pathOfDataFile = pat;
                //pathOfDataFile = file.getPath();

                // check if we can download the file by ranges
                String range = conn.getHeaderField("Accept-Ranges");

                if(range == null || range.isEmpty()) {
                    rangeAllowed = false;
                } else {
                    rangeAllowed = true;
                }

            }

            else
            {
                System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());
                System.exit(-1);
            }

        }

        else if ( link.getProtocol().equals("ftp") || link.getProtocol().equals("ftps"))
        {

        }


    }

    public void addToStartRange (int num)
    {
        startrange += num ;
    }


    public long getSpeedInByte() {
        return speedInByte;
    }

    public void setSpeedInByte(long speedInByte) {
        this.speedInByte = speedInByte;
    }

    public void addToSpeed (int num)
    {
        speedInByte+=num;
    }

    public void setStartrange(long startrange) {
        this.startrange = startrange;
    }

    public void setEndrange(long endrange) {
        this.endrange = endrange;
    }

    public long getStartrange() {
        return startrange;
    }

    public long getEndrange() {
        return endrange;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public boolean isRangeAllowed() {
        return rangeAllowed;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime.setTime(startTime);
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime.setTime(endTime);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Status getStatus() {
        return status;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUserPath() {
        return userPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setStatus(Status st) {
        status = st;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public void addToCompleted(int num)
    {
        completed += num ;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public int getNumOfParts() {
        return numOfParts;
    }

    public double getDownloadPercent() {
        return downloadPercent;
    }

    public void setNumOfParts(int numOfParts) {
        this.numOfParts = numOfParts;
    }

    public void setDownloadPercent(double downloadPercent) {
        this.downloadPercent = downloadPercent;
    }

    public void calculatePercent()
    {
        downloadPercent = (double) ( completed * 100 ) / fileSize ;
    }

    public String getPathOfDataFile() {
        return pathOfDataFile;
    }

    public void setPathOfDataFile(String pathOfDataFile) {
        this.pathOfDataFile = pathOfDataFile;
    }

    public void setRangeAllowed(boolean rangeAllowed) {
        this.rangeAllowed = rangeAllowed;
    }

}
