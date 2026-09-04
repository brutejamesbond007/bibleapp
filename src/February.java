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

public class February extends Application {

    private final Map<Integer, String> februaryReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadFebruaryReadings();

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
                new Label("February");

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
         * FEBRUARY CALENDAR
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
         * DAYS OF THE WEEK
         * ============================================================
         */

        String[] weekdayNames = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        };

        for (int column = 0;
             column < weekdayNames.length;
             column++) {

            Label weekday =
                    new Label(weekdayNames[column]);

            weekday.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            14
                    )
            );

            weekday.setAlignment(Pos.CENTER);
            weekday.setMinWidth(115);

            calendar.add(
                    weekday,
                    column,
                    0
            );
        }

        /*
         * ============================================================
         * FEBRUARY 2026 CALENDAR
         *
         * February 1, 2026 = Sunday
         * ============================================================
         */

        int column = 0;
        int row = 1;

        for (int day = 1;
             day <= 28;
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
                new Label("February");

        selectedDateLabel.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        22
                )
        );

        selectedReadingLabel =
                new Label(
                        "Choose a day from the calendar."
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
     * CREATE CALENDAR DAY BUTTON
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
                februaryReadings.get(day);

        selectedDateLabel.setText(
                "February "
                        + day
                        
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * FEBRUARY 2026
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadFebruaryReadings() {

        februaryReadings.put(
                1,
                """
                EXODUS 1:1-2:25
                1 CHRONICLES 6:1-3
                EXODUS 3:1-4:17
                """
        );

        februaryReadings.put(
                2,
                "EXODUS 4:18-7:13"
        );

        februaryReadings.put(
                3,
                "EXODUS 7:14-9:35"
        );

        februaryReadings.put(
                4,
                "EXODUS 10:1-12:51"
        );

        februaryReadings.put(
                5,
                "EXODUS 13:1-15:27"
        );

        februaryReadings.put(
                6,
                "EXODUS 16:1-19:25"
        );

        februaryReadings.put(
                7,
                "EXODUS 20:1-22:15"
        );

        februaryReadings.put(
                8,
                "EXODUS 22:16-24:18"
        );

        februaryReadings.put(
                9,
                "EXODUS 25:1-28:43"
        );

        februaryReadings.put(
                10,
                "EXODUS 29:1-31:18"
        );

        februaryReadings.put(
                11,
                "EXODUS 32:1-34:35"
        );

        februaryReadings.put(
                12,
                "EXODUS 35:1-36:38"
        );

        februaryReadings.put(
                13,
                "EXODUS 37:1-39:31"
        );

        februaryReadings.put(
                14,
                """
                EXODUS 39:32-40:38
                NUMBERS 9:15-23
                """
        );

        februaryReadings.put(
                15,
                "NUMBERS 7:1-89"
        );

        februaryReadings.put(
                16,
                """
                NUMBERS 8:1-9:14
                LEVITICUS 1:1-3:17
                """
        );

        februaryReadings.put(
                17,
                "LEVITICUS 4:1-6:30"
        );

        februaryReadings.put(
                18,
                "LEVITICUS 7:1-8:36"
        );

        februaryReadings.put(
                19,
                "LEVITICUS 9:1-11:47"
        );

        februaryReadings.put(
                20,
                "LEVITICUS 12:1-14:32"
        );

        februaryReadings.put(
                21,
                "LEVITICUS 14:33-16:34"
        );

        februaryReadings.put(
                22,
                "LEVITICUS 17:1-19:37"
        );

        februaryReadings.put(
                23,
                "LEVITICUS 20:1-22:33"
        );

        februaryReadings.put(
                24,
                "LEVITICUS 23:1-25:23"
        );

        februaryReadings.put(
                25,
                "LEVITICUS 25:24-26:46"
        );

        februaryReadings.put(
                26,
                """
                LEVITICUS 27:1-34
                NUMBERS 1:1-54
                """
        );

        februaryReadings.put(
                27,
                "NUMBERS 2:1-3:51"
        );

        februaryReadings.put(
                28,
                "NUMBERS 4:1-5:31"
        );
    }
public static Map<Integer, String> getReadings() {

    February month = new February();
    month.loadFebruaryReadings();

    return new LinkedHashMap<>(
            month.februaryReadings
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
