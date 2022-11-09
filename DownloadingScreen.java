package DownloadProject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;

public class DownloadingScreen
{
    private HttpDownload download ;

    public class BAR_DOWNLOADING_CONTROLLER implements Runnable
    {

        private ProgressBar bar=new ProgressBar();
        private TextArea text=new TextArea();
        private HttpDownload http=new HttpDownload();

        public BAR_DOWNLOADING_CONTROLLER(ProgressBar bar, TextArea text, HttpDownload http){

            this.bar=bar;
            this.text=text;
            this.http=http;

        }

        @Override
        public void run() {
            http.getFileInfo().setSpeedInByte(1);
            while (http.getFileInfo().getFileSize()!=http.getFileInfo().getCompleted())
            {
                new Thread(new Runnable() {
                    @Override public void run() {
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                try{  text.setText("FILE NAME:" + " " + http.getFileInfo().getFileName() + "\n" +
                                        "FILES SIZE : " + String.format("%.2f",http.getFileInfo().getFileSize()/1048576.0 ) + " MB\n" +
                                        "DOWNLOADED : " + String.format("%.2f", http.getFileInfo().getCompleted()/1048576.0) + " MB\n" +
                                        "SPEED RATE : " +  http.getFileInfo().getSpeedInByte()/1024 + " KB \n" +
                                        "TIME LEFT : "+ " M\n"+
                                        "PRECENT : % "+String.format("%.2f",http.getFileInfo().getDownloadPercent()));
                                }catch (Exception  ArithmeticException){
                                    http.getFileInfo().setSpeedInByte(30);
                                }
                                bar.setProgress(((http.getFileInfo().getCompleted()*100.0)/http.getFileInfo().getFileSize())/100.0 );
                            }
                        });
                    }
                }).start();
            }
        }
    }

    public Scene LoadHttpDownloadInfo (Stage stage,HttpDownload httpDownload)
    {
        download=httpDownload;

        ProgressBar progressBar =   new ProgressBar();
        progressBar.setMaxWidth(500);
        progressBar.setMinHeight(21);
        //Here put the downloaded percent

        TabPane tabPane =   new TabPane();
        tabPane.setMinWidth(400);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //Download status tab, it content INFO and progress bar
        Tab downloadStatusTab    =   new Tab("Download status ");
        tabPane.getTabs().add(downloadStatusTab);

        Tab speedLimiterAndOption    =   new Tab("Speed limiter & Options");
        //we should add some check box and .....for speed limiter
        tabPane.getTabs().add(speedLimiterAndOption);

        //INFO TABLE OF downloading file
        TextArea INFO = new TextArea();
        INFO.setText("URL:\n" +
                "FILES SIZE:\n"+
                "DOWNLOADED:\n"+
                "SPEED RATE:\n"+
                "TIME LEFT: \n");
        INFO.setEditable(false);
        INFO.setMaxWidth(600);
        INFO.setMaxHeight(110);
        downloadStatusTab.setContent(INFO);
        //BAR

        HBox hBox   =   new HBox();
        hBox.setPadding(new Insets(0,0,0,288));
        hBox.setSpacing(30);
        //Start Button
        Button startAndPauseButton =   new Button("Pause");
        startAndPauseButton.setMinWidth(80);


        //Cancel Button
        Button cancelButton =   new Button("Cancel");
        cancelButton.setMinWidth(80);
        cancelButton.setOnAction(e->{
            //first you have to put the download status pause then close
            stage.close();
            Pause(download);
        });

        stage.setOnCloseRequest(event -> {
            stage.close();
            Pause(download);
        });

        hBox.getChildren().addAll(startAndPauseButton,cancelButton);

        //create Grid pane
        GridPane gridPane=new GridPane();
        gridPane.setVgap(8);
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.TOP_CENTER);
        GridPane.setConstraints(progressBar,0,1);
        GridPane.setConstraints(hBox,0,4);
        gridPane.getChildren().addAll(tabPane,progressBar,hBox);

        //Speed limiter and more option Tab
        Label   transferRate    =   new Label("Transfer rate:");
        Label   speed   =   new Label("200");
        Label kb    =   new Label("KB/sec");

        CheckBox useSpeedLimiter    =   new CheckBox("Use speed Limiter");
        Label   maxspeed    =   new Label("Maximum download speed:");
        TextField   speedresField   =   new TextField("10");
        speedresField.setMaxWidth(40);
        speedresField.setDisable(true);

        useSpeedLimiter.setOnAction(e->{
            if(!useSpeedLimiter.isSelected())
            {
                speedresField.setDisable(true);
                download.setLimited(false);
            }
            else
                {
                    speedresField.setDisable(false);
                    String s = speedresField.getText();
                    if (!s.isEmpty())
                    {
                        download.setValueOfLimit(Integer.parseInt(s));
                        download.setLimited(true);
                    }
                }
        });

        HBox    transHBox    =   new HBox();
        transHBox.setSpacing(18);
        transHBox.getChildren().addAll(transferRate,speed,kb);

        VBox vBox    =   new VBox();
        vBox.setPadding(new Insets(10,0,0,10));
        vBox.getChildren().addAll(transHBox,useSpeedLimiter,maxspeed,speedresField);
        vBox.setSpacing(10);
        speedLimiterAndOption.setContent(vBox);

        Scene scene;
        scene=new Scene(gridPane,500,400);

        //setInfo(progressBar,INFO,httpDownload);
        final BAR_DOWNLOADING_CONTROLLER[] bar_downloading_controller = {new BAR_DOWNLOADING_CONTROLLER(progressBar, INFO, download)};
        final Thread[] cThread = {new Thread(bar_downloading_controller[0])};
        cThread[0].start();

        startAndPauseButton.setOnAction(event -> {
            if (startAndPauseButton.getText().equals("Pause"))
            {
                Pause(download);
                startAndPauseButton.setText("Resume");
            }

            else if (startAndPauseButton.getText().equals("Resume"))
            {
                download = Resume(download);
                startAndPauseButton.setText("Pause");

                cThread[0].stop();

                bar_downloading_controller[0] =new BAR_DOWNLOADING_CONTROLLER(progressBar,INFO,download);
                cThread[0] = new Thread(bar_downloading_controller[0]);
                cThread[0].start();
            }

        });

        String css = this.getClass().getResource("Css.css").toExternalForm();
        scene.getStylesheets().add(css);

        return scene;
    }

    public Scene LoadFtpDownloadInfo (Stage stage,FtpDownload ftpDownload)
    {
        return null;
    }

    public HttpDownload Resume(HttpDownload old)
    {
        HttpDownload httpDownload=new HttpDownload();
        httpDownload.setFileInfo(old.getFileInfo());

        old=null;

        Thread thread=new Thread(httpDownload);
        thread.start();

        return httpDownload;
    }

    public void Pause(HttpDownload httpDownload)
    {
        for (int i=0;i<httpDownload.getPartsOfDownload().size();i++)
        {
            if (httpDownload.getPartsOfDownload().get(i).getPartFileInfo().getStatus() != Status.COMPLETED)
                httpDownload.getPartsOfDownload().get(i).getPartFileInfo().setStatus(Status.PAUSE);
        }
        if (! httpDownload.getFileInfo().getStatus().equals(Status.COMPLETED))
        httpDownload.getFileInfo().setStatus(Status.PAUSE);

        // save data after completed the all parts
        DownloadData ob1 = new DownloadData();
        ob1.setAll(httpDownload.getFileInfo(),httpDownload.getLink(),httpDownload.getPartsOfDownload(),
                httpDownload.isLimited(),httpDownload.getValueOfLimit());
        httpDownload.saveDownlaodsData(ob1);
        //
    }

}
