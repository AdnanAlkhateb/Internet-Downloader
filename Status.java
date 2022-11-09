package DownloadProject;

import java.io.Serializable;

public enum Status implements Serializable
{
    DOWNLOADING,
    ERROR,
    COMPLETED,
    PAUSE,
    REBUILDING
}
