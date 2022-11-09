package DownloadProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TableView_Data {

    private String name_files    ;
    private String status_file   ;
    private String size_file     ;
    private String timeleft_file ;
    private String speedrate_file;

    public ObservableList<TableView_Data> MAKE_ELEMENT_TABLEVIEW()
    {
        ObservableList<TableView_Data> tableViews= FXCollections.observableArrayList();
        //HERE WE SHOW FILES AND ADD IT TO TABLE VIEW
        //tableViews.add();

        return tableViews;

    }
}
