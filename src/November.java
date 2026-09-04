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

public class November extends Application {

    private final Map<Integer, String> novemberReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadNovemberReadings();

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
                new Label("November");

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
         * NOVEMBER READING PLAN
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
         * NOVEMBER DAYS 1 - 30
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
                new Label("November");

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
                novemberReadings.get(day);

        selectedDateLabel.setText(
                "November " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * NOVEMBER
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadNovemberReadings() {

        /*
         * NOVEMBER 1
         */

        novemberReadings.put(
                1,
                """
                JOHN 18:1-2
                MARK 14:32-42
                MATTHEW 26:36-46
                LUKE 22:39-46
                MARK 14:43-52
                MATTHEW 26:47-56
                LUKE 22:47-53
                JOHN 18:3-24
                """
        );

        /*
         * NOVEMBER 2
         */

        novemberReadings.put(
                2,
                """
                MARK 14:53-65
                MATTHEW 26:57-68
                LUKE 22:54a,63-65
                MATTHEW 26:69-75
                MARK 14:66-72
                LUKE 22:54b-62
                JOHN 18:25-27
                MARK 15:1
                MATTHEW 27:1-2
                LUKE 22:66-71
                MATTHEW 27:3-10
                """
        );

        /*
         * NOVEMBER 3
         */

        novemberReadings.put(
                3,
                """
                MARK 15:2-5
                MATTHEW 27:11-14
                LUKE 23:1-7
                JOHN 18:28-37
                MARK 15:6-15
                MATTHEW 27:15-26
                LUKE 23:13-25
                JOHN 18:38-19:16
                MARK 15:16-20
                MATTHEW 27:27-31
                """
        );

        /*
         * NOVEMBER 4
         */

        novemberReadings.put(
                4,
                """
                MATTHEW 27:32
                MARK 15:21
                LUKE 23:26-31
                MATTHEW 27:33-44
                MARK 15:22-32
                LUKE 23:32-43
                JOHN 19:17-27
                MATTHEW 27:45-56
                MARK 15:33-41
                LUKE 23:44-49
                JOHN 19:28-37
                """
        );

        /*
         * NOVEMBER 5
         */

        novemberReadings.put(
                5,
                """
                MARK 15:42-47
                MATTHEW 27:57-61
                LUKE 23:50-56
                JOHN 19:38-42
                MATTHEW 27:62-66
                MARK 16:1-8
                MATTHEW 28:1-8
                LUKE 24:1-12
                MARK 16:9-11
                JOHN 20:1-18
                MATTHEW 28:9-15
                """
        );

        /*
         * NOVEMBER 6
         */

        novemberReadings.put(
                6,
                """
                LUKE 24:13-43
                MARK 16:12-13
                JOHN 20:19-23
                MARK 16:14
                JOHN 20:24-21:25
                MATTHEW 28:16-20
                MARK 16:15-18
                LUKE 24:44-49
                """
        );

        /*
         * NOVEMBER 7
         */

        novemberReadings.put(
                7,
                """
                MARK 16:19-20
                LUKE 24:50-53
                ACTS 1:1-2:47
                """
        );

        /*
         * NOVEMBER 8
         */

        novemberReadings.put(
                8,
                "ACTS 3:1-5:42"
        );

        /*
         * NOVEMBER 9
         */

        novemberReadings.put(
                9,
                "ACTS 6:1-8:1a"
        );

        /*
         * NOVEMBER 10
         */

        novemberReadings.put(
                10,
                "ACTS 8:1b-9:43"
        );

        /*
         * NOVEMBER 11
         */

        novemberReadings.put(
                11,
                "ACTS 10:1-11:30"
        );

        /*
         * NOVEMBER 12
         */

        novemberReadings.put(
                12,
                "ACTS 12:1-14:20"
        );

        /*
         * NOVEMBER 13
         */

        novemberReadings.put(
                13,
                """
                ACTS 14:21-28
                JAMES 1:1-3:18
                """
        );

        /*
         * NOVEMBER 14
         */

        novemberReadings.put(
                14,
                """
                JAMES 4:1-5:20
                GALATIANS 1:1-3:22
                """
        );

        /*
         * NOVEMBER 15
         */

        novemberReadings.put(
                15,
                """
                GALATIANS 3:23-6:18
                ACTS 15:1-21
                """
        );

        /*
         * NOVEMBER 16
         */

        novemberReadings.put(
                16,
                "ACTS 15:22-17:15"
        );

        /*
         * NOVEMBER 17
         */

        novemberReadings.put(
                17,
                """
                ACTS 17:16-18:11
                1 THESSALONIANS 1:1-5:28
                """
        );

        /*
         * NOVEMBER 18
         */

        novemberReadings.put(
                18,
                """
                2 THESSALONIANS 1:1-3:18
                ACTS 18:12-23
                """
        );

        /*
         * NOVEMBER 19
         */

        novemberReadings.put(
                19,
                """
                ACTS 18:24-19:20
                1 CORINTHIANS 1:1-3:23
                """
        );

        /*
         * NOVEMBER 20
         */

        novemberReadings.put(
                20,
                "1 CORINTHIANS 4:1-7:40"
        );

        /*
         * NOVEMBER 21
         */

        novemberReadings.put(
                21,
                "1 CORINTHIANS 8:1-11:1"
        );

        /*
         * NOVEMBER 22
         */

        novemberReadings.put(
                22,
                "1 CORINTHIANS 11:2-13:13"
        );

        /*
         * NOVEMBER 23
         */

        novemberReadings.put(
                23,
                "1 CORINTHIANS 14:1-15:58"
        );

        /*
         * NOVEMBER 24
         */

        novemberReadings.put(
                24,
                """
                1 CORINTHIANS 16:1-24
                ACTS 19:21-20:1-2a
                2 CORINTHIANS 1:1-2:4
                """
        );

        /*
         * NOVEMBER 25
         */

        novemberReadings.put(
                25,
                "2 CORINTHIANS 2:5-6:13"
        );

        /*
         * NOVEMBER 26
         */

        novemberReadings.put(
                26,
                "2 CORINTHIANS 6:14-10:18"
        );

        /*
         * NOVEMBER 27
         */

        novemberReadings.put(
                27,
                "2 CORINTHIANS 11:1-13:13"
        );

        /*
         * NOVEMBER 28
         */

        novemberReadings.put(
                28,
                "ROMANS 1:1-32"
        );

        /*
         * NOVEMBER 29
         */

        novemberReadings.put(
                29,
                "ROMANS 2:1-4:25"
        );

        /*
         * NOVEMBER 30
         */

        novemberReadings.put(
                30,
                "ROMANS 5:1-8:17"
        );
    }
public static Map<Integer, String> getReadings() {

    November month = new November();
    month.loadNovemberReadings();

    return new LinkedHashMap<>(
            month.novemberReadings
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
