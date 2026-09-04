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

public class June extends Application {

    private final Map<Integer, String> juneReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadJuneReadings();

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
                new Label("June");

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
         * JUNE READING PLAN
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
         * JUNE DAYS 1 - 30
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
                new Label("June");

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
                juneReadings.get(day);

        selectedDateLabel.setText(
                "June " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * JUNE
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadJuneReadings() {

        juneReadings.put(
                1,
                "PROVERBS 17:1-19:29"
        );

        juneReadings.put(
                2,
                "PROVERBS 20:1-21:31"
        );

        juneReadings.put(
                3,
                "PROVERBS 22:1-24:34"
        );

        juneReadings.put(
                4,
                "SONG 1:1-8:14"
        );

        juneReadings.put(
                5,
                """
                1 KINGS 11:1-43
                2 CHRONICLES 9:29-31
                ECCLESIASTES 1:1-11
                """
        );

        juneReadings.put(
                6,
                "ECCLESIASTES 1:12-5:19"
        );

        juneReadings.put(
                7,
                "ECCLESIASTES 6:1-10:20"
        );

        juneReadings.put(
                8,
                """
                ECCLESIASTES 11:1-12:14
                1 KINGS 12:1-20
                2 CHRONICLES 10:1-19
                1 KINGS 12:21-24
                2 CHRONICLES 11:1-4
                1 KINGS 12:25-33
                2 CHRONICLES 11:5-17
                1 KINGS 13:1-14:18
                """
        );

        juneReadings.put(
                9,
                """
                1 KINGS 14:21-24
                2 CHRONICLES 12:13-14
                2 CHRONICLES 11:18-23
                2 CHRONICLES 12:1-12
                1 KINGS 14:25-28
                2 CHRONICLES 12:13-16
                1 KINGS 14:29-15:5
                2 CHRONICLES 13:1-22
                1 KINGS 15:6-8
                2 CHRONICLES 14:1-8
                1 KINGS 15:9-15
                1 KINGS 14:19-20
                1 KINGS 15:25-34
                2 CHRONICLES 14:9-15
                """
        );

        juneReadings.put(
                10,
                """
                1 KINGS 15:16-22
                2 CHRONICLES 16:1-10
                1 KINGS 16:1-34
                1 KINGS 15:23-24
                2 CHRONICLES 16:11-17:19
                """
        );

        juneReadings.put(
                11,
                "1 KINGS 17:1-19:21"
        );

        juneReadings.put(
                12,
                "1 KINGS 20:1-21:29"
        );

        juneReadings.put(
                13,
                """
                1 KINGS 22:1-28
                2 CHRONICLES 18:1-27
                1 KINGS 22:29-35
                2 CHRONICLES 18:28-34
                1 KINGS 22:36-40
                1 KINGS 22:51-53
                2 CHRONICLES 19:1-20:30
                """
        );

        juneReadings.put(
                14,
                """
                2 KINGS 1:1-18
                2 KINGS 3:1-27
                1 KINGS 22:41-49
                2 CHRONICLES 20:31-37
                1 KINGS 22:50
                2 CHRONICLES 21:1-4
                2 KINGS 8:16-22
                2 CHRONICLES 21:5-17
                """
        );

        juneReadings.put(
                15,
                """
                2 KINGS 2:1-25
                2 KINGS 4:1-44
                """
        );

        juneReadings.put(
                16,
                "2 KINGS 5:1-8:15"
        );

        juneReadings.put(
                17,
                """
                2 CHRONICLES 21:18-20
                2 KINGS 8:23-29
                2 CHRONICLES 22:1-7
                2 KINGS 9:1-10:17
                2 CHRONICLES 22:8-9
                2 KINGS 10:18-31
                """
        );

        juneReadings.put(
                18,
                """
                2 KINGS 11:1-3
                2 CHRONICLES 22:10-12
                2 KINGS 11:4-12
                2 CHRONICLES 23:1-11
                2 KINGS 11:13-16
                2 CHRONICLES 23:12-15
                2 KINGS 11:17-21
                2 CHRONICLES 23:16-21
                2 KINGS 12:1-16
                2 CHRONICLES 24:1-22
                2 KINGS 10:32-36
                """
        );

        juneReadings.put(
                19,
                """
                2 KINGS 13:1-11
                2 KINGS 12:17-21
                2 CHRONICLES 24:23-27
                2 KINGS 13:14-25
                """
        );

        juneReadings.put(
                20,
                """
                2 KINGS 14:1-14
                2 CHRONICLES 25:1-24
                2 KINGS 13:12-13
                2 KINGS 14:15-16
                2 KINGS 14:23-27
                2 CHRONICLES 25:25-28
                2 KINGS 14:17-22
                2 KINGS 15:1-5
                2 CHRONICLES 26:1-21
                JONAH 1:1-4:11
                """
        );

        juneReadings.put(
                21,
                "AMOS 1:1-6:14"
        );

        juneReadings.put(
                22,
                """
                AMOS 7:1-9:15
                2 KINGS 14:28-29
                2 KINGS 15:8-29
                2 KINGS 15:6-7
                2 CHRONICLES 26:22-23
                ISAIAH 6:1-13
                """
        );

        juneReadings.put(
                23,
                """
                2 KINGS 15:32-38
                2 CHRONICLES 27:1-9
                MICAH 1:1-16
                2 KINGS 16:1-9
                2 CHRONICLES 28:1-15
                ISAIAH 7:1-25
                """
        );

        juneReadings.put(
                24,
                "ISAIAH 8:1-11:16"
        );

        juneReadings.put(
                25,
                """
                ISAIAH 12:1-6
                ISAIAH 17:1-14
                2 CHRONICLES 28:16-21
                2 KINGS 16:10-18
                2 CHRONICLES 28:22-25
                2 KINGS 18:1-8
                2 CHRONICLES 29:1-2
                2 KINGS 15:30-31
                2 KINGS 17:1-4
                HOSEA 1:1-2:13
                """
        );

        juneReadings.put(
                26,
                "HOSEA 2:14-8:14"
        );

        juneReadings.put(
                27,
                "HOSEA 9:1-14:9"
        );

        juneReadings.put(
                28,
                """
                ISAIAH 28:1-29
                2 KINGS 17:5
                2 KINGS 18:9-12
                2 KINGS 17:6-41
                ISAIAH 1:1-20
                """
        );

        juneReadings.put(
                29,
                "ISAIAH 1:21-5:30"
        );

        juneReadings.put(
                30,
                """
                2 KINGS 16:19-20
                2 CHRONICLES 28:26-27
                ISAIAH 13:1-16:14
                """
        );
    }
public static Map<Integer, String> getReadings() {

    June month = new June();
    month.loadJuneReadings();

    return new LinkedHashMap<>(
            month.juneReadings
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
