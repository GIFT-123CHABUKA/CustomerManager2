package
org.example.customermanager;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CustomerManager extends Application {

    public static class Customer {
        private final String name;
        private final String province;

        public Customer(String name, String province) {
            this.name = name;
            this.province = province;
        }
        public String getName() { return name; }
        public String getProvince() { return province; }
    }

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Full name");

        ComboBox<String> provinceBox = new ComboBox<>(FXCollections.observableArrayList(
                "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
                "Muchinga", "Northern", "North-Western", "Southern", "Western"));
        provinceBox.setPromptText("Select province");

        Label nameLabel = new Label("_Name:");
        nameLabel.setMnemonicParsing(true);
        nameLabel.setLabelFor(nameField);
        Label provinceLabel = new Label("_Province:");
        provinceLabel.setMnemonicParsing(true);
        provinceLabel.setLabelFor(provinceBox);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button addBtn = new Button("_Add");
        addBtn.setDefaultButton(true);
        Button deleteBtn = new Button("_Delete");

        TableView<Customer> table = new TableView<>(customers);
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getName()));
        TableColumn<Customer, String> provCol = new TableColumn<>("Province");
        provCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getProvince()));
        table.getColumns().addAll(nameCol, provCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        deleteBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String province = provinceBox.getValue();

            if (name.isEmpty()) {
                errorLabel.setText("Name is required.");
                nameField.requestFocus();
                return;
            }
            if (!name.matches("[\\p{L} .'-]+")) {
                errorLabel.setText("Name may only contain letters, spaces, . ' -");
                nameField.requestFocus();
                return;
            }
            if (province == null) {
                errorLabel.setText("Please select a province.");
                provinceBox.requestFocus();
                return;
            }

            customers.add(new Customer(name, province));
            errorLabel.setText("");
            nameField.clear();
            provinceBox.setValue(null);
            nameField.requestFocus();
        });

        deleteBtn.setOnAction(e -> deleteSelected(table));
        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE && !table.getSelectionModel().isEmpty()) {
                deleteSelected(table);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, nameLabel, nameField);
        form.addRow(1, provinceLabel, provinceBox);
        form.add(new HBox(10, addBtn, deleteBtn), 1, 2);
        form.add(errorLabel, 1, 3);

        VBox root = new VBox(15, form, table);
        root.setPadding(new Insets(15));
        VBox.setVgrow(table, Priority.ALWAYS);

        stage.setTitle("Customer Manager");
        stage.setScene(new Scene(root, 450, 450));
        stage.show();
        nameField.requestFocus();
    }

    private void deleteSelected(TableView<Customer> table) {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm deletion");
        confirm.setHeaderText(null);
        confirm.showAndWait()
                .filter(b -> b == ButtonType.YES)
                .ifPresent(b -> customers.remove(selected));
    }

    public static void main(String[] args) {
        launch(args);
    }
}