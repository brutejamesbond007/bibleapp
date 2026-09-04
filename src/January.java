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

public class January extends Application {

    private final Map<Integer, String> januaryReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadJanuaryReadings();

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
                new Label("January");

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
         * JANUARY CALENDAR
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
         * JANUARY 2026 CALENDAR
         *
         * January 1, 2026 = Thursday
         *
         * Sunday    = 0
         * Monday    = 1
         * Tuesday   = 2
         * Wednesday = 3
         * Thursday  = 4
         * Friday    = 5
         * Saturday  = 6
         * ============================================================
         */

        int column = 4;
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
                new Label("January");

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


        /*
         * START READING BUTTON
         *
         * For now this selects January 1.
         *
         * Later we will make this actually
         * open the Bible Gateway reading page.
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


        /*
         * ADD BOTTOM COMPONENTS
         */

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
         * CREATE WINDOW
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
     * CREATE A CALENDAR DAY BUTTON
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
     * SELECT A DAY
     * ================================================================
     */

    private void selectDay(
            int day
    ) {

        String reading =
                januaryReadings.get(day);

        selectedDateLabel.setText(
                "January "
                        + day
                        
        );

        selectedReadingLabel.setText(
                reading
        );
    }


    /*
     * ================================================================
     * JANUARY 2026
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadJanuaryReadings() {


        /*
         * JANUARY 1
         */

        januaryReadings.put(
                1,
                "GENESIS 1:1-3:24"
        );


        /*
         * JANUARY 2
         */

        januaryReadings.put(
                2,
                """
                GENESIS 4:1-5:32
                1 CHRONICLES 1:1-4
                GENESIS 6:1-22
                """
        );


        /*
         * JANUARY 3
         */

        januaryReadings.put(
                3,
                """
                GENESIS 7:1-10:5
                1 CHRONICLES 1:5-7
                GENESIS 10:6-20
                1 CHRONICLES 1:8-16
                GENESIS 10:21-30
                1 CHRONICLES 1:17-23
                GENESIS 10:31-32
                """
        );


        /*
         * JANUARY 4
         */

        januaryReadings.put(
                4,
                """
                GENESIS 11:1-26
                1 CHRONICLES 1:24-27
                GENESIS 11:27-31
                GENESIS 12:1-14:24
                """
        );


        /*
         * JANUARY 5
         */

        januaryReadings.put(
                5,
                "GENESIS 15:1-17:27"
        );


        /*
         * JANUARY 6
         */

        januaryReadings.put(
                6,
                "GENESIS 18:1-21:7"
        );


        /*
         * JANUARY 7
         */

        januaryReadings.put(
                7,
                """
                GENESIS 21:8-23:20
                GENESIS 11:32
                GENESIS 24:1-67
                """
        );


        /*
         * JANUARY 8
         */

        januaryReadings.put(
                8,
                """
                GENESIS 25:1-4
                1 CHRONICLES 1:32-33
                GENESIS 25:5-6
                GENESIS 25:12-18
                1 CHRONICLES 1:28-31
                1 CHRONICLES 1:34
                GENESIS 25:19-26
                GENESIS 25:7-11
                """
        );


        /*
         * JANUARY 9
         */

        januaryReadings.put(
                9,
                "GENESIS 25:27-28:5"
        );


        /*
         * JANUARY 10
         */

        januaryReadings.put(
                10,
                "GENESIS 28:6-30:24"
        );


        /*
         * JANUARY 11
         */

        januaryReadings.put(
                11,
                "GENESIS 30:25-31:55"
        );


        /*
         * JANUARY 12
         */

        januaryReadings.put(
                12,
                "GENESIS 32:1-35:27"
        );


        /*
         * JANUARY 13
         */

        januaryReadings.put(
                13,
                """
                GENESIS 36:1-19
                1 CHRONICLES 1:35-37
                GENESIS 36:20-30
                1 CHRONICLES 1:38-42
                GENESIS 36:31-43
                1 CHRONICLES 1:43-2:2
                """
        );


        /*
         * JANUARY 14
         */

        januaryReadings.put(
                14,
                """
                GENESIS 37:1-38:30
                1 CHRONICLES 2:3-6
                1 CHRONICLES 2:8
                GENESIS 39:1-23
                """
        );


        /*
         * JANUARY 15
         */

        januaryReadings.put(
                15,
                """
                GENESIS 40:1-23
                GENESIS 35:28-29
                GENESIS 41:1-57
                """
        );


        /*
         * JANUARY 16
         */

        januaryReadings.put(
                16,
                "GENESIS 42:1-45:15"
        );


        /*
         * JANUARY 17
         */

        januaryReadings.put(
                17,
                "GENESIS 45:16-47:27"
        );


        /*
         * JANUARY 18
         */

        januaryReadings.put(
                18,
                "GENESIS 47:28-50:26"
        );


        /*
         * JANUARY 19
         */

        januaryReadings.put(
                19,
                "JOB 1:1-4:21"
        );


        /*
         * JANUARY 20
         */

        januaryReadings.put(
                20,
                "JOB 5:1-7:21"
        );


        /*
         * JANUARY 21
         */

        januaryReadings.put(
                21,
                "JOB 8:1-11:20"
        );


        /*
         * JANUARY 22
         */

        januaryReadings.put(
                22,
                "JOB 12:1-14:22"
        );


        /*
         * JANUARY 23
         */

        januaryReadings.put(
                23,
                "JOB 15:1-18:21"
        );


        /*
         * JANUARY 24
         */

        januaryReadings.put(
                24,
                "JOB 19:1-21:34"
        );


        /*
         * JANUARY 25
         */

        januaryReadings.put(
                25,
                "JOB 22:1-25:6"
        );


        /*
         * JANUARY 26
         */

        januaryReadings.put(
                26,
                "JOB 26:1-29:25"
        );


        /*
         * JANUARY 27
         */

        januaryReadings.put(
                27,
                "JOB 30:1-31:40"
        );


        /*
         * JANUARY 28
         */

        januaryReadings.put(
                28,
                "JOB 32:1-34:37"
        );


        /*
         * JANUARY 29
         */

        januaryReadings.put(
                29,
                "JOB 35:1-37:24"
        );


        /*
         * JANUARY 30
         */

        januaryReadings.put(
                30,
                "JOB 38:1-40:5"
        );


        /*
         * JANUARY 31
         */

        januaryReadings.put(
                31,
                "JOB 40:6-42:17"
        );
    }
public static Map<Integer, String> getReadings() {

    January month = new January();
    month.loadJanuaryReadings();

    return new LinkedHashMap<>(
            month.januaryReadings
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
