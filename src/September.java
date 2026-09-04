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

public class September extends Application {

    private final Map<Integer, String> septemberReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadSeptemberReadings();

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
                new Label("September");

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
         * SEPTEMBER READING PLAN
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
         * SEPTEMBER DAYS 1 - 30
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
                new Label("September");

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
                septemberReadings.get(day);

        selectedDateLabel.setText(
                "September " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * SEPTEMBER
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadSeptemberReadings() {

        /*
         * SEPTEMBER 1
         */

        septemberReadings.put(
                1,
                """
                EZEKIEL 32:17-33:20
                JEREMIAH 52:28-30
                PSALM 137:1-9
                """
        );

        /*
         * SEPTEMBER 2
         */

        septemberReadings.put(
                2,
                "DANIEL 4:1-37"
        );

        /*
         * SEPTEMBER 3
         */

        septemberReadings.put(
                3,
                "EZEKIEL 40:1-37"
        );

        /*
         * SEPTEMBER 4
         */

        septemberReadings.put(
                4,
                "EZEKIEL 40:38-43:27"
        );

        /*
         * SEPTEMBER 5
         */

        septemberReadings.put(
                5,
                "EZEKIEL 44:1-46:24"
        );

        /*
         * SEPTEMBER 6
         */

        septemberReadings.put(
                6,
                """
                EZEKIEL 47:1-48:35
                EZEKIEL 29:17-30:19
                2 KINGS 25:27-30
                JEREMIAH 52:31-34
                """
        );

        /*
         * SEPTEMBER 7
         */

        septemberReadings.put(
                7,
                """
                DANIEL 7:1-8:27
                DANIEL 5:1-31
                """
        );

        /*
         * SEPTEMBER 8
         */

        septemberReadings.put(
                8,
                """
                DANIEL 6:1-28
                DANIEL 9:1-27
                2 CHRONICLES 36:22-23
                EZRA 1:1-11
                """
        );

        /*
         * SEPTEMBER 9
         */

        septemberReadings.put(
                9,
                """
                EZRA 2:1-4:5
                PSALM 126
                PSALM 147
                """
        );

        /*
         * SEPTEMBER 10
         */

        septemberReadings.put(
                10,
                """
                DANIEL 10:1-12:13
                EZRA 4:24-5:1
                HAGGAI 1:1-15
                """
        );

        /*
         * SEPTEMBER 11
         */

        septemberReadings.put(
                11,
                """
                HAGGAI 2:1-9
                ZECHARIAH 1:1-6
                HAGGAI 2:10-19
                EZRA 5:2
                HAGGAI 2:20-23
                ZECHARIAH 1:7-5:11
                """
        );

        /*
         * SEPTEMBER 12
         */

        septemberReadings.put(
                12,
                """
                ZECHARIAH 6:1-15
                EZRA 5:3-6:14
                ZECHARIAH 7:1-8:23
                """
        );

        /*
         * SEPTEMBER 13
         */

        septemberReadings.put(
                13,
                "ZECHARIAH 9:1-14:21"
        );

        /*
         * SEPTEMBER 14
         */

        septemberReadings.put(
                14,
                """
                EZRA 6:14-22
                EZRA 4:6
                ESTHER 1:1-4:17
                """
        );

        /*
         * SEPTEMBER 15
         */

        septemberReadings.put(
                15,
                "ESTHER 5:1-10:3"
        );

        /*
         * SEPTEMBER 16
         */

        septemberReadings.put(
                16,
                """
                EZRA 4:7-23
                EZRA 7:1-8:36
                """
        );

        /*
         * SEPTEMBER 17
         */

        septemberReadings.put(
                17,
                """
                EZRA 9:1-10:44
                NEHEMIAH 1:1-2:20
                """
        );

        /*
         * SEPTEMBER 18
         */

        septemberReadings.put(
                18,
                """
                NEHEMIAH 3:1-5:19
                NEHEMIAH 6:1-7:3
                """
        );

        /*
         * SEPTEMBER 19
         */

        septemberReadings.put(
                19,
                "NEHEMIAH 7:4-8:12"
        );

        /*
         * SEPTEMBER 20
         */

        septemberReadings.put(
                20,
                "NEHEMIAH 8:13-9:6"
        );

        /*
         * SEPTEMBER 21
         */

        septemberReadings.put(
                21,
                "NEHEMIAH 9:7-13:31"
        );

        /*
         * SEPTEMBER 22
         */

        septemberReadings.put(
                22,
                "1 CHRONICLES 1:1-9:44"
        );

        /*
         * SEPTEMBER 23
         */

        septemberReadings.put(
                23,
                """
                JOEL 1:1-3:21
                MALACHI 1:1-4:6
                """
        );

        /*
         * SEPTEMBER 24
         */

        septemberReadings.put(
                24,
                """
                MARK 1:1
                LUKE 1:1-4
                JOHN 1:1-18
                LUKE 1:5-80
                MATTHEW 1:18-25
                """
        );

        /*
         * SEPTEMBER 25
         */

        septemberReadings.put(
                25,
                """
                MATTHEW 1:1-17
                LUKE 3:23-38
                LUKE 2:1-38
                """
        );

        /*
         * SEPTEMBER 26
         */

        septemberReadings.put(
                26,
                """
                MATTHEW 2:1-23
                LUKE 2:39-52
                MARK 1:2-8
                MATTHEW 3:1-12
                LUKE 3:1-18
                MARK 1:9-11
                MATTHEW 3:13-17
                JOHN 1:19-28
                LUKE 3:21-22
                """
        );

        /*
         * SEPTEMBER 27
         */

        septemberReadings.put(
                27,
                """
                MARK 1:12-13
                MATTHEW 4:1-11
                LUKE 4:1-13
                """
        );

        /*
         * SEPTEMBER 28
         */

        septemberReadings.put(
                28,
                """
                JOHN 1:29-3:36
                LUKE 3:19-20
                """
        );

        /*
         * SEPTEMBER 29
         */

        septemberReadings.put(
                29,
                """
                MARK 1:14
                MATTHEW 4:12
                JOHN 4:1-42
                MATTHEW 4:13-17
                MARK 1:15
                LUKE 4:14-16
                JOHN 4:43-54
                MARK 1:16-20
                MATTHEW 4:18-22
                LUKE 5:1-11
                """
        );

        /*
         * SEPTEMBER 30
         */

        septemberReadings.put(
                30,
                """
                MARK 1:21-28
                LUKE 4:31-37
                MARK 1:29-34
                MATTHEW 8:14-17
                LUKE 4:38-41
                MARK 1:35-39
                LUKE 4:42-44
                MATTHEW 4:23-25
                MARK 1:40-45
                MATTHEW 8:1-4
                LUKE 5:12-16
                MARK 2:1-12
                MATTHEW 9:1-8
                LUKE 5:17-26
                MARK 2:13-17
                MATTHEW 9:9-13
                LUKE 5:27-32
                MARK 2:18-22
                MATTHEW 9:14-17
                LUKE 5:33-39
                """
        );
    }
public static Map<Integer, String> getReadings() {

    September month = new September();
    month.loadSeptemberReadings();

    return new LinkedHashMap<>(
            month.septemberReadings
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
