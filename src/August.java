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

public class August extends Application {

    private final Map<Integer, String> augustReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadAugustReadings();

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
                new Label("August");

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
         * AUGUST READING PLAN
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
         * AUGUST DAYS 1 - 31
         * ============================================================
         */

        int column = 0;
        int row = 1;

        for (int day = 1;
             day <= 31;
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
                new Label("August");

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
                augustReadings.get(day);

        selectedDateLabel.setText(
                "August " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * AUGUST
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadAugustReadings() {

        /*
         * AUGUST 1
         */

        augustReadings.put(
                1,
                """
                2 KINGS 23:1-20
                2 CHRONICLES 34:29-33
                2 KINGS 23:21-28
                2 CHRONICLES 35:1-19
                NAHUM 1:1-3:19
                """
        );

        /*
         * AUGUST 2
         */

        augustReadings.put(
                2,
                """
                HABAKKUK 1:1-3:19
                ZEPHANIAH 1:1-2:7
                """
        );

        /*
         * AUGUST 3
         */

        augustReadings.put(
                3,
                """
                ZEPHANIAH 2:8-3:20
                2 CHRONICLES 35:20-27
                2 KINGS 23:29-30
                JEREMIAH 47:1-48:47
                """
        );

        /*
         * AUGUST 4
         */

        augustReadings.put(
                4,
                """
                2 CHRONICLES 36:1-4
                2 KINGS 23:31-37
                2 CHRONICLES 36:5
                JEREMIAH 22:1-23
                JEREMIAH 26:1-24
                2 KINGS 24:1-4
                JEREMIAH 25:1-14
                """
        );

        /*
         * AUGUST 5
         */

        augustReadings.put(
                5,
                """
                JEREMIAH 25:15-38
                JEREMIAH 36:1-32
                JEREMIAH 45:1-46:28
                """
        );

        /*
         * AUGUST 6
         */

        augustReadings.put(
                6,
                """
                JEREMIAH 19:1-20:18
                DANIEL 1:1-21
                """
        );

        /*
         * AUGUST 7
         */

        augustReadings.put(
                7,
                "DANIEL 2:1-3:30"
        );

        /*
         * AUGUST 8
         */

        augustReadings.put(
                8,
                "JEREMIAH 7:1-11:23"
        );

        /*
         * AUGUST 9
         */

        augustReadings.put(
                9,
                "JEREMIAH 12:1-15:21"
        );

        /*
         * AUGUST 10
         */

        augustReadings.put(
                10,
                """
                JEREMIAH 16:1-18:23
                JEREMIAH 35:1-19
                """
        );

        /*
         * AUGUST 11
         */

        augustReadings.put(
                11,
                """
                JEREMIAH 49:1-33
                2 KINGS 24:5-7
                2 CHRONICLES 36:6-8
                2 KINGS 24:8-9
                2 CHRONICLES 36:9
                JEREMIAH 22:24-23:32
                """
        );

        /*
         * AUGUST 12
         */

        augustReadings.put(
                12,
                """
                JEREMIAH 23:33-24:10
                JEREMIAH 29:1-31:14
                """
        );

        /*
         * AUGUST 13
         */

        augustReadings.put(
                13,
                """
                JEREMIAH 31:15-40
                JEREMIAH 49:34-51:14
                """
        );

        /*
         * AUGUST 14
         */

        augustReadings.put(
                14,
                """
                JEREMIAH 51:15-58
                2 CHRONICLES 36:10
                2 KINGS 24:10-17
                2 CHRONICLES 36:11-14
                JEREMIAH 52:1-3
                2 KINGS 24:18-20
                JEREMIAH 37:1-10
                """
        );

        /*
         * AUGUST 15
         */

        augustReadings.put(
                15,
                """
                JEREMIAH 37:11-38:28
                EZEKIEL 1:1-3:15
                """
        );

        /*
         * AUGUST 16
         */

        augustReadings.put(
                16,
                """
                EZEKIEL 3:16-4:17
                JEREMIAH 27:1-28:17
                JEREMIAH 51:59-64
                """
        );

        /*
         * AUGUST 17
         */

        augustReadings.put(
                17,
                "EZEKIEL 5:1-9:11"
        );

        /*
         * AUGUST 18
         */

        augustReadings.put(
                18,
                "EZEKIEL 10:1-13:23"
        );

        /*
         * AUGUST 19
         */

        augustReadings.put(
                19,
                "EZEKIEL 14:1-16:63"
        );

        /*
         * AUGUST 20
         */

        augustReadings.put(
                20,
                "EZEKIEL 17:1-19:14"
        );

        /*
         * AUGUST 21
         */

        augustReadings.put(
                21,
                "EZEKIEL 20:1-22:16"
        );

        /*
         * AUGUST 22
         */

        augustReadings.put(
                22,
                """
                EZEKIEL 22:17-23:49
                2 KINGS 24:20-25:2
                JEREMIAH 52:3-5
                JEREMIAH 39:1
                EZEKIEL 24:1-14
                """
        );

        /*
         * AUGUST 23
         */

        augustReadings.put(
                23,
                """
                EZEKIEL 24:15-25:17
                JEREMIAH 34:1-22
                JEREMIAH 21:1-14
                EZEKIEL 29:1-16
                EZEKIEL 30:20-31:18
                """
        );

        /*
         * AUGUST 24
         */

        augustReadings.put(
                24,
                """
                JEREMIAH 32:1-33:26
                EZEKIEL 26:1-14
                """
        );

        /*
         * AUGUST 25
         */

        augustReadings.put(
                25,
                """
                EZEKIEL 26:15-28:26
                2 KINGS 25:3-7
                JEREMIAH 52:6-11
                JEREMIAH 39:2-10
                """
        );

        /*
         * AUGUST 26
         */

        augustReadings.put(
                26,
                """
                JEREMIAH 39:11-18
                JEREMIAH 40:1-6
                2 KINGS 25:8-21
                JEREMIAH 52:12-27
                2 CHRONICLES 36:15-21
                LAMENTATIONS 1:1-22
                """
        );

        /*
         * AUGUST 27
         */

        augustReadings.put(
                27,
                "LAMENTATIONS 2:1-4:22"
        );

        /*
         * AUGUST 28
         */

        augustReadings.put(
                28,
                """
                LAMENTATIONS 5:1-22
                OBADIAH 1:1-21
                2 KINGS 25:22-26
                JEREMIAH 40:7-41:18
                """
        );

        /*
         * AUGUST 29
         */

        augustReadings.put(
                29,
                """
                JEREMIAH 42:1-44:30
                EZEKIEL 33:21-33
                """
        );

        /*
         * AUGUST 30
         */

        augustReadings.put(
                30,
                "EZEKIEL 34:1-36:38"
        );

        /*
         * AUGUST 31
         */

        augustReadings.put(
                31,
                """
                EZEKIEL 37:1-39:29
                EZEKIEL 32:1-16
                """
        );
    }
public static Map<Integer, String> getReadings() {

    August month = new August();
    month.loadAugustReadings();

    return new LinkedHashMap<>(
            month.augustReadings
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
