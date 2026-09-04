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

public class July extends Application {

    private final Map<Integer, String> julyReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadJulyReadings();

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
                new Label("July");

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
         * JULY READING PLAN
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
         * JULY DAYS 1 - 31
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
                new Label("July");

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
                julyReadings.get(day);

        selectedDateLabel.setText(
                "July " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * JULY
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadJulyReadings() {

        /*
         * JULY 1
         */

        julyReadings.put(
                1,
                "2 CHRONICLES 29:3-31:21"
        );

        /*
         * JULY 2
         */

        julyReadings.put(
                2,
                "PROVERBS 25:1-29:27"
        );

        /*
         * JULY 3
         */

        julyReadings.put(
                3,
                "PROVERBS 30:1-31:31"
        );

        /*
         * JULY 4
         */

        julyReadings.put(
                4,
                """
                PSALM 42
                PSALM 43
                PSALM 44
                PSALM 45
                PSALM 46
                """
        );

        /*
         * JULY 5
         */

        julyReadings.put(
                5,
                """
                PSALM 47
                PSALM 48
                PSALM 49
                PSALM 84
                PSALM 85
                PSALM 87
                """
        );

        /*
         * JULY 6
         */

        julyReadings.put(
                6,
                """
                PSALM 1-2
                PSALM 10
                PSALM 33
                PSALM 66
                PSALM 67
                PSALM 71
                PSALM 91
                """
        );

        /*
         * JULY 7
         */

        julyReadings.put(
                7,
                """
                PSALM 92
                PSALM 93
                PSALM 94
                PSALM 95
                PSALM 96
                PSALM 97
                """
        );

        /*
         * JULY 8
         */

        julyReadings.put(
                8,
                """
                PSALM 98
                PSALM 99
                PSALM 100
                PSALM 102
                PSALM 104
                """
        );

        /*
         * JULY 9
         */

        julyReadings.put(
                9,
                """
                PSALM 105
                PSALM 106
                """
        );

        /*
         * JULY 10
         */

        julyReadings.put(
                10,
                """
                PSALM 107
                PSALM 111
                PSALM 112
                PSALM 113
                PSALM 114
                """
        );

        /*
         * JULY 11
         */

        julyReadings.put(
                11,
                """
                PSALM 115
                PSALM 116
                PSALM 117
                PSALM 118
                """
        );

        /*
         * JULY 12
         */

        julyReadings.put(
                12,
                "PSALM 119"
        );

        /*
         * JULY 13
         */

        julyReadings.put(
                13,
                """
                PSALM 120
                PSALM 121
                PSALM 123
                PSALM 125
                """
        );

        /*
         * JULY 14
         */

        julyReadings.put(
                14,
                """
                PSALM 128
                PSALM 129
                PSALM 130
                PSALM 132
                PSALM 134
                PSALM 135
                """
        );

        /*
         * JULY 15
         */

        julyReadings.put(
                15,
                """
                PSALM 136
                PSALM 146
                PSALM 148
                PSALM 149
                PSALM 150
                """
        );

        /*
         * JULY 16
         */

        julyReadings.put(
                16,
                "ISAIAH 18:1-23:18"
        );

        /*
         * JULY 17
         */

        julyReadings.put(
                17,
                """
                ISAIAH 24:1-27:13
                ISAIAH 29:1-24
                """
        );

        /*
         * JULY 18
         */

        julyReadings.put(
                18,
                "ISAIAH 30:1-33:24"
        );

        /*
         * JULY 19
         */

        julyReadings.put(
                19,
                """
                ISAIAH 34:1-35:10
                MICAH 2:1-5:15
                """
        );

        /*
         * JULY 20
         */

        julyReadings.put(
                20,
                """
                MICAH 6:1-7:20
                2 CHRONICLES 32:1-8
                2 KINGS 18:13-18
                ISAIAH 36:1-3
                2 KINGS 18:19-37
                ISAIAH 36:4-22
                """
        );

        /*
         * JULY 21
         */

        julyReadings.put(
                21,
                """
                2 KINGS 19:1-19
                ISAIAH 37:1-20
                2 CHRONICLES 32:9-19
                2 KINGS 19:20-37
                ISAIAH 37:21-38
                2 CHRONICLES 32:20-23
                """
        );

        /*
         * JULY 22
         */

        julyReadings.put(
                22,
                """
                2 KINGS 20:1-11
                ISAIAH 38:1-8
                2 CHRONICLES 32:24-31
                ISAIAH 38:9-22
                2 KINGS 20:12-19
                ISAIAH 39:1-8
                2 KINGS 20:20-21
                2 CHRONICLES 32:32-33
                """
        );

        /*
         * JULY 23
         */

        julyReadings.put(
                23,
                "ISAIAH 40:1-44:5"
        );

        /*
         * JULY 24
         */

        julyReadings.put(
                24,
                "ISAIAH 44:6-48:11"
        );

        /*
         * JULY 25
         */

        julyReadings.put(
                25,
                "ISAIAH 48:12-52:12"
        );

        /*
         * JULY 26
         */

        julyReadings.put(
                26,
                "ISAIAH 52:13-57:21"
        );

        /*
         * JULY 27
         */

        julyReadings.put(
                27,
                "ISAIAH 58:1-63:14"
        );

        /*
         * JULY 28
         */

        julyReadings.put(
                28,
                "ISAIAH 63:15-66:24"
        );

        /*
         * JULY 29
         */

        julyReadings.put(
                29,
                """
                2 KINGS 21:1-9
                2 CHRONICLES 33:1-9
                2 KINGS 21:10-17
                2 CHRONICLES 33:10-19
                2 KINGS 21:18
                2 CHRONICLES 33:20
                2 KINGS 21:19-26
                2 CHRONICLES 33:21-25
                2 KINGS 22:1-2
                2 CHRONICLES 34:1-7
                JEREMIAH 1:1-2:22
                """
        );

        /*
         * JULY 30
         */

        julyReadings.put(
                30,
                "JEREMIAH 2:23-5:19"
        );

        /*
         * JULY 31
         */

        julyReadings.put(
                31,
                """
                JEREMIAH 5:20-6:30
                2 KINGS 22:3-20
                2 CHRONICLES 34:8-28
                """
        );
    }
public static Map<Integer, String> getReadings() {

    July month = new July();
    month.loadJulyReadings();

    return new LinkedHashMap<>(
            month.julyReadings
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
