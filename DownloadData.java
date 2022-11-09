package DownloadProject;

import java.io.Serializable;
import java.util.ArrayList;

public class DownloadData implements Serializable
{
    private MetaData Info = null ;
    private String url ;
    private ArrayList<PartsMetaData> partsMeta = new ArrayList<>();
    private boolean limit = false ;
    private int valueLimit ;

    public void setAll(MetaData fileInfo,String link,ArrayList<HttpDownload.PartsDownload> partsOfDownload,
                       boolean limited,int valueOfLimit)
    {
        this.Info = fileInfo;
        this.url = link;

        for (int i=0;i<partsOfDownload.size();i++)
        {
            partsMeta.add(partsOfDownload.get(i).getPartFileInfo());
        }

        this.limit = limited;
        this.valueLimit = valueOfLimit;
    }

    public void setInfo(MetaData info) {
        Info = info;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

    public void setValueLimit(int valueLimit) {
        this.valueLimit = valueLimit;
    }

    public MetaData getInfo() {
        return Info;
    }

    public String getUrl() {
        return url;
    }

    public boolean isLimit() {
        return limit;
    }

    public int getValueLimit() {
        return valueLimit;
    }

    public boolean checkAllRightToResume()
    {
        if (Info != null && partsMeta != null && partsMeta.size() > 0)
        {
            System.out.println("All Right");
            return  true;
        }

        return false;
    }

    public ArrayList<PartsMetaData> getPartsMeta() {
        return partsMeta;
    }

    public void setPartsMeta(ArrayList<PartsMetaData> partsMeta) {
        this.partsMeta = partsMeta;
    }

}
