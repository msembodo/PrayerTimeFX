package net.msembodo.prayertimefx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.msembodo.jprayertime.HourMinute;
import net.msembodo.jprayertime.PrayerTimes;
import org.json.JSONException;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Main window.
 */
public class MainWindow extends Application implements HourMinute {
    private MigPane root;
    private TableView tableView;
    private Label locationLbl;
    private Label tzLbl;
    private Label timeLbl;
    private ObservableList<ObservableList> data;
    private List<String[]> ptData;

    @Override
    public void start(Stage stage) {
        root = new MigPane("insets 4", "[grow]", "[][grow][]");

        Scene scene = new Scene(root);

        tableView = new TableView();
        tableView.setPrefHeight(200);

        HBox findBox = new HBox();
        findBox.setSpacing(6);
        VBox detailBox = new VBox();
        detailBox.setPadding(new Insets(6));
        detailBox.setPrefHeight(140);

        TextField locationTxtfld = new TextField();
        locationTxtfld.setPrefWidth(300);
        Button findBtn = new Button("Find");
        findBtn.setDefaultButton(true);
        locationLbl = new Label();
        locationLbl.setPrefWidth(300);
        locationLbl.setWrapText(true);
        tzLbl = new Label();
        tzLbl.setPrefWidth(300);
        tzLbl.setWrapText(true);
        timeLbl = new Label();

        List<String> columns = new ArrayList<>();
        columns.add("PRAYER");
        columns.add("TIME");

        TableColumn[] tableColumns = new TableColumn[columns.size()];
        int colIndex = 0;
        for (String colName : columns) {
            final int j = colIndex;
            tableColumns[colIndex] = new TableColumn(colName);
            tableColumns[colIndex].setPrefWidth(175.0);
            tableColumns[colIndex].setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            colIndex++;
        }
        tableView.getColumns().addAll(tableColumns);

        findBtn.setOnAction((ActionEvent event) -> {
            try {
                buildData(locationTxtfld.getText());
            }
            catch (JSONException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Location error");
                alert.setHeaderText("");
                alert.setContentText("Location not found or does not exist.");
                alert.showAndWait();
            }
        });

        findBox.getChildren().addAll(locationTxtfld, findBtn);
        detailBox.getChildren().addAll(locationLbl, tzLbl, timeLbl);

        root.add(findBox, "grow, wrap");
        root.add(detailBox, "grow, wrap");
        root.add(tableView, "grow");

        stage.setTitle("PrayerTimeFX");
        stage.setScene(scene);
        stage.show();
    }

    public void buildData(String location) throws JSONException {
        data = FXCollections.observableArrayList();


        PrayerTimes pt = new PrayerTimes(location);
        locationLbl.setText("Prayer time for " + pt.formattedAddress);
        tzLbl.setText("\nStandard time name: " + pt.timeZoneName);
        timeLbl.setText("\nLocal date & time: " + pt.formattedDateTime);

        String[] fajr = {"Fajr", String.format("%1$02d:%2$02d", pt.fajrTime[HOUR], pt.fajrTime[MINUTE])};
        String[] sunrise = {"Sunrise", String.format("%1$02d:%2$02d", pt.sunriseTime[HOUR], pt.sunriseTime[MINUTE])};
        String[] zuhr = {"Zuhr", String.format("%1$02d:%2$02d", pt.zuhrTime[HOUR], pt.zuhrTime[MINUTE])};
        String[] asr = {"Asr", String.format("%1$02d:%2$02d", pt.asrTime[HOUR], pt.asrTime[MINUTE])};
        String[] maghrib = {"Maghrib", String.format("%1$02d:%2$02d", pt.maghribTime[HOUR], pt.maghribTime[MINUTE])};
        String[] isha = {"Isha", String.format("%1$02d:%2$02d", pt.ishaTime[HOUR], pt.ishaTime[MINUTE])};

        ptData = new ArrayList<>();
        ptData.add(fajr);
        ptData.add(sunrise);
        ptData.add(zuhr);
        ptData.add(asr);
        ptData.add(maghrib);
        ptData.add(isha);

        for (String[] prayer : ptData) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < prayer.length; i++)
                row.addAll(prayer[i]);

            data.add(row);
        }

        tableView.setItems(data);
    }
}
