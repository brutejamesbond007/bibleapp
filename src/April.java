import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class April extends Application {

    private final Map<Integer, String> aprilReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadAprilReadings();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        /*
         * ============================================================
         * TOP
         * ============================================================
         */

        VBox topSection = new VBox(5);
        topSection.setAlignment(Pos.CENTER);

        Label title =
                new Label("Chronological Bible Reader");

        title.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        28
                )
        );

        Label monthTitle =
                new Label("April");

        monthTitle.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        24
                )
        );

        topSection.getChildren().addAll(
                title,
                monthTitle
        );

        root.setTop(topSection);

        /*
         * ============================================================
         * CENTER
         * APRIL READING PLAN
         * ============================================================
         */

        VBox calendarSection = new VBox(10);

        calendarSection.setPadding(
                new Insets(25, 0, 20, 0)
        );

        GridPane calendar = new GridPane();

        calendar.setHgap(8);
        calendar.setVgap(8);
        calendar.setAlignment(Pos.CENTER);

        /*
         * ============================================================
         * DAY HEADERS
         * ============================================================
         */

        String[] headers = {
                "Day 1",
                "Day 2",
                "Day 3",
                "Day 4",
                "Day 5",
                "Day 6",
                "Day 7"
        };

        for (int column = 0;
             column < headers.length;
             column++) {

            Label header =
                    new Label(headers[column]);

            header.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            14
                    )
            );

            header.setAlignment(Pos.CENTER);
            header.setMinWidth(115);

            calendar.add(
                    header,
                    column,
                    0
            );
        }

        /*
         * ============================================================
         * APRIL DAYS 1 - 30
         * ============================================================
         */

        int column = 0;
        int row = 1;

        for (int day = 1;
             day <= 30;
             day++) {

            Button dayButton =
                    createDayButton(day);

            calendar.add(
                    dayButton,
                    column,
                    row
            );

            column++;

            if (column == 7) {
                column = 0;
                row++;
            }
        }

        calendarSection
                .getChildren()
                .add(calendar);

        root.setCenter(calendarSection);

        /*
         * ============================================================
         * BOTTOM
         * SELECTED READING
         * ============================================================
         */

        VBox bottomSection =
                new VBox(12);

        bottomSection.setAlignment(
                Pos.CENTER
        );

        bottomSection.setPadding(
                new Insets(15)
        );

        selectedDateLabel =
                new Label("April");

        selectedDateLabel.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        22
                )
        );

        selectedReadingLabel =
                new Label(
                        "Choose a day from the reading plan."
                );

        selectedReadingLabel.setFont(
                Font.font(
                        "Serif",
                        17
                )
        );

        selectedReadingLabel.setWrapText(true);

        selectedReadingLabel.setAlignment(
                Pos.CENTER
        );

        selectedReadingLabel.setMaxWidth(
                850
        );

        Button startReadingButton =
                new Button(
                        "Start Reading \u25B6"
                );

        startReadingButton.setPrefWidth(
                180
        );

        startReadingButton.setPrefHeight(
                45
        );

        startReadingButton.setOnAction(
                event -> selectDay(1)
        );

        bottomSection
                .getChildren()
                .addAll(
                        selectedDateLabel,
                        selectedReadingLabel,
                        startReadingButton
                );

        root.setBottom(bottomSection);

        /*
         * ============================================================
         * WINDOW
         * ============================================================
         */

        Scene scene =
                new Scene(
                        root,
                        1000,
                        720
                );

        stage.setTitle(
                "Chronological Bible Reader"
        );

        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(650);

        stage.show();
    }

    /*
     * ================================================================
     * CREATE DAY BUTTON
     * ================================================================
     */

    private Button createDayButton(int day) {

        Button button =
                new Button(
                        String.valueOf(day)
                );

        button.setPrefSize(
                115,
                70
        );

        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        button.setOnAction(
                event -> selectDay(day)
        );

        return button;
    }

    /*
     * ================================================================
     * SELECT DAY
     * ================================================================
     */

    private void selectDay(int day) {

        String reading =
                aprilReadings.get(day);

        selectedDateLabel.setText(
                "April " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * APRIL
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadAprilReadings() {

        aprilReadings.put(
                1,
                "JUDGES 3:31-6:40"
        );

        aprilReadings.put(
                2,
                "JUDGES 7:1-9:21"
        );

        aprilReadings.put(
                3,
                "JUDGES 9:22-11:28"
        );

        aprilReadings.put(
                4,
                "JUDGES 11:29-15:20"
        );

        aprilReadings.put(
                5,
                "JUDGES 16:1-18:31"
        );

        aprilReadings.put(
                6,
                "JUDGES 19:1-21:25"
        );

        aprilReadings.put(
                7,
                "RUTH 1:1-4:12"
        );

        aprilReadings.put(
                8,
                """
                RUTH 4:13-22
                1 CHRONICLES 2:9-55
                1 CHRONICLES 4:1-23
                1 SAMUEL 1:1-8
                """
        );

        aprilReadings.put(
                9,
                "1 SAMUEL 1:9-4:11"
        );

        aprilReadings.put(
                10,
                "1 SAMUEL 4:12-8:22"
        );

        aprilReadings.put(
                11,
                "1 SAMUEL 9:1-12:25"
        );

        aprilReadings.put(
                12,
                """
                1 CHRONICLES 9:35-39
                1 SAMUEL 13:1-5
                1 SAMUEL 13:19-23
                1 SAMUEL 13:6-18
                1 SAMUEL 14:1-52
                """
        );

        aprilReadings.put(
                13,
                "1 SAMUEL 15:1-17:31"
        );

        aprilReadings.put(
                14,
                """
                1 SAMUEL 17:32-19:17
                PSALM 59
                1 SAMUEL 19:18-24
                """
        );

        aprilReadings.put(
                15,
                """
                1 SAMUEL 20:1-21:15
                PSALM 34
                """
        );

        aprilReadings.put(
                16,
                """
                1 SAMUEL 22:1-2
                PSALM 57
                PSALM 142
                1 CHRONICLES 12:8-18
                1 SAMUEL 22:3-23
                PSALM 52
                1 SAMUEL 23:1-12
                """
        );

        aprilReadings.put(
                17,
                """
                1 SAMUEL 23:13-29
                PSALM 54
                1 SAMUEL 24:1-25:44
                """
        );

        aprilReadings.put(
                18,
                """
                1 SAMUEL 26:1-27:7
                1 CHRONICLES 12:1-7
                1 SAMUEL 27:8-29:11
                1 CHRONICLES 12:19
                PSALM 56
                """
        );

        aprilReadings.put(
                19,
                """
                1 SAMUEL 30:1-31
                1 CHRONICLES 12:20-22
                1 SAMUEL 31:1-13
                1 CHRONICLES 10:1-14
                1 CHRONICLES 9:40-44
                2 SAMUEL 4:4
                2 SAMUEL 1:1-27
                """
        );

        aprilReadings.put(
                20,
                """
                2 SAMUEL 2:1-3:5
                1 CHRONICLES 3:1-4
                2 SAMUEL 23:8-17
                1 CHRONICLES 11:10-19
                2 SAMUEL 23:18-39
                1 CHRONICLES 11:20-47
                """
        );

        aprilReadings.put(
                21,
                """
                2 SAMUEL 3:6-4:3
                2 SAMUEL 4:5-4:12
                """
        );

        aprilReadings.put(
                22,
                """
                2 SAMUEL 5:1-3
                1 CHRONICLES 11:1-3
                1 CHRONICLES 12:23-40
                2 SAMUEL 5:17-25
                1 CHRONICLES 14:8-17
                2 SAMUEL 5:6-10
                1 CHRONICLES 11:4-9
                1 CHRONICLES 3:4
                2 SAMUEL 5:13
                2 SAMUEL 5:4-5
                2 SAMUEL 5:11-12
                1 CHRONICLES 14:1-2
                1 CHRONICLES 13:1-5
                2 SAMUEL 6:1-11
                1 CHRONICLES 13:6-14
                """
        );

        aprilReadings.put(
                23,
                """
                2 SAMUEL 6:12
                1 CHRONICLES 15:1-28
                2 SAMUEL 6:12-16
                1 CHRONICLES 15:29
                2 SAMUEL 6:17-19
                1 CHRONICLES 16:1-43
                2 SAMUEL 6:19-23
                """
        );

        aprilReadings.put(
                24,
                """
                2 SAMUEL 7:1-17
                1 CHRONICLES 17:1-15
                2 SAMUEL 7:18-29
                1 CHRONICLES 17:16-27
                2 SAMUEL 8:1-14
                1 CHRONICLES 18:1-13
                PSALM 60
                """
        );

        aprilReadings.put(
                25,
                """
                2 SAMUEL 8:15-18
                1 CHRONICLES 18:14-17
                1 CHRONICLES 6:16-30
                1 CHRONICLES 6:50-53
                1 CHRONICLES 6:31-48
                2 SAMUEL 9:1-10:19
                1 CHRONICLES 19:1-19
                """
        );

        aprilReadings.put(
                26,
                """
                1 CHRONICLES 20:1
                2 SAMUEL 11:1-12:14
                PSALM 51
                2 SAMUEL 12:15-25
                2 SAMUEL 5:14-16
                1 CHRONICLES 14:3-7
                1 CHRONICLES 3:5-9
                """
        );

        aprilReadings.put(
                27,
                """
                2 SAMUEL 12:26-31
                1 CHRONICLES 20:2-3
                2 SAMUEL 13:1-14:33
                """
        );

        aprilReadings.put(
                28,
                "2 SAMUEL 15:1-17:14"
        );

        aprilReadings.put(
                29,
                """
                2 SAMUEL 17:15-29
                PSALM 3
                PSALM 63
                2 SAMUEL 18:1-19:30
                """
        );

        aprilReadings.put(
                30,
                """
                2 SAMUEL 19:31-20:26
                PSALM 7
                2 SAMUEL 21:1-22
                1 CHRONICLES 20:4-8
                """
        );
    }
public static Map<Integer, String> getReadings() {

    April month = new April();
    month.loadAprilReadings();

    return new LinkedHashMap<>(
            month.aprilReadings
    );
}
    /*
     * ================================================================
     * MAIN
     * ================================================================
     */

    public static void main(String[] args) {
        launch(args);
    }
}
