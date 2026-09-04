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

public class December extends Application {

    private final Map<Integer, String> decemberReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadDecemberReadings();

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
                new Label("December");

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
         * DECEMBER READING PLAN
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
         * DECEMBER DAYS 1 - 31
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
                new Label("December");

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
                decemberReadings.get(day);

        selectedDateLabel.setText(
                "December " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * DECEMBER
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadDecemberReadings() {

        /*
         * DECEMBER 1
         */

        decemberReadings.put(
                1,
                "ROMANS 8:18-10:21"
        );

        /*
         * DECEMBER 2
         */

        decemberReadings.put(
                2,
                "ROMANS 11:1-14:23"
        );

        /*
         * DECEMBER 3
         */

        decemberReadings.put(
                3,
                """
                ROMANS 15:1-16:27
                ACTS 20:3b-12
                """
        );

        /*
         * DECEMBER 4
         */

        decemberReadings.put(
                4,
                "ACTS 20:13-21:38"
        );

        /*
         * DECEMBER 5
         */

        decemberReadings.put(
                5,
                "ACTS 21:37-23:35"
        );

        /*
         * DECEMBER 6
         */

        decemberReadings.put(
                6,
                "ACTS 24:1-26:32"
        );

        /*
         * DECEMBER 7
         */

        decemberReadings.put(
                7,
                "ACTS 27:1-44"
        );

        /*
         * DECEMBER 8
         */

        decemberReadings.put(
                8,
                """
                ACTS 28:1-31
                PHILEMON 1:1-25
                """
        );

        /*
         * DECEMBER 9
         */

        decemberReadings.put(
                9,
                "COLOSSIANS 1:1-23"
        );

        /*
         * DECEMBER 10
         */

        decemberReadings.put(
                10,
                "COLOSSIANS 1:24-4:18"
        );

        /*
         * DECEMBER 11
         */

        decemberReadings.put(
                11,
                "EPHESIANS 1:1-2:22"
        );

        /*
         * DECEMBER 12
         */

        decemberReadings.put(
                12,
                "EPHESIANS 3:1-5:14"
        );

        /*
         * DECEMBER 13
         */

        decemberReadings.put(
                13,
                "EPHESIANS 5:15-6:24"
        );

        /*
         * DECEMBER 14
         */

        decemberReadings.put(
                14,
                "PHILIPPIANS 1:1-2:11"
        );

        /*
         * DECEMBER 15
         */

        decemberReadings.put(
                15,
                "PHILIPPIANS 2:12-4:23"
        );

        /*
         * DECEMBER 16
         */

        decemberReadings.put(
                16,
                "1 TIMOTHY 1:1-2:15"
        );

        /*
         * DECEMBER 17
         */

        decemberReadings.put(
                17,
                "1 TIMOTHY 3:1-6:10"
        );

        /*
         * DECEMBER 18
         */

        decemberReadings.put(
                18,
                """
                1 TIMOTHY 6:11-21
                TITUS 1:1-3:15
                2 TIMOTHY 1:1-18
                """
        );

        /*
         * DECEMBER 19
         */

        decemberReadings.put(
                19,
                "2 TIMOTHY 2:1-4:18"
        );

        /*
         * DECEMBER 20
         */

        decemberReadings.put(
                20,
                """
                2 TIMOTHY 4:19-22
                1 PETER 1:1-2:3
                """
        );

        /*
         * DECEMBER 21
         */

        decemberReadings.put(
                21,
                "1 PETER 2:4-5:11"
        );

        /*
         * DECEMBER 22
         */

        decemberReadings.put(
                22,
                """
                1 PETER 5:12-14
                2 PETER 1:1-3:18
                HEBREWS 1:1-4:13
                """
        );

        /*
         * DECEMBER 23
         */

        decemberReadings.put(
                23,
                "HEBREWS 4:14-7:28"
        );

        /*
         * DECEMBER 24
         */

        decemberReadings.put(
                24,
                "HEBREWS 8:1-10:39"
        );

        /*
         * DECEMBER 25
         */

        decemberReadings.put(
                25,
                "HEBREWS 11:1-13:25"
        );

        /*
         * DECEMBER 26
         */

        decemberReadings.put(
                26,
                """
                JUDE 1:1-25
                1 JOHN 1:1-5:21
                """
        );

        /*
         * DECEMBER 27
         */

        decemberReadings.put(
                27,
                """
                2 JOHN 1:1-13
                3 JOHN 1:1-15
                REVELATION 1:1-5:14
                """
        );

        /*
         * DECEMBER 28
         */

        decemberReadings.put(
                28,
                "REVELATION 6:1-10:11"
        );

        /*
         * DECEMBER 29
         */

        decemberReadings.put(
                29,
                "REVELATION 11:1-14:20"
        );

        /*
         * DECEMBER 30
         */

        decemberReadings.put(
                30,
                "REVELATION 15:1-18:24"
        );

        /*
         * DECEMBER 31
         */

        decemberReadings.put(
                31,
                "REVELATION 19:1-22:21"
        );
    }
public static Map<Integer, String> getReadings() {

    December month = new December();
    month.loadDecemberReadings();

    return new LinkedHashMap<>(
            month.decemberReadings
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
