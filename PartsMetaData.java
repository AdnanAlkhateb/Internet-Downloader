package DownloadProject;

import java.io.Serializable;

public class PartsMetaData implements Serializable
{
    private int      id ;
    private String   link ;
    private long     fileSize ;
    private String   fileName ;
    private String   filePath ;
    private long     startRange ;
    private long     endRange ;
    private long     completed = 0l ;
    private Status   status ;


    public PartsMetaData() {}

    public void setPartInfo (int id, long fileSize, long startRange, long endRange, String pathFile , String fileName ,String link)
    {
        this.id = id;
        this.fileSize = fileSize;
        this.startRange = startRange;
        this.endRange = endRange;
        this.filePath = pathFile;
        this.fileName = fileName;
        this.link = link;

    }

    public void addToStartRange(int num)
    {
        startRange += num ;
    }

    public void addToCompleted(int num)
    {
        completed += num ;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getCompleted() {
        return completed;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setStartRange(long startRange) {
        this.startRange = startRange;
    }

    public void setEndRange(long endRange) {
        this.endRange = endRange;
    }

    public int getId() {
        return id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getStartRange() {
        return startRange;
    }

    public long getEndRange() {
        return endRange;
    }

}

