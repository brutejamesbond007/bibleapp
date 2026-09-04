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

public class May extends Application {

    private final Map<Integer, String> mayReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadMayReadings();

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
                new Label("May");

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
         * MAY READING PLAN
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
         * MAY DAYS 1 - 31
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

        selectedDateLabel =
                new Label("May");

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
                mayReadings.get(day);

        selectedDateLabel.setText(
                "May " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * MAY
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadMayReadings() {

        /*
         * MAY 1
         */

        mayReadings.put(
                1,
                """
                2 SAMUEL 22:1-51
                PSALM 18
                """
        );

        /*
         * MAY 2
         */

        mayReadings.put(
                2,
                """
                2 SAMUEL 24:1-9
                1 CHRONICLES 21:1-6
                2 SAMUEL 24:10-17
                1 CHRONICLES 21:7-17
                2 SAMUEL 24:18-25
                1 CHRONICLES 21:18-22:19
                """
        );

        /*
         * MAY 3
         */

        mayReadings.put(
                3,
                "1 CHRONICLES 23:1-25:31"
        );

        /*
         * MAY 4
         */

        mayReadings.put(
                4,
                "1 CHRONICLES 26:1-28:21"
        );

        /*
         * MAY 5
         */

        mayReadings.put(
                5,
                """
                1 CHRONICLES 29:1-22
                1 KINGS 1:1-53
                """
        );

        /*
         * MAY 6
         */

        mayReadings.put(
                6,
                """
                1 KINGS 2:1-9
                2 SAMUEL 23:1-7
                1 KINGS 2:10-12
                1 CHRONICLES 29:26-30
                PSALM 4-6
                PSALM 8-9
                PSALM 11
                """
        );

        /*
         * MAY 7
         */

        mayReadings.put(
                7,
                """
                PSALM 12-17
                PSALM 19-21
                """
        );

        /*
         * MAY 8
         */

        mayReadings.put(
                8,
                "PSALM 22-26"
        );

        /*
         * MAY 9
         */

        mayReadings.put(
                9,
                "PSALM 27-32"
        );

        /*
         * MAY 10
         */

        mayReadings.put(
                10,
                "PSALM 35-38"
        );

        /*
         * MAY 11
         */

        mayReadings.put(
                11,
                """
                PSALM 39-41
                PSALM 53
                PSALM 55
                PSALM 58
                """
        );

        /*
         * MAY 12
         */

        mayReadings.put(
                12,
                """
                PSALM 61-62
                PSALM 64-65
                """
        );

        /*
         * MAY 13
         */

        mayReadings.put(
                13,
                """
                PSALM 68-70
                PSALM 86
                PSALM 101
                """
        );

        /*
         * MAY 14
         */

        mayReadings.put(
                14,
                """
                PSALM 103
                PSALM 108-110
                PSALM 122
                PSALM 124
                """
        );

        /*
         * MAY 15
         */

        mayReadings.put(
                15,
                """
                PSALM 131
                PSALM 133
                PSALM 138-141
                PSALM 143
                """
        );

        /*
         * MAY 16
         */

        mayReadings.put(
                16,
                """
                PSALM 144-145
                PSALM 88-89
                """
        );

        /*
         * MAY 17
         */

        mayReadings.put(
                17,
                """
                PSALM 50
                PSALM 73-74
                """
        );

        /*
         * MAY 18
         */

        mayReadings.put(
                18,
                "PSALM 75-78"
        );

        /*
         * MAY 19
         */

        mayReadings.put(
                19,
                """
                PSALM 79-82
                PSALM 83
                """
        );

        /*
         * MAY 20
         */

        mayReadings.put(
                20,
                """
                1 CHRONICLES 29:23-25
                2 CHRONICLES 1:1
                1 KINGS 2:13-3:4
                2 CHRONICLES 1:2-6
                1 KINGS 3:5-15
                2 CHRONICLES 1:7-13
                """
        );

        /*
         * MAY 21
         */

        mayReadings.put(
                21,
                """
                1 KINGS 3:16-28
                1 KINGS 5:1-18
                2 CHRONICLES 2:1-18
                1 KINGS 6:1-13
                2 CHRONICLES 3:1-14
                1 KINGS 6:14-38
                """
        );

        /*
         * MAY 22
         */

        mayReadings.put(
                22,
                """
                1 KINGS 7:1-51
                2 CHRONICLES 3:15-4:22
                """
        );

        /*
         * MAY 23
         */

        mayReadings.put(
                23,
                """
                1 KINGS 8:1-11
                2 CHRONICLES 5:1-14
                1 KINGS 8:12-21
                2 CHRONICLES 6:1-11
                1 KINGS 8:22-53
                2 CHRONICLES 6:12-42
                """
        );

        /*
         * MAY 24
         */

        mayReadings.put(
                24,
                """
                1 KINGS 8:54-66
                2 CHRONICLES 7:1-10
                1 KINGS 9:1-9
                2 CHRONICLES 7:11-22
                1 KINGS 9:10-14
                """
        );

        /*
         * MAY 25
         */

        mayReadings.put(
                25,
                """
                2 CHRONICLES 8:1-18
                1 KINGS 9:15-10:13
                2 CHRONICLES 9:1-12
                1 KINGS 10:14-29
                2 CHRONICLES 9:13-28
                """
        );

        /*
         * MAY 26
         */

        mayReadings.put(
                26,
                """
                1 KINGS 4:1-34
                2 CHRONICLES 1:14-17
                PSALM 72
                PSALM 127
                """
        );

        /*
         * MAY 27
         */

        mayReadings.put(
                27,
                "PROVERBS 1:1-4:27"
        );

        /*
         * MAY 28
         */

        mayReadings.put(
                28,
                "PROVERBS 5:1-7:27"
        );

        /*
         * MAY 29
         */

        mayReadings.put(
                29,
                "PROVERBS 8:1-10:32"
        );

        /*
         * MAY 30
         */

        mayReadings.put(
                30,
                "PROVERBS 11:1-13:25"
        );

        /*
         * MAY 31
         */

        mayReadings.put(
                31,
                "PROVERBS 14:1-16:33"
        );
    }
public static Map<Integer, String> getReadings() {

    May month = new May();
    month.loadMayReadings();

    return new LinkedHashMap<>(
            month.mayReadings
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
