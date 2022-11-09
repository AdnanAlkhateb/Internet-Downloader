package DownloadProject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class GUI extends Application
{
    private Stage window;
    private Scene primaryScene;
    private ArrayList<HttpDownload> httpDownloas = new ArrayList<>();
    private ArrayList<FtpDownload> ftpDownloas = new ArrayList<>();

    @Override
    public void start(Stage primaryStage)
    {

        window = primaryStage;
        window.setTitle("Internet Downloader");
        window.setScene(tableView());
        window.show();

    }

    public Scene tableView(){
        Scene scene;
        TableView<TableView_Data> table;
        TableView_Data data_table=new TableView_Data();


        //name column
        TableColumn<TableView_Data,String> name_column_table=new TableColumn<>("NAME");
        name_column_table.setMinWidth(150);
        name_column_table.setCellValueFactory(new PropertyValueFactory<>("name_files"));
        //status column
        TableColumn<TableView_Data,String> status_column_table=new TableColumn<>("STATUS");
        name_column_table.setMinWidth(50);
        name_column_table.setCellValueFactory(new PropertyValueFactory<>("status_file"));
        //SIZE_FILE column
        TableColumn<TableView_Data,Float> size_column_table=new TableColumn<>("SIZE FILE");
        name_column_table.setMinWidth(50);
        name_column_table.setCellValueFactory(new PropertyValueFactory<>("size_file"));
        //time left column
        TableColumn<TableView_Data,String> time_column_table=new TableColumn<>("TIME LEFT");
        name_column_table.setMinWidth(50);
        name_column_table.setCellValueFactory(new PropertyValueFactory<>("timeleft_file"));
        //speed rate column
        TableColumn<TableView_Data,Float> speed_column_table=new TableColumn<>("RATE");
        name_column_table.setMinWidth(50);
        name_column_table.setCellValueFactory(new PropertyValueFactory<>("speedrate_file"));
        //make table
        table=new TableView<>();
        table.setMaxWidth(900);
        table.setMaxHeight(600);
        table.setItems(data_table.MAKE_ELEMENT_TABLEVIEW());
        table.getColumns().addAll( name_column_table,status_column_table,size_column_table
                ,time_column_table,speed_column_table);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(barButton());
        borderPane.setCenter(table);

        scene=new Scene(borderPane,900,450);

        String css = this.getClass().getResource("Css.css").toExternalForm();
        scene.getStylesheets().add(css);

        return scene;


    }

    public HBox barButton()
    {
        Stage stage =   new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);  //Now we remove the minimize and maximize Buttons for the window
        stage.setResizable(false);  //Here the window has single size
        HBox hBox = new HBox();

        hBox.setSpacing(10);
        hBox.setPadding(new Insets(3,2, 3,1));
        Button addURLButton   =   new Button("Add URL");
        Button startButton    =   new Button("Start ");
        Button resumeButton   =   new Button("Resume");
        Button stopButton     =   new Button("Stop");
        Button stopAllButton  =   new Button("Stop All");
        addURLButton.setMinSize(30,40);
        startButton.setMinSize(30,40);
        resumeButton.setMinSize(30,40);
        stopButton.setMinSize(30,40);
        stopAllButton.setMinSize(30,40);


        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        addURLButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        addURLButton.setEffect(shadow);
                    }
                });

        //Removing the shadow when the mouse cursor is off
        addURLButton.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        addURLButton.setEffect(null);
                    }
                });

        // Add all buttons to hbox
        hBox.getChildren().addAll(addURLButton,startButton,resumeButton,stopButton,stopAllButton);

        addURLButton.setOnAction(e->{
            stage.setScene(addUrl(stage));
            stage.setTitle("Enter new address to download");
            stage.show();
        });

        return hBox;
    }

    public Scene addUrl(Stage stage)
    {
        Scene scene;
        Stage stage1 = new Stage();
        stage1.resizableProperty().setValue(Boolean.FALSE);
        BorderPane borderPane = new BorderPane();

        HBox hBox   =   new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(0,0,10,325));

        Label url=new Label("URL: ");
        TextField urlField = new TextField();
        urlField.setMinWidth(300);

        Label path=new Label("Path: ");
        TextField pathField = new TextField();
        pathField.setMinWidth(300);

        Button browseButton = new Button("Browse");
        browseButton.setMinWidth(60);

        Button statrtDownloadButton = new Button("OK");
        statrtDownloadButton.setMinWidth(60);
        hBox.getChildren().add(statrtDownloadButton);

        Button cancelDownloadButton = new Button("Cancel");
        cancelDownloadButton.setMinWidth(60);
        hBox.getChildren().add(cancelDownloadButton);

        //create Grid pane
        GridPane gridPane=new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        GridPane.setConstraints(url,0,0);
        GridPane.setConstraints(urlField,1,0);
        GridPane.setConstraints(path,0,1);
        GridPane.setConstraints(pathField,1,1);
        GridPane.setConstraints(browseButton,2,1);
        gridPane.getChildren().addAll(url,urlField,path,pathField,browseButton);
        gridPane.setAlignment(Pos.CENTER);

        borderPane.setCenter(gridPane);
        borderPane.setBottom(hBox);

        browseButton.setOnAction(event -> {

            FileChooser browseFile = new FileChooser();
            browseFile.setTitle("Path Of Save");

            String userName = System.getenv("USERNAME");
            File f = new File("C:\\Users\\"+userName +"\\Downloads\\[InternetDownloader] Downloads");
            f.mkdirs();
            String defaultPath = f.getAbsolutePath();

            browseFile.setInitialFileName("ID");

            File file  =  browseFile.showSaveDialog(stage);

            if (file == null)
                pathField.setText(defaultPath);
            else
            {
                int last = file.getAbsolutePath().lastIndexOf("\\");
                String finalPath = file.getAbsolutePath().substring(0,last);
                pathField.setText(finalPath);
            }

        });

        statrtDownloadButton.setOnAction(e->{
            try
            {
                String link = urlField.getText();
                URL testUrl=new URL(link);

                if (testUrl.getProtocol().equals("ftp") || testUrl.getProtocol().equals("ftps"))
                { }

                else if (testUrl.getProtocol().equals("http") || testUrl.getProtocol().equals("https"))
                {
                    HttpDownload downlaodObject = new HttpDownload(link);
                    httpDownloas.add(downlaodObject);
                    Thread thread = new Thread(downlaodObject);
                    thread.start();

                    if (!pathField.getText().isEmpty())
                    {
                        String userPath = pathField.getText() + "\\" +downlaodObject.getFileInfo().getFileName()
                                + "." + downlaodObject.getFileInfo().getFileType();
                        downlaodObject.getFileInfo().setUserPath(userPath);
                    }

                    DownloadingScreen downloadingScreen =new DownloadingScreen();
                    stage1.setScene(downloadingScreen.LoadHttpDownloadInfo(stage1,downlaodObject));
                    stage1.show();
                }

            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        cancelDownloadButton.setOnAction(event ->{
            stage.close();
        });

        scene=new Scene(borderPane,500,150);

        String css = this.getClass().getResource("Css.css").toExternalForm();
        scene.getStylesheets().add(css);

        return scene;
    }

}
