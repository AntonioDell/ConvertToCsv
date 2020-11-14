package dell.antonio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

    @FXML
    private TextField textInputFile;
    @FXML
    private TextField textOutputFile;
    @FXML
    private Button buttonStartConversion;
    @FXML
    private Text textConversionStatus;

    private final StringProperty inputFile = new SimpleStringProperty();
    private final StringProperty outputFile = new SimpleStringProperty();
    private final StringProperty conversionStatus = new SimpleStringProperty();

    private final BooleanProperty disableConversion = new SimpleBooleanProperty(true);

    private Path inputPath;
    private Path outputPath;

    @FXML
    void initialize() {
        textInputFile.textProperty().bind(inputFile);
        textOutputFile.textProperty().bind(outputFile);
        buttonStartConversion.disableProperty().bind(disableConversion);
        textConversionStatus.textProperty().bind(conversionStatus);
    }

    public void selectInputFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select input file");
        File userDirectory = new File(System.getProperty("user.home"));
        if(userDirectory.canRead()) {
            fileChooser.setInitialDirectory(userDirectory);
        }

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            inputFile.setValue(selectedFile.getAbsolutePath());
            inputPath = selectedFile.toPath();
        } else {
            inputPath = null;
        }

        enableStartButton();
    }

    public void selectOutputFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select output file");
        fileChooser.getExtensionFilters().addAll(
              new ExtensionFilter("CSV-Files", "*.csv")
        );
        File userDirectory = new File(System.getProperty("user.home"));
        if(userDirectory.canRead()) {
            fileChooser.setInitialDirectory(userDirectory);
        }


        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            outputFile.setValue(selectedFile.getAbsolutePath());
            outputPath = selectedFile.toPath();
        } else {
            outputPath = null;
        }

        enableStartButton();
    }

    public void startConversion(ActionEvent event) {
        try {
            List<String> dataToConvert = Files.readAllLines(inputPath);
            List<String> convertedData = dataToConvert.stream()
                  .map(data -> data.replaceAll("\\s+", ","))
                  .collect(Collectors.toList());
            Files.write(outputPath, convertedData);

            conversionStatus.set("Conversion successfull! You may now close the application");
        } catch (IOException e) {
            e.printStackTrace();
            conversionStatus.set("An error happned during conversion!");
        }
    }

    private void enableStartButton() {
        if (inputPath != null
              && outputPath != null
              && Files.exists(inputPath)) {
            disableConversion.setValue(false);
        }
    }
}
