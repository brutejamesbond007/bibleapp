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

public class October extends Application {

    private final Map<Integer, String> octoberReadings =
            new LinkedHashMap<>();

    private Label selectedDateLabel;
    private Label selectedReadingLabel;

    @Override
    public void start(Stage stage) {

        loadOctoberReadings();

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
                new Label("October");

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
         * OCTOBER READING PLAN
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
         * OCTOBER DAYS 1 - 31
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
                new Label("October");

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
                octoberReadings.get(day);

        selectedDateLabel.setText(
                "October " + day
        );

        selectedReadingLabel.setText(
                reading
        );
    }

    /*
     * ================================================================
     * OCTOBER
     * CHRONOLOGICAL READING PLAN
     * ================================================================
     */

    private void loadOctoberReadings() {

        /*
         * OCTOBER 1
         */

        octoberReadings.put(
                1,
                """
                JOHN 5:1-47
                MARK 2:23-28
                MATTHEW 12:1-8
                LUKE 6:1-5
                MARK 3:1-6
                MATTHEW 12:9-14
                LUKE 6:6-11
                MATTHEW 12:15-21
                MARK 3:7-12
                LUKE 6:17-19
                """
        );

        /*
         * OCTOBER 2
         */

        octoberReadings.put(
                2,
                """
                MARK 3:13-19
                LUKE 6:12-16
                MATTHEW 5:1-12
                LUKE 6:20-26
                MATTHEW 5:13-30
                """
        );

        /*
         * OCTOBER 3
         */

        octoberReadings.put(
                3,
                """
                MATTHEW 5:31-48
                LUKE 6:27-36
                MATTHEW 6:1-7:6
                LUKE 6:37-42
                MATTHEW 7:7-29
                LUKE 6:43-49
                """
        );

        /*
         * OCTOBER 4
         */

        octoberReadings.put(
                4,
                """
                MATTHEW 8:5-13
                LUKE 7:1-17
                MATTHEW 11:1-19
                LUKE 7:18-35
                MATTHEW 11:20-30
                LUKE 7:36-50
                """
        );

        /*
         * OCTOBER 5
         */

        octoberReadings.put(
                5,
                """
                LUKE 8:1-3
                MATTHEW 12:22-37
                MARK 3:20-30
                LUKE 11:14-23
                MATTHEW 12:38-45
                LUKE 11:24-32
                MARK 3:31-35
                MATTHEW 12:46-50
                LUKE 8:19-21
                MARK 4:1-20
                MATTHEW 13:1-23
                LUKE 8:4-15
                MARK 4:21-25
                LUKE 8:16-18
                """
        );

        /*
         * OCTOBER 6
         */

        octoberReadings.put(
                6,
                """
                MARK 4:26-29
                MATTHEW 13:24-33
                MARK 4:30-32
                LUKE 13:18-21
                MATTHEW 13:34-35
                MARK 4:33-34
                MATTHEW 13:36-52
                MATTHEW 8:23-27
                MARK 4:35-41
                LUKE 8:22-25
                """
        );

        /*
         * OCTOBER 7
         */

        octoberReadings.put(
                7,
                """
                MARK 5:1-20
                MATTHEW 8:28-34
                LUKE 8:26-39
                MARK 5:21-43
                MATTHEW 9:18-26
                LUKE 8:40-56
                """
        );

        /*
         * OCTOBER 8
         */

        octoberReadings.put(
                8,
                """
                MATTHEW 9:27-34
                MATTHEW 13:53-58
                MARK 6:1-6
                LUKE 4:16-30
                MATTHEW 9:35-10:15
                MARK 6:6-13
                LUKE 9:1-6
                """
        );

        /*
         * OCTOBER 9
         */

        octoberReadings.put(
                9,
                """
                MATTHEW 10:16-42
                MATTHEW 14:3-12
                MARK 6:17-29
                MATTHEW 14:1-2
                MARK 6:14-16
                LUKE 9:7-9
                MATTHEW 14:13-21
                MARK 6:30-44
                LUKE 9:10-17
                JOHN 6:1-15
                MATTHEW 14:22-33
                MARK 6:45-52
                JOHN 6:16-21
                MATTHEW 14:34-36
                MARK 6:53-56
                """
        );

        /*
         * OCTOBER 10
         */

        octoberReadings.put(
                10,
                """
                JOHN 6:22-71
                MARK 7:1-23
                MATTHEW 15:1-20
                """
        );

        /*
         * OCTOBER 11
         */

        octoberReadings.put(
                11,
                """
                MARK 7:24-30
                MATTHEW 15:21-28
                MARK 7:31-37
                MATTHEW 15:29-31
                MARK 8:1-10
                MATTHEW 15:32-16:4
                MARK 8:11-13
                MATTHEW 16:5-12
                MARK 8:14-21
                """
        );

        /*
         * OCTOBER 12
         */

        octoberReadings.put(
                12,
                """
                MARK 8:22-26
                MATTHEW 16:13-20
                MARK 8:27-30
                LUKE 9:18-21
                MATTHEW 16:21-28
                MARK 8:31-9:1
                LUKE 9:22-27
                MARK 9:2-13
                MATTHEW 17:1-13
                LUKE 9:28-36
                """
        );

        /*
         * OCTOBER 13
         */

        octoberReadings.put(
                13,
                """
                MARK 9:14-29
                MATTHEW 17:14-20
                LUKE 9:37-43
                MARK 9:30-32
                MATTHEW 17:22-23
                LUKE 9:43-45
                MATTHEW 17:24-27
                MARK 9:33-37
                MATTHEW 18:1-5
                LUKE 9:46-48
                MARK 9:38-41
                LUKE 9:49-50
                MARK 9:42-50
                MATTHEW 18:6-35
                """
        );

        /*
         * OCTOBER 14
         */

        octoberReadings.put(
                14,
                """
                JOHN 7:1-9
                MATTHEW 19:1-2
                MARK 10:1
                LUKE 9:51-56
                MATTHEW 8:18-22
                LUKE 9:57-62
                JOHN 7:10-8:20
                """
        );

        /*
         * OCTOBER 15
         */

        octoberReadings.put(
                15,
                """
                JOHN 8:21-59
                LUKE 10:1-11:13
                """
        );

        /*
         * OCTOBER 16
         */

        octoberReadings.put(
                16,
                "LUKE 11:33-12:34"
        );

        /*
         * OCTOBER 17
         */

        octoberReadings.put(
                17,
                """
                LUKE 12:35-13:17
                JOHN 9:1-41
                """
        );

        /*
         * OCTOBER 18
         */

        octoberReadings.put(
                18,
                """
                JOHN 10:1-42
                LUKE 13:22-30
                MATTHEW 23:37-39
                LUKE 13:31-35
                """
        );

        /*
         * OCTOBER 19
         */

        octoberReadings.put(
                19,
                """
                LUKE 14:1-17:10
                JOHN 11:1-37
                """
        );

        /*
         * OCTOBER 20
         */

        octoberReadings.put(
                20,
                """
                JOHN 11:38-57
                LUKE 17:11-18:8
                """
        );

        /*
         * OCTOBER 21
         */

        octoberReadings.put(
                21,
                """
                LUKE 18:9-14
                MARK 10:2-12
                MATTHEW 19:3-12
                MARK 10:13-16
                MATTHEW 19:13-15
                LUKE 18:15-17
                MARK 10:17-31
                MATTHEW 19:16-30
                LUKE 18:18-30
                """
        );

        /*
         * OCTOBER 22
         */

        octoberReadings.put(
                22,
                """
                MATTHEW 20:1-16
                MARK 10:32-34
                MATTHEW 20:17-19
                LUKE 18:31-34
                MARK 10:35-45
                MATTHEW 20:20-34
                MARK 10:46-52
                LUKE 18:35-19:27
                """
        );

        /*
         * OCTOBER 23
         */

        octoberReadings.put(
                23,
                """
                MARK 14:3-9
                MATTHEW 26:6-13
                JOHN 12:1-11
                MARK 11:1-11
                MATTHEW 21:1-11
                LUKE 19:28-40
                JOHN 12:12-19
                LUKE 19:41-44
                """
        );

        /*
         * OCTOBER 24
         */

        octoberReadings.put(
                24,
                """
                MATTHEW 21:12-17
                MARK 11:15-19
                LUKE 19:45-48
                JOHN 12:20-50
                MATTHEW 21:18-22
                MARK 11:12-14,20-25
                MATTHEW 21:23-27
                MARK 11:27-33
                LUKE 20:1-8
                MATTHEW 21:28-32
                """
        );

        /*
         * OCTOBER 25
         */

        octoberReadings.put(
                25,
                """
                MARK 12:1-12
                MATTHEW 21:33-46
                LUKE 20:9-19
                MATTHEW 22:1-14
                MARK 12:13-17
                MATTHEW 22:15-22
                LUKE 20:20-26
                MARK 12:18-27
                MATTHEW 22:23-33
                LUKE 20:27-40
                """
        );

        /*
         * OCTOBER 26
         */

        octoberReadings.put(
                26,
                """
                MARK 12:28-34
                MATTHEW 22:34-40
                MARK 12:35-37
                MATTHEW 22:41-46
                LUKE 20:41-44
                MARK 12:38-40
                MATTHEW 23:1-12
                LUKE 20:45-47
                MATTHEW 23:13-36
                MARK 12:41-44
                LUKE 21:1-4
                """
        );

        /*
         * OCTOBER 27
         */

        octoberReadings.put(
                27,
                """
                MARK 13:1-37
                MATTHEW 24:1-51
                LUKE 21:5-38
                """
        );

        /*
         * OCTOBER 28
         */

        octoberReadings.put(
                28,
                "MATTHEW 25:1-46"
        );

        /*
         * OCTOBER 29
         */

        octoberReadings.put(
                29,
                """
                MARK 14:1-2
                MATTHEW 26:1-5
                LUKE 22:1-2
                MARK 14:10-11
                MATTHEW 26:14-16
                LUKE 22:3-6
                MARK 14:12-16
                MATTHEW 26:17-19
                LUKE 22:7-13
                JOHN 13:1-20
                MARK 14:17-26
                MATTHEW 26:20-30
                LUKE 22:14-30
                JOHN 13:21-30
                """
        );

        /*
         * OCTOBER 30
         */

        octoberReadings.put(
                30,
                """
                JOHN 13:31-38
                MARK 14:27-31
                MATTHEW 26:31-35
                LUKE 22:31-38
                """
        );

        /*
         * OCTOBER 31
         */

        octoberReadings.put(
                31,
                "JOHN 14:1-17:26"
        );
    }
public static Map<Integer, String> getReadings() {

    October month = new October();
    month.loadOctoberReadings();

    return new LinkedHashMap<>(
            month.octoberReadings
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
