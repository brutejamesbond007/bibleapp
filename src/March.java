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

public class March extends Application {

    private final Map<Integer, String> marchReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadMarchReadings();

        /*
         * ============================================================
         * MAIN WINDOW
         * ============================================================
         */

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
                new Label("March");

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
         * MARCH READING PLAN
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
         *
         * We are not using weekday names because this reading plan
         * should work every year.
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
         * MARCH DAYS 1 - 31
         *
         * The days are sequential instead of being tied to
         * a specific year's weekdays.
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

        /*
         * DATE
         */

        selectedDateLabel =
                new Label("March");

        selectedDateLabel.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        22
                )
        );

        /*
         * READING
         */

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

        /*
         * START READING BUTTON
         *
         * For now this selects March 1.
         * Later the shared Bible Reader will send
         * this day's passage to Bible Gateway.
         */

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

    private Button createDayButton(
            int day
    ) {

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

    private void selectDay(
            int day
    ) {

        String reading =
                marchReadings.get(day);

        selectedDateLabel.setText(
                "March " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * MARCH
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadMarchReadings() {

        /*
         * MARCH 1
         */

        marchReadings.put(
                1,
                """
                NUMBERS 6:1-27
                NUMBERS 10:1-36
                """
        );

        /*
         * MARCH 2
         */

        marchReadings.put(
                2,
                "NUMBERS 11:1-13:33"
        );

        /*
         * MARCH 3
         */

        marchReadings.put(
                3,
                "NUMBERS 14:1-15:41"
        );

        /*
         * MARCH 4
         */

        marchReadings.put(
                4,
                "NUMBERS 16:1-18:32"
        );

        /*
         * MARCH 5
         */

        marchReadings.put(
                5,
                "NUMBERS 19:1-21:35"
        );

        /*
         * MARCH 6
         */

        marchReadings.put(
                6,
                "NUMBERS 22:1-24:25"
        );

        /*
         * MARCH 7
         */

        marchReadings.put(
                7,
                "NUMBERS 25:1-26:65"
        );

        /*
         * MARCH 8
         */

        marchReadings.put(
                8,
                "NUMBERS 27:1-29:40"
        );

        /*
         * MARCH 9
         */

        marchReadings.put(
                9,
                "NUMBERS 30:1-31:54"
        );

        /*
         * MARCH 10
         */

        marchReadings.put(
                10,
                "NUMBERS 32:1-33:56"
        );

        /*
         * MARCH 11
         */

        marchReadings.put(
                11,
                "NUMBERS 34:1-36:13"
        );

        /*
         * MARCH 12
         */

        marchReadings.put(
                12,
                "DEUTERONOMY 1:1-3:20"
        );

        /*
         * MARCH 13
         */

        marchReadings.put(
                13,
                "DEUTERONOMY 3:21-5:33"
        );

        /*
         * MARCH 14
         */

        marchReadings.put(
                14,
                "DEUTERONOMY 6:1-9:29"
        );

        /*
         * MARCH 15
         */

        marchReadings.put(
                15,
                "DEUTERONOMY 10:1-12:32"
        );

        /*
         * MARCH 16
         */

        marchReadings.put(
                16,
                "DEUTERONOMY 13:1-16:17"
        );

        /*
         * MARCH 17
         */

        marchReadings.put(
                17,
                "DEUTERONOMY 16:18-21:9"
        );

        /*
         * MARCH 18
         */

        marchReadings.put(
                18,
                "DEUTERONOMY 21:10-25:19"
        );

        /*
         * MARCH 19
         */

        marchReadings.put(
                19,
                "DEUTERONOMY 26:1-29:1"
        );

        /*
         * MARCH 20
         */

        marchReadings.put(
                20,
                "DEUTERONOMY 29:2-31:29"
        );

        /*
         * MARCH 21
         */

        marchReadings.put(
                21,
                """
                DEUTERONOMY 31:30-32:52
                PSALM 90
                """
        );

        /*
         * MARCH 22
         */

        marchReadings.put(
                22,
                """
                DEUTERONOMY 33:1-34:12
                JOSHUA 1:1-2:24
                """
        );

        /*
         * MARCH 23
         */

        marchReadings.put(
                23,
                "JOSHUA 3:1-6:27"
        );

        /*
         * MARCH 24
         */

        marchReadings.put(
                24,
                """
                JOSHUA 7:1
                1 CHRONICLES 2:7
                JOSHUA 7:2-9:27
                """
        );

        /*
         * MARCH 25
         */

        marchReadings.put(
                25,
                "JOSHUA 10:1-12:6"
        );

        /*
         * MARCH 26
         */

        marchReadings.put(
                26,
                "JOSHUA 12:7-15:19"
        );

        /*
         * MARCH 27
         */

        marchReadings.put(
                27,
                "JOSHUA 15:20-17:18"
        );

        /*
         * MARCH 28
         */

        marchReadings.put(
                28,
                "JOSHUA 18:1-19:48"
        );

        /*
         * MARCH 29
         */

        marchReadings.put(
                29,
                """
                JOSHUA 19:49-21:45
                1 CHRONICLES 6:54-81
                """
        );

        /*
         * MARCH 30
         */

        marchReadings.put(
                30,
                "JOSHUA 22:1-24:33"
        );

        /*
         * MARCH 31
         */

        marchReadings.put(
                31,
                "JUDGES 1:1-3:30"
        );
    }
public static Map<Integer, String> getReadings() {

    March month = new March();
    month.loadMarchReadings();

    return new LinkedHashMap<>(
            month.marchReadings
    );
}
    /*
     * ================================================================
     * MAIN
     * ================================================================
     */

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}
