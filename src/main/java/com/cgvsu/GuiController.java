package com.cgvsu;

import com.cgvsu.math.vectors.Vector3D;
import com.cgvsu.render_engine.RenderEngWithTriangFill;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    private static final float TRANSLATION_SPEED = 0.5F;
    private static final float ZOOM_SPEED = 0.6F;
    public CheckBox wireframeCheckBox;
    public CheckBox textureCheckBox;
    public CheckBox lightningCheckBox;
    public TextField eyeX;
    public TextField targetX;
    public TextField eyeY;
    public TextField targetY;
    public TextField eyeZ;
    public TextField targetZ;
    public AnchorPane modelPane;
    public TextField sy;
    public TextField sx;
    public TextField sz;
    public TextField rx;
    public TextField ry;
    public TextField rz;
    public TextField tx;
    public TextField ty;
    public TextField tz;
    public Button translate;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    public AnchorPane cameraPane;

    @FXML
    private Canvas canvas;

    Alert messageWarning = new Alert(Alert.AlertType.WARNING);
    Alert messageError = new Alert(Alert.AlertType.ERROR);
    Alert messageInformation = new Alert(Alert.AlertType.INFORMATION);

    private Model mesh = null;
    private final List<Camera> cameras = new ArrayList<>();
    private final List<Button> addedButtonsCamera = new ArrayList<>();
    private final List<Button> deletedButtonsCamera = new ArrayList<>();


    private final Camera camera = new Camera(

            new Vector3D(0, 0, 1500),
            new Vector3D(0, 0, 0),

            1.0F, 1, 0.01F, 100, true);

    private double mousePrevX = 0;
    private double mousePrevY = 0;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(50), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
           /* for (Camera c : cameras) {
                c.setAspectRatio((float) (width / height));
            }

            */
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                try {
                    boolean[] renderOptions = {
                            wireframeCheckBox.isSelected(), // Wireframe
                            textureCheckBox.isSelected(),   // Texture
                            lightningCheckBox.isSelected()  // Lighting
                    };
                    RenderEngWithTriangFill.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, renderOptions);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        setupMouseControls();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
        } catch (IOException ignored) {
            System.err.println("Error reading file: " + fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onOpenTextureMenuItemClick() {
        if (mesh == null) {
            showMessage("No Model Loaded", "Please load a model before applying a texture.", messageError);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Texture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Загружаем текстуру как Image
                Image texture = new Image(selectedFile.toURI().toString());
                mesh.setTexture(texture); //присваиваем текстуру для модели
                showMessage("Texture Loaded", "The texture has been successfully applied.", messageInformation);
            } catch (Exception e) {
                showMessage("Texture Load Failed", "Failed to load texture: " + e.getMessage(), messageError);
            }
        }
    }


    public void addCameraButtons() {
        Button addButton = new Button("Камера " + (addedButtonsCamera.size() + 1));
        addButton.setLayoutY((addedButtonsCamera.size() > 0) ?
                addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutY() + 40 :
                185);
        addButton.setLayoutX(33);
        addButton.setOnAction(event -> showCamera(addButton.getText()));
        addedButtonsCamera.add(addButton);
        Button deleteButton = new Button("Удалить");
        deleteButton.setLayoutY(addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutY());
        deleteButton.setLayoutX(addedButtonsCamera.get(addedButtonsCamera.size() - 1).getLayoutX() + 85);
        deleteButton.setOnAction(event -> deleteCamera(addButton.getText()));
        deletedButtonsCamera.add(deleteButton);

        cameraPane.getChildren().add(addButton);
        cameraPane.getChildren().add(deleteButton);
    }

    private Camera activeCamera() {
        for (Camera camera : cameras) {
            if (camera.isActive()) {
                return camera;
            }
        }
        showMessage("Осторожно", "Переключено на Камеру 1", messageInformation);
        return cameras.get(0);
    }


    @FXML
    private void createCamera() {
        if (!Objects.equals(eyeX.getText(), "") && !Objects.equals(eyeY.getText(), "") && !Objects.equals(eyeZ.getText(), "")
                && !Objects.equals(targetX.getText(), "") && !Objects.equals(targetY.getText(), "") && !Objects.equals(targetZ.getText(), "")) {
            for (Camera camera : cameras) {
                camera.setActive(false);
            }
            cameras.add(new Camera(
                    new Vector3D(Float.parseFloat(eyeX.getText()), Float.parseFloat(eyeY.getText()), Float.parseFloat(eyeZ.getText())),
                    new Vector3D(Float.parseFloat(targetX.getText()), Float.parseFloat(targetY.getText()), Float.parseFloat(targetZ.getText())),
                    1.0F, 1, 0.01F, 100, true));
            addCameraButtons();
        } else {
            showMessage("Предупреждение", "Введите необходимые данные!", messageWarning);
        }
    }

    public void showCamera(String text) {
        int numOfCamera = Integer.parseInt(text.substring(text.length() - 1));
        for (int i = 0; i < cameras.size(); i++) {
            cameras.get(i).setActive(false);
            if (i + 1 == numOfCamera) {
                cameras.get(i).setActive(true);
            }
        }
    }

    public void deleteCamera(String text) {
        int numOfCamera = Integer.parseInt(text.substring(text.length() - 1));
        for (int i = 0; i < addedButtonsCamera.size(); i++) {
            if (i + 1 == numOfCamera) {
                if (cameras.get(i).isActive()) {
                    showMessage("Информация", "Вы перенаправлены на: Камера 1", messageInformation);
                    cameras.get(0).setActive(true);
                }
                deleteCameraUI(i);
                break;
            }
        }
    }

    public void deleteCameraUI(int cameraID) {
        if (cameras.size() == 1) {
            showMessage("Ошибка", "Нельзя удалить единственную камеру!", messageError);
        } else {
            cameras.remove(cameraID);
            cameraPane.getChildren().remove(addedButtonsCamera.get(cameraID));
            cameraPane.getChildren().remove(deletedButtonsCamera.get(cameraID));
            for (int i = 0; i < addedButtonsCamera.size(); i++) {
                if (i + 1 > cameraID) {
                    addedButtonsCamera.get(i).setText("Камера " + i);
                }
            }
            for (int i = addedButtonsCamera.size() - 1; i >= 1; i--) {
                if (i + 1 > cameraID) {
                    addedButtonsCamera.get(i).setLayoutY(addedButtonsCamera.get(i - 1).getLayoutY());
                    deletedButtonsCamera.get(i).setLayoutY(deletedButtonsCamera.get(i - 1).getLayoutY());
                }
            }
            addedButtonsCamera.remove(cameraID);
            deletedButtonsCamera.remove(cameraID);
        }
    }

    private void showMessage(String headText, String messageText, Alert alert) {
        alert.setHeaderText(headText);
        alert.setContentText(messageText);
        alert.showAndWait();
    }


    private void setupMouseControls() {
        canvas.setOnMousePressed(event -> {
            mousePrevX = event.getSceneX();
            mousePrevY = event.getSceneY();
        });

        canvas.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mousePrevX;
            double deltaY = event.getSceneY() - mousePrevY;

            camera.rotate(deltaX, deltaY);

            mousePrevX = event.getSceneX();
            mousePrevY = event.getSceneY();
        });

        canvas.setOnScroll((ScrollEvent event) -> {
            double zoom = event.getDeltaY();
            camera.movePosition(new Vector3D(0, 0, -zoom * ZOOM_SPEED));
        });
    }


    @FXML
    public void handleCameraLeft() {
        camera.movePosition(new Vector3D(TRANSLATION_SPEED, 0, 0));
    }

    @FXML
    public void handleCameraRight() {
        camera.movePosition(new Vector3D(-TRANSLATION_SPEED, 0, 0));
    }

    @FXML
    public void handleCameraUp() {
        camera.movePosition(new Vector3D(0, -TRANSLATION_SPEED, 0));
    }

    @FXML
    public void handleCameraDown() {
        camera.movePosition(new Vector3D(0, TRANSLATION_SPEED, 0));
    }

    public void resetModelPosition() {

    }

    public void translate(ActionEvent mouseEvent) {
        if (Objects.equals(tx.getText(), "") || Objects.equals(ty.getText(), "") || Objects.equals(tz.getText(), "")
                || Objects.equals(sx.getText(), "") || Objects.equals(sy.getText(), "") || Objects.equals(sz.getText(), "")
                || Objects.equals(rx.getText(), "") || Objects.equals(ry.getText(), "") || Objects.equals(rz.getText(), "")) {
            showMessage("Ошибка", "Введите необходимые данные!", messageError);
        } else {
            try {
                float txVal = Float.parseFloat(tx.getText());
                float tyVal = Float.parseFloat(ty.getText());
                float tzVal = Float.parseFloat(tz.getText());
                float rxVal = Float.parseFloat(rx.getText());
                float ryVal = Float.parseFloat(ry.getText());
                float rzVal = Float.parseFloat(rz.getText());
                float sxVal = Float.parseFloat(sx.getText());
                float syVal = Float.parseFloat(sy.getText());
                float szVal = Float.parseFloat(sz.getText());
                //scene.transform(txVal, tyVal, tzVal, rxVal, ryVal, rzVal, sxVal, syVal, szVal);
            } catch (NumberFormatException e) {
                showMessage("Ошибка", "Неправильный формат чисел!", messageError);
            } catch (RuntimeException ex) {
                showMessage("Ошибка", ex.getMessage(), messageError);
            }
        }
    }

}
