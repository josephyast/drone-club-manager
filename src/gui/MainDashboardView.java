package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainDashboardView extends BorderPane {

    private Button btnDrones;
    private Button btnPilots;
    private Button btnFlights;
    private Button btnParts;
    private Button btnLogout;
    private Button btnAdd;
    private Button btnRemove;
    private Button btnUpdate;
    private Button btnFreqRequest;
    private Button btnFreqRelease;
    private Button btnAttachPart;
    private Button btnDetachPart;
    private StackPane contentArea;
    private TableView<Object> mainTable;
    private Button btnFixPart;
    private Button btnFlightNow;

    public MainDashboardView() {
        attachStylesheetOnceOnScene();

        HBox header = new HBox();
        header.getStyleClass().add("app-header");
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Drone Operations Center - Management Panel");
        title.getStyleClass().add("label-title");
        header.getChildren().add(title);
        this.setTop(header);

        VBox sideMenu = new VBox(6);
        sideMenu.getStyleClass().add("nav-bar");
        sideMenu.setPadding(new Insets(20, 12, 20, 12));
        sideMenu.setPrefWidth(180);

        btnDrones = createMenuButton("✈  Drones");
        btnPilots = createMenuButton("◎  Pilots");
        btnFlights = createMenuButton("▤  Flights");
        btnParts = createMenuButton("⚙  Parts");
        btnLogout = createMenuButton("⏻  Logout");
        btnLogout.getStyleClass().add("button-danger");

        Separator separator = new Separator();

        sideMenu.getChildren().addAll(btnDrones, btnPilots, btnFlights, btnParts, separator, btnLogout);
        this.setLeft(sideMenu);

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(20));

        HBox actionToolbar = new HBox(10);
        actionToolbar.setAlignment(Pos.CENTER_LEFT);

        btnAdd = new Button("Add New");
        btnAdd.getStyleClass().add("button-accent");
        btnUpdate = new Button("Update Selected");
        btnRemove = new Button("Remove Selected");
        btnRemove.getStyleClass().add("button-danger");

        btnFreqRequest = new Button("Req Frequency");
        btnFreqRelease = new Button("Release Freq");
        btnAttachPart = new Button("Attach Part");
        btnDetachPart = new Button("Detach Part");

        btnFixPart = new Button("Fix Part");
        btnFlightNow = new Button("Flight Now");
        btnFlightNow.getStyleClass().add("button-accent");


        actionToolbar.getChildren().addAll(btnAdd, btnUpdate,btnRemove,btnFreqRequest,btnFreqRelease, btnAttachPart, btnDetachPart, btnFixPart,btnFlightNow);

        mainTable = new TableView<>();
        mainTable.setPlaceholder(new Label("Select a category from the left menu to load data."));
        VBox.setVgrow(mainTable, Priority.ALWAYS);

        centerLayout.getChildren().addAll(actionToolbar, mainTable);
        this.setCenter(centerLayout);

        hideSpecialButtons();
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
    private void attachStylesheetOnceOnScene() {
        String cssUrl = getClass().getResource("modern.css").toExternalForm();
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !newScene.getStylesheets().contains(cssUrl)) {
                newScene.getStylesheets().add(cssUrl);
            }
        });
    }

    public void hideSpecialButtons() {
        btnFreqRequest.setVisible(false);
        btnFreqRelease.setVisible(false);
        btnAttachPart.setVisible(false);
        btnDetachPart.setVisible(false);
        btnFixPart.setVisible(false);
        btnFlightNow.setVisible(false);
    }

    public Button getBtnDrones() { return btnDrones; }
    public Button getBtnPilots() { return btnPilots; }
    public Button getBtnFlights() { return btnFlights; }
    public Button getBtnParts() { return btnParts; }
    public Button getBtnLogout() { return btnLogout; }
    public Button getBtnAdd() { return btnAdd; }
    public Button getBtnUpdate() { return btnUpdate; }
    public Button getBtnRemove() { return btnRemove; }
    public Button getBtnFreqRequest() { return btnFreqRequest; }
    public Button getBtnFreqRelease() { return btnFreqRelease; }
    public Button getBtnAttachPart() { return btnAttachPart; }
    public Button getBtnDetachPart() { return btnDetachPart; }
    public TableView<Object> getMainTable() { return mainTable; }
    public Button getBtnFixPart() {return btnFixPart;}
    public Button getBtnFlightNow() { return btnFlightNow; }

}