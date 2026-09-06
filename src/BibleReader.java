import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BibleReader extends Application {

    private static final DateTimeFormatter NOTE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");

    private static final double READING_PLAN_WIDTH = 0.17;
    private static final double LEFT_NAVIGATION_DIVIDER = 0.48;
    private static final double JOURNAL_DIVIDER_POSITION = 0.76;
    private static final double COMPARISON_DIVIDER_POSITION = 0.50;
    private static final double RIGHT_INFO_DIVIDER_POSITION = 0.42;
    private static final double TIMELINE_DIVIDER_POSITION = 0.76;
    private static final String STUDY_BIBLE_FOLDER = ".";
    private static final String STUDY_BIBLE_FILENAME =
            "dokumen.pub_nlt-life-application-study-bible-third-edition-9781496441652-9781496433824.epub";

    private static final String READING_PLAN_1_ID = "reading_plan_1";
    private static final String READING_PLAN_2_ID = "reading_plan_2";
    private static final String READING_PLAN_3_ID = "reading_plan_3";

    private static final String READING_PLAN_1_NAME =
            "Reading Plan 1 - One Year Bible";
    private static final String READING_PLAN_2_NAME =
            "Reading Plan 2 - Chronological Version 1";
    private static final String READING_PLAN_3_NAME =
            "Reading Plan 3 - Current Chronological";

    private final Map<String, Map<Integer, String>> readingPlanOneMonths =
            new LinkedHashMap<>();
    private final Map<String, Map<Integer, String>> readingPlanTwoMonths =
            new LinkedHashMap<>();

    private ComboBox<String> readingPlanSelector;
    private String currentReadingPlanId = READING_PLAN_3_ID;

    private static final String SQLITE_DATABASE_URL =
            "jdbc:sqlite:data/bible-reader.db";

    private final List<ReadingDay> readingDays = new ArrayList<>();
    private final Map<TreeItem<String>, ReadingDay> treeReadingMap = new HashMap<>();
    private final Map<TreeItem<String>, String> chapterReferenceMap = new HashMap<>();
    private final Map<String, String> translations = new LinkedHashMap<>();
    private final Map<String, String> studyBookCodes = new LinkedHashMap<>();

    private BorderPane root;
    private BorderPane mainContentHolder;
    private VBox headerPanel;
    private VBox leftPanel;
    private VBox journalPanel;
    private VBox comparisonPane;

    private SplitPane readingPlanSplitPane;
    private SplitPane leftNavigationSplitPane;
    private SplitPane bibleJournalSplitPane;
    private SplitPane bibleComparisonSplitPane;
    private SplitPane contentTimelineSplitPane;

    private TreeView<String> bibleBooksTree;
    private TreeView<String> readingPlanTree;

    private Label dateLabel;
    private Label passageLabel;

    private TextField bibleSearchField;
    private Button bibleSearchButton;

    private WebView webView;
    private WebEngine webEngine;
    private ComboBox<String> translationSelector;

    private WebView comparisonWebView;
    private WebEngine comparisonWebEngine;
    private ComboBox<String> comparisonTranslationSelector;
    private HBox comparisonToolbar;
    private Button comparisonButton;
    private boolean comparisonVisible = false;

    private TextArea newNoteArea;
    private ListView<NoteEntry> noteHistoryList;
    private Label notesTitleLabel;
    private Label noteStatusLabel;
    private Button addEntryButton;
    private Button removeEntryButton;
    private CheckBox readingCompletedCheckBox;
    private boolean updatingCompletionCheckBox = false;

    private SplitPane rightInfoSplitPane;
    private TabPane topInfoTabs;
    private Tab bookIntroductionTab;
    private Tab personalityProfileTab;
    private Tab chartsTab;
    private TabPane rightSideTabs;

    private Label bookIntroductionTitleLabel;
    private Label bookIntroductionStatusLabel;
    private ComboBox<String> bookIntroductionSelector;
    private WebView bookIntroductionWebView;
    private WebEngine bookIntroductionWebEngine;
    private final Map<String, String> bookIntroductionEntries = new LinkedHashMap<>();
    private Label personalityProfileTitleLabel;
    private Label personalityProfileStatusLabel;
    private ComboBox<String> personalityProfileSelector;
    private WebView personalityProfileWebView;
    private WebEngine personalityProfileWebEngine;
    private final Map<String, String> personalityProfileEntries = new LinkedHashMap<>();

    private Label chartsTitleLabel;
    private Label chartsStatusLabel;
    private ComboBox<String> chartsSelector;
    private WebView chartsWebView;
    private WebEngine chartsWebEngine;
    private final Map<String, String> chartEntries = new LinkedHashMap<>();

    private Tab mapsTab;
    private Label mapsTitleLabel;
    private Label mapsStatusLabel;
    private ComboBox<String> mapsSelector;
    private WebView mapsWebView;
    private WebEngine mapsWebEngine;
    private final Map<String, String> mapEntries = new LinkedHashMap<>();

    private Tab originalLanguageTab;
    private Label originalLanguageTitleLabel;
    private Label originalLanguageStatusLabel;
    private ComboBox<String> originalLanguageSelector;
    private WebView originalLanguageWebView;
    private WebEngine originalLanguageWebEngine;

    private ComboBox<String> studyReferenceSelector;
    private Label studyNotesTitleLabel;
    private Label studyNotesStatusLabel;
    private WebView studyNotesWebView;
    private WebEngine studyNotesWebEngine;
    private File studyBibleEpubFile;

    // Constant, resizable bottom timeline panel.
    private VBox timelinePanel;
    private Label timelineTitleLabel;
    private Label timelineStatusLabel;
    private ComboBox<String> timelineSelector;
    private WebView timelineWebView;
    private WebEngine timelineWebEngine;
    private final Map<String, String> timelineEntries = new LinkedHashMap<>();

    private Button previousButton;
    private Button readingPlanButton;
    private Button nextButton;
    private Button toggleReadingPlanButton;
    private Button autoHideHeaderButton;
    private Button autoHideSidesButton;
    private Button autoHideBottomButton;

    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;
    private boolean databaseConfigLoaded = false;

    private int currentDayIndex = -1;
    private String currentStudyReference = null;
    private boolean readingPlanVisible = true;
    private double savedReadingPlanDividerPosition = READING_PLAN_WIDTH;
    private boolean autoHideHeaderEnabled = true;
    private boolean headerVisible = true;

    private boolean autoHideSidesEnabled = true;
    private boolean leftSideVisible = true;
    private boolean rightSideVisible = true;
    private double savedJournalDividerPosition = JOURNAL_DIVIDER_POSITION;

    private boolean autoHideBottomEnabled = true;
    private boolean bottomTimelineVisible = true;
    private double savedTimelineDividerPosition = TIMELINE_DIVIDER_POSITION;

    private final PauseTransition headerHideTimer =
            new PauseTransition(Duration.seconds(3));

    private final PauseTransition sidesHideTimer =
            new PauseTransition(Duration.seconds(3));

    private final PauseTransition bottomHideTimer =
            new PauseTransition(Duration.seconds(3));

    private static class ReadingDay {
        private final String month;
        private final int day;
        private final String reading;

        ReadingDay(String month, int day, String reading) {
            this.month = month;
            this.day = day;
            this.reading = reading;
        }

        String getDisplayDate() { return month + " " + day; }
        String getReading() { return reading; }
        String getMonth() { return month; }
        int getDay() { return day; }
    }

    private static class NoteEntry {
        private final int noteId;
        private final String noteText;
        private final Timestamp createdAt;

        NoteEntry(int noteId, String noteText, Timestamp createdAt) {
            this.noteId = noteId;
            this.noteText = noteText;
            this.createdAt = createdAt;
        }

        int getNoteId() { return noteId; }
        String getNoteText() { return noteText; }

        String getFormattedDate() {
            if (createdAt == null) return "";
            return createdAt.toLocalDateTime().format(NOTE_DATE_FORMAT);
        }
    }

    @Override
    public void start(Stage stage) {
        loadDatabaseConfig();
        loadReadingPlanOneData();
        loadReadingPlanTwoData();
        loadFullReadingPlan();
        createTranslations();
        createStudyBookCodes();

        root = new BorderPane();

        createHeader();
        createPrimaryBible();
        createComparisonBible();
        createJournalPanel();
        createReadingArea();
        createReadingPlanPanel();
        createTimelinePanel();
        createNavigationBar();

        studyBibleEpubFile = findStudyBibleEpub();
        initializeReadingProgressForCurrentYear();
        refreshReadingPlanCompletionMarks();
        showReadingPlanHome();

        Scene scene = new Scene(root, 1400, 850);
        stage.setTitle("Chronological Bible Reader Portable");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setResizable(true);

        configureHeaderAutoShow(scene);
        stage.show();

        Platform.runLater(() -> {
            if (!readingPlanSplitPane.getDividers().isEmpty()) {
                readingPlanSplitPane.setDividerPositions(savedReadingPlanDividerPosition);
            }
            if (!leftNavigationSplitPane.getDividers().isEmpty()) {
                leftNavigationSplitPane.setDividerPositions(LEFT_NAVIGATION_DIVIDER);
            }
            if (!bibleJournalSplitPane.getDividers().isEmpty()) {
                bibleJournalSplitPane.setDividerPositions(JOURNAL_DIVIDER_POSITION);
            }
            if (rightInfoSplitPane != null && !rightInfoSplitPane.getDividers().isEmpty()) {
                rightInfoSplitPane.setDividerPositions(RIGHT_INFO_DIVIDER_POSITION);
            }
            if (contentTimelineSplitPane != null && !contentTimelineSplitPane.getDividers().isEmpty()) {
                contentTimelineSplitPane.setDividerPositions(TIMELINE_DIVIDER_POSITION);
            }
        });
    }

    private void createTranslations() {
        translations.put("ESV - English Standard Version", "ESV");
        translations.put("NIV - New International Version", "NIV");
        translations.put("KJV - King James Version", "KJV");
        translations.put("NKJV - New King James Version", "NKJV");
        translations.put("NASB - New American Standard Bible", "NASB");
        translations.put("NASB 1995 - Legacy NASB", "NASB1995");
        translations.put("LSB - Legacy Standard Bible", "LSB");
        translations.put("NLT - New Living Translation", "NLT");
    }

    private void createStudyBookCodes() {
        studyBookCodes.put("Genesis", "01-Gen");
        studyBookCodes.put("Exodus", "02-Exod");
        studyBookCodes.put("Leviticus", "03-Lev");
        studyBookCodes.put("Numbers", "04-Num");
        studyBookCodes.put("Deuteronomy", "05-Deut");
        studyBookCodes.put("Joshua", "06-Josh");
        studyBookCodes.put("Judges", "07-Judg");
        studyBookCodes.put("Ruth", "08-Ruth");
        studyBookCodes.put("1 Samuel", "09-1Sam");
        studyBookCodes.put("2 Samuel", "10-2Sam");
        studyBookCodes.put("1 Kings", "11-1Kgs");
        studyBookCodes.put("2 Kings", "12-2Kgs");
        studyBookCodes.put("1 Chronicles", "13-1Chr");
        studyBookCodes.put("2 Chronicles", "14-2Chr");
        studyBookCodes.put("Ezra", "15-Ezra");
        studyBookCodes.put("Nehemiah", "16-Neh");
        studyBookCodes.put("Esther", "17-Esth");
        studyBookCodes.put("Job", "18-Job");
        studyBookCodes.put("Psalms", "19-Ps");
        studyBookCodes.put("Psalm", "19-Ps");
        studyBookCodes.put("Proverbs", "20-Pr");
        studyBookCodes.put("Proverb", "20-Pr");
        studyBookCodes.put("Ecclesiastes", "21-Eccl");
        studyBookCodes.put("Song of Solomon", "22-Song");
        studyBookCodes.put("Song of Songs", "22-Song");
        studyBookCodes.put("Isaiah", "23-Isa");
        studyBookCodes.put("Jeremiah", "24-Jer");
        studyBookCodes.put("Lamentations", "25-Lam");
        studyBookCodes.put("Ezekiel", "26-Ezek");
        studyBookCodes.put("Daniel", "27-Dan");
        studyBookCodes.put("Hosea", "28-Hos");
        studyBookCodes.put("Joel", "29-Joel");
        studyBookCodes.put("Amos", "30-Amos");
        studyBookCodes.put("Obadiah", "31-Obad");
        studyBookCodes.put("Jonah", "32-Jon");
        studyBookCodes.put("Micah", "33-Mic");
        studyBookCodes.put("Nahum", "34-Nah");
        studyBookCodes.put("Habakkuk", "35-Hab");
        studyBookCodes.put("Zephaniah", "36-Zeph");
        studyBookCodes.put("Haggai", "37-Hagg");
        studyBookCodes.put("Zechariah", "38-Zech");
        studyBookCodes.put("Malachi", "39-Mal");
        studyBookCodes.put("Matthew", "40-Matt");
        studyBookCodes.put("Mark", "41-Mark");
        studyBookCodes.put("Luke", "42-Luke");
        studyBookCodes.put("John", "43-John");
        studyBookCodes.put("Acts", "44-Acts");
        studyBookCodes.put("Romans", "45-Rom");
        studyBookCodes.put("1 Corinthians", "46-1Cor");
        studyBookCodes.put("2 Corinthians", "47-2Cor");
        studyBookCodes.put("Galatians", "48-Gal");
        studyBookCodes.put("Ephesians", "49-Eph");
        studyBookCodes.put("Philippians", "50-Phil");
        studyBookCodes.put("Colossians", "51-Col");
        studyBookCodes.put("1 Thessalonians", "52-1Thes");
        studyBookCodes.put("2 Thessalonians", "53-2Thes");
        studyBookCodes.put("1 Timothy", "54-1Tim");
        studyBookCodes.put("2 Timothy", "55-2Tim");
        studyBookCodes.put("Titus", "56-Titus");
        studyBookCodes.put("Philemon", "57-Phlm");
        studyBookCodes.put("Hebrews", "58-Heb");
        studyBookCodes.put("James", "59-Jas");
        studyBookCodes.put("1 Peter", "60-1Pet");
        studyBookCodes.put("2 Peter", "61-2Pet");
        studyBookCodes.put("1 John", "62-1Jn");
        studyBookCodes.put("2 John", "63-2Jn");
        studyBookCodes.put("3 John", "64-3Jn");
        studyBookCodes.put("Jude", "65-Jude");
        studyBookCodes.put("Revelation", "66-Rev");
    }

    private void createHeader() {
        Label title = new Label("Chronological Bible Reader");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 28));

        Label searchLabel = new Label("Search:");
        searchLabel.setFont(Font.font("Serif", FontWeight.BOLD, 15));

        bibleSearchField = new TextField();
        bibleSearchField.setPromptText("Example: John 3:16, Romans 8:28-30, Psalm 23");
        bibleSearchField.setPrefWidth(420);

        bibleSearchButton = new Button("Search");
        bibleSearchButton.setOnAction(event -> searchBible());
        bibleSearchField.setOnAction(event -> searchBible());

        HBox searchBar = new HBox(
                10,
                searchLabel,
                bibleSearchField,
                bibleSearchButton
        );
        searchBar.setAlignment(Pos.CENTER);

        /*
         * Verse of the Day now sits directly below the search bar.
         * It always displays the built-in public-domain KJV text.
         */
        DailyVerse verseOfTheDay =
                getVerseOfTheDay(LocalDate.now());

        Label verseOfDayLabel = new Label(
                "Random Verse — "
                        + verseOfTheDay.reference
                        + " (KJV):  “"
                        + verseOfTheDay.text
                        + "”"
        );
        verseOfDayLabel.setFont(Font.font("Serif", 14));
        verseOfDayLabel.setWrapText(true);
        verseOfDayLabel.setMaxWidth(1050);
        verseOfDayLabel.setAlignment(Pos.CENTER);

        Button openVerseButton =
                new Button("Open Verse");
        openVerseButton.setOnAction(event -> {
            bibleSearchField.setText(
                    verseOfTheDay.reference
            );
            searchBible();
        });

        HBox verseOfDayBar = new HBox(
                10,
                verseOfDayLabel,
                openVerseButton
        );
        verseOfDayBar.setAlignment(Pos.CENTER);
        verseOfDayBar.setPadding(
                new Insets(4, 12, 4, 12)
        );
        verseOfDayBar.setStyle(
                "-fx-background-color: #f7f3e8;"
                        + "-fx-border-color: #b7aa86;"
                        + "-fx-border-radius: 5;"
                        + "-fx-background-radius: 5;"
        );

        Label translationLabel = new Label("Translation:");
        translationLabel.setFont(Font.font("Serif", FontWeight.BOLD, 15));

        translationSelector = new ComboBox<>();
        translationSelector.getItems().addAll(translations.keySet());
        translationSelector.setValue("ESV - English Standard Version");
        translationSelector.setPrefWidth(350);
        translationSelector.setOnAction(
                event -> reloadCurrentReadingTranslation()
        );

        HBox translationBar = new HBox(
                10,
                translationLabel,
                translationSelector
        );
        translationBar.setAlignment(Pos.CENTER);

        dateLabel = new Label();
        dateLabel.setFont(
                Font.font("Serif", FontWeight.BOLD, 22)
        );

        passageLabel = new Label();
        passageLabel.setFont(Font.font("Serif", 16));
        passageLabel.setWrapText(true);
        passageLabel.setMaxWidth(1000);
        passageLabel.setAlignment(Pos.CENTER);

        headerPanel = new VBox(
                8,
                title,
                searchBar,
                verseOfDayBar,
                translationBar,
                dateLabel,
                passageLabel
        );
        headerPanel.setAlignment(Pos.CENTER);
        headerPanel.setPadding(new Insets(15));
        root.setTop(headerPanel);

        headerPanel.setOnMouseEntered(
                event -> stopHeaderHideTimer()
        );

        headerPanel.setOnMouseExited(event -> {
            if (
                    autoHideHeaderEnabled
                            && isReadingContentOpen()
            ) {
                scheduleHeaderHide();
            }
        });

        headerHideTimer.setOnFinished(event -> {
            if (
                    autoHideHeaderEnabled
                            && isReadingContentOpen()
            ) {
                hideHeader();
            }
        });
    }

    private void searchBible() {
        String reference = bibleSearchField.getText().trim();

        if (reference.isEmpty()) {
            passageLabel.setText("Enter a Bible reference to search.");
            return;
        }

        currentStudyReference = reference;
        currentDayIndex = -1;

        showHeader();
        showLeftSide();
        showRightSide();
        dateLabel.setText("Bible Search");
        passageLabel.setText(reference);
        notesTitleLabel.setText("My Journal — Bible Study");
        noteHistoryList.getItems().clear();
        newNoteArea.clear();
        addEntryButton.setDisable(true);
        removeEntryButton.setDisable(true);
        setCompletionCheckBoxForStudyMode();
        updateStudyNotesForReference(reference);
        noteStatusLabel.setText(
                "Journal entries are currently attached to chronological reading days."
        );

        String primaryVersion = translations.getOrDefault(
                translationSelector.getValue(),
                "ESV"
        );

        loadReference(webEngine, reference, primaryVersion);

        if (comparisonVisible) {
            String comparisonVersion = translations.getOrDefault(
                    comparisonTranslationSelector.getValue(),
                    "NIV"
            );
            loadReference(comparisonWebEngine, reference, comparisonVersion);
        }

        mainContentHolder.setCenter(bibleJournalSplitPane);
        updateNavigationButtons();

        /*
         * When a chronological reading begins, immediately collapse
         * the large top header if Auto-hide Header is ON.  This makes
         * the Scripture/study area open at full height right away,
         * like the expanded reading view, instead of waiting for the
         * normal header auto-hide delay.
         *
         * Moving the mouse to the top edge still reveals the header.
         */
        if (autoHideHeaderEnabled) {
            stopHeaderHideTimer();
            hideHeader();
        }

        if (autoHideSidesEnabled) scheduleSidesHide();
        if (autoHideBottomEnabled) scheduleBottomHide();
    }

    private void createPrimaryBible() {
        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setMinWidth(300);
        webView.setMinHeight(200);
    }

    private void createComparisonBible() {
        comparisonWebView = new WebView();
        comparisonWebEngine = comparisonWebView.getEngine();
        comparisonWebView.setMinWidth(250);

        Label comparisonLabel = new Label("Compare With:");
        comparisonLabel.setFont(Font.font("Serif", FontWeight.BOLD, 14));

        comparisonTranslationSelector = new ComboBox<>();
        comparisonTranslationSelector.getItems().addAll(translations.keySet());
        comparisonTranslationSelector.setValue("NIV - New International Version");
        comparisonTranslationSelector.setMaxWidth(Double.MAX_VALUE);
        comparisonTranslationSelector.setOnAction(event -> reloadComparisonReading());

        comparisonToolbar = new HBox(
                10,
                comparisonLabel,
                comparisonTranslationSelector
        );
        comparisonToolbar.setAlignment(Pos.CENTER_LEFT);
        comparisonToolbar.setPadding(new Insets(6, 8, 6, 8));
        HBox.setHgrow(comparisonTranslationSelector, Priority.ALWAYS);

        comparisonPane = new VBox(comparisonToolbar, comparisonWebView);
        comparisonPane.setMinWidth(250);
        VBox.setVgrow(comparisonWebView, Priority.ALWAYS);
    }

    private void createJournalPanel() {
        readingCompletedCheckBox = new CheckBox("Reading Completed");
        readingCompletedCheckBox.setFont(Font.font("Serif", FontWeight.BOLD, 15));
        readingCompletedCheckBox.setDisable(true);
        readingCompletedCheckBox.setOnAction(event -> {
            if (!updatingCompletionCheckBox) {
                saveCurrentReadingCompletion();
            }
        });

        notesTitleLabel = new Label("My Journal");
        notesTitleLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));

        Label previousEntriesLabel = new Label("Previous Entries");
        previousEntriesLabel.setFont(Font.font("Serif", FontWeight.BOLD, 15));

        noteHistoryList = new ListView<>();
        noteHistoryList.setMinHeight(100);
        noteHistoryList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(NoteEntry entry, boolean empty) {
                super.updateItem(entry, empty);

                if (empty || entry == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label date = new Label(entry.getFormattedDate());
                date.setFont(Font.font("Serif", FontWeight.BOLD, 14));

                Label text = new Label(entry.getNoteText());
                text.setFont(Font.font("Serif", 14));
                text.setWrapText(true);

                VBox entryBox = new VBox(5, date, text);
                entryBox.setPadding(new Insets(7));

                setText(null);
                setGraphic(entryBox);
            }
        });

        Label newEntryLabel = new Label("New Entry");
        newEntryLabel.setFont(Font.font("Serif", FontWeight.BOLD, 15));

        newNoteArea = new TextArea();
        newNoteArea.setPromptText("Write a new thought, reflection, question, or prayer...");
        newNoteArea.setWrapText(true);
        newNoteArea.setPrefRowCount(7);
        newNoteArea.setMinHeight(100);

        addEntryButton = new Button("Add Entry");
        addEntryButton.setOnAction(event -> addCurrentNoteEntry());

        removeEntryButton = new Button("Remove Entry");
        removeEntryButton.setOnAction(event -> removeSelectedNoteEntry());

        noteStatusLabel = new Label();
        noteStatusLabel.setWrapText(true);

        HBox journalButtons = new HBox(10, addEntryButton, removeEntryButton);
        journalButtons.setAlignment(Pos.CENTER_LEFT);

        VBox journalContent = new VBox(
                8,
                readingCompletedCheckBox,
                notesTitleLabel,
                previousEntriesLabel,
                noteHistoryList,
                newEntryLabel,
                newNoteArea,
                journalButtons,
                noteStatusLabel
        );
        journalContent.setPadding(new Insets(10));
        VBox.setVgrow(noteHistoryList, Priority.ALWAYS);

        studyNotesTitleLabel = new Label("Life Application Study Notes");
        studyNotesTitleLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));

        studyReferenceSelector = new ComboBox<>();
        studyReferenceSelector.setPromptText("Choose chapter notes");
        studyReferenceSelector.setMaxWidth(Double.MAX_VALUE);
        studyReferenceSelector.setOnAction(event -> loadSelectedStudyNotes());

        studyNotesStatusLabel = new Label();
        studyNotesStatusLabel.setWrapText(true);

        studyNotesWebView = new WebView();
        studyNotesWebEngine = studyNotesWebView.getEngine();
        studyNotesWebView.setMinHeight(200);

        VBox studyNotesContent = new VBox(
                8,
                studyNotesTitleLabel,
                studyReferenceSelector,
                studyNotesStatusLabel,
                studyNotesWebView
        );
        studyNotesContent.setPadding(new Insets(10));
        VBox.setVgrow(studyNotesWebView, Priority.ALWAYS);

        Tab studyNotesTab = new Tab("Study Notes", studyNotesContent);
        studyNotesTab.setClosable(false);

        Tab journalTab = new Tab("Journal", journalContent);
        journalTab.setClosable(false);

        rightSideTabs = new TabPane(studyNotesTab, journalTab);
        rightSideTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // ------------------------------------------------------------
        // Top-right information area:
        // [ Book Introduction ] [ Personality Profiles ] [ Charts ]
        // ------------------------------------------------------------

        bookIntroductionTitleLabel = new Label("Book Introduction");
        bookIntroductionTitleLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));

        bookIntroductionSelector = new ComboBox<>();
        bookIntroductionSelector.setPromptText("Choose a book introduction");
        bookIntroductionSelector.setMaxWidth(Double.MAX_VALUE);
        bookIntroductionSelector.setOnAction(event -> loadSelectedBookIntroduction());

        bookIntroductionStatusLabel = new Label(
                "Open a Bible reading to see the matching book introduction."
        );
        bookIntroductionStatusLabel.setWrapText(true);

        bookIntroductionWebView = new WebView();
        bookIntroductionWebEngine = bookIntroductionWebView.getEngine();
        bookIntroductionWebView.setMinHeight(150);

        VBox bookIntroductionContent = new VBox(
                8,
                bookIntroductionTitleLabel,
                bookIntroductionSelector,
                bookIntroductionStatusLabel,
                bookIntroductionWebView
        );
        bookIntroductionContent.setPadding(new Insets(10));
        bookIntroductionContent.setMinHeight(180);
        VBox.setVgrow(bookIntroductionWebView, Priority.ALWAYS);

        personalityProfileTitleLabel = new Label("Personality Profiles");
        personalityProfileTitleLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));

        personalityProfileSelector = new ComboBox<>();
        personalityProfileSelector.setPromptText("Choose a personality profile");
        personalityProfileSelector.setMaxWidth(Double.MAX_VALUE);
        personalityProfileSelector.setOnAction(event -> loadSelectedPersonalityProfile());

        personalityProfileStatusLabel = new Label(
                "Select a Bible reading to see available personality profiles."
        );
        personalityProfileStatusLabel.setWrapText(true);

        personalityProfileWebView = new WebView();
        personalityProfileWebEngine = personalityProfileWebView.getEngine();
        personalityProfileWebView.setMinHeight(150);

        VBox personalityProfileContent = new VBox(
                8,
                personalityProfileTitleLabel,
                personalityProfileSelector,
                personalityProfileStatusLabel,
                personalityProfileWebView
        );
        personalityProfileContent.setPadding(new Insets(10));
        personalityProfileContent.setMinHeight(180);
        VBox.setVgrow(personalityProfileWebView, Priority.ALWAYS);

        // ------------------------------------------------------------
        // Day-by-day Life Application Charts
        // ------------------------------------------------------------
        chartsTitleLabel = new Label("Charts");
        chartsTitleLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));

        chartsSelector = new ComboBox<>();
        chartsSelector.setPromptText("Choose a chart for this reading");
        chartsSelector.setMaxWidth(Double.MAX_VALUE);
        chartsSelector.setOnAction(event -> loadSelectedChart());

        chartsStatusLabel = new Label(
                "Select a Bible reading to see matching charts."
        );
        chartsStatusLabel.setWrapText(true);

        chartsWebView = new WebView();
        chartsWebEngine = chartsWebView.getEngine();
        chartsWebView.setMinHeight(150);

        VBox chartsContent = new VBox(
                8,
                chartsTitleLabel,
                chartsSelector,
                chartsStatusLabel,
                chartsWebView
        );
        chartsContent.setPadding(new Insets(10));
        chartsContent.setMinHeight(180);
        VBox.setVgrow(chartsWebView, Priority.ALWAYS);

        // ------------------------------------------------------------
        // Life Application Maps
        // ------------------------------------------------------------
        mapsTitleLabel = new Label("Maps");
        mapsTitleLabel.setFont(
                Font.font("Serif", FontWeight.BOLD, 18)
        );

        mapsSelector = new ComboBox<>();
        mapsSelector.setPromptText(
                "Choose a map for this reading"
        );
        mapsSelector.setMaxWidth(Double.MAX_VALUE);
        mapsSelector.setOnAction(
                event -> loadSelectedMap()
        );

        mapsStatusLabel = new Label(
                "Select a Bible reading to see matching maps."
        );
        mapsStatusLabel.setWrapText(true);

        mapsWebView = new WebView();
        mapsWebEngine = mapsWebView.getEngine();
        mapsWebView.setMinHeight(150);

        VBox mapsContent = new VBox(
                8,
                mapsTitleLabel,
                mapsSelector,
                mapsStatusLabel,
                mapsWebView
        );
        mapsContent.setPadding(new Insets(10));
        mapsContent.setMinHeight(180);
        VBox.setVgrow(mapsWebView, Priority.ALWAYS);

        // ------------------------------------------------------------
        // Bible Hub Hebrew / Greek Interlinear
        // ------------------------------------------------------------
        originalLanguageTitleLabel =
                new Label("Hebrew / Greek Concordance");
        originalLanguageTitleLabel.setFont(
                Font.font("Serif", FontWeight.BOLD, 18)
        );

        originalLanguageSelector = new ComboBox<>();
        originalLanguageSelector.setPromptText(
                "Choose a chapter from this reading"
        );
        originalLanguageSelector.setMaxWidth(Double.MAX_VALUE);
        originalLanguageSelector.setOnAction(
                event -> loadSelectedOriginalLanguageChapter()
        );

        originalLanguageStatusLabel = new Label(
                "Select a Bible reading to open its Hebrew or Greek interlinear."
        );
        originalLanguageStatusLabel.setWrapText(true);

        originalLanguageWebView = new WebView();
        originalLanguageWebEngine = originalLanguageWebView.getEngine();
        originalLanguageWebView.setMinHeight(150);

        VBox originalLanguageContent = new VBox(
                8,
                originalLanguageTitleLabel,
                originalLanguageSelector,
                originalLanguageStatusLabel,
                originalLanguageWebView
        );
        originalLanguageContent.setPadding(new Insets(10));
        originalLanguageContent.setMinHeight(180);
        VBox.setVgrow(originalLanguageWebView, Priority.ALWAYS);

        bookIntroductionTab = new Tab("Book Introduction", bookIntroductionContent);
        bookIntroductionTab.setClosable(false);

        personalityProfileTab = new Tab("Personality Profiles", personalityProfileContent);
        personalityProfileTab.setClosable(false);

        chartsTab = new Tab("Charts", chartsContent);
        chartsTab.setClosable(false);

        mapsTab = new Tab("Maps", mapsContent);
        mapsTab.setClosable(false);

        originalLanguageTab =
                new Tab("Hebrew / Greek", originalLanguageContent);
        originalLanguageTab.setClosable(false);

        /*
         * Hebrew / Greek belongs with the lower study area alongside
         * Study Notes and Journal.
         */
        /*
         * Maps belongs in the lower study area between Study Notes
         * and Journal, followed by Hebrew / Greek.
         */
        if (!rightSideTabs.getTabs().contains(mapsTab)) {
            rightSideTabs.getTabs().add(1, mapsTab);
        }
        rightSideTabs.getTabs().add(originalLanguageTab);

        /*
         * Book Introduction remains available throughout every
         * chapter of the current Bible book so it can be reviewed at any time.
         *
         * Personality Profiles and Charts remain in the
         * upper information area.
         */
        topInfoTabs = new TabPane(
                bookIntroductionTab,
                personalityProfileTab,
                chartsTab
        );
        topInfoTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        topInfoTabs.getSelectionModel().select(personalityProfileTab);

        rightInfoSplitPane = new SplitPane();
        rightInfoSplitPane.setOrientation(Orientation.VERTICAL);
        rightInfoSplitPane.getItems().addAll(topInfoTabs, rightSideTabs);
        rightInfoSplitPane.setDividerPositions(RIGHT_INFO_DIVIDER_POSITION);

        journalPanel = new VBox(rightInfoSplitPane);
        journalPanel.setMinWidth(260);
        journalPanel.setPrefWidth(330);
        VBox.setVgrow(rightInfoSplitPane, Priority.ALWAYS);

        /*
         * The right-side Personality Profiles / Charts / Maps / Study Notes / Journal / Hebrew-Greek
         * panel stays visible while reading.  It no longer participates
         * in auto-hide behavior.
         */
    }

    private void createReadingArea() {
        bibleComparisonSplitPane = new SplitPane();
        bibleComparisonSplitPane.setOrientation(Orientation.HORIZONTAL);
        bibleComparisonSplitPane.getItems().add(webView);

        bibleJournalSplitPane = new SplitPane();
        bibleJournalSplitPane.setOrientation(Orientation.HORIZONTAL);
        bibleJournalSplitPane.getItems().addAll(bibleComparisonSplitPane, journalPanel);
        bibleJournalSplitPane.setDividerPositions(JOURNAL_DIVIDER_POSITION);
    }

    private void createReadingPlanPanel() {
        bibleBooksTree = createBibleBooksTree();

        Label bibleBooksLabel = new Label("Books of the Bible");
        bibleBooksLabel.setFont(Font.font("Serif", FontWeight.BOLD, 20));

        VBox bibleBooksPanel = new VBox(8, bibleBooksLabel, bibleBooksTree);
        bibleBooksPanel.setPadding(new Insets(8));
        bibleBooksPanel.setMinHeight(150);
        VBox.setVgrow(bibleBooksTree, Priority.ALWAYS);

        readingPlanTree = createReadingPlanTree();

        Label readingPlanLabel = new Label("Reading Plan");
        readingPlanLabel.setFont(Font.font("Serif", FontWeight.BOLD, 20));

        readingPlanSelector = new ComboBox<>();
        readingPlanSelector.getItems().addAll(
                READING_PLAN_1_NAME,
                READING_PLAN_2_NAME,
                READING_PLAN_3_NAME
        );
        readingPlanSelector.setValue(READING_PLAN_3_NAME);
        readingPlanSelector.setMaxWidth(Double.MAX_VALUE);
        readingPlanSelector.setOnAction(event -> switchReadingPlan());

        VBox readingPlanPanel = new VBox(
                8,
                readingPlanLabel,
                readingPlanSelector,
                readingPlanTree
        );
        readingPlanPanel.setPadding(new Insets(8));
        readingPlanPanel.setMinHeight(150);
        VBox.setVgrow(readingPlanTree, Priority.ALWAYS);

        leftNavigationSplitPane = new SplitPane();
        leftNavigationSplitPane.setOrientation(Orientation.VERTICAL);
        leftNavigationSplitPane.getItems().addAll(bibleBooksPanel, readingPlanPanel);
        leftNavigationSplitPane.setDividerPositions(LEFT_NAVIGATION_DIVIDER);

        leftPanel = new VBox(leftNavigationSplitPane);
        leftPanel.setMinWidth(170);
        leftPanel.setPrefWidth(230);

        leftPanel.setOnMouseEntered(event -> stopSidesHideTimer());
        leftPanel.setOnMouseExited(event -> {
            if (autoHideSidesEnabled && isReadingContentOpen()) {
                scheduleSidesHide();
            }
        });
        VBox.setVgrow(leftNavigationSplitPane, Priority.ALWAYS);

        mainContentHolder = new BorderPane();
        mainContentHolder.setMinWidth(300);

        readingPlanSplitPane = new SplitPane();
        readingPlanSplitPane.setOrientation(Orientation.HORIZONTAL);
        readingPlanSplitPane.getItems().addAll(leftPanel, mainContentHolder);
        readingPlanSplitPane.setDividerPositions(READING_PLAN_WIDTH);

        root.setCenter(readingPlanSplitPane);
    }

    private void switchReadingPlan() {
        if (readingPlanSelector == null) return;

        String selection = readingPlanSelector.getValue();

        if (READING_PLAN_1_NAME.equals(selection)) {
            currentReadingPlanId = READING_PLAN_1_ID;
        } else if (READING_PLAN_2_NAME.equals(selection)) {
            currentReadingPlanId = READING_PLAN_2_ID;
        } else {
            currentReadingPlanId = READING_PLAN_3_ID;
        }

        currentDayIndex = -1;
        currentStudyReference = null;

        loadFullReadingPlan();

        if (readingPlanTree != null) {
            readingPlanTree.setRoot(createReadingPlanRoot());
        }

        refreshReadingPlanCompletionMarks();
        showReadingPlanHome();
    }

    private String getCurrentReadingPlanName() {
        if (READING_PLAN_1_ID.equals(currentReadingPlanId)) {
            return READING_PLAN_1_NAME;
        }
        if (READING_PLAN_2_ID.equals(currentReadingPlanId)) {
            return READING_PLAN_2_NAME;
        }
        return READING_PLAN_3_NAME;
    }

    private TreeView<String> createBibleBooksTree() {
        chapterReferenceMap.clear();

        TreeItem<String> rootItem = new TreeItem<>("66 Books of the Bible");
        rootItem.setExpanded(true);

        TreeItem<String> oldTestament = new TreeItem<>("Old Testament");
        TreeItem<String> newTestament = new TreeItem<>("New Testament");

        addBookToTree(oldTestament, "Genesis", 50);
        addBookToTree(oldTestament, "Exodus", 40);
        addBookToTree(oldTestament, "Leviticus", 27);
        addBookToTree(oldTestament, "Numbers", 36);
        addBookToTree(oldTestament, "Deuteronomy", 34);
        addBookToTree(oldTestament, "Joshua", 24);
        addBookToTree(oldTestament, "Judges", 21);
        addBookToTree(oldTestament, "Ruth", 4);
        addBookToTree(oldTestament, "1 Samuel", 31);
        addBookToTree(oldTestament, "2 Samuel", 24);
        addBookToTree(oldTestament, "1 Kings", 22);
        addBookToTree(oldTestament, "2 Kings", 25);
        addBookToTree(oldTestament, "1 Chronicles", 29);
        addBookToTree(oldTestament, "2 Chronicles", 36);
        addBookToTree(oldTestament, "Ezra", 10);
        addBookToTree(oldTestament, "Nehemiah", 13);
        addBookToTree(oldTestament, "Esther", 10);
        addBookToTree(oldTestament, "Job", 42);
        addBookToTree(oldTestament, "Psalms", 150);
        addBookToTree(oldTestament, "Proverbs", 31);
        addBookToTree(oldTestament, "Ecclesiastes", 12);
        addBookToTree(oldTestament, "Song of Solomon", 8);
        addBookToTree(oldTestament, "Isaiah", 66);
        addBookToTree(oldTestament, "Jeremiah", 52);
        addBookToTree(oldTestament, "Lamentations", 5);
        addBookToTree(oldTestament, "Ezekiel", 48);
        addBookToTree(oldTestament, "Daniel", 12);
        addBookToTree(oldTestament, "Hosea", 14);
        addBookToTree(oldTestament, "Joel", 3);
        addBookToTree(oldTestament, "Amos", 9);
        addBookToTree(oldTestament, "Obadiah", 1);
        addBookToTree(oldTestament, "Jonah", 4);
        addBookToTree(oldTestament, "Micah", 7);
        addBookToTree(oldTestament, "Nahum", 3);
        addBookToTree(oldTestament, "Habakkuk", 3);
        addBookToTree(oldTestament, "Zephaniah", 3);
        addBookToTree(oldTestament, "Haggai", 2);
        addBookToTree(oldTestament, "Zechariah", 14);
        addBookToTree(oldTestament, "Malachi", 4);

        addBookToTree(newTestament, "Matthew", 28);
        addBookToTree(newTestament, "Mark", 16);
        addBookToTree(newTestament, "Luke", 24);
        addBookToTree(newTestament, "John", 21);
        addBookToTree(newTestament, "Acts", 28);
        addBookToTree(newTestament, "Romans", 16);
        addBookToTree(newTestament, "1 Corinthians", 16);
        addBookToTree(newTestament, "2 Corinthians", 13);
        addBookToTree(newTestament, "Galatians", 6);
        addBookToTree(newTestament, "Ephesians", 6);
        addBookToTree(newTestament, "Philippians", 4);
        addBookToTree(newTestament, "Colossians", 4);
        addBookToTree(newTestament, "1 Thessalonians", 5);
        addBookToTree(newTestament, "2 Thessalonians", 3);
        addBookToTree(newTestament, "1 Timothy", 6);
        addBookToTree(newTestament, "2 Timothy", 4);
        addBookToTree(newTestament, "Titus", 3);
        addBookToTree(newTestament, "Philemon", 1);
        addBookToTree(newTestament, "Hebrews", 13);
        addBookToTree(newTestament, "James", 5);
        addBookToTree(newTestament, "1 Peter", 5);
        addBookToTree(newTestament, "2 Peter", 3);
        addBookToTree(newTestament, "1 John", 5);
        addBookToTree(newTestament, "2 John", 1);
        addBookToTree(newTestament, "3 John", 1);
        addBookToTree(newTestament, "Jude", 1);
        addBookToTree(newTestament, "Revelation", 22);

        rootItem.getChildren().addAll(oldTestament, newTestament);

        TreeView<String> tree = new TreeView<>(rootItem);
        tree.setShowRoot(true);
        tree.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) return;
                    String reference = chapterReferenceMap.get(newValue);
                    if (reference != null) openStudyChapter(reference);
                }
        );

        return tree;
    }

    private void addBookToTree(
            TreeItem<String> testament,
            String bookName,
            int chapterCount
    ) {
        TreeItem<String> bookItem = new TreeItem<>(bookName);

        for (int chapter = 1; chapter <= chapterCount; chapter++) {
            TreeItem<String> chapterItem = new TreeItem<>("Chapter " + chapter);
            chapterReferenceMap.put(chapterItem, bookName + " " + chapter);
            bookItem.getChildren().add(chapterItem);
        }

        testament.getChildren().add(bookItem);
    }

    private TreeView<String> createReadingPlanTree() {
        TreeView<String> tree = new TreeView<>(createReadingPlanRoot());
        tree.setShowRoot(true);
        tree.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) return;
                    ReadingDay readingDay = treeReadingMap.get(newValue);
                    if (readingDay != null) {
                        int index = readingDays.indexOf(readingDay);
                        if (index >= 0) openReading(index);
                    }
                }
        );

        return tree;
    }

    private TreeItem<String> createReadingPlanRoot() {
        treeReadingMap.clear();

        TreeItem<String> rootItem =
                new TreeItem<>(getCurrentReadingPlanName());
        rootItem.setExpanded(true);

        for (String month : new String[]{
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        }) {
            TreeItem<String> monthItem = new TreeItem<>(month);

            for (ReadingDay readingDay : readingDays) {
                if (!readingDay.getMonth().equals(month)) continue;

                TreeItem<String> dayItem =
                        new TreeItem<>(
                                month + " " + readingDay.getDay()
                        );

                treeReadingMap.put(dayItem, readingDay);
                monthItem.getChildren().add(dayItem);
            }

            rootItem.getChildren().add(monthItem);
        }

        return rootItem;
    }

    private void loadFullReadingPlan() {
        readingDays.clear();

        if (READING_PLAN_1_ID.equals(currentReadingPlanId)) {
            addPlanMonths(readingPlanOneMonths);
            return;
        }

        if (READING_PLAN_2_ID.equals(currentReadingPlanId)) {
            addPlanMonths(readingPlanTwoMonths);
            return;
        }

        // Reading Plan 3 is the existing modified chronological plan.
        addMonth("January", January.getReadings());
        addMonth("February", February.getReadings());
        addMonth("March", March.getReadings());
        addMonth("April", April.getReadings());
        addMonth("May", May.getReadings());
        addMonth("June", June.getReadings());
        addMonth("July", July.getReadings());
        addMonth("August", August.getReadings());
        addMonth("September", September.getReadings());
        addMonth("October", October.getReadings());
        addMonth("November", November.getReadings());
        addMonth("December", December.getReadings());
    }

    private void addPlanMonths(
            Map<String, Map<Integer, String>> plan
    ) {
        for (String month : new String[]{
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        }) {
            Map<Integer, String> monthReadings = plan.get(month);
            if (monthReadings != null) {
                addMonth(month, monthReadings);
            }
        }
    }

    private void addMonth(String month, Map<Integer, String> readings) {
        for (Map.Entry<Integer, String> entry : readings.entrySet()) {
            readingDays.add(
                    new ReadingDay(
                            month,
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }
    }


    private void loadReadingPlanOneData() {
        // Loaded from the user-supplied reading-plan PDF.
        Map<String, Map<Integer, String>> target =
                readingPlanOneMonths;

        target.put("January", new LinkedHashMap<>());
        target.put("February", new LinkedHashMap<>());
        target.put("March", new LinkedHashMap<>());
        target.put("April", new LinkedHashMap<>());
        target.put("May", new LinkedHashMap<>());
        target.put("June", new LinkedHashMap<>());
        target.put("July", new LinkedHashMap<>());
        target.put("August", new LinkedHashMap<>());
        target.put("September", new LinkedHashMap<>());
        target.put("October", new LinkedHashMap<>());
        target.put("November", new LinkedHashMap<>());
        target.put("December", new LinkedHashMap<>());

        target.get("January").put(1, "Genesis 1:1-2:25; Matthew 1:1-2:12; Psalm 1:1-6; Proverbs 1:1-6");
        target.get("January").put(2, "Genesis 3:1-4:26; Matthew 2:13-3:6; Psalm 2:1-12; Proverbs 1:7-9");
        target.get("January").put(3, "Genesis 5:1-7:24; Matthew 3:7-4:11; Psalm 3:1-8; Proverbs 1:10-19");
        target.get("January").put(4, "Genesis 8:1-10:32; Matthew 4:12-25; Psalm 4:1-8; Proverbs 1:20-23");
        target.get("January").put(5, "Genesis 11:1-13:4; Matthew 5:1-26; Psalm 5:1-12; Proverbs 1:24-28");
        target.get("January").put(6, "Genesis 13:5-15:21; Matthew 5:27-48; Psalm 6:1-10; Proverbs 1:29-33");
        target.get("January").put(7, "Genesis 16:1-18:15; Matthew 6:1-24; Psalm 7:1-17; Proverbs 2:1-5");
        target.get("January").put(8, "Genesis 18:16-19:38; Matthew 6:25-7:14; Psalm 8:1-9; Proverbs 2:6-15");
        target.get("January").put(9, "Genesis 20:1-22:24; Matthew 7:15-29; Psalm 9:1-12; Proverbs 2:16-22");
        target.get("January").put(10, "Genesis 23:1-24:51; Matthew 8:1-17; Psalm 9:13-20; Proverbs 3:1-6");
        target.get("January").put(11, "Genesis 24:52-26:16; Matthew 8:18-34; Psalm 10:1-15; Proverbs 3:7-8");
        target.get("January").put(12, "Genesis 26:17-27:46; Matthew 9:1-17; Psalm 10:16-18; Proverbs 3:9-10");
        target.get("January").put(13, "Genesis 28:1-29:35; Matthew 9:18-38; Psalm 11:1-7; Proverbs 3:11-12");
        target.get("January").put(14, "Genesis 30:1-31:16; Matthew 10:1-23; Psalm 12:1-8; Proverbs 3:13-15");
        target.get("January").put(15, "Genesis 31:17-32:12; Matthew 10:24-11:6; Psalm 13:1-6; Proverbs 3:16-18");
        target.get("January").put(16, "Genesis 32:13-34:31; Matthew 11:7-30; Psalm 14:1-7; Proverbs 3:19-20");
        target.get("January").put(17, "Genesis 35:1-36:43; Matthew 12:1-21; Psalm 15:1-5; Proverbs 3:21-26");
        target.get("January").put(18, "Genesis 37:1-38:30; Matthew 12:22-45; Psalm 16:1-11; Proverbs 3:27-32");
        target.get("January").put(19, "Genesis 39:1-41:16; Matthew 12:46-13:23; Psalm 17:1-15; Proverbs 3:33-35");
        target.get("January").put(20, "Genesis 41:17-42:17; Matthew 13:24-46; Psalm 18:1-15; Proverbs 4:1-6");
        target.get("January").put(21, "Genesis 42:18-43:34; Matthew 13:47-14:12; Psalm 18:16-36; Proverbs 4:7-10");
        target.get("January").put(22, "Genesis 44:1-45:28; Matthew 14:13-36; Psalm 18:37-50; Proverbs 4:11-13");
        target.get("January").put(23, "Genesis 46:1-47:31; Matthew 15:1-28; Psalm 19:1-14; Proverbs 4:14-19");
        target.get("January").put(24, "Genesis 48:1-49:33; Matthew 15:29-16:12; Psalm 20:1-9; Proverbs 4:20-27");
        target.get("January").put(25, "Genesis 50:1 - Exodus 2:10; Matthew 16:13-17:9; Psalm 21:1-13; Proverbs 5:1-6");
        target.get("January").put(26, "Exodus 2:11-3:22; Matthew 17:10-27; Psalm 22:1-18; Proverbs 5:7-14");
        target.get("January").put(27, "Exodus 4:1-5:21; Matthew 18:1-20; Psalm 22:19-31; Proverbs 5:15-21");
        target.get("January").put(28, "Exodus 5:22-7:25; Matthew 18:21-19:12; Psalm 23:1-6; Proverbs 5:22-23");
        target.get("January").put(29, "Exodus 8:1-9:35; Matthew 19:13-30; Psalm 24:1-10; Proverbs 6:1-5");
        target.get("January").put(30, "Exodus 10:1-12:13; Matthew 20:1-28; Psalm 25:1-15; Proverbs 6:6-11");
        target.get("January").put(31, "Exodus 12:14-13:16; Matthew 20:29-21:22; Psalm 25:16-22; Proverbs 6:12-15");
        target.get("February").put(1, "Exodus 13:17-15:18; Matthew 21:23-46; Psalm 26:1-12; Proverbs 6:16-19");
        target.get("February").put(2, "Exodus 15:19-17:7; Matthew 22:1-33; Psalm 27:1-6; Proverbs 6:20-26");
        target.get("February").put(3, "Exodus 17:8-19:15; Matthew 22:34-23:12; Psalm 27:7-14; Proverbs 6:27-35");
        target.get("February").put(4, "Exodus 19:16-21:21; Matthew 23:13-39; Psalm 28:1-9; Proverbs 7:1-5");
        target.get("February").put(5, "Exodus 21:22-23:13; Matthew 24:1-28; Psalm 29:1-11; Proverbs 7:6-23");
        target.get("February").put(6, "Exodus 23:14-25:40; Matthew 24:29-51; Psalm 30:1-12; Proverbs 7:24-27");
        target.get("February").put(7, "Exodus 26:1-27:21; Matthew 25:1-30; Psalm 31:1-8; Proverbs 8:1-11");
        target.get("February").put(8, "Exodus 28:1-43; Matthew 25:31-26:13; Psalm 31:9-18; Proverbs 8:12-13");
        target.get("February").put(9, "Exodus 29:1-30:10; Matthew 26:14-46; Psalm 31:19-24; Proverbs 8:14-26");
        target.get("February").put(10, "Exodus 30:11-31:18; Matthew 26:47-68; Psalm 32:1-11; Proverbs 8:27-32");
        target.get("February").put(11, "Exodus 32:1-33:23; Matthew 26:69-27:14; Psalm 33:1-11; Proverbs 8:33-36");
        target.get("February").put(12, "Exodus 34:1-35:9; Matthew 27:15-31; Psalm 33:12-22; Proverbs 9:1-6");
        target.get("February").put(13, "Exodus 35:10-36:38; Matthew 27:32-66; Psalm 34:1-10; Proverbs 9:7-8");
        target.get("February").put(14, "Exodus 37:1-38:31; Matthew 28:1-20; Psalm 34:11-22; Proverbs 9:9-10");
        target.get("February").put(15, "Exodus 39:1-40:38; Mark 1:1-28; Psalm 35:1-16; Proverbs 9:11-12");
        target.get("February").put(16, "Leviticus 1:1-3:17; Mark 1:29-2:12; Psalm 35:17-28; Proverbs 9:13-18");
        target.get("February").put(17, "Leviticus 4:1-5:19; Mark 2:13-3:6; Psalm 36:1-12; Proverbs 10:1-2");
        target.get("February").put(18, "Leviticus 6:1-7:27; Mark 3:7-30; Psalm 37:1-11; Proverbs 10:3-4");
        target.get("February").put(19, "Leviticus 7:28-9:6; Mark 3:31-4:25; Psalm 37:12-29; Proverbs 10:5");
        target.get("February").put(20, "Leviticus 9:7-10:20; Mark 4:26-5:20; Psalm 37:30-40; Proverbs 10:6-7");
        target.get("February").put(21, "Leviticus 11:1-12:8; Mark 5:21-43; Psalm 38:1-22; Proverbs 10:8-9");
        target.get("February").put(22, "Leviticus 13:1-59; Mark 6:1-29; Psalm 39:1-13; Proverbs 10:10");
        target.get("February").put(23, "Leviticus 14:1-57; Mark 6:30-56; Psalm 40:1-10; Proverbs 10:11-12");
        target.get("February").put(24, "Leviticus 15:1-16:28; Mark 7:1-23; Psalm 40:11-17; Proverbs 10:13-14");
        target.get("February").put(25, "Leviticus 16:29-18:30; Mark 7:24-8:10; Psalm 41:1-13; Proverbs 10:15-16");
        target.get("February").put(26, "Leviticus 19:1-20:21; Mark 8:11-38; Psalm 42:1-11; Proverbs 10:17");
        target.get("February").put(27, "Leviticus 20:22-22:16; Mark 9:1-29; Psalm 43:1-5; Proverbs 10:18");
        target.get("February").put(28, "Leviticus 22:17-23:44; Mark 9:30-10:12; Psalm 44:1-8; Proverbs 10:19");
        target.get("March").put(1, "Leviticus 24:1-25:46; Mark 10:13-31; Psalm 44:9-26; Proverbs 10:20-21");
        target.get("March").put(2, "Leviticus 25:47-27:13; Mark 10:32-52; Psalm 45:1-17; Proverbs 10:22");
        target.get("March").put(3, "Leviticus 27:14-; Numbers 1:54; Mark 11:1-26; Psalm 46:1-11; Proverbs 10:23");
        target.get("March").put(4, "Numbers 2:1-3:51; Mark 11:27-12:17; Psalm 47:1-9; Proverbs 10:24-25");
        target.get("March").put(5, "Numbers 4:1-5:31; Mark 12:18-37; Psalm 48:1-14; Proverbs 10:26");
        target.get("March").put(6, "Numbers 6:1-7:89; Mark 12:38-13:13; Psalm 49:1-20; Proverbs 10:27-28");
        target.get("March").put(7, "Numbers 8:1-9:23; Mark 13:14-37; Psalm 50:1-23; Proverbs 10:29-30");
        target.get("March").put(8, "Numbers 10:1-11:23; Mark 14:1-21; Psalm 51:1-19; Proverbs 10:31-32");
        target.get("March").put(9, "Numbers 11:24-13:33; Mark 14:22-52; Psalm 52:1-9; Proverbs 11:1-3");
        target.get("March").put(10, "Numbers 14:1-15:16; Mark 14:53-72; Psalm 53:1-6; Proverbs 11:4");
        target.get("March").put(11, "Numbers 15:17-16:40; Mark 15:1-47; Psalm 54:1-7; Proverbs 11:5-6");
        target.get("March").put(12, "Numbers 16:41-18:32; Mark 16:1-20; Psalm 55:1-23; Proverbs 11:7");
        target.get("March").put(13, "Numbers 19:1-20:29; Luke 1:1-25; Psalm 56:1-13; Proverbs 11:8");
        target.get("March").put(14, "Numbers 21:1-22:20; Luke 1:26-56; Psalm 57:1-11; Proverbs 11:9-11");
        target.get("March").put(15, "Numbers 22:21-23:30; Luke 1:57-80; Psalm 58:1-11; Proverbs 11:12-13");
        target.get("March").put(16, "Numbers 24:1-25:18; Luke 2:1-35; Psalm 59:1-17; Proverbs 11:14");
        target.get("March").put(17, "Numbers 26:1-51; Luke 2:36-52; Psalm 60:1-12; Proverbs 11:15");
        target.get("March").put(18, "Numbers 26:52-28:15; Luke 3:1-22; Psalm 61:1-8; Proverbs 11:16-17");
        target.get("March").put(19, "Numbers 28:16-29:40; Luke 3:23-38; Psalm 62:1-12; Proverbs 11:18-19");
        target.get("March").put(20, "Numbers 30:1-31:54; Luke 4:1-30; Psalm 63:1-11; Proverbs 11:20-21");
        target.get("March").put(21, "Numbers 32:1-33:39; Luke 4:31-5:11; Psalm 64:1-10; Proverbs 11:22");
        target.get("March").put(22, "Numbers 33:40-35:34; Luke 5:12-28; Psalm 65:1-13; Proverbs 11:23");
        target.get("March").put(23, "Numbers 36:1-; Deuteronomy 1:46; Luke 5:29-6:11; Psalm 66:1-20; Proverbs 11:24-26");
        target.get("March").put(24, "Deuteronomy 2:1-3:29; Luke 6:12-38; Psalm 67:1-7; Proverbs 11:27");
        target.get("March").put(25, "Deuteronomy 4:1-49; Luke 6:39-7:10; Psalm 68:1-18; Proverbs 11:28");
        target.get("March").put(26, "Deuteronomy 5:1-6:25; Luke 7:11-35; Psalm 68:19-35; Proverbs 11:29-31");
        target.get("March").put(27, "Deuteronomy 7:1-8:20; Luke 7:36-8:3; Psalm 69:1-18; Proverbs 12:1");
        target.get("March").put(28, "Deuteronomy 9:1-10:22; Luke 8:4-21; Psalm 69:19-36; Proverbs 12:2-3");
        target.get("March").put(29, "Deuteronomy 11:1-12:32; Luke 8:22-39; Psalm 70:1-5; Proverbs 12:4");
        target.get("March").put(30, "Deuteronomy 13:1-15:23; Luke 8:40-9:6; Psalm 71:1-24; Proverbs 12:5-7");
        target.get("March").put(31, "Deuteronomy 16:1-17:20; Luke 9:7-27; Psalm 72:1-20; Proverbs 12:8-9");
        target.get("April").put(1, "Deuteronomy 18:1-; Luke 9:28-50; Psalm 73:1-28; Proverbs 12:10");
        target.get("April").put(2, "Deuteronomy 21:1-22:30; Luke 9:51-10:12; Psalm 74:1-23; Proverbs 12:11");
        target.get("April").put(3, "Deuteronomy 23:1-25:19; Luke 10:13-37; Psalm 75:1-10; Proverbs 12:12-14");
        target.get("April").put(4, "Deuteronomy 26:1-27:26; Luke 10:38-11:13; Psalm 76:1-12; Proverbs 12:15-17");
        target.get("April").put(5, "Deuteronomy 28:1-68; Luke 11:14-36; Psalm 77:1-20; Proverbs 12:18");
        target.get("April").put(6, "Deuteronomy 29:1-30:20; Luke 11:37-12:7; Psalm 78:1-31; Proverbs 12:19-20");
        target.get("April").put(7, "Deuteronomy 31:1-32:27; Luke 12:8-34; Psalm 78:32-55; Proverbs 12:21-23");
        target.get("April").put(8, "Deuteronomy 32:28-52; Luke 12:35-59; Psalm 78:56-64; Proverbs 12:24");
        target.get("April").put(9, "Deuteronomy 33:1-29; Luke 13:1-21; Psalm 78:65-72; Proverbs 12:25");
        target.get("April").put(10, "Deuteronomy 34:1-; Joshua 2:24; Luke 13:22-14:6; Psalm 79:1-13; Proverbs 12:26");
        target.get("April").put(11, "Joshua 3:1-4:24; Luke 14:7-35; Psalm 80:1-19; Proverbs 12:27-28");
        target.get("April").put(12, "Joshua 5:1-7:15; Luke 15:1-32; Psalm 81:1-16; Proverbs 13:1");
        target.get("April").put(13, "Joshua 7:16-9:2; Luke 16:1-18; Psalm 82:1-8; Proverbs 13:2-3");
        target.get("April").put(14, "Joshua 9:3-10:43; Luke 16:19-17:10; Psalm 83:1-18; Proverbs 13:4");
        target.get("April").put(15, "Joshua 11:1-12:24; Luke 17:11-37; Psalm 84:1-12; Proverbs 13:5-6");
        target.get("April").put(16, "Joshua 13:1-14:15; Luke 18:1-17; Psalm 85:1-13; Proverbs 13:7-8");
        target.get("April").put(17, "Joshua 15:1-63; Luke 18:18-43; Psalm 86:1-17; Proverbs 13:9-10");
        target.get("April").put(18, "Joshua 16:1-18:28; Luke 19:1-27; Psalm 87:1-7; Proverbs 13:11");
        target.get("April").put(19, "Joshua 19:1-20:9; Luke 19:28-48; Psalm 88:1-18; Proverbs 13:12-14");
        target.get("April").put(20, "Joshua 21:1-22:20; Luke 20:1-26; Psalm 89:1-13; Proverbs 13:15-16");
        target.get("April").put(21, "Joshua 22:21-23:16; Luke 20:27-47; Psalm 89:14-37; Proverbs 13:17-19");
        target.get("April").put(22, "Joshua 24:1-33; Luke 21:1-28; Psalm 89:38-52; Proverbs 13:20-23");
        target.get("April").put(23, "Judges 1:1-2:9; Luke 21:29-22:13; Psalm 90:1-91:16; Proverbs 13:24-25");
        target.get("April").put(24, "Judges 2:10-3:31; Luke 22:14-34; Psalm 92:1-93:5; Proverbs 14:1-2");
        target.get("April").put(25, "Judges 4:1-5:31; Luke 22:35-53; Psalm 94:1-23; Proverbs 14:3-4");
        target.get("April").put(26, "Judges 6:1-40; Luke 22:54-23:12; Psalm 95:1-96:13; Proverbs 14:5-6");
        target.get("April").put(27, "Judges 7:1-8:17; Luke 23:13-43; Psalm 97:1-98:9; Proverbs 14:7-8");
        target.get("April").put(28, "Judges 8:18-9:21; Luke 23:44-24:12; Psalm 99:1-9; Proverbs 14:9-10");
        target.get("April").put(29, "Judges 9:22-10:18; Luke 24:13-53; Psalm 100:1-5; Proverbs 14:11-12");
        target.get("April").put(30, "Judges 11:1-12:15; John 1:1-28; Psalm 101:1-8; Proverbs 14:13-14");
        target.get("May").put(1, "Judges 13:1-14:20; John 1:29-51; Psalm 102:1-28; Proverbs 14:15-16");
        target.get("May").put(2, "Judges 15:1-16:31; John 2:1-25; Psalm 103:1-22; Proverbs 14:17-19");
        target.get("May").put(3, "Judges 17:1-18:31; John 3:1-21; Psalm 104:1-24; Proverbs 14:20-21");
        target.get("May").put(4, "Judges 19:1-20:48; John 3:22-4:3; Psalm 104:24-35; Proverbs 14:22-24");
        target.get("May").put(5, "Judges 21:1-Ruth 1:22; John 4:4-42; Psalm 105:1-15; Proverbs 14:25");
        target.get("May").put(6, "Ruth 2:1-4:22; John 4:43-54; Psalm 105:16-36; Proverbs 14:26-27");
        target.get("May").put(7, "1 Samuel 1:1-2:21; John 5:1-23; Psalm 105:37-45; Proverbs 14:28-29");
        target.get("May").put(8, "1 Samuel 2:22-4:22; John 5:24-47; Psalm 106:1-12; Proverbs 14:30-31");
        target.get("May").put(9, "1 Samuel 5:1-7:17; John 6:1-21; Psalm 106:13-31; Proverbs 14:32-33");
        target.get("May").put(10, "1 Samuel 8:1-9:27; John 6:22-42; Psalm 106:32-48; Proverbs 14:34-35");
        target.get("May").put(11, "1 Samuel 10:1-11:15; John 6:43-71; Psalm 107:1-43; Proverbs 15:1-3");
        target.get("May").put(12, "1 Samuel 12:1-13:23; John 7:1-30; Psalm 108:1-13; Proverbs 15:4");
        target.get("May").put(13, "1 Samuel 14:1-52; John 7:31-53; Psalm 109:1-31; Proverbs 15:5-7");
        target.get("May").put(14, "1 Samuel 15:1-16:23; John 8:1-20; Psalm 110:1-7; Proverbs 15:8-10");
        target.get("May").put(15, "1 Samuel 17:1-18:4; John 8:21-30; Psalm 111:1-10; Proverbs 15:11");
        target.get("May").put(16, "1 Samuel 18:5-19:24; John 8:31-59; Psalm 112:1-10; Proverbs 15:12-14");
        target.get("May").put(17, "1 Samuel 20:1-21:15; John 9:1-41; Psalm 113:1-114:8; Proverbs 15:15-17");
        target.get("May").put(18, "1 Samuel 22:1-23:29; John 10:1-21; Psalm 115:1-18; Proverbs 15:18-19");
        target.get("May").put(19, "1 Samuel 24:1-25:44; John 10:22-42; Psalm 116:1-19; Proverbs 15:20-21");
        target.get("May").put(20, "1 Samuel 26:1-28:25; John 11:1-54; Psalm 117:1-2; Proverbs 15:22-23");
        target.get("May").put(21, "1 Samuel 29:1-31:13; John 11:55-12:19; Psalm 118:1-18; Proverbs 15:24-26");
        target.get("May").put(22, "2 Samuel 1:1-2:11; John 12:20-50; Psalm 118:19-29; Proverbs 15:27-28");
        target.get("May").put(23, "2 Samuel 2:12-3:39; John 13:1-30; Psalm 119:1-16; Proverbs 15:29-30");
        target.get("May").put(24, "2 Samuel 4:1-6:23; John 13:31-14:14; Psalm 119:17-32; Proverbs 15:31-32");
        target.get("May").put(25, "2 Samuel 7:1-8:18; John 14:15-31; Psalm 119:33-48; Proverbs 15:33");
        target.get("May").put(26, "2 Samuel 9:1-11:27; John 15:1-27; Psalm 119:49-64; Proverbs 16:1-3");
        target.get("May").put(27, "2 Samuel 12:1-31; John 16:1-33; Psalm 119:65-80; Proverbs 16:4-5");
        target.get("May").put(28, "2 Samuel 13:1-39; John 17:1-26; Psalm 119:81-96; Proverbs 16:6-7");
        target.get("May").put(29, "2 Samuel 14:1-15:22; John 18:1-24; Psalm 119:97-112; Proverbs 16:8-9");
        target.get("May").put(30, "2 Samuel 15:23-16:23; John 18:25-19:22; Psalm 119:113-128; Proverbs 16:10-11");
        target.get("May").put(31, "2 Samuel 17:1-29; John 19:23-42; Psalm 119:129-152; Proverbs 16:12-13");
        target.get("June").put(1, "2 Samuel 18:1-19:10; John 20:1-31; Psalm 119:153-176; Proverbs 16:14-15");
        target.get("June").put(2, "2 Samuel 19:11-20:13; John 21:1-25; Psalm 120:1-7; Proverbs 16:16-17");
        target.get("June").put(3, "2 Samuel 20:14-21:22; Acts 1:1-26; Psalm 121:1-8; Proverbs 16:18");
        target.get("June").put(4, "2 Samuel 22:1-23:23; Acts 2:1-47; Psalm 122:1-9; Proverbs 16:19-20");
        target.get("June").put(5, "2 Samuel 23:24-24:25; Acts 3:1-26; Psalm 123:1-4; Proverbs 16:21-23");
        target.get("June").put(6, "1 Kings 1:1-53; Acts 4:1-37; Psalm 124:1-8; Proverbs 16:24");
        target.get("June").put(7, "1 Kings 2:1-3:2; Acts 5:1-42; Psalm 125:1-5; Proverbs 16:25");
        target.get("June").put(8, "1 Kings 3:3-4:34; Acts 6:1-15; Psalm 126:1-6; Proverbs 16:26-27");
        target.get("June").put(9, "1 Kings 5:1-6:38; Acts 7:1-29; Psalm 127:1-5; Proverbs 16:28-30");
        target.get("June").put(10, "1 Kings 7:1-51; Acts 7:30-50; Psalm 128:1-6; Proverbs 16:31-33");
        target.get("June").put(11, "1 Kings 8:1-66; Acts 7:51-8:13; Psalm 129:1-8; Proverbs 17:1");
        target.get("June").put(12, "1 Kings 9:1-10:29; Acts 8:14-40; Psalm 130:1-8; Proverbs 17:2-3");
        target.get("June").put(13, "1 Kings 11:1-12:19; Acts 9:1-25; Psalm 131:1-3; Proverbs 17:4-5");
        target.get("June").put(14, "1 Kings 12:20-13:34; Acts 9:26-43; Psalm 132:1-18; Proverbs 17:6");
        target.get("June").put(15, "1 Kings 14:1-15:24; Acts 10:1-23; Psalm 133:1-3; Proverbs 17:7-8");
        target.get("June").put(16, "1 Kings 15:25-17:24; Acts 10:24-48; Psalm 134:1-3; Proverbs 17:9-11");
        target.get("June").put(17, "1 Kings 18:1-46; Acts 11:1-30; Psalm 135:1-21; Proverbs 17:12-13");
        target.get("June").put(18, "1 Kings 19:1-21; Acts 12:1-23; Psalm 136:1-26; Proverbs 17:14-15");
        target.get("June").put(19, "1 Kings 20:1-21:29; Acts 12:24-13:15; Psalm 137:1-9; Proverbs 17:16");
        target.get("June").put(20, "1 Kings 22:1-53; Acts 13:16-41; Psalm 138:1-8; Proverbs 17:17-18");
        target.get("June").put(21, "2 Kings 1:1-2:25; Acts 13:42-14:7; Psalm 139:1-24; Proverbs 17:19-21");
        target.get("June").put(22, "2 Kings 3:1-4:17; Acts 14:8-28; Psalm 140:1-13; Proverbs 17:22");
        target.get("June").put(23, "2 Kings 4:18-5:27; Acts 15:1-35; Psalm 141:1-10; Proverbs 17:23");
        target.get("June").put(24, "2 Kings 6:1-7:20; Acts 15:36-16:15; Psalm 142:1-7; Proverbs 17:24-25");
        target.get("June").put(25, "2 Kings 8:1-9:13; Acts 16:16-40; Psalm 143:1-12; Proverbs 17:26");
        target.get("June").put(26, "2 Kings 9:14-10:31; Acts 17:1-34; Psalm 144:1-15; Proverbs 17:27-28");
        target.get("June").put(27, "2 Kings 10:32-12:21; Acts 18:1-22; Psalm 145:1-21; Proverbs 18:1");
        target.get("June").put(28, "2 Kings 13:1-14:29; Acts 18:23-19:12; Psalm 146:1-10; Proverbs 18:2-3");
        target.get("June").put(29, "2 Kings 15:1-16:20; Acts 19:13-41; Psalm 147:1-20; Proverbs 18:4-5");
        target.get("June").put(30, "2 Kings 17:1-18:12; Acts 20:1-38; Psalm 148:1-14; Proverbs 18:6-7");
        target.get("July").put(1, "2 Kings 18:13-19:37; Acts 21:1-17; Psalm 149:1-9; Proverbs 18:8");
        target.get("July").put(2, "2 Kings 20:1-22:2; Acts 21:18-36; Psalm 150:1-6; Proverbs 18:9-10");
        target.get("July").put(3, "2 Kings 22:3-23:30; Acts 21:37-22:16; Psalm 1:1-6; Proverbs 18:11-12");
        target.get("July").put(4, "2 Kings 23:31-25:30; Acts 22:17-23:10; Psalm 2:1-12; Proverbs 18:13");
        target.get("July").put(5, "1 Chronicles 1:1-2:17; Acts 23:11-35; Psalm 3:1-8; Proverbs 18:14-15");
        target.get("July").put(6, "1 Chronicles 2:18-4:4; Acts 24:1-27; Psalm 4:1-8; Proverbs 18:16-18");
        target.get("July").put(7, "1 Chronicles 4:5-5:17; Acts 25:1-27; Psalm 5:1-12; Proverbs 18:19");
        target.get("July").put(8, "1 Chronicles 5:18-6:81; Acts 26:1-32; Psalm 6:1-10; Proverbs 18:20-21");
        target.get("July").put(9, "1 Chronicles 7:1-8:40; Acts 27:1-20; Psalm 7:1-17; Proverbs 18:22");
        target.get("July").put(10, "1 Chronicles 9:1-10:14; Acts 27:21-44; Psalm 8:1-9; Proverbs 18:23-24");
        target.get("July").put(11, "1 Chronicles 11:1-12:18; Acts 28:1-31; Psalm 9:1-12; Proverbs 19:1-3");
        target.get("July").put(12, "1 Chronicles 12:19-14:17; Romans 1:1-17; Psalm 9:13-20; Proverbs 19:4-5");
        target.get("July").put(13, "1 Chronicles 15:1-16:36; Romans 1:18-32; Psalm 10:1-15; Proverbs 19:6-7");
        target.get("July").put(14, "1 Chronicles 16:37-18:17; Romans 2:1-24; Psalm 10:16-18; Proverbs 19:8-9");
        target.get("July").put(15, "1 Chronicles 19:1-21:30; Romans 2:25-3:8; Psalm 11:1-7; Proverbs 19:10-12");
        target.get("July").put(16, "1 Chronicles 22:1-23:32; Romans 3:9-31; Psalm 12:1-8; Proverbs 19:13-14");
        target.get("July").put(17, "1 Chronicles 24:1-26:11; Romans 4:1-12; Psalm 13:1-6; Proverbs 19:15-16");
        target.get("July").put(18, "1 Chronicles 26:12-27:34; Romans 4:13-5:5; Psalm 14:1-7; Proverbs 19:17");
        target.get("July").put(19, "1 Chronicles 28:1-29:30; Romans 5:6-21; Psalm 15:1-5; Proverbs 19:18-19");
        target.get("July").put(20, "2 Chronicles 1:1-3:17; Romans 6:1-23; Psalm 16:1-11; Proverbs 19:20-21");
        target.get("July").put(21, "2 Chronicles 4:1-6:11; Romans 7:1-13; Psalm 17:1-15; Proverbs 19:22-23");
        target.get("July").put(22, "2 Chronicles 6:12-8:10; Romans 7:14-8:8; Psalm 18:1-15; Proverbs 19:24-25");
        target.get("July").put(23, "2 Chronicles 8:11-10:19; Romans 8:9-25; Psalm 18:16-36; Proverbs 19:26");
        target.get("July").put(24, "2 Chronicles 11:1-13:22; Romans 8:26-39; Psalm 18:37-50; Proverbs 19:27-29");
        target.get("July").put(25, "2 Chronicles 14:1-16:14; Romans 9:1-24; Psalm 19:1-14; Proverbs 20:1");
        target.get("July").put(26, "2 Chronicles 17:1-18:34; Romans 9:25-10:13; Psalm 20:1-9; Proverbs 20:2-3");
        target.get("July").put(27, "2 Chronicles 19:1-20:37; Romans 10:14-11:12; Psalm 21:1-13; Proverbs 20:4-6");
        target.get("July").put(28, "2 Chronicles 21:1-23:21; Romans 11:13-36; Psalm 22:1-18; Proverbs 20:7");
        target.get("July").put(29, "2 Chronicles 24:1-25:28; Romans 12:1-21; Psalm 22:19-31; Proverbs 20:8-10");
        target.get("July").put(30, "2 Chronicles 26:1-28:27; Romans 13:1-14; Psalm 23:1-6; Proverbs 20:11");
        target.get("July").put(31, "2 Chronicles 29:1-36; Romans 14:1-23; Psalm 24:1-10; Proverbs 20:12");
        target.get("August").put(1, "2 Chronicles 30:1-31:21; Romans 15:1-22; Psalm 25:1-15; Proverbs 20:13-15");
        target.get("August").put(2, "2 Chronicles 32:1-33:13; Romans 15:23-16:9; Psalm 25:16-22; Proverbs 20:16-18");
        target.get("August").put(3, "2 Chronicles 33:14-34:33; Romans 16:10-27; Psalm 26:1-12; Proverbs 20:19");
        target.get("August").put(4, "2 Chronicles 35:1-36:23; 1 Corinthians 1:1-17; Psalm 27:1-6; Proverbs 20:20-21");
        target.get("August").put(5, "Ezra 1:1-2:70; 1 Corinthians 1:18-2:5; Psalm 27:7-14; Proverbs 20:22-23");
        target.get("August").put(6, "Ezra 3:1-4:23; 1 Corinthians 2:6-3:4; Psalm 28:1-9; Proverbs 20:24-25");
        target.get("August").put(7, "Ezra 4:24-6:22; 1 Corinthians 3:5-23; Psalm 29:1-11; Proverbs 20:26-27");
        target.get("August").put(8, "Ezra 7:1-8:20; 1 Corinthians 4:1-21; Psalm 30:1-12; Proverbs 20:28-30");
        target.get("August").put(9, "Ezra 8:21-9:15; 1 Corinthians 5:1-13; Psalm 31:1-8; Proverbs 21:1-2");
        target.get("August").put(10, "Ezra 10:1-44; 1 Corinthians 6:1-20; Psalm 31:9-18; Proverbs 21:3");
        target.get("August").put(11, "Nehemiah 1:1-3:14; 1 Corinthians 7:1-24; Psalm 31:19-24; Proverbs 21:4");
        target.get("August").put(12, "Nehemiah 3:15-5:13; 1 Corinthians 7:25-40; Psalm 32:1-11; Proverbs 21:5-7");
        target.get("August").put(13, "Nehemiah 5:14-7:73; 1 Corinthians 8:1-13; Psalm 33:1-11; Proverbs 21:8-10");
        target.get("August").put(14, "Nehemiah 7:73-9:21; 1 Corinthians 9:1-18; Psalm 33:12-22; Proverbs 21:11-12");
        target.get("August").put(15, "Nehemiah 9:22-10:39; 1 Corinthians 9:19-10:13; Psalm 34:1-10; Proverbs 21:13");
        target.get("August").put(16, "Nehemiah 11:1-12:26; 1 Corinthians 10:14-33; Psalm 34:11-22; Proverbs 21:14-16");
        target.get("August").put(17, "Nehemiah 12:27-13:31; 1 Corinthians 11:1-16; Psalm 35:1-16; Proverbs 21:17-18");
        target.get("August").put(18, "Esther 1:1-3:15; 1 Corinthians 11:17-34; Psalm 35:17-28; Proverbs 21:19-20");
        target.get("August").put(19, "Esther 4:1-7:10; 1 Corinthians 12:1-26; Psalm 36:1-12; Proverbs 21:21-22");
        target.get("August").put(20, "Esther 8:1-10:3; 1 Corinthians 12:27-13:13; Psalm 37:1-11; Proverbs 21:23-24");
        target.get("August").put(21, "Job 1:1-3:26; 1 Corinthians 14:1-17; Psalm 37:12-29; Proverbs 21:25-26");
        target.get("August").put(22, "Job 4:1-7:21; 1 Corinthians 14:18-40; Psalm 37:30-40; Proverbs 21:27");
        target.get("August").put(23, "Job 8:1-11:20; 1 Corinthians 15:1-28; Psalm 38:1-22; Proverbs 21:28-29");
        target.get("August").put(24, "Job 12:1-15:35; 1 Corinthians 15:29-58; Psalm 39:1-13; Proverbs 21:30-31");
        target.get("August").put(25, "Job 16:1-19:29; 1 Corinthians 16:1-24; Psalm 40:1-10; Proverbs 22:1");
        target.get("August").put(26, "Job 20:1-22:30; 2 Corinthians 1:1-11; Psalm 40:11-17; Proverbs 22:2-4");
        target.get("August").put(27, "Job 23:1-27:23; 2 Corinthians 1:12-2:11; Psalm 41:1-13; Proverbs 22:5-6");
        target.get("August").put(28, "Job 28:1-30:31; 2 Corinthians 2:12-17; Psalm 42:1-11; Proverbs 22:7");
        target.get("August").put(29, "Job 31:1-33:33; 2 Corinthians 3:1-18; Psalm 43:1-5; Proverbs 22:8-9");
        target.get("August").put(30, "Job 34:1-36:33; 2 Corinthians 4:1-12; Psalm 44:1-8; Proverbs 22:10-12");
        target.get("August").put(31, "Job 37:1-39:30; 2 Corinthians 4:13-5:10; Psalm 44:9-26; Proverbs 22:13");
        target.get("September").put(1, "Job 40:1-42:17; 2 Corinthians 5:11-21; Psalm 45:1-17; Proverbs 22:14");
        target.get("September").put(2, "Ecclesiastes 1:1-3:22; 2 Corinthians 6:1-13; Psalm 46:1-11; Proverbs 22:15");
        target.get("September").put(3, "Ecclesiastes 4:1-6:12; 2 Corinthians 6:14-7:7; Psalm 47:1-9; Proverbs 22:16");
        target.get("September").put(4, "Ecclesiastes 7:1-9:18; 2 Corinthians 7:8-16; Psalm 48:1-14; Proverbs 22:17-19");
        target.get("September").put(5, "Ecclesiastes 10:1-12:14; 2 Corinthians 8:1-15; Psalm 49:1-20; Proverbs 22:20-21");
        target.get("September").put(6, "Song of Solomon 1:1-; 2 Corinthians 8:16-24; Psalm 50:1-23; Proverbs 22:22-23");
        target.get("September").put(7, "Song of Solomon 5:1-8:14; 2 Corinthians 9:1-15; Psalm 51:1-19; Proverbs 22:24-25");
        target.get("September").put(8, "Isaiah 1:1-2:22; 2 Corinthians 10:1-18; Psalm 52:1-9; Proverbs 22:26-27");
        target.get("September").put(9, "Isaiah 3:1-5:30; 2 Corinthians 11:1-15; Psalm 53:1-6; Proverbs 22:28-29");
        target.get("September").put(10, "Isaiah 6:1-7:25; 2 Corinthians 11:16-33; Psalm 54:1-7; Proverbs 23:1-3");
        target.get("September").put(11, "Isaiah 8:1-9:21; 2 Corinthians 12:1-10; Psalm 55:1-23; Proverbs 23:4-5");
        target.get("September").put(12, "Isaiah 10:1-11:16; 2 Corinthians 12:11-21; Psalm 56:1-13; Proverbs 23:6-8");
        target.get("September").put(13, "Isaiah 12:1-14:32; 2 Corinthians 13:1-14; Psalm 57:1-11; Proverbs 23:9-11");
        target.get("September").put(14, "Isaiah 15:1-18:7; Galatians 1:1-24; Psalm 58:1-11; Proverbs 23:12");
        target.get("September").put(15, "Isaiah 19:1-21:17; Galatians 2:1-16; Psalm 59:1-17; Proverbs 23:13-14");
        target.get("September").put(16, "Isaiah 22:1-24:23; Galatians 2:17-3:9; Psalm 60:1-12; Proverbs 23:15-16");
        target.get("September").put(17, "Isaiah 25:1-28:13; Galatians 3:10-22; Psalm 61:1-8; Proverbs 23:17-18");
        target.get("September").put(18, "Isaiah 28:14-30:11; Galatians 3:23-4:31; Psalm 62:1-12; Proverbs 23:19-21");
        target.get("September").put(19, "Isaiah 30:12-33:9; Galatians 5:1-12; Psalm 63:1-11; Proverbs 23:22");
        target.get("September").put(20, "Isaiah 33:10-36:22; Galatians 5:13-26; Psalm 64:1-10; Proverbs 23:23");
        target.get("September").put(21, "Isaiah 37:1-38:22; Galatians 6:1-18; Psalm 65:1-13; Proverbs 23:24");
        target.get("September").put(22, "Isaiah 39:1-41:16; Ephesians 1:1-23; Psalm 66:1-20; Proverbs 23:25-28");
        target.get("September").put(23, "Isaiah 41:17-43:13; Ephesians 2:1-22; Psalm 67:1-7; Proverbs 23:29-35");
        target.get("September").put(24, "Isaiah 43:14-45:10; Ephesians 3:1-21; Psalm 68:1-18; Proverbs 24:1-2");
        target.get("September").put(25, "Isaiah 45:11-48:11; Ephesians 4:1-16; Psalm 68:19-35; Proverbs 24:3-4");
        target.get("September").put(26, "Isaiah 48:12-50:11; Ephesians 4:17-32; Psalm 69:1-18; Proverbs 24:5-6");
        target.get("September").put(27, "Isaiah 51:1-53:12; Ephesians 5:1-33; Psalm 69:19-36; Proverbs 24:7");
        target.get("September").put(28, "Isaiah 54:1-57:14; Ephesians 6:1-24; Psalm 70:1-5; Proverbs 24:8");
        target.get("September").put(29, "Isaiah 57:15-59:21; Philippians 1:1-26; Psalm 71:1-24; Proverbs 24:9-10");
        target.get("September").put(30, "Isaiah 60:1-62:5; Philippians 1:27-2:18; Psalm 72:1-20; Proverbs 24:11-12");
        target.get("October").put(1, "Isaiah 62:6-65:25; Philippians 2:19-3:3; Psalm 73:1-28; Proverbs 24:13-14");
        target.get("October").put(2, "Isaiah 66:1-24; Philippians 3:4-21; Psalm 74:1-23; Proverbs 24:15-16");
        target.get("October").put(3, "Jeremiah 1:1-2:30; Philippians 4:1-23; Psalm 75:1-10; Proverbs 24:17-20");
        target.get("October").put(4, "Jeremiah 2:31-4:18; Colossians 1:1-17; Psalm 76:1-12; Proverbs 24:21-22");
        target.get("October").put(5, "Jeremiah 4:19-6:15; Colossians 1:18-2:7; Psalm 77:1-20; Proverbs 24:23-25");
        target.get("October").put(6, "Jeremiah 6:16-8:7; Colossians 2:8-23; Psalm 78:1-31; Proverbs 24:26");
        target.get("October").put(7, "Jeremiah 8:8-9:26; Colossians 3:1-17; Psalm 78:32-55; Proverbs 24:27");
        target.get("October").put(8, "Jeremiah 10:1-11:23; Colossians 3:18-4:18; Psalm 78:56-72; Proverbs 24:28-29");
        target.get("October").put(9, "Jeremiah 12:1-14:10; 1 Thessalonians 1:1-2:8; Psalm 79:1-13; Proverbs 24:30-34");
        target.get("October").put(10, "Jeremiah 14:11-16:15; 1 Thessalonians 2:9-3:13; Psalm 80:1-19; Proverbs 25:1-5");
        target.get("October").put(11, "Jeremiah 16:16-18:23; 1 Thessalonians 4:1-5:3; Psalm 81:1-16; Proverbs 25:6-8");
        target.get("October").put(12, "Jeremiah 19:1-21:14; 1 Thessalonians 5:4-28; Psalm 82:1-8; Proverbs 25:9-10");
        target.get("October").put(13, "Jeremiah 22:1-23:20; 2 Thessalonians 1:1-12; Psalm 83:1-18; Proverbs 25:11-14");
        target.get("October").put(14, "Jeremiah 23:21-25:38; 2 Thessalonians 2:1-17; Psalm 84:1-12; Proverbs 25:15");
        target.get("October").put(15, "Jeremiah 26:1-27:22; 2 Thessalonians 3:1-18; Psalm 85:1-13; Proverbs 25:16");
        target.get("October").put(16, "Jeremiah 28:1-29:32; 1 Timothy 1:1-20; Psalm 86:1-17; Proverbs 25:17");
        target.get("October").put(17, "Jeremiah 30:1-31:26; 1 Timothy 2:1-15; Psalm 87:1-7; Proverbs 25:18-19");
        target.get("October").put(18, "Jeremiah 31:27-32:44; 1 Timothy 3:1-16; Psalm 88:1-18; Proverbs 25:20-22");
        target.get("October").put(19, "Jeremiah 33:1-34:22; 1 Timothy 4:1-16; Psalm 89:1-13; Proverbs 25:23-24");
        target.get("October").put(20, "Jeremiah 35:1-36:32; 1 Timothy 5:1-25; Psalm 89:14-37; Proverbs 25:25-27");
        target.get("October").put(21, "Jeremiah 37:1-38:28; 1 Timothy 6:1-21; Psalm 89:38-52; Proverbs 25:28");
        target.get("October").put(22, "Jeremiah 39:1-41:18; 2 Timothy 1:1-18; Psalm 90:1-91:16; Proverbs 26:1-2");
        target.get("October").put(23, "Jeremiah 42:1-44:23; 2 Timothy 2:1-21; Psalm 92:1-93; Proverbs 26:3-5");
        target.get("October").put(24, "Jeremiah 44:24-47:7; 2 Timothy 2:22-3:17; Psalm 94:1-23; Proverbs 26:6-8");
        target.get("October").put(25, "Jeremiah 48:1-49:22; 2 Timothy 4:1-22; Psalm 95:1-96:13; Proverbs 26:9-12");
        target.get("October").put(26, "Jeremiah 49:23-50:46; Titus 1:1-16; Psalm 97:1-98:9; Proverbs 26:13-16");
        target.get("October").put(27, "Jeremiah 51:1-53; Titus 2:1-15; Psalm 99:1-9; Proverbs 26:17");
        target.get("October").put(28, "Jeremiah 51:54-52:34; Titus 3:1-15; Psalm 100:1-5; Proverbs 26:18-19");
        target.get("October").put(29, "Lamentations 1:1-2:22; Philemon 1:1-25; Psalm 101:1-8; Proverbs 26:20");
        target.get("October").put(30, "Lamentations 3:1-66; Hebrews 1:1-14; Psalm 102:1-28; Proverbs 26:21-22");
        target.get("October").put(31, "Lamentations 4:1-5:22; Hebrews 2:1-18; Psalm 103:1-22; Proverbs 26:23");
        target.get("November").put(1, "Ezekiel 1:1-3:15; Hebrews 3:1-19; Psalm 104:1-23; Proverbs 26:24-26");
        target.get("November").put(2, "Ezekiel 3:16-6:14; Hebrews 4:1-16; Psalm 104:24-35; Proverbs 26:27");
        target.get("November").put(3, "Ezekiel 7:1-9:11; Hebrews 5:1-14; Psalm 105:1-15; Proverbs 26:28");
        target.get("November").put(4, "Ezekiel 10:1-11:25; Hebrews 6:1-20; Psalm 105:16-36; Proverbs 27:1-2");
        target.get("November").put(5, "Ezekiel 12:1-14:11; Hebrews 7:1-17; Psalm 105:37-45; Proverbs 27:3");
        target.get("November").put(6, "Ezekiel 14:12-16:41; Hebrews 7:18-28; Psalm 106:1-12; Proverbs 27:4-6");
        target.get("November").put(7, "Ezekiel 16:42-17:24; Hebrews 8:1-13; Psalm 106:13-31; Proverbs 27:7-9");
        target.get("November").put(8, "Ezekiel 18:1-19:14; Hebrews 9:1-10; Psalm 106:32-48; Proverbs 27:10");
        target.get("November").put(9, "Ezekiel 20:1-49; Hebrews 9:11-28; Psalm 107:1-43; Proverbs 27:11");
        target.get("November").put(10, "Ezekiel 21:1-22:31; Hebrews 10:1-17; Psalm 108:1-13; Proverbs 27:12");
        target.get("November").put(11, "Ezekiel 23:1-49; Hebrews 10:18-39; Psalm 109:1-31; Proverbs 27:13");
        target.get("November").put(12, "Ezekiel 24:1-26:21; Hebrews 11:1-16; Psalm 110:1-7; Proverbs 27:14");
        target.get("November").put(13, "Ezekiel 27:1-28:26; Hebrews 11:17-31; Psalm 111:1-10; Proverbs 27:15-16");
        target.get("November").put(14, "Ezekiel 29:1-30:26; Hebrews 11:32-12:13; Psalm 112:1-10; Proverbs 27:17");
        target.get("November").put(15, "Ezekiel 31:1-32:32; Hebrews 12:14-29; Psalm 113:1-114:8; Proverbs 27:18-20");
        target.get("November").put(16, "Ezekiel 33:1-34:31; Hebrews 13:1-25; Psalm 115:1-18; Proverbs 27:21-22");
        target.get("November").put(17, "Ezekiel 35:1-36:38; James 1:1-18; Psalm 116:1-19; Proverbs 27:23-27");
        target.get("November").put(18, "Ezekiel 37:1-38:23; James 1:19-2:17; Psalm 117:1-2; Proverbs 28:1");
        target.get("November").put(19, "Ezekiel 39:1-40:27; James 2:18-3:18; Psalm 118:1-18; Proverbs 28:2");
        target.get("November").put(20, "Ezekiel 40:28-41:26; James 4:1-17; Psalm 118:19-29; Proverbs 28:3-5");
        target.get("November").put(21, "Ezekiel 42:1-43:27; James 5:1-20; Psalm 119:1-16; Proverbs 28:6-7");
        target.get("November").put(22, "Ezekiel 44:1-45:12; 1 Peter 1:1-12; Psalm 119:17-32; Proverbs 28:8-10");
        target.get("November").put(23, "Ezekiel 45:13-46:24; 1 Peter 1:13-2:10; Psalm 119:33-48; Proverbs 28:11");
        target.get("November").put(24, "Ezekiel 47:1-48:35; 1 Peter 2:11-3:7; Psalm 119:49-64; Proverbs 28:12-13");
        target.get("November").put(25, "Daniel 1:1-2:23; 1 Peter 3:8-4:6; Psalm 119:65-80; Proverbs 28:14");
        target.get("November").put(26, "Daniel 2:24-3:30; 1 Peter 4:7-5:14; Psalm 119:81-96; Proverbs 28:15-16");
        target.get("November").put(27, "Daniel 4:1-37; 2 Peter 1:1-21; Psalm 119:97-112; Proverbs 28:17-18");
        target.get("November").put(28, "Daniel 5:1-31; 2 Peter 2:1-22; Psalm 119:113-128; Proverbs 28:19-20");
        target.get("November").put(29, "Daniel 6:1-28; 2 Peter 3:1-18; Psalm 119:129-152; Proverbs 28:21-22");
        target.get("November").put(30, "Daniel 7:1-28; 1 John 1:1-10; Psalm 119:153-176; Proverbs 28:23-24");
        target.get("December").put(1, "Daniel 8:1-27; 1 John 2:1-17; Psalm 120:1-7; Proverbs 28:25-26");
        target.get("December").put(2, "Daniel 9:1-11:1; 1 John 2:18-3:6; Psalm 121:1-8; Proverbs 28:27-28");
        target.get("December").put(3, "Daniel 11:2-35; 1 John 3:7-24; Psalm 122:1-9; Proverbs 29:1");
        target.get("December").put(4, "Daniel 11:36-12:13; 1 John 4:1-21; Psalm 123:1-4; Proverbs 29:2-4");
        target.get("December").put(5, "Hosea 1:1-3:5; 1 John 5:1-21; Psalm 124:1-8; Proverbs 29:5-8");
        target.get("December").put(6, "Hosea 4:1-5:15; 2 John 1:1-13; Psalm 125:1-5; Proverbs 29:9-11");
        target.get("December").put(7, "Hosea 6:1-9:17; 3 John 1:1-15; Psalm 126:1-6; Proverbs 29:12-14");
        target.get("December").put(8, "Hosea 10:1-14:9; Jude 1:1-25; Psalm 127:1-5; Proverbs 29:15-17");
        target.get("December").put(9, "Joel 1:1-3:21; Revelation 1:1-20; Psalm 128:1-6; Proverbs 29:18");
        target.get("December").put(10, "Amos 1:1-3:15; Revelation 2:1-17; Psalm 129:1-8; Proverbs 29:19-20");
        target.get("December").put(11, "Amos 4:1-6:14; Revelation 2:18-3:6; Psalm 130:1-8; Proverbs 29:21-22");
        target.get("December").put(12, "Amos 7:1-9:15; Revelation 3:7-22; Psalm 131:1-3; Proverbs 29:23");
        target.get("December").put(13, "Obadiah 1:1-21; Revelation 4:1-11; Psalm 132:1-18; Proverbs 29:24-25");
        target.get("December").put(14, "Jonah 1:1-4:11; Revelation 5:1-14; Psalm 133:1-3; Proverbs 29:26-27");
        target.get("December").put(15, "Micah 1:1-4:13; Revelation 6:1-17; Psalm 134:1-3; Proverbs 30:1-4");
        target.get("December").put(16, "Micah 5:1-7:20; Revelation 7:1-17; Psalm 135:1-21; Proverbs 30:5-6");
        target.get("December").put(17, "Nahum 1:1-3:19; Revelation 8:1-13; Psalm 136:1-26; Proverbs 30:7-9");
        target.get("December").put(18, "Habakkuk 1:1-3:19; Revelation 9:1-21; Psalm 137:1-9; Proverbs 30:10");
        target.get("December").put(19, "Zephaniah 1:1-3:20; Revelation 10:1-11; Psalm 138:1-8; Proverbs 30:11-14");
        target.get("December").put(20, "Haggai 1:1-2:23; Revelation 11:1-19; Psalm 139:1-24; Proverbs 30:15-16");
        target.get("December").put(21, "Zechariah 1:1-21; Revelation 12:1-17; Psalm 140:1-13; Proverbs 30:17");
        target.get("December").put(22, "Zechariah 2:1-3:10; Revelation 13:1-13:18; Psalm 141:1-10; Proverbs 30:18-20");
        target.get("December").put(23, "Zechariah 4:1-5:11; Revelation 14:1-20; Psalm 142:1-7; Proverbs 30:21-23");
        target.get("December").put(24, "Zechariah 6:1-7:14; Revelation 15:1-8; Psalm 143:1-12; Proverbs 30:24-28");
        target.get("December").put(25, "Zechariah 8:1-23; Revelation 16:1-21; Psalm 144:1-15; Proverbs 30:29-31");
        target.get("December").put(26, "Zechariah 9:1-17; Revelation 17:1-18; Psalm 145:1-21; Proverbs 30:32");
        target.get("December").put(27, "Zechariah 10:1-11:17; Revelation 18:1-24; Psalm 146:1-10; Proverbs 30:33");
        target.get("December").put(28, "Zechariah 12:1-13:9; Revelation 19:1-21; Psalm 147:1-20; Proverbs 31:1-7");
        target.get("December").put(29, "Zechariah 14:1-21; Revelation 20:1-15; Psalm 148:1-14; Proverbs 31:8-9");
        target.get("December").put(30, "Malachi 1:1-2:17; Revelation 21:1-27; Psalm 149:1-9; Proverbs 31:10-24");
        target.get("December").put(31, "Malachi 3:1-4:6; Revelation 22:1-21; Psalm 150:1-6; Proverbs 31:25-31");
    }

    private void loadReadingPlanTwoData() {
        // Loaded from the user-supplied reading-plan PDF.
        Map<String, Map<Integer, String>> target =
                readingPlanTwoMonths;

        target.put("January", new LinkedHashMap<>());
        target.put("February", new LinkedHashMap<>());
        target.put("March", new LinkedHashMap<>());
        target.put("April", new LinkedHashMap<>());
        target.put("May", new LinkedHashMap<>());
        target.put("June", new LinkedHashMap<>());
        target.put("July", new LinkedHashMap<>());
        target.put("August", new LinkedHashMap<>());
        target.put("September", new LinkedHashMap<>());
        target.put("October", new LinkedHashMap<>());
        target.put("November", new LinkedHashMap<>());
        target.put("December", new LinkedHashMap<>());

        target.get("January").put(1, "Genesis 1:1-3:24");
        target.get("January").put(2, "Genesis 4:1-5:32; 1 Chronicles 1:1-4; Genesis 6:1-22");
        target.get("January").put(3, "Genesis 7:1-10:5; 1 Chronicles 1:5-7; Genesis 10:6-20; 1 Chronicles 1:8-16; Genesis 10:21-30; 1 Chronicles 1:17-23; Genesis 10:31-32");
        target.get("January").put(4, "Genesis 11:1-26; 1 Chronicles 1:24-27; Genesis 11:27-11:31; Genesis 12:1-14:24");
        target.get("January").put(5, "Genesis 15:1-17:27");
        target.get("January").put(6, "Genesis 18:1-21:7");
        target.get("January").put(7, "Genesis 21:8-23:20; Genesis 11:32; Genesis 24:1-67");
        target.get("January").put(8, "Genesis 25:1-4; 1 Chronicles 1:32-33; Genesis 25:5-6; Genesis 25:12-18; 1 Chronicles 1:28-31; 1 Chronicles 1:34; Genesis 25:19-26; Genesis 25:7-11");
        target.get("January").put(9, "Genesis 25:27-28:5");
        target.get("January").put(10, "Genesis 28:6-30:24");
        target.get("January").put(11, "Genesis 30:25-31:55");
        target.get("January").put(12, "Genesis 32:1-35:27");
        target.get("January").put(13, "Genesis 36:1-19; 1 Chronicles 1:35-37; Genesis 36:20-30; 1 Chronicles 1:38-42; Genesis 36:31-43; 1 Chronicles 1:43-2:2");
        target.get("January").put(14, "Genesis 37:1-38:30; 1 Chronicles 2:3-6; 1 Chronicles 2:8; Genesis 39:1-23");
        target.get("January").put(15, "Genesis 40:1-23; Genesis 35:28-29; Genesis 41:1-57");
        target.get("January").put(16, "Genesis 42:1-45:15");
        target.get("January").put(17, "Genesis 45:16-47:27");
        target.get("January").put(18, "Genesis 47:28-50:26");
        target.get("January").put(19, "Job 1:1-4:21");
        target.get("January").put(20, "Job 5:1-7:21");
        target.get("January").put(21, "Job 8:1-11:20");
        target.get("January").put(22, "Job 12:1-14:22");
        target.get("January").put(23, "Job 15:1-18:21");
        target.get("January").put(24, "Job 19:1-21:34");
        target.get("January").put(25, "Job 22:1-25:6");
        target.get("January").put(26, "Job 26:1-29:25");
        target.get("January").put(27, "Job 30:1-31:40");
        target.get("January").put(28, "Job 32:1-34:37");
        target.get("January").put(29, "Job 35:1-37:24");
        target.get("January").put(30, "Job 38:1-40:5");
        target.get("January").put(31, "Job 40:6-42:17");
        target.get("February").put(1, "Exodus 1:1-2:25; 1 Chronicles 6:1-3; Exodus 3:1-4:17");
        target.get("February").put(2, "Exodus 4:18-7:13");
        target.get("February").put(3, "Exodus 7:14-9:35");
        target.get("February").put(4, "Exodus 10:1-12:51");
        target.get("February").put(5, "Exodus 13:1-15:27");
        target.get("February").put(6, "Exodus 16:1-19:25");
        target.get("February").put(7, "Exodus 20:1-22:15");
        target.get("February").put(8, "Exodus 22:16-24:18");
        target.get("February").put(9, "Exodus 25:1-28:43");
        target.get("February").put(10, "Exodus 29:1-31:18");
        target.get("February").put(11, "Exodus 32:1-34:35");
        target.get("February").put(12, "Exodus 35:1-36:38");
        target.get("February").put(13, "Exodus 37:1-39:31");
        target.get("February").put(14, "Exodus 39:32-40:38; Numbers 9:15-23");
        target.get("February").put(15, "Numbers 7:1-89");
        target.get("February").put(16, "Numbers 8:1-9:14; Leviticus 1:1-3:17");
        target.get("February").put(17, "Leviticus 4:1-6:30");
        target.get("February").put(18, "Leviticus 7:1-8:36");
        target.get("February").put(19, "Leviticus 9:1-11:47");
        target.get("February").put(20, "Leviticus 12:1-14:32");
        target.get("February").put(21, "Leviticus 14:33-16:34");
        target.get("February").put(22, "Leviticus 17:1-19:37");
        target.get("February").put(23, "Leviticus 20:1-22:33");
        target.get("February").put(24, "Leviticus 23:1-25:23");
        target.get("February").put(25, "Leviticus 25:24-26:46");
        target.get("February").put(26, "Leviticus 27:1-34; Numbers 1:1-54");
        target.get("February").put(27, "Numbers 2:1-3:51");
        target.get("February").put(28, "Numbers 4:1-5:31");
        target.get("March").put(1, "Numbers 6:1-27; Numbers 10:1-36");
        target.get("March").put(2, "Numbers 11:1-13:33");
        target.get("March").put(3, "Numbers 14:1-15:41");
        target.get("March").put(4, "Numbers 16:1-18:32");
        target.get("March").put(5, "Numbers 19:1-21:35");
        target.get("March").put(6, "Numbers 22:1-24:25");
        target.get("March").put(7, "Numbers 25:1-26:65");
        target.get("March").put(8, "Numbers 27:1-29:40");
        target.get("March").put(9, "Numbers 30:1-31:54");
        target.get("March").put(10, "Numbers 32:1-33:56");
        target.get("March").put(11, "Numbers 34:1-36:13");
        target.get("March").put(12, "Deuteronomy 1:1-3:20");
        target.get("March").put(13, "Deuteronomy 3:21-5:33");
        target.get("March").put(14, "Deuteronomy 6:1-9:29");
        target.get("March").put(15, "Deuteronomy 10:1-12:32");
        target.get("March").put(16, "Deuteronomy 13:1-16:17");
        target.get("March").put(17, "Deuteronomy 16:18-21:9");
        target.get("March").put(18, "Deuteronomy 21:10-25:19");
        target.get("March").put(19, "Deuteronomy 26:1-29:1");
        target.get("March").put(20, "Deuteronomy 29:2-31:29");
        target.get("March").put(21, "Deuteronomy 31:30-32:52; Psalms 90");
        target.get("March").put(22, "Deuteronomy 33:1-34:12; Joshua 1:1-2:24");
        target.get("March").put(23, "Joshua 3:1-6:27");
        target.get("March").put(24, "Joshua 7:1; 1 Chronicles 2:7; Joshua 7:2-9:27");
        target.get("March").put(25, "Joshua 10:1-12:6");
        target.get("March").put(26, "Joshua 12:7-15:19");
        target.get("March").put(27, "Joshua 15:20-17:18");
        target.get("March").put(28, "Joshua 18:1-19:48");
        target.get("March").put(29, "Joshua 19:49-21:45; 1 Chronicles 6:54-81");
        target.get("March").put(30, "Joshua 22:1-24:33");
        target.get("March").put(31, "Judges 1:1-3:30");
        target.get("April").put(1, "Judges 3:31-6:40");
        target.get("April").put(2, "Judges 7:1-9:21");
        target.get("April").put(3, "Judges 9:22-11:28");
        target.get("April").put(4, "Judges 11:29-15:20");
        target.get("April").put(5, "Judges 16:1-18:31");
        target.get("April").put(6, "Judges 19:1-21:25");
        target.get("April").put(7, "Ruth 1:1-4:12");
        target.get("April").put(8, "Ruth 4:13-22; 1 Chronicles 2:9-55; 1 Chronicles 4:1-23; 1 Samuel 1:1-8");
        target.get("April").put(9, "1 Samuel 1:9-4:11");
        target.get("April").put(10, "1 Samuel 4:12-8:22");
        target.get("April").put(11, "1 Samuel 9:1-12:25");
        target.get("April").put(12, "1 Chronicles 9:35-39; 1 Samuel 13:1-5; 1 Samuel 13:19-23; 1 Samuel 13:6-18; 1 Samuel 14:1-52");
        target.get("April").put(13, "1 Samuel 15:1-17:31");
        target.get("April").put(14, "1 Samuel 17:32-19:17; Psalms 59; 1 Samuel 19:18-24");
        target.get("April").put(15, "1 Samuel 20:1-21:15; Psalms 34");
        target.get("April").put(16, "1 Samuel 22:1-2; Psalms 57; Psalms 142; 1 Chronicles 12:8-18; 1 Samuel 22:3-23; Psalms 52; 1 Samuel 23:1-12");
        target.get("April").put(17, "1 Samuel 23:13-29; Psalms 54; 1 Samuel 24:1-25:44");
        target.get("April").put(18, "1 Samuel 26:1-27:7; 1 Chronicles 12:1-7; 1 Samuel 27:8-29:11; 1 Chronicles 12:19; Psalms 56");
        target.get("April").put(19, "1 Samuel 30:1-31; 1 Chronicles 12:20-22; 1 Samuel 31:1-13; 1 Chronicles 10:1-14; 1 Chronicles 9:40-44; 2 Samuel 4:4; 2 Samuel 1:1-27");
        target.get("April").put(20, "2 Samuel 2:1-3:5; 1 Chronicles 3:1-4; 2 Samuel 23:8-17; 1 Chronicles 11:10-19; 2 Samuel 23:18-39; 1 Chronicles 11:20-47");
        target.get("April").put(21, "2 Samuel 3:6-4:12");
        target.get("April").put(22, "2 Samuel 5:1-3; 1 Chronicles 11:1-3; 1 Chronicles 12:23-40; 2 Samuel 5:17-25; 1 Chronicles 14:8-17; 2 Samuel 5:6-10; 1 Chronicles 11:4-9; 1 Chronicles 3:4; 2 Samuel 5:13; 2 Samuel 5:4-5; 2 Samuel 5:11-12; 1 Chronicles 14:1-2; 1 Chronicles 13:1-5; 2 Samuel 6:1-11; 1 Chronicles 13:6-14");
        target.get("April").put(23, "2 Samuel 6:12; 1 Chronicles 15:1-28; 2 Samuel 6:12-16; 1 Chronicles 15:29; 2 Samuel 6:17-19; 1 Chronicles 16:1-43; 2 Samuel 6:19-23");
        target.get("April").put(24, "2 Samuel 7:1-17; 1 Chronicles 17:1-15; 2 Samuel 7:18-29; 1 Chronicles 17:16-27; 2 Samuel 8:1-14; 1 Chronicles 18:1-13; Psalms 60");
        target.get("April").put(25, "2 Samuel 8:15-18; 1 Chronicles 18:14-17; 1 Chronicles 6:16-30; 1 Chronicles 6:50-53; 1 Chronicles 6:31-48; 2 Samuel 9:1-10:19; 1 Chronicles 19:1-19");
        target.get("April").put(26, "1 Chronicles 20:1; 2 Samuel 11:1-12:14; Psalms 51; 2 Samuel 12:15-25; 2 Samuel 5:14-16; 1 Chronicles 14:3-7; 1 Chronicles 3:5-9");
        target.get("April").put(27, "2 Samuel 12:26-31; 1 Chronicles 20:2-3; 2 Samuel 13:1-14:33");
        target.get("April").put(28, "2 Samuel 15:1-17:14");
        target.get("April").put(29, "2 Samuel 17:15-29; Psalms 3; Psalms 63; 2 Samuel 18:1-19:30");
        target.get("April").put(30, "2 Samuel 19:31-20:26; Psalms 7; 2 Samuel 21:1-22; 1 Chronicles 20:4-8");
        target.get("May").put(1, "2 Samuel 22:1-51; Psalms 18");
        target.get("May").put(2, "2 Samuel 24:1-9; 1 Chronicles 21:1-6; 2 Samuel 24:10-17; 1 Chronicles 21:7-17; 2 Samuel 24:18-25; 1 Chronicles 21:18-22:19");
        target.get("May").put(3, "1 Chronicles 23:1-25:31");
        target.get("May").put(4, "1 Chronicles 26:1-28:21");
        target.get("May").put(5, "1 Chronicles 29:1-22; 1 Kings 1:1-53");
        target.get("May").put(6, "1 Kings 2:1-9; 2 Samuel 23:1-7; 1 Kings 2:10-12; 1 Chronicles 29:26-30; Psalms 4-6; Psalms 8-9; Psalms 11");
        target.get("May").put(7, "Psalms 12-17; Psalms 19-21");
        target.get("May").put(8, "Psalms 22-26");
        target.get("May").put(9, "Psalms 27-32");
        target.get("May").put(10, "Psalms 35-38");
        target.get("May").put(11, "Psalms 39-41; Psalms 53; Psalms 55; Psalms 58");
        target.get("May").put(12, "Psalms 61-62; Psalms 64-67");
        target.get("May").put(13, "Psalms 68-70; Psalms 86; Psalms 101");
        target.get("May").put(14, "Psalms 103; Psalms 108-110; Psalms 122; Psalms 124");
        target.get("May").put(15, "Psalms 131; Psalms 133; Psalms 138-141; Psalms 143");
        target.get("May").put(16, "Psalms 144-145; Psalms 88-89");
        target.get("May").put(17, "Psalms 50; Psalms 73-74");
        target.get("May").put(18, "Psalms 75-78");
        target.get("May").put(19, "Psalms 79-82");
        target.get("May").put(20, "Psalms 83; 1 Chronicles 29:23-25; 2 Chronicles 1:1; 1 Kings 2:13-3:4; 2 Chronicles 1:2-6; 1 Kings 3:5-15; 2 Chronicles 1:7-13");
        target.get("May").put(21, "1 Kings 3:16-28; 1 Kings 5:1-18; 2 Chronicles 2:1-18; 1 Kings 6:1-13; 2 Chronicles 3:1-14; 1 Kings 6:14-38");
        target.get("May").put(22, "1 Kings 7:1-51; 2 Chronicles 3:15-4:22");
        target.get("May").put(23, "1 Kings 8:1-11; 2 Chronicles 5:1-14; 1 Kings 8:12-21; 2 Chronicles 6:1-11; 1 Kings 8:22-53; 2 Chronicles 6:12-42");
        target.get("May").put(24, "1 Kings 8:54-66; 2 Chronicles 7:1-10; 1 Kings 9:1-9; 2 Chronicles 7:11-22; 1 Kings 9:10-14");
        target.get("May").put(25, "2 Chronicles 8:1-18; 1 Kings 9:15-10:13; 2 Chronicles 9:1-12; 1 Kings 10:14-29; 2 Chronicles 9:13-28; 2 Chronicles 1:14-17");
        target.get("May").put(26, "1 Kings 4:1-34; Psalms 72; Psalms 127");
        target.get("May").put(27, "Proverbs 1:1-4:27");
        target.get("May").put(28, "Proverbs 5:1-7:27");
        target.get("May").put(29, "Proverbs 8:1-10:32");
        target.get("May").put(30, "Proverbs 11:1-13:25");
        target.get("May").put(31, "Proverbs 14:1-16:33");
        target.get("June").put(1, "Proverbs 17:1-19:29");
        target.get("June").put(2, "Proverbs 20:1-22:16");
        target.get("June").put(3, "Proverbs 22:17-24:34");
        target.get("June").put(4, "Song of Solomon 1:1-8:14");
        target.get("June").put(5, "1 Kings 11:1-43; 2 Chronicles 9:29-31; Ecclesiastes 1:1-11");
        target.get("June").put(6, "Ecclesiastes 1:12-6:12");
        target.get("June").put(7, "Ecclesiastes 7:1-11:6");
        target.get("June").put(8, "Ecclesiastes 11:7-12:14; 1 Kings 12:1-20; 2 Chronicles 10:1-19; 1 Kings 12:21-24; 2 Chronicles 11:1-4; 1 Kings 12:25-33; 2 Chronicles 11:5-17");
        target.get("June").put(9, "1 Kings 13:1-14:18; 1 Kings 14:21-14:24; 2 Chronicles 12:13-14; 2 Chronicles 11:18-23; 2 Chronicles 12:1-12; 1 Kings 14:25-28; 2 Chronicles 12:15-16; 1 Kings 14:29-15:5; 2 Chronicles 13:1-22; 1 Kings 15:6-8; 2 Chronicles 14:1-8; 1 Kings 15:9-15; 1 Kings 14:19-20; 1 Kings 15:25-34; 2 Chronicles 14:9-15; 2 Chronicles 15:1-19");
        target.get("June").put(10, "1 Kings 15:16-22; 2 Chronicles 16:1-10; 1 Kings 16:1-34; 1 Kings 15:23-24; 2 Chronicles 16:11-17:19; 1 Kings 17:1-7");
        target.get("June").put(11, "1 Kings 17:8-20:22");
        target.get("June").put(12, "1 Kings 20:23-22:9; 2 Chronicles 18:1-8");
        target.get("June").put(13, "1 Kings 22:10-28; 2 Chronicles 18:9-27; 1 Kings 22:29-35; 2 Chronicles 18:28-34; 1 Kings 22:36-40; 1 Kings 22:51-53; 2 Chronicles 19:1-20:30");
        target.get("June").put(14, "2 Kings 1:1-18; 2 Kings 3:1-27; 1 Kings 22:41-49; 2 Chronicles 20:31-37; 1 Kings 22:50; 2 Chronicles 21:1-4; 2 Kings 8:16-22; 2 Chronicles 21:5-7");
        target.get("June").put(15, "2 Kings 2:1-25; 2 Kings 4:1-44");
        target.get("June").put(16, "2 Kings 5:1-8:15");
        target.get("June").put(17, "2 Chronicles 21:8-20; 2 Kings 8:23-29; 2 Chronicles 22:1-7; 2 Kings 9:1-10:17; 2 Chronicles 22:8-9; 2 Kings 10:18-31");
        target.get("June").put(18, "2 Kings 11:1-3; 2 Chronicles 22:10-12; 2 Kings 11:4-12; 2 Chronicles 23:1-11; 2 Kings 11:13-16; 2 Chronicles 23:12-15; 2 Kings 11:17-21; 2 Chronicles 23:16-21; 2 Kings 12:1-16; 2 Chronicles 24:1-22; 2 Kings 10:32-36");
        target.get("June").put(19, "2 Kings 13:1-11; 2 Kings 12:17-21; 2 Chronicles 24:23-27; 2 Kings 13:14-25");
        target.get("June").put(20, "2 Kings 14:1-14; 2 Chronicles 25:1-24; 2 Kings 13:12-13; 2 Kings 14:15-16; 2 Kings 14:23-27; 2 Chronicles 25:25-28; 2 Kings 14:17-22; 2 Kings 15:1-15; 2 Chronicles 26:1-21; Jonah 1:1-4:11");
        target.get("June").put(21, "Amos 1:1-6:14");
        target.get("June").put(22, "Amos 7:1-9:15; 2 Kings 14:28-29; 2 Kings 15:8-29; 2 Kings 15:6-7; 2 Chronicles 26:22-23; Isaiah 6:1-13");
        target.get("June").put(23, "2 Kings 15:32-38; 2 Chronicles 27:1-9; Micah 1:1-16; 2 Kings 16:1-9; 2 Chronicles 28:1-15; Isaiah 7:1-25");
        target.get("June").put(24, "Isaiah 8:1-11:16");
        target.get("June").put(25, "Isaiah 12:1-6; Isaiah 17:1-14; 2 Chronicles 28:16-21; 2 Kings 16:10-18; 2 Chronicles 28:22-25; 2 Kings 18:1-8; 2 Chronicles 29:1-2; 2 Kings 15:30-31; 2 Kings 17:1-4; Hosea 1:1-2:13");
        target.get("June").put(26, "Hosea 2:14-8:14");
        target.get("June").put(27, "Hosea 9:1-14:9");
        target.get("June").put(28, "Isaiah 28:1-29; 2 Kings 17:5; 2 Kings 18:9-12; 2 Kings 17:6-41; Isaiah 1:1-20");
        target.get("June").put(29, "Isaiah 1:21-5:30");
        target.get("June").put(30, "2 Kings 16:19-20; 2 Chronicles 28:26-27; Isaiah 13:1-16:14");
        target.get("July").put(1, "2 Chronicles 29:3-31:21");
        target.get("July").put(2, "Proverbs 25:1-29:27");
        target.get("July").put(3, "Proverbs 30:1-31:31");
        target.get("July").put(4, "Psalms 42; Psalms 43; Psalms 44; Psalms 45; Psalms 46");
        target.get("July").put(5, "Psalms 47; Psalms 48; Psalms 49; Psalms 84; Psalms 85; Psalms 87");
        target.get("July").put(6, "Psalms 1-2; Psalms 10; Psalms 33; Psalms 71; Psalms 91");
        target.get("July").put(7, "Psalms 92; Psalms 93; Psalms 94; Psalms 95; Psalms 96; Psalms 97");
        target.get("July").put(8, "Psalms 98; Psalms 99; Psalms 100; Psalms 102; Psalms 104");
        target.get("July").put(9, "Psalms 105; Psalms 106");
        target.get("July").put(10, "Psalms 107; Psalms 111; Psalms 112; Psalms 113; Psalms 114");
        target.get("July").put(11, "Psalms 115; Psalms 116; Psalms 117; Psalms 118");
        target.get("July").put(12, "Psalms 119");
        target.get("July").put(13, "Psalms 120; Psalms 121; Psalms 123; Psalms 125; Psalms 126");
        target.get("July").put(14, "Psalms 128; Psalms 129; Psalms 130; Psalms 132; Psalms 134; Psalms 135");
        target.get("July").put(15, "Psalms 136; Psalms 146; Psalms 147; Psalms 148; Psalms 149; Psalms 150");
        target.get("July").put(16, "Isaiah 18:1-23:18");
        target.get("July").put(17, "Isaiah 24:1-27:13; Isaiah 29:1-24");
        target.get("July").put(18, "Isaiah 30:1-33:24");
        target.get("July").put(19, "Isaiah 34:1-35:10; Micah 2:1-5:15");
        target.get("July").put(20, "Micah 6:1-7:20; 2 Chronicles 32:1-8; 2 Kings 18:13-18; Isaiah 36:1-3; 2 Kings 18:19-37; Isaiah 36:4-22");
        target.get("July").put(21, "2 Kings 19:1-19; Isaiah 37:1-20; 2 Chronicles 32:9-19; 2 Kings 19:20-37; Isaiah 37:21-38; 2 Chronicles 32:20-23");
        target.get("July").put(22, "2 Kings 20:1-11; Isaiah 38:1-8; 2 Chronicles 32:24:31; Isaiah 38:9-22; 2 Kings 20:12-19; Isaiah 39:1-8");
        target.get("July").put(23, "Isaiah 40:1-44:5");
        target.get("July").put(24, "Isaiah 44:6-48:11");
        target.get("July").put(25, "Isaiah 48:12-52:12");
        target.get("July").put(26, "Isaiah 52:13-57:21");
        target.get("July").put(27, "Isaiah 58:1-63:14");
        target.get("July").put(28, "Isaiah 63:15-66:24; 2 Kings 20:20-21; 2 Chronicles 32:32-33");
        target.get("July").put(29, "2 Kings 21:1-9; 2 Chronicles 33:1-9; 2 Kings 21:10-17; 2 Chronicles 33:10-19; 2 Kings 21:18; 2 Chronicles 33:20; 2 Kings 21:19-26; 2 Chronicles 33:21-25; 2 Kings 22:1-2; 2 Chronicles 34:1-7; Jeremiah 1:1-2:22");
        target.get("July").put(30, "Jeremiah 2:23-5:19");
        target.get("July").put(31, "Jeremiah 5:20-6:30; 2 Kings 22:3-20; 2 Chronicles 34:8-28");
        target.get("August").put(1, "2 Kings 23:1-20; 2 Chronicles 34:29-33; 2 Kings 23:21-28; 2 Chronicles 35:1-19; Nahum 1:1-3:19");
        target.get("August").put(2, "Habakkuk 1:1-3:19; Zephaniah 1:1-2:7");
        target.get("August").put(3, "Zephaniah 2:8-3:20; 2 Chronicles 35:20-27; 2 Kings 23:29-30; Jeremiah 47:1-48:47");
        target.get("August").put(4, "2 Chronicles 36:1-4; 2 Kings 23:31-37; 2 Chronicles 36:5; Jeremiah 22:1-23; Jeremiah 26:1-24; 2 Kings 24:1-4; Jeremiah 25:1-14");
        target.get("August").put(5, "Jeremiah 25:15-38; Jeremiah 36:1-32; Jeremiah 45:1-46:28");
        target.get("August").put(6, "Jeremiah 19:1-20:18; Daniel 1:1-21");
        target.get("August").put(7, "Daniel 2:1-3:30; Jeremiah 7:1-8:3");
        target.get("August").put(8, "Jeremiah 8:4-11:23");
        target.get("August").put(9, "Jeremiah 12:1-15:21");
        target.get("August").put(10, "Jeremiah 16:1-18:23; Jeremiah 35:1-19");
        target.get("August").put(11, "Jeremiah 49:1-33; 2 Kings 24:5-7; 2 Chronicles 36:6-8; 2 Kings 24:8-9; 2 Chronicles 36:9; Jeremiah 22:24-23:32");
        target.get("August").put(12, "Jeremiah 23:33-24:10; Jeremiah 29:1-31:14");
        target.get("August").put(13, "Jeremiah 31:15-40; Jeremiah 49:34-51:14");
        target.get("August").put(14, "Jeremiah 51:15-58; 2 Chronicles 36:10; 2 Kings 24:10-17; 1 Chronicles 3:10-16; 2 Chronicles 36:11-14; Jeremiah 52:1-3; 2 Kings 24:18-20; Jeremiah 37:1-10");
        target.get("August").put(15, "Jeremiah 37:11-38:28; Ezekiel 1:1-3:15");
        target.get("August").put(16, "Ezekiel 3:16-4:17; Jeremiah 27:1-28:17; Jeremiah 51:59-64");
        target.get("August").put(17, "Ezekiel 5:1-9:11");
        target.get("August").put(18, "Ezekiel 10:1-13:23");
        target.get("August").put(19, "Ezekiel 14:1-16:63");
        target.get("August").put(20, "Ezekiel 17:1-19:14");
        target.get("August").put(21, "Ezekiel 20:1-22:16");
        target.get("August").put(22, "Ezekiel 22:17-23:49; 2 Kings 24:20-25:2; Jeremiah 52:3-5; Jeremiah 39:1; Ezekiel 24:1-14");
        target.get("August").put(23, "Ezekiel 24:15-25:17; Jeremiah 34:1-22; Jeremiah 21:1-14; Ezekiel 29:1-16; Ezekiel 30:20-31:18");
        target.get("August").put(24, "Jeremiah 32:1-33:26; Ezekiel 26:1-14");
        target.get("August").put(25, "Ezekiel 26:15-28:26; 2 Kings 25:3-7; Jeremiah 52:6-11; Jeremiah 39:2-10");
        target.get("August").put(26, "Jeremiah 39:11-18; Jeremiah 40:1-6; 2 Kings 25:8-21; Jeremiah 52:12-27; 2 Chronicles 36:15-21; Lamentations 1:1-22");
        target.get("August").put(27, "Lamentations 2:1-4:22");
        target.get("August").put(28, "Lamentations 5:1-22; Obadiah 1:1-21; 2 Kings 25:22-26; Jeremiah 40:7-41:18");
        target.get("August").put(29, "Jeremiah 42:1-44:30; Ezekiel 33:21-33");
        target.get("August").put(30, "Ezekiel 34:1-36:38");
        target.get("August").put(31, "Ezekiel 37:1-39:29; Ezekiel 32:1-16");
        target.get("September").put(1, "Ezekiel 32:17-33:20; Jeremiah 52:28-30; Psalms 137:1-9; 1 Chronicles 4:24-5:17");
        target.get("September").put(2, "1 Chronicles 5:18-26; 1 Chronicles 6:3; 1 Chronicles 6:49; 1 Chronicles 6:4-15; 1 Chronicles 7:1-8:28");
        target.get("September").put(3, "1 Chronicles 8:29-9:1; Daniel 4:1-37; Ezekiel 40:1-37");
        target.get("September").put(4, "Ezekiel 40:38-43:27");
        target.get("September").put(5, "Ezekiel 44:1-46:24");
        target.get("September").put(6, "Ezekiel 47:1-48:35; Ezekiel 29:17-30:19; 2 Kings 25:27-30; Jeremiah 52:31-34");
        target.get("September").put(7, "Daniel 7:1-8:27; Daniel 5:1-31");
        target.get("September").put(8, "Daniel 6:1-28; Daniel 9:1-27; 2 Chronicles 36:22-23; Ezra 1:1-11; 1 Chronicles 3:17-19");
        target.get("September").put(9, "Ezra 2:1-4:5; 1 Chronicles 3:19-24");
        target.get("September").put(10, "Daniel 10:1-12:13; Ezra 4:24-5:1; Haggai 1:1-15");
        target.get("September").put(11, "Haggai 2:1-9; Zechariah 1:1-6; Haggai 2:10-19; Ezra 5:2; Haggai 2:20-23; Zechariah 1:7-5:11");
        target.get("September").put(12, "Zechariah 6:1-15; Ezra 5:3-6:14; Zechariah 7:1-8:23");
        target.get("September").put(13, "Zechariah 9:1-14:21");
        target.get("September").put(14, "Ezra 6:14-22; Ezra 4:6; Esther 1:1-4:17");
        target.get("September").put(15, "Esther 5:1-10:3");
        target.get("September").put(16, "Ezra 4:7-23; Ezra 7:1-8:36");
        target.get("September").put(17, "Ezra 9:1-10:44; Nehemiah 1:1-2:20");
        target.get("September").put(18, "Nehemiah 3:1-5:13; Nehemiah 6:1-7:3");
        target.get("September").put(19, "Nehemiah 7:4-8:12");
        target.get("September").put(20, "Nehemiah 8:13-10:39");
        target.get("September").put(21, "Nehemiah 11:1-12:26; 1 Chronicles 9:1-34");
        target.get("September").put(22, "Nehemiah 12:27-13:6; Nehemiah 5:14-19; Nehemiah 13:7-31; Malachi 1:1-2:9");
        target.get("September").put(23, "Malachi 2:10-4:6; Joel 1:1-3:21");
        target.get("September").put(24, "Mark 1:1; Luke 1:1-4; John 1:1-18; Matthew 1:1-17; Luke 3:23-38; Luke 1:5-38");
        target.get("September").put(25, "Luke 1:39-80; Matthew 1:18-25; Luke 2:1-40");
        target.get("September").put(26, "Matthew 2:1-23; Luke 2:41-52; Mark 1:2-8; Matthew 3:1-12; Luke 3:1-18; Mark 1:9-11; Matthew 3:13-17; Luke 3:21-22");
        target.get("September").put(27, "Mark 1:12-13; Matthew 4:1-11; Luke 4:1-15; John 1:19-2:25");
        target.get("September").put(28, "John 3:1-4:45; Luke 3:19-20");
        target.get("September").put(29, "Mark 1:14-15; Matthew 4:12-17; Luke 3:23; John 4:46-54; Luke 4:16-30; Mark 1:16-20; Matthew 4:18-22; Mark 1:21-28; Luke 4:31-37; Mark 1:29-34; Matthew 8:14-17; Luke 4:38-41; Mark 1:35-39; Luke 4:42-44; Matthew 4:23-25");
        target.get("September").put(30, "Luke 5:1-11; Mark 1:40-45; Matthew 8:1-4; Luke 5:12-16; Mark 2:1-12; Matthew 9:1-8; Luke 5:17-26; Mark 2:13-17; Matthew 9:9-13; Luke 5:27-32; Mark 2:18-22; Matthew 9:14-17; Luke 5:33-39");
        target.get("October").put(1, "John 5:1-47; Mark 2:23-28; Matthew 12:1-8; Luke 6:1-5; Mark 3:1-6; Matthew 12:9-14; Luke 6:6-11; Matthew 12:15-21");
        target.get("October").put(2, "Mark 3:7-19; Luke 6:12-16; Matthew 5:1-12; Luke 6:17-26; Matthew 5:13-48; Luke 6:27-36; Matthew 6:1-4");
        target.get("October").put(3, "Matthew 6:5-7:6; Luke 6:37-42; Matthew 7:7-20; Luke 6:43-45; Matthew 7:21-29; Luke 6:46-49");
        target.get("October").put(4, "Matthew 8:5-13; Luke 7:1-17; Matthew 11:1-19; Luke 7:18-35; Matthew 11:20-30; Luke 7:36-50");
        target.get("October").put(5, "Luke 8:1-3; Mark 3:20-30; Matthew 12:22-45; Mark 3:31-35; Matthew 12:46-50; Luke 8:19-21; Mark 4:1-9; Matthew 13:1-9; Luke 8:4-8; Mark 4:10-20");
        target.get("October").put(6, "Matthew 13:10-23; Luke 8:9-18; Mark 4:21-29; Matthew 13:24-30; Mark 4:30-34; Matthew 13:31-52; Mark 4:35-41; Matthew 8:23-27; Luke 8:22-25");
        target.get("October").put(7, "Mark 5:1-20; Matthew 8:28-34; Luke 8:26-39; Mark 5:21-43; Matthew 9:18-26; Luke 8:40-56");
        target.get("October").put(8, "Matthew 9:27-34; Mark 6:1-6; Matthew 13:53-58; Matthew 9:35-38; Mark 6:7-13; Matthew 10:1-42; Luke 9:1-6");
        target.get("October").put(9, "Luke 9:7-9; Mark 6:14-29; Matthew 14:1-21; Mark 6:30-44; Luke 9:10-17; John 6:1-15; Mark 6:45-52; Matthew 14:22-33; John 6:16-21; Mark 6:53-56; Matthew 14:34-36");
        target.get("October").put(10, "John 6:22-71; Mark 7:1-23; Matthew 15:1-20");
        target.get("October").put(11, "Mark 7:24-30; Matthew 15:21-28; Mark 7:31-37; Matthew 15:29-31; Mark 8:1-10; Matthew 15:32-16:4; Mark 8:11-21; Matthew 16:5-12");
        target.get("October").put(12, "Mark 8:22-30; Matthew 16:13-20; Luke 9:18-20; Mark 8:31-9:1; Matthew 16:21-28; Luke 9:21-27; Mark 9:2-13; Matthew 17:1-13; Luke 9:28-36");
        target.get("October").put(13, "Mark 9:14-29; Matthew 17:14-21; Luke 9:37-43; Mark 9:30-32; Matthew 17:22-23; Luke 9:43-45; Matthew 17:24-27; Mark 9:33-37; Matthew 18:1-6; Luke 9:46-48; Mark 9:38-41; Luke 9:49-50; Mark 9:42-50; Matthew 18:7-35");
        target.get("October").put(14, "John 7:1-9; Luke 9:51-56; Matthew 8:18-22; Luke 9:57-62; John 7:10-8:20");
        target.get("October").put(15, "John 8:21-59; Luke 10:1-11:13");
        target.get("October").put(16, "Luke 11:14-12:34");
        target.get("October").put(17, "Luke 12:35-13:21; John 9:1-41");
        target.get("October").put(18, "John 10:1-42; Luke 13:22-14:24");
        target.get("October").put(19, "Luke 14:25-17:10; John 11:1-37");
        target.get("October").put(20, "John 11:38-57; Luke 17:11-18:8");
        target.get("October").put(21, "Luke 18:9-14; Mark 10:1-12; Matthew 19:1-12; Mark 10:13-16; Matthew 19:13-15; Luke 18:15-17; Mark 10:17-31; Matthew 19:16-30; Luke 18:18-30");
        target.get("October").put(22, "Matthew 20:1-16; Mark 10:32-34; Matthew 20:17-19; Luke 18:31-34; Mark 10:35-45; Matthew 20:20-34; Mark 10:46-52; Luke 18:35-19:27");
        target.get("October").put(23, "Mark 14:3-9; Matthew 26:6-13; John 12:1-11; Mark 11:1-11; Matthew 21:1-11; Luke 19:28-40; John 12:12-19; Luke 19:41-44; John 12:20-36");
        target.get("October").put(24, "John 12:37-50; Mark 11:12-14; Matthew 21:18-22; Mark 11:15-19; Matthew 21:12-17; Luke 19:45-48; Mark 11:20-33; Matthew 21:23-27; Luke 20:1-8");
        target.get("October").put(25, "Matthew 21:28-32; Mark 12:1-12; Matthew 21:33-46; Luke 20:9-19; Matthew 22:1-14; Mark 12:13-17; Matthew 22:15-22; Luke 20:20-26; Mark 12:18-27; Matthew 22:23-33; Luke 20:27-40");
        target.get("October").put(26, "Mark 12:28-34; Matthew 22:34-40; Mark 12:35-37; Matthew 22:41-46; Luke 20:41-44; Mark 12:38-40; Matthew 23:1-12; Luke 20:45-47; Matthew 23:13-39; Mark 12:41-44; Luke 21:1-4");
        target.get("October").put(27, "Mark 13:1-23; Matthew 24:1-25; Luke 21:5-24; Mark 13:24-31; Matthew 24:26-35; Luke 21:25-33");
        target.get("October").put(28, "Mark 13:32-37; Matthew 24:36-51; Luke 21:34-38; Matthew 25:1-46");
        target.get("October").put(29, "Mark 14:1-2; Matthew 26:1-5; Luke 22:1-2; Mark 14:10-11; Matthew 26:14-16; Luke 22:3-6; Mark 14:12-16; Matthew 26:17-19; Luke 22:7-13; John 13:1-20; Mark 14:17-26; Matthew 26:20-30; Luke 22:14-30; John 13:18-30");
        target.get("October").put(30, "John 13:31-38; Mark 14:27-31; Matthew 26:31-35; Luke 22:31-38; John 14:1-15:17");
        target.get("October").put(31, "John 15:18-17:26");
        target.get("November").put(1, "John 18:1-2; Mark 14:32-42; Matthew 26:36-46; Luke 22:39-46; Mark 14:43-52; Matthew 26:47-56; Luke 22:47-53; John 18:3-24");
        target.get("November").put(2, "Mark 14:53-65; Matthew 26:57-68; Mark 14:66-72; Matthew 26:69-75; Luke 22:54-65; John 18:25-27; Mark 15:1; Matthew 27:1-2; Luke 22:66-71; Matthew 27:3-10");
        target.get("November").put(3, "Mark 15:2-5; Matthew 27:11-14; Luke 23:1-12; John 18:28-40; Mark 15:6-15; Matthew 27:15-26; Luke 23:13-25; John 19:1-16; Mark 15:16-20; Matthew 27:27-31");
        target.get("November").put(4, "Mark 15:21-24; Matthew 27:32-34; Luke 23:26-31; John 19:17; Mark 15:25-32; Matthew 27:35-44; Luke 23:32-43; John 19:18-27; Mark 15:33-41; Matthew 27:45-56; Luke 23:44-49; John 19:28-37");
        target.get("November").put(5, "Mark 15:42-47; Matthew 27:57-61; Luke 23:50-56; John 19:38-42; Matthew 27:62-66; Mark 16:1-8; Matthew 28:1-7; Luke 24:1-12; Mark 16:9-11; John 20:1-18; Matthew 28:8-15");
        target.get("November").put(6, "Luke 24:13-43; Mark 16:12-13; John 20:19-23; Mark 16:14; John 20:24-21:25; Matthew 28:16-20; Mark 16:15-18; Luke 24:44-49");
        target.get("November").put(7, "Mark 16:19-20; Luke 24:50-53; Acts 1:1-2:47");
        target.get("November").put(8, "Acts 3:1-5:42");
        target.get("November").put(9, "Acts 6:1-8:1");
        target.get("November").put(10, "Acts 8:1-9:43");
        target.get("November").put(11, "Acts 10:1-12:5");
        target.get("November").put(12, "Acts 12:6-14:20");
        target.get("November").put(13, "Acts 14:21-28; Galatians 1:1-3:23");
        target.get("November").put(14, "Galations 3:24-6:18; Acts 15:1-21");
        target.get("November").put(15, "Acts 15:22-17:15");
        target.get("November").put(16, "Acts 17:16-18:3; 1 Thessalonians 1:1-5:11");
        target.get("November").put(17, "1 Thessalonians 5:12-28; 2 Thessalonians 1:1-3:18; Acts 18:4-23");
        target.get("November").put(18, "Acts 18:24-19:20; 1 Corinthians 1:1-3:23");
        target.get("November").put(19, "1 Corinthians 4:1-7:40");
        target.get("November").put(20, "1 Corinthians 8:1-11:1");
        target.get("November").put(21, "1 Corinthians 11:2-13:13");
        target.get("November").put(22, "1 Corinthians 14:1-15:58");
        target.get("November").put(23, "1 Corinthians 16:1-24; Acts 19:21-20:6; Romans 1:1-32");
        target.get("November").put(24, "Romans 2:1-4:25");
        target.get("November").put(25, "Romans 5:1-8:17");
        target.get("November").put(26, "Romans 8:18-10:21");
        target.get("November").put(27, "Romans 11:1-14:23");
        target.get("November").put(28, "Romans 15:1-16:27; 2 Corinthians 1:1-2:4");
        target.get("November").put(29, "2 Corinthians2:5-6:13");
        target.get("November").put(30, "2 Corinthians 6:14-10:18");
        target.get("December").put(1, "2 Corinthians 11:1-13:13; Acts 20:7-12");
        target.get("December").put(2, "Acts 20:13-21:36");
        target.get("December").put(3, "Acts 21:37-23:35");
        target.get("December").put(4, "Acts 24:1-26:32");
        target.get("December").put(5, "Acts 27:1-44");
        target.get("December").put(6, "Acts 28:1-31; Ephesians 1:1-2:22");
        target.get("December").put(7, "Ephesians 3:1-5:14");
        target.get("December").put(8, "Ephesians 5:15-6:23; Colossians 1:1-23");
        target.get("December").put(9, "Colossians 1:24-4:18");
        target.get("December").put(10, "Philemon 1:1-25; Philippians 1:1-2:11");
        target.get("December").put(11, "Philippians 2:12-4:23");
        target.get("December").put(12, "James 1:1-3:18");
        target.get("December").put(13, "James 4:1-5:20; 1 Timothy 1:1-2:15");
        target.get("December").put(14, "1 Timothy 3:1-6:10");
        target.get("December").put(15, "1 Timothy 6:11-21; Titus 1:1-3:15; 2 Timothy 1:1-18");
        target.get("December").put(16, "2 Timothy 2:1-4:18");
        target.get("December").put(17, "2 Timothy 4:19-22; Hebrews 1:1-4:13");
        target.get("December").put(18, "Hebrews 4:14-7:28");
        target.get("December").put(19, "Hebrews 8:1-10:39");
        target.get("December").put(20, "Hebrews 11:1-12:29");
        target.get("December").put(21, "Hebrews 13:1-25; 1 Peter 1:1-2:3");
        target.get("December").put(22, "1 Peter 2:4-5:11");
        target.get("December").put(23, "1 Peter 5:12-14; 2 Peter 1:1-3:18");
        target.get("December").put(24, "1 John 1:1-4:6");
        target.get("December").put(25, "1 John 4:7-5:21; 2 John 1:1-13; 3 John 1:1-15");
        target.get("December").put(26, "Jude 1:1-25; Revelation 1:1-2:29");
        target.get("December").put(27, "Revelation 3:1-6:17");
        target.get("December").put(28, "Revelation 7:1-10:11");
        target.get("December").put(29, "Revelation 11:1-14:20");
        target.get("December").put(30, "Revelation 15:1-18:24");
        target.get("December").put(31, "Revelation 19:1-22:21");
    }

    private ReadingDay findReadingDay(String month, int day) {
        for (ReadingDay readingDay : readingDays) {
            if (readingDay.getMonth().equals(month) && readingDay.getDay() == day) {
                return readingDay;
            }
        }
        return null;
    }

    private int findReadingIndex(String month, int day) {
        for (int i = 0; i < readingDays.size(); i++) {
            ReadingDay readingDay = readingDays.get(i);
            if (readingDay.getMonth().equals(month) && readingDay.getDay() == day) {
                return i;
            }
        }
        return -1;
    }

    // ================================================================
    // Verse of the Day
    // ================================================================

    private static class DailyVerse {
        private final String reference;
        private final String text;

        private DailyVerse(String reference, String text) {
            this.reference = reference;
            this.text = text;
        }
    }

    private DailyVerse getVerseOfTheDay(LocalDate date) {
        /*
         * These Verse of the Day texts use the public-domain
         * King James Version (KJV), so the verse can be displayed
         * directly on the home screen without opening BibleGateway.
         *
         * A date-seeded random generator chooses the entry. This means
         * the verse is random for each day but remains the same if the
         * application is reopened during that same day.
         */
        DailyVerse[] dailyVerses = {
                new DailyVerse(
                        "Genesis 1:1",
                        "In the beginning God created the heaven and the earth."
                ),
                new DailyVerse(
                        "Joshua 1:9",
                        "Have not I commanded thee? Be strong and of a good courage; "
                                + "be not afraid, neither be thou dismayed: for the LORD thy God "
                                + "is with thee whithersoever thou goest."
                ),
                new DailyVerse(
                        "Psalm 23:1",
                        "The LORD is my shepherd; I shall not want."
                ),
                new DailyVerse(
                        "Psalm 27:1",
                        "The LORD is my light and my salvation; whom shall I fear? "
                                + "the LORD is the strength of my life; of whom shall I be afraid?"
                ),
                new DailyVerse(
                        "Psalm 34:8",
                        "O taste and see that the LORD is good: blessed is the man that trusteth in him."
                ),
                new DailyVerse(
                        "Psalm 46:1",
                        "God is our refuge and strength, a very present help in trouble."
                ),
                new DailyVerse(
                        "Psalm 56:3",
                        "What time I am afraid, I will trust in thee."
                ),
                new DailyVerse(
                        "Psalm 100:5",
                        "For the LORD is good; his mercy is everlasting; "
                                + "and his truth endureth to all generations."
                ),
                new DailyVerse(
                        "Psalm 118:24",
                        "This is the day which the LORD hath made; we will rejoice and be glad in it."
                ),
                new DailyVerse(
                        "Psalm 119:105",
                        "Thy word is a lamp unto my feet, and a light unto my path."
                ),
                new DailyVerse(
                        "Proverbs 3:5-6",
                        "Trust in the LORD with all thine heart; and lean not unto thine own understanding. "
                                + "In all thy ways acknowledge him, and he shall direct thy paths."
                ),
                new DailyVerse(
                        "Proverbs 16:3",
                        "Commit thy works unto the LORD, and thy thoughts shall be established."
                ),
                new DailyVerse(
                        "Isaiah 26:3",
                        "Thou wilt keep him in perfect peace, whose mind is stayed on thee: "
                                + "because he trusteth in thee."
                ),
                new DailyVerse(
                        "Isaiah 40:31",
                        "But they that wait upon the LORD shall renew their strength; "
                                + "they shall mount up with wings as eagles; they shall run, and not be weary; "
                                + "and they shall walk, and not faint."
                ),
                new DailyVerse(
                        "Isaiah 41:10",
                        "Fear thou not; for I am with thee: be not dismayed; for I am thy God: "
                                + "I will strengthen thee; yea, I will help thee; yea, I will uphold thee "
                                + "with the right hand of my righteousness."
                ),
                new DailyVerse(
                        "Jeremiah 29:11",
                        "For I know the thoughts that I think toward you, saith the LORD, "
                                + "thoughts of peace, and not of evil, to give you an expected end."
                ),
                new DailyVerse(
                        "Lamentations 3:22-23",
                        "It is of the LORD'S mercies that we are not consumed, because his compassions fail not. "
                                + "They are new every morning: great is thy faithfulness."
                ),
                new DailyVerse(
                        "Micah 6:8",
                        "He hath shewed thee, O man, what is good; and what doth the LORD require of thee, "
                                + "but to do justly, and to love mercy, and to walk humbly with thy God?"
                ),
                new DailyVerse(
                        "Matthew 5:16",
                        "Let your light so shine before men, that they may see your good works, "
                                + "and glorify your Father which is in heaven."
                ),
                new DailyVerse(
                        "Matthew 6:33",
                        "But seek ye first the kingdom of God, and his righteousness; "
                                + "and all these things shall be added unto you."
                ),
                new DailyVerse(
                        "Matthew 11:28",
                        "Come unto me, all ye that labour and are heavy laden, and I will give you rest."
                ),
                new DailyVerse(
                        "Matthew 19:26",
                        "But Jesus beheld them, and said unto them, With men this is impossible; "
                                + "but with God all things are possible."
                ),
                new DailyVerse(
                        "Luke 1:37",
                        "For with God nothing shall be impossible."
                ),
                new DailyVerse(
                        "John 1:1",
                        "In the beginning was the Word, and the Word was with God, and the Word was God."
                ),
                new DailyVerse(
                        "John 3:16",
                        "For God so loved the world, that he gave his only begotten Son, "
                                + "that whosoever believeth in him should not perish, but have everlasting life."
                ),
                new DailyVerse(
                        "John 8:12",
                        "Then spake Jesus again unto them, saying, I am the light of the world: "
                                + "he that followeth me shall not walk in darkness, but shall have the light of life."
                ),
                new DailyVerse(
                        "John 14:6",
                        "Jesus saith unto him, I am the way, the truth, and the life: "
                                + "no man cometh unto the Father, but by me."
                ),
                new DailyVerse(
                        "John 14:27",
                        "Peace I leave with you, my peace I give unto you: not as the world giveth, "
                                + "give I unto you. Let not your heart be troubled, neither let it be afraid."
                ),
                new DailyVerse(
                        "John 16:33",
                        "These things I have spoken unto you, that in me ye might have peace. "
                                + "In the world ye shall have tribulation: but be of good cheer; I have overcome the world."
                ),
                new DailyVerse(
                        "Romans 5:8",
                        "But God commendeth his love toward us, in that, while we were yet sinners, Christ died for us."
                ),
                new DailyVerse(
                        "Romans 8:28",
                        "And we know that all things work together for good to them that love God, "
                                + "to them who are the called according to his purpose."
                ),
                new DailyVerse(
                        "Romans 8:31",
                        "What shall we then say to these things? If God be for us, who can be against us?"
                ),
                new DailyVerse(
                        "Romans 12:2",
                        "And be not conformed to this world: but be ye transformed by the renewing of your mind, "
                                + "that ye may prove what is that good, and acceptable, and perfect, will of God."
                ),
                new DailyVerse(
                        "Romans 15:13",
                        "Now the God of hope fill you with all joy and peace in believing, "
                                + "that ye may abound in hope, through the power of the Holy Ghost."
                ),
                new DailyVerse(
                        "1 Corinthians 13:13",
                        "And now abideth faith, hope, charity, these three; but the greatest of these is charity."
                ),
                new DailyVerse(
                        "2 Corinthians 5:7",
                        "For we walk by faith, not by sight."
                ),
                new DailyVerse(
                        "2 Corinthians 12:9",
                        "And he said unto me, My grace is sufficient for thee: for my strength is made perfect in weakness."
                ),
                new DailyVerse(
                        "Galatians 6:9",
                        "And let us not be weary in well doing: for in due season we shall reap, if we faint not."
                ),
                new DailyVerse(
                        "Ephesians 2:8-9",
                        "For by grace are ye saved through faith; and that not of yourselves: it is the gift of God: "
                                + "Not of works, lest any man should boast."
                ),
                new DailyVerse(
                        "Ephesians 4:32",
                        "And be ye kind one to another, tenderhearted, forgiving one another, "
                                + "even as God for Christ's sake hath forgiven you."
                ),
                new DailyVerse(
                        "Philippians 4:6-7",
                        "Be careful for nothing; but in every thing by prayer and supplication with thanksgiving "
                                + "let your requests be made known unto God. And the peace of God, which passeth all understanding, "
                                + "shall keep your hearts and minds through Christ Jesus."
                ),
                new DailyVerse(
                        "Philippians 4:13",
                        "I can do all things through Christ which strengtheneth me."
                ),
                new DailyVerse(
                        "Philippians 4:19",
                        "But my God shall supply all your need according to his riches in glory by Christ Jesus."
                ),
                new DailyVerse(
                        "1 Thessalonians 5:16-18",
                        "Rejoice evermore. Pray without ceasing. In every thing give thanks: "
                                + "for this is the will of God in Christ Jesus concerning you."
                ),
                new DailyVerse(
                        "2 Timothy 1:7",
                        "For God hath not given us the spirit of fear; but of power, and of love, and of a sound mind."
                ),
                new DailyVerse(
                        "Hebrews 11:1",
                        "Now faith is the substance of things hoped for, the evidence of things not seen."
                ),
                new DailyVerse(
                        "Hebrews 13:8",
                        "Jesus Christ the same yesterday, and to day, and for ever."
                ),
                new DailyVerse(
                        "James 1:5",
                        "If any of you lack wisdom, let him ask of God, that giveth to all men liberally, "
                                + "and upbraideth not; and it shall be given him."
                ),
                new DailyVerse(
                        "1 Peter 5:7",
                        "Casting all your care upon him; for he careth for you."
                ),
                new DailyVerse(
                        "1 John 1:9",
                        "If we confess our sins, he is faithful and just to forgive us our sins, "
                                + "and to cleanse us from all unrighteousness."
                ),
                new DailyVerse(
                        "1 John 4:19",
                        "We love him, because he first loved us."
                ),
                new DailyVerse(
                        "Revelation 21:4",
                        "And God shall wipe away all tears from their eyes; and there shall be no more death, "
                                + "neither sorrow, nor crying, neither shall there be any more pain: "
                                + "for the former things are passed away."
                )
        };

        /*
         * Use Java's Random generator, seeded with today's date.
         *
         * This gives us a randomly selected verse for each calendar day,
         * while keeping that verse stable for the entire day. Reopening
         * the application on the same date will therefore show the same
         * random verse. On the next date, a new random selection is made.
         */
        Random random = new Random(date.toEpochDay());
        int index = random.nextInt(dailyVerses.length);

        return dailyVerses[index];
    }

    // ================================================================
    // Chronological Reading Calendar
    // ================================================================

    private VBox createReadingCalendar(LocalDate monthDate) {
        String monthName =
                monthDate
                        .getMonth()
                        .getDisplayName(
                                TextStyle.FULL,
                                Locale.ENGLISH
                        );

        Label calendarTitle = new Label(
                monthName + " " + monthDate.getYear()
        );
        calendarTitle.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        24
                )
        );
        calendarTitle.setStyle("-fx-text-fill: #222222;");

        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setAlignment(Pos.CENTER);

        String[] weekdays = {
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        };

        for (int column = 0; column < 7; column++) {
            Label weekdayLabel =
                    new Label(weekdays[column]);

            weekdayLabel.setFont(
                    Font.font(
                            "Serif",
                            FontWeight.BOLD,
                            13
                    )
            );
            weekdayLabel.setAlignment(Pos.CENTER);
            weekdayLabel.setPrefWidth(130);
            weekdayLabel.setStyle("-fx-text-fill: #222222;");

            calendarGrid.add(
                    weekdayLabel,
                    column,
                    0
            );
        }

        LocalDate firstOfMonth =
                monthDate.withDayOfMonth(1);

        /*
         * Java DayOfWeek uses Monday=1 ... Sunday=7.
         * Convert it to a Sunday-first calendar index.
         */
        int firstColumn =
                firstOfMonth
                        .getDayOfWeek()
                        .getValue() % 7;

        int daysInMonth =
                monthDate.lengthOfMonth();

        /*
         * Read the completion state for this calendar year once.
         *
         * A date is shown with a check mark only when its
         * reading_progress row says completed = TRUE.
         * Unfinished readings remain unchecked.
         */
        Set<String> completedCalendarDays =
                loadCompletedReadingKeysForYear(
                        monthDate.getYear()
                );

        for (int day = 1; day <= daysInMonth; day++) {
            int calendarIndex =
                    firstColumn + day - 1;

            int column =
                    calendarIndex % 7;

            int row =
                    (calendarIndex / 7) + 1;

            int readingIndex =
                    findReadingIndex(
                            monthName,
                            day
                    );

            String readingText =
                    "No reading assigned";

            if (
                    readingIndex >= 0
                            && readingIndex < readingDays.size()
            ) {
                readingText =
                        readingDays
                                .get(readingIndex)
                                .getReading()
                                .replace("\r", "")
                                .replace("\n", "; ")
                                .trim();
            }

            boolean completed =
                    completedCalendarDays.contains(
                            progressKey(
                                    monthName,
                                    day
                            )
                    );

            String dayHeading =
                    completed
                            ? "✓ " + day
                            : Integer.toString(day);

            Button dayButton = new Button(
                    dayHeading + "\n" + readingText
            );

            dayButton.setWrapText(true);
            dayButton.setAlignment(Pos.TOP_LEFT);
            dayButton.setPrefSize(130, 82);
            dayButton.setMinSize(115, 72);
            dayButton.setMaxSize(
                    Double.MAX_VALUE,
                    Double.MAX_VALUE
            );

            if (readingIndex >= 0) {
                final int selectedReadingIndex =
                        readingIndex;

                dayButton.setOnAction(
                        event ->
                                openReading(
                                        selectedReadingIndex
                                )
                );
            } else {
                dayButton.setDisable(true);
            }

            if (
                    monthDate.getYear()
                            == LocalDate.now().getYear()
                            && monthDate.getMonth()
                            == LocalDate.now().getMonth()
                            && day
                            == LocalDate.now()
                                    .getDayOfMonth()
            ) {
                dayButton.setStyle(
                        "-fx-font-weight: bold;"
                                + "-fx-border-width: 2;"
                                + (completed
                                        ? "-fx-background-color: #e8f3e8;"
                                        : "")
                );
            }

            calendarGrid.add(
                    dayButton,
                    column,
                    row
            );
        }

        VBox calendarPanel = new VBox(
                10,
                calendarTitle,
                calendarGrid
        );
        calendarPanel.setAlignment(Pos.CENTER);
        calendarPanel.setPadding(
                new Insets(10)
        );

        return calendarPanel;
    }

    private void showReadingPlanHome() {
        currentStudyReference = null;
        currentDayIndex = -1;

        stopHeaderHideTimer();
        stopSidesHideTimer();
        showHeader();
        showLeftSide();
        showRightSide();
        clearPersonalityProfile(
                "Select a Bible reading to see available personality profiles."
        );
        clearCharts(
                "Select a Bible reading to see matching charts."
        );
        clearBookIntroduction(
                "Open a Bible reading to see the matching book introduction."
        );
        clearOriginalLanguage(
                "Select a Bible reading to open its Hebrew or Greek interlinear."
        );

        if (
                topInfoTabs != null
                        && bookIntroductionTab != null
                        && personalityProfileTab != null
        ) {
            if (!topInfoTabs.getTabs().contains(bookIntroductionTab)) {
                topInfoTabs.getTabs().add(0, bookIntroductionTab);
            }

            if (!topInfoTabs.getTabs().contains(personalityProfileTab)) {
                topInfoTabs.getTabs().add(personalityProfileTab);
            }

            if (chartsTab != null && !topInfoTabs.getTabs().contains(chartsTab)) {
                topInfoTabs.getTabs().add(chartsTab);
            }

            topInfoTabs.getSelectionModel().select(personalityProfileTab);
        }

        dateLabel.setText("Reading Plan");
        passageLabel.setText(
                "Choose a chapter from Books of the Bible, select a day from the Reading Plan, "
                        + "or go directly to today's reading."
        );

        Label welcome = new Label(
                getCurrentReadingPlanName()
        );
        welcome.setFont(
                Font.font(
                        "Serif",
                        FontWeight.BOLD,
                        30
                )
        );
        welcome.setStyle("-fx-text-fill: #222222;");

        LocalDate today = LocalDate.now();

        String todayMonth =
                today
                        .getMonth()
                        .getDisplayName(
                                TextStyle.FULL,
                                Locale.ENGLISH
                        );

        int todayDay =
                today.getDayOfMonth();

        int todayReadingIndex =
                findReadingIndex(
                        todayMonth,
                        todayDay
                );

        Label instructions = new Label(
                "Select any date in the calendar to open that day's reading from the selected plan."
        );
        instructions.setFont(
                Font.font("Serif", 17)
        );
        instructions.setStyle("-fx-text-fill: #222222;");
        instructions.setWrapText(true);
        instructions.setMaxWidth(800);
        instructions.setAlignment(Pos.CENTER);

        VBox calendarPanel =
                createReadingCalendar(today);

        Button todayButton = new Button(
                "Today's Reading — "
                        + todayMonth
                        + " "
                        + todayDay
                        + " ▶"
        );

        todayButton.setPrefWidth(290);
        todayButton.setPrefHeight(42);

        todayButton.setOnAction(event -> {
            if (todayReadingIndex >= 0) {
                openReading(
                        todayReadingIndex
                );
            }
        });

        if (todayReadingIndex < 0) {
            todayButton.setDisable(true);
            todayButton.setText(
                    "Today's Reading Unavailable"
            );
        }

        VBox home = new VBox(
                18,
                welcome,
                instructions,
                calendarPanel,
                todayButton
        );

        home.setAlignment(Pos.TOP_CENTER);
        home.setPadding(new Insets(20));
        home.setStyle("-fx-background-color: #f7f7f7;");

        /*
         * Make the Reading Plan / calendar screen vertically scrollable.
         * This is especially useful when the header, timeline, or a
         * smaller display leaves less vertical room for the calendar.
         */
        ScrollPane calendarScrollPane = new ScrollPane(home);
        calendarScrollPane.setFitToWidth(true);
        calendarScrollPane.setPannable(true);
        calendarScrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        calendarScrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        calendarScrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
        );

        mainContentHolder.setCenter(calendarScrollPane);

        /*
         * The Reading Plan home is the navigation screen, so show all
         * panels there. Auto-hide resumes once an actual reading opens.
         */
        stopSidesHideTimer();
        stopBottomHideTimer();
        showLeftSide();
        showRightSide();
        showBottomTimeline();

        addEntryButton.setDisable(false);
        removeEntryButton.setDisable(false);
        setCompletionCheckBoxForHome();

    }

    private void openReading(int index) {
        if (index < 0 || index >= readingDays.size()) return;

        currentStudyReference = null;
        currentDayIndex = index;

        ReadingDay readingDay = readingDays.get(index);

        showHeader();
        showLeftSide();
        showRightSide();
        dateLabel.setText(readingDay.getDisplayDate());
        passageLabel.setText(readingDay.getReading());
        notesTitleLabel.setText("My Journal — " + readingDay.getDisplayDate());

        addEntryButton.setDisable(false);
        removeEntryButton.setDisable(false);
        newNoteArea.clear();
        noteStatusLabel.setText("");

        loadNoteHistory();
        loadCurrentReadingCompletion();
        updateStudyNotesForReference(readingDay.getReading());

        /*
         * If today's chronological reading is the first appearance of
         * one or more Bible books in the Reading Plan, automatically
         * open that book's Introduction tab.
         */
        showIntroductionForNewBooks(index, readingDay.getReading());

        String primaryVersion = translations.getOrDefault(
                translationSelector.getValue(),
                "ESV"
        );
        loadBibleGatewayPage(webEngine, readingDay, primaryVersion);

        if (comparisonVisible) reloadComparisonReading();

        mainContentHolder.setCenter(bibleJournalSplitPane);
        updateNavigationButtons();

        if (autoHideHeaderEnabled) scheduleHeaderHide();
        if (autoHideSidesEnabled) scheduleSidesHide();
    }

    private void openStudyChapter(String reference) {
        currentStudyReference = reference;
        currentDayIndex = -1;

        showHeader();
        showLeftSide();
        showRightSide();
        dateLabel.setText("Bible Study");
        passageLabel.setText(reference);
        notesTitleLabel.setText("My Journal — Bible Study");
        noteHistoryList.getItems().clear();
        newNoteArea.clear();
        noteStatusLabel.setText(
                "Journal entries are currently attached to chronological reading days."
        );
        addEntryButton.setDisable(true);
        removeEntryButton.setDisable(true);
        setCompletionCheckBoxForStudyMode();
        updateStudyNotesForReference(reference);

        String primaryVersion = translations.getOrDefault(
                translationSelector.getValue(),
                "ESV"
        );
        loadReference(webEngine, reference, primaryVersion);

        if (comparisonVisible) {
            String comparisonVersion = translations.getOrDefault(
                    comparisonTranslationSelector.getValue(),
                    "NIV"
            );
            loadReference(comparisonWebEngine, reference, comparisonVersion);
        }

        mainContentHolder.setCenter(bibleJournalSplitPane);
        updateNavigationButtons();

        if (autoHideHeaderEnabled) scheduleHeaderHide();
        if (autoHideSidesEnabled) scheduleSidesHide();
    }

    private void loadBibleGatewayPage(
            WebEngine engine,
            ReadingDay readingDay,
            String version
    ) {
        String passage = readingDay.getReading()
                .replace("\r", "")
                .replace("\n", "; ")
                .trim();

        loadReference(engine, passage, version);
    }

    private void loadReference(WebEngine engine, String reference, String version) {
        String encodedReference = URLEncoder.encode(reference, StandardCharsets.UTF_8);
        String url = "https://www.biblegateway.com/passage/?search="
                + encodedReference
                + "&version="
                + version;
        engine.load(url);
    }

    private void reloadCurrentReadingTranslation() {
        String version = translations.getOrDefault(
                translationSelector.getValue(),
                "ESV"
        );

        if (currentStudyReference != null) {
            loadReference(webEngine, currentStudyReference, version);
            return;
        }

        if (currentDayIndex < 0 || currentDayIndex >= readingDays.size()) return;

        loadBibleGatewayPage(webEngine, readingDays.get(currentDayIndex), version);
    }

    private void toggleComparisonView() {
        if (comparisonVisible) {
            bibleComparisonSplitPane.getItems().remove(comparisonPane);
            comparisonVisible = false;
            comparisonButton.setText("Add Comparison");
        } else {
            bibleComparisonSplitPane.getItems().add(comparisonPane);
            comparisonVisible = true;
            comparisonButton.setText("Remove Comparison");

            Platform.runLater(() -> {
                if (!bibleComparisonSplitPane.getDividers().isEmpty()) {
                    bibleComparisonSplitPane.setDividerPositions(COMPARISON_DIVIDER_POSITION);
                }
            });

            reloadComparisonReading();

            if (autoHideHeaderEnabled && !headerVisible) {
                hideComparisonToolbar();
            } else {
                showComparisonToolbar();
            }
        }
    }

    private void reloadComparisonReading() {
        if (!comparisonVisible) return;

        String version = translations.getOrDefault(
                comparisonTranslationSelector.getValue(),
                "NIV"
        );

        if (currentStudyReference != null) {
            loadReference(comparisonWebEngine, currentStudyReference, version);
            return;
        }

        if (currentDayIndex < 0 || currentDayIndex >= readingDays.size()) return;

        loadBibleGatewayPage(comparisonWebEngine, readingDays.get(currentDayIndex), version);
    }

    // ================================================================
    // Constant resizable Book Timeline panel (local EPUB)
    // ================================================================

    private void createTimelinePanel() {
        timelineTitleLabel = new Label("Book Timeline");
        timelineTitleLabel.setFont(Font.font("Serif", FontWeight.BOLD, 18));

        timelineSelector = new ComboBox<>();
        timelineSelector.setPromptText("Timeline for current reading");
        timelineSelector.setPrefWidth(260);
        timelineSelector.setOnAction(event -> loadSelectedTimeline());

        timelineStatusLabel = new Label(
                "Open a chronological reading to see the timeline chart for the current Bible book."
        );
        timelineStatusLabel.setWrapText(true);

        HBox timelineHeader = new HBox(
                12,
                timelineTitleLabel,
                timelineSelector,
                timelineStatusLabel
        );
        timelineHeader.setAlignment(Pos.CENTER_LEFT);

        timelineWebView = new WebView();
        timelineWebEngine = timelineWebView.getEngine();
        timelineWebView.setContextMenuEnabled(true);
        timelineWebView.setMinHeight(90);
        timelineWebView.setPrefHeight(190);

        timelinePanel = new VBox(
                6,
                timelineHeader,
                timelineWebView
        );
        timelinePanel.setPadding(new Insets(8, 10, 8, 10));
        timelinePanel.setMinHeight(95);
        timelinePanel.setPrefHeight(245);
        timelinePanel.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(timelineWebView, Priority.ALWAYS);

        clearTimeline(
                "Open a chronological reading to see the timeline chart for the current Bible book."
        );
    }

    private void updateTimelineForReference(String referenceText) {
        if (timelineSelector == null || timelineWebEngine == null) return;

        timelineEntries.clear();
        timelineSelector.getItems().clear();

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearTimeline(
                    "Study Bible EPUB not found. Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        Map<String, Set<Integer>> readingBooks =
                extractReadingChapterMap(referenceText);

        if (readingBooks.isEmpty()) {
            clearTimeline(
                    "No matching Bible book was found for this reading."
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            for (String book : readingBooks.keySet()) {
                addTimelineEntriesForBook(zipFile, book);
            }
        } catch (IOException error) {
            clearTimeline(
                    "Could not open the Study Bible EPUB: " + error.getMessage()
            );
            error.printStackTrace();
            return;
        }

        timelineSelector.getItems().addAll(timelineEntries.keySet());

        if (timelineEntries.isEmpty()) {
            clearTimeline(
                    "No timeline chart is included in this Study Bible for "
                            + String.join(", ", readingBooks.keySet()) + "."
            );
            return;
        }

        String firstTimeline =
                timelineEntries.keySet().iterator().next();

        timelineSelector.setValue(firstTimeline);
        loadSelectedTimeline();
    }

    private void addTimelineEntriesForBook(
            ZipFile zipFile,
            String book
    ) {
        String studyCode = studyBookCodes.get(book);
        if (studyCode == null || studyCode.length() < 2) return;

        String numberPrefix = studyCode.substring(0, 2) + "_";
        String folder = "BookTimelines/artfiles/nlt/";

        List<String> matches = new ArrayList<>();

        zipFile.stream()
                .map(ZipEntry::getName)
                .filter(name ->
                        name.startsWith(folder + numberPrefix)
                                && name.toLowerCase(Locale.ENGLISH).endsWith(".jpg")
                                && name.contains("_timeline")
                )
                .sorted()
                .forEach(matches::add);

        for (int i = 0; i < matches.size(); i++) {
            String label =
                    matches.size() == 1
                            ? book
                            : book + " — Timeline " + (i + 1);

            timelineEntries.putIfAbsent(
                    label,
                    matches.get(i)
            );
        }
    }

    private void loadSelectedTimeline() {
        if (timelineSelector == null || timelineWebEngine == null) return;

        String selection = timelineSelector.getValue();
        if (selection == null || selection.isBlank()) return;

        String entryName = timelineEntries.get(selection);

        if (entryName == null) {
            clearTimeline(
                    "Could not find the selected timeline chart."
            );
            return;
        }

        loadTimelineImage(selection, entryName);
    }

    private void loadTimelineImage(
            String displayName,
            String entryName
    ) {
        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearTimeline(
                    "Study Bible EPUB not found. Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            ZipEntry entry = zipFile.getEntry(entryName);

            if (entry == null) {
                clearTimeline(
                        "The selected timeline chart was not found in the EPUB."
                );
                return;
            }

            byte[] imageBytes;

            try (InputStream input = zipFile.getInputStream(entry)) {
                imageBytes = input.readAllBytes();
            }

            String base64 =
                    Base64.getEncoder().encodeToString(imageBytes);

            String html =
                    "<html><head><meta charset='UTF-8'>"
                            + "<style>"
                            + "html,body{margin:0;padding:0;background:#fff;"
                            + "width:100%;height:100%;overflow:auto;}"
                            + ".wrap{width:100%;min-height:100%;display:flex;"
                            + "align-items:center;justify-content:center;}"
                            + "img{display:block;width:100%;height:auto;max-width:none;}"
                            + "</style></head><body>"
                            + "<div class='wrap'>"
                            + "<img src='data:image/jpeg;base64,"
                            + base64
                            + "'>"
                            + "</div></body></html>";

            timelineTitleLabel.setText(
                    "Book Timeline — " + displayName
            );

            timelineStatusLabel.setText(
                    "Follows the Bible book in the current chronological reading."
            );

            timelineWebEngine.loadContent(
                    html,
                    "text/html"
            );

        } catch (IOException error) {
            clearTimeline(
                    "Could not load the timeline chart: " + error.getMessage()
            );
            error.printStackTrace();
        }
    }

    private void clearTimeline(String message) {
        timelineEntries.clear();

        if (timelineSelector != null) {
            timelineSelector.getItems().clear();
            timelineSelector.setValue(null);
        }

        if (timelineTitleLabel != null) {
            timelineTitleLabel.setText("Book Timeline");
        }

        if (timelineStatusLabel != null) {
            timelineStatusLabel.setText(message);
        }

        if (timelineWebEngine != null) {
            timelineWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;"
                            + "padding:12px;margin:0;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    private void createNavigationBar() {
        previousButton = new Button("◀ Previous");
        readingPlanButton = new Button("Reading Plan");
        nextButton = new Button("Next ▶");
        toggleReadingPlanButton = new Button("Hide Reading Plan");
        autoHideHeaderButton = new Button("Auto-hide Header: ON");
        autoHideSidesButton = new Button("Auto-hide Sides: ON");
        autoHideBottomButton = new Button("Auto-hide Bottom: ON");
        comparisonButton = new Button("Add Comparison");

        previousButton.setPrefWidth(125);
        readingPlanButton.setPrefWidth(125);
        nextButton.setPrefWidth(125);
        toggleReadingPlanButton.setPrefWidth(150);
        autoHideHeaderButton.setPrefWidth(170);
        autoHideSidesButton.setPrefWidth(170);
        autoHideBottomButton.setPrefWidth(180);
        comparisonButton.setPrefWidth(150);

        previousButton.setOnAction(event -> previousDay());
        readingPlanButton.setOnAction(event -> showReadingPlanHome());
        nextButton.setOnAction(event -> nextDay());
        toggleReadingPlanButton.setOnAction(event -> toggleReadingPlan());
        autoHideHeaderButton.setOnAction(event -> toggleAutoHideHeader());
        autoHideSidesButton.setOnAction(event -> toggleAutoHideSides());
        autoHideBottomButton.setOnAction(event -> toggleAutoHideBottom());
        comparisonButton.setOnAction(event -> toggleComparisonView());

        HBox navigation = new HBox(
                10,
                previousButton,
                readingPlanButton,
                nextButton,
                toggleReadingPlanButton,
                autoHideHeaderButton,
                autoHideSidesButton,
                autoHideBottomButton,
                comparisonButton
        );

        navigation.setAlignment(Pos.CENTER);
        navigation.setPadding(new Insets(10));

        /*
         * Main reading area + constant timeline are separated by a
         * draggable horizontal divider.
         */
        contentTimelineSplitPane = new SplitPane();
        contentTimelineSplitPane.setOrientation(Orientation.VERTICAL);
        contentTimelineSplitPane.getItems().addAll(
                readingPlanSplitPane,
                timelinePanel
        );
        contentTimelineSplitPane.setDividerPositions(
                TIMELINE_DIVIDER_POSITION
        );

        root.setCenter(contentTimelineSplitPane);
        root.setBottom(navigation);
    }

    private void previousDay() {
        if (currentStudyReference != null) {
            showReadingPlanHome();
            return;
        }

        if (currentDayIndex < 0) return;

        if (currentDayIndex == 0) {
            showReadingPlanHome();
            return;
        }

        openReading(currentDayIndex - 1);
    }

    private void nextDay() {
        if (currentStudyReference != null) {
            openReading(0);
            return;
        }

        if (currentDayIndex < 0) {
            openReading(0);
            return;
        }

        if (currentDayIndex >= readingDays.size() - 1) return;

        openReading(currentDayIndex + 1);
    }

    private void updateNavigationButtons() {
        if (previousButton == null || nextButton == null) return;

        if (currentStudyReference != null) {
            previousButton.setDisable(false);
            previousButton.setText("◀ Reading Plan");
            nextButton.setDisable(false);
            nextButton.setText("January 1 ▶");
            return;
        }

        previousButton.setText("◀ Previous");

        if (currentDayIndex < 0) {
            previousButton.setDisable(true);
            nextButton.setDisable(false);
            nextButton.setText("January 1 ▶");
            return;
        }

        previousButton.setDisable(false);

        if (currentDayIndex == readingDays.size() - 1) {
            nextButton.setDisable(true);
            nextButton.setText("Finished");
        } else {
            nextButton.setDisable(false);
            nextButton.setText("Next ▶");
        }
    }

    private void toggleReadingPlan() {

        if (readingPlanVisible) {

            if (
                    leftSideVisible
                            &&
                    !readingPlanSplitPane.getDividers().isEmpty()
            ) {
                savedReadingPlanDividerPosition =
                        readingPlanSplitPane
                                .getDividers()
                                .get(0)
                                .getPosition();
            }

            hideLeftSide();
            readingPlanVisible = false;
            toggleReadingPlanButton.setText("Show Reading Plan");

        } else {

            readingPlanVisible = true;
            showLeftSide();
            toggleReadingPlanButton.setText("Hide Reading Plan");

            if (autoHideSidesEnabled && isReadingContentOpen()) {
                scheduleSidesHide();
            }
        }
    }

    private boolean isReadingContentOpen() {
        return currentDayIndex >= 0 || currentStudyReference != null;
    }

    private void configureHeaderAutoShow(Scene scene) {

        scene.addEventFilter(
                MouseEvent.MOUSE_MOVED,
                event -> {

                    if (!isReadingContentOpen()) {
                        return;
                    }

                    double x = event.getSceneX();
                    double y = event.getSceneY();
                    double sceneWidth = scene.getWidth();
                    double sceneHeight = scene.getHeight();

                    /*
                     * TOP EDGE
                     */
                    if (y <= 15) {
                        showHeader();
                        stopHeaderHideTimer();
                    } else if (
                            autoHideHeaderEnabled
                                    && headerVisible
                                    && headerPanel != null
                    ) {
                        double headerHeight =
                                headerPanel.getHeight();

                        if (
                                y > headerHeight + 5
                                        && headerHideTimer.getStatus()
                                        != Animation.Status.RUNNING
                        ) {
                            scheduleHeaderHide();
                        }
                    }

                    /*
                     * LEFT / RIGHT EDGES
                     *
                     * When a side is visible, keep it open while the
                     * mouse is actually inside that panel. When hidden,
                     * moving to the corresponding screen edge restores it.
                     */
                    if (autoHideSidesEnabled) {

                        boolean overLeftPanel =
                                leftSideVisible
                                        && leftPanel != null
                                        && x <= leftPanel.getWidth() + 8;

                        boolean overRightPanel =
                                rightSideVisible
                                        && journalPanel != null
                                        && x >= sceneWidth
                                                - journalPanel.getWidth()
                                                - 8;

                        if (x <= 15) {
                            showLeftSide();
                            stopSidesHideTimer();

                        } else if (x >= sceneWidth - 15) {
                            showRightSide();
                            stopSidesHideTimer();

                        } else if (overLeftPanel || overRightPanel) {
                            stopSidesHideTimer();

                        } else if (
                                sidesHideTimer.getStatus()
                                        != Animation.Status.RUNNING
                        ) {
                            scheduleSidesHide();
                        }
                    }

                    /*
                     * BOTTOM EDGE / TIMELINE
                     *
                     * The navigation bar remains visible. Moving down
                     * near it brings the Book Timeline back.
                     */
                    if (autoHideBottomEnabled) {

                        boolean overTimeline =
                                bottomTimelineVisible
                                        && timelinePanel != null
                                        && y >= sceneHeight
                                                - timelinePanel.getHeight()
                                                - 70;

                        if (y >= sceneHeight - 18) {
                            showBottomTimeline();
                            stopBottomHideTimer();

                        } else if (overTimeline) {
                            stopBottomHideTimer();

                        } else if (
                                bottomHideTimer.getStatus()
                                        != Animation.Status.RUNNING
                        ) {
                            scheduleBottomHide();
                        }
                    }
                }
        );

        sidesHideTimer.setOnFinished(
                event -> {
                    if (
                            autoHideSidesEnabled
                                    && isReadingContentOpen()
                    ) {
                        hideLeftSide();
                        hideRightSide();
                    }
                }
        );

        bottomHideTimer.setOnFinished(
                event -> {
                    if (
                            autoHideBottomEnabled
                                    && isReadingContentOpen()
                    ) {
                        hideBottomTimeline();
                    }
                }
        );
    }

    private void toggleAutoHideHeader() {
        autoHideHeaderEnabled = !autoHideHeaderEnabled;

        if (autoHideHeaderEnabled) {
            autoHideHeaderButton.setText("Auto-hide Header: ON");
            if (isReadingContentOpen()) scheduleHeaderHide();
        } else {
            autoHideHeaderButton.setText("Auto-hide Header: OFF");
            stopHeaderHideTimer();
            showHeader();
            showComparisonToolbar();
        }
    }

    private void showHeader() {
        if (headerPanel == null) return;

        if (!headerVisible) {
            root.setTop(headerPanel);
            headerVisible = true;
        }

        showComparisonToolbar();
    }

    private void hideHeader() {
        if (!isReadingContentOpen() || !autoHideHeaderEnabled) return;

        if (headerVisible) {
            root.setTop(null);
            headerVisible = false;
        }

        hideComparisonToolbar();
    }

    private void showComparisonToolbar() {
        if (comparisonToolbar == null) return;

        comparisonToolbar.setManaged(true);
        comparisonToolbar.setVisible(true);
    }

    private void hideComparisonToolbar() {
        if (comparisonToolbar == null) return;

        if (comparisonVisible) {
            comparisonToolbar.setVisible(false);
            comparisonToolbar.setManaged(false);
        }
    }

    private void scheduleHeaderHide() {
        if (!autoHideHeaderEnabled || !isReadingContentOpen()) return;
        headerHideTimer.playFromStart();
    }

    private void stopHeaderHideTimer() {
        headerHideTimer.stop();
    }

    private void toggleAutoHideSides() {

        autoHideSidesEnabled = !autoHideSidesEnabled;

        if (autoHideSidesEnabled) {

            autoHideSidesButton.setText("Auto-hide Sides: ON");

            if (isReadingContentOpen()) {
                showLeftSide();
                showRightSide();
                scheduleSidesHide();
            }

        } else {

            autoHideSidesButton.setText("Auto-hide Sides: OFF");
            stopSidesHideTimer();
            showLeftSide();
            showRightSide();
        }
    }

    private void showLeftSide() {

        if (!readingPlanVisible) {
            return;
        }

        if (readingPlanSplitPane == null || leftPanel == null) {
            return;
        }

        if (!readingPlanSplitPane.getItems().contains(leftPanel)) {

            readingPlanSplitPane.getItems().add(0, leftPanel);
            leftSideVisible = true;

            Platform.runLater(() -> {
                if (!readingPlanSplitPane.getDividers().isEmpty()) {
                    readingPlanSplitPane.setDividerPositions(
                            savedReadingPlanDividerPosition
                    );
                }
            });

        } else {
            leftSideVisible = true;
        }
    }

    private void hideLeftSide() {

        if (readingPlanSplitPane == null || leftPanel == null) {
            return;
        }

        if (readingPlanSplitPane.getItems().contains(leftPanel)) {

            if (!readingPlanSplitPane.getDividers().isEmpty()) {
                savedReadingPlanDividerPosition =
                        readingPlanSplitPane
                                .getDividers()
                                .get(0)
                                .getPosition();
            }

            readingPlanSplitPane.getItems().remove(leftPanel);
        }

        leftSideVisible = false;
    }

    private void showRightSide() {

        if (bibleJournalSplitPane == null || journalPanel == null) {
            return;
        }

        if (!bibleJournalSplitPane.getItems().contains(journalPanel)) {

            bibleJournalSplitPane.getItems().add(journalPanel);
            rightSideVisible = true;

            Platform.runLater(() -> {
                if (!bibleJournalSplitPane.getDividers().isEmpty()) {
                    bibleJournalSplitPane.setDividerPositions(
                            savedJournalDividerPosition
                    );
                }
            });

        } else {
            rightSideVisible = true;
        }
    }

    private void hideRightSide() {

        if (bibleJournalSplitPane == null || journalPanel == null) {
            return;
        }

        if (bibleJournalSplitPane.getItems().contains(journalPanel)) {

            if (!bibleJournalSplitPane.getDividers().isEmpty()) {
                savedJournalDividerPosition =
                        bibleJournalSplitPane
                                .getDividers()
                                .get(0)
                                .getPosition();
            }

            bibleJournalSplitPane.getItems().remove(journalPanel);
        }

        rightSideVisible = false;
    }

    private void scheduleSidesHide() {

        if (!autoHideSidesEnabled || !isReadingContentOpen()) {
            return;
        }

        sidesHideTimer.playFromStart();
    }

    private void stopSidesHideTimer() {
        sidesHideTimer.stop();
    }

    private void toggleAutoHideBottom() {

        autoHideBottomEnabled = !autoHideBottomEnabled;

        if (autoHideBottomEnabled) {

            autoHideBottomButton.setText("Auto-hide Bottom: ON");

            if (isReadingContentOpen()) {
                showBottomTimeline();
                scheduleBottomHide();
            }

        } else {

            autoHideBottomButton.setText("Auto-hide Bottom: OFF");
            stopBottomHideTimer();
            showBottomTimeline();
        }
    }

    private void showBottomTimeline() {

        if (
                contentTimelineSplitPane == null
                        || timelinePanel == null
        ) {
            return;
        }

        if (
                !contentTimelineSplitPane
                        .getItems()
                        .contains(timelinePanel)
        ) {
            contentTimelineSplitPane
                    .getItems()
                    .add(timelinePanel);

            bottomTimelineVisible = true;

            Platform.runLater(() -> {
                if (
                        !contentTimelineSplitPane
                                .getDividers()
                                .isEmpty()
                ) {
                    contentTimelineSplitPane
                            .setDividerPositions(
                                    savedTimelineDividerPosition
                            );
                }
            });

        } else {
            bottomTimelineVisible = true;
        }
    }

    private void hideBottomTimeline() {

        if (
                contentTimelineSplitPane == null
                        || timelinePanel == null
        ) {
            return;
        }

        if (
                contentTimelineSplitPane
                        .getItems()
                        .contains(timelinePanel)
        ) {
            if (
                    !contentTimelineSplitPane
                            .getDividers()
                            .isEmpty()
            ) {
                savedTimelineDividerPosition =
                        contentTimelineSplitPane
                                .getDividers()
                                .get(0)
                                .getPosition();
            }

            contentTimelineSplitPane
                    .getItems()
                    .remove(timelinePanel);
        }

        bottomTimelineVisible = false;
    }

    private void scheduleBottomHide() {

        if (
                !autoHideBottomEnabled
                        || !isReadingContentOpen()
        ) {
            return;
        }

        bottomHideTimer.playFromStart();
    }

    private void stopBottomHideTimer() {
        bottomHideTimer.stop();
    }

    // ================================================================
    // Bible Hub Hebrew / Greek Interlinear
    // ================================================================

    private void updateOriginalLanguageForReference(
            String referenceText
    ) {
        if (
                originalLanguageSelector == null
                        || originalLanguageWebEngine == null
        ) {
            return;
        }

        List<String> references =
                extractStudyReferences(referenceText);

        originalLanguageSelector.getItems().clear();
        originalLanguageSelector.getItems().addAll(references);

        if (references.isEmpty()) {
            clearOriginalLanguage(
                    "No matching Bible chapter was found for this reading."
            );
            return;
        }

        originalLanguageSelector.setValue(references.get(0));
        loadSelectedOriginalLanguageChapter();
    }

    private void loadSelectedOriginalLanguageChapter() {
        if (
                originalLanguageSelector == null
                        || originalLanguageWebEngine == null
        ) {
            return;
        }

        String selection =
                originalLanguageSelector.getValue();

        if (selection == null || selection.isBlank()) {
            return;
        }

        int lastSpace = selection.lastIndexOf(' ');

        if (lastSpace <= 0) {
            clearOriginalLanguage(
                    "Could not determine the selected Bible chapter."
            );
            return;
        }

        String book =
                selection.substring(0, lastSpace).trim();

        int chapter;

        try {
            chapter =
                    Integer.parseInt(
                            selection.substring(lastSpace + 1).trim()
                    );
        } catch (NumberFormatException error) {
            clearOriginalLanguage(
                    "Could not determine the selected Bible chapter."
            );
            return;
        }

        String slug =
                bibleHubBookSlug(book);

        if (slug == null) {
            clearOriginalLanguage(
                    "Bible Hub chapter mapping is unavailable for "
                            + book
                            + "."
            );
            return;
        }

        String url =
                "https://biblehub.com/interlinear/"
                        + slug
                        + "/"
                        + chapter
                        + ".htm";

        originalLanguageTitleLabel.setText(
                isNewTestamentBook(book)
                        ? "Greek Interlinear — " + book + " " + chapter
                        : "Hebrew Interlinear — " + book + " " + chapter
        );

        originalLanguageStatusLabel.setText(
                "Loaded from Bible Hub. "
                        + "Use the Strong's links and word entries for concordance study."
        );

        originalLanguageWebEngine.load(url);
    }

    private void clearOriginalLanguage(String message) {
        if (originalLanguageSelector != null) {
            originalLanguageSelector.getItems().clear();
            originalLanguageSelector.setValue(null);
        }

        if (originalLanguageTitleLabel != null) {
            originalLanguageTitleLabel.setText(
                    "Hebrew / Greek Concordance"
            );
        }

        if (originalLanguageStatusLabel != null) {
            originalLanguageStatusLabel.setText(message);
        }

        if (originalLanguageWebEngine != null) {
            originalLanguageWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;"
                            + "padding:16px;color:#222;background:#fff;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    private String bibleHubBookSlug(String book) {
        if (book == null) return null;

        switch (book) {
            case "Genesis": return "genesis";
            case "Exodus": return "exodus";
            case "Leviticus": return "leviticus";
            case "Numbers": return "numbers";
            case "Deuteronomy": return "deuteronomy";
            case "Joshua": return "joshua";
            case "Judges": return "judges";
            case "Ruth": return "ruth";
            case "1 Samuel": return "1_samuel";
            case "2 Samuel": return "2_samuel";
            case "1 Kings": return "1_kings";
            case "2 Kings": return "2_kings";
            case "1 Chronicles": return "1_chronicles";
            case "2 Chronicles": return "2_chronicles";
            case "Ezra": return "ezra";
            case "Nehemiah": return "nehemiah";
            case "Esther": return "esther";
            case "Job": return "job";
            case "Psalm":
            case "Psalms": return "psalms";
            case "Proverb":
            case "Proverbs": return "proverbs";
            case "Ecclesiastes": return "ecclesiastes";
            case "Song of Solomon":
            case "Song of Songs": return "songs";
            case "Isaiah": return "isaiah";
            case "Jeremiah": return "jeremiah";
            case "Lamentations": return "lamentations";
            case "Ezekiel": return "ezekiel";
            case "Daniel": return "daniel";
            case "Hosea": return "hosea";
            case "Joel": return "joel";
            case "Amos": return "amos";
            case "Obadiah": return "obadiah";
            case "Jonah": return "jonah";
            case "Micah": return "micah";
            case "Nahum": return "nahum";
            case "Habakkuk": return "habakkuk";
            case "Zephaniah": return "zephaniah";
            case "Haggai": return "haggai";
            case "Zechariah": return "zechariah";
            case "Malachi": return "malachi";
            case "Matthew": return "matthew";
            case "Mark": return "mark";
            case "Luke": return "luke";
            case "John": return "john";
            case "Acts": return "acts";
            case "Romans": return "romans";
            case "1 Corinthians": return "1_corinthians";
            case "2 Corinthians": return "2_corinthians";
            case "Galatians": return "galatians";
            case "Ephesians": return "ephesians";
            case "Philippians": return "philippians";
            case "Colossians": return "colossians";
            case "1 Thessalonians": return "1_thessalonians";
            case "2 Thessalonians": return "2_thessalonians";
            case "1 Timothy": return "1_timothy";
            case "2 Timothy": return "2_timothy";
            case "Titus": return "titus";
            case "Philemon": return "philemon";
            case "Hebrews": return "hebrews";
            case "James": return "james";
            case "1 Peter": return "1_peter";
            case "2 Peter": return "2_peter";
            case "1 John": return "1_john";
            case "2 John": return "2_john";
            case "3 John": return "3_john";
            case "Jude": return "jude";
            case "Revelation": return "revelation";
            default: return null;
        }
    }

    private boolean isNewTestamentBook(String book) {
        if (book == null) return false;

        switch (book) {
            case "Matthew":
            case "Mark":
            case "Luke":
            case "John":
            case "Acts":
            case "Romans":
            case "1 Corinthians":
            case "2 Corinthians":
            case "Galatians":
            case "Ephesians":
            case "Philippians":
            case "Colossians":
            case "1 Thessalonians":
            case "2 Thessalonians":
            case "1 Timothy":
            case "2 Timothy":
            case "Titus":
            case "Philemon":
            case "Hebrews":
            case "James":
            case "1 Peter":
            case "2 Peter":
            case "1 John":
            case "2 John":
            case "3 John":
            case "Jude":
            case "Revelation":
                return true;
            default:
                return false;
        }
    }

    // ================================================================
    // Life Application Book Introductions (local EPUB)
    // ================================================================

    private void updateBookIntroductionsForReference(String referenceText) {
        if (bookIntroductionSelector == null) return;

        bookIntroductionEntries.clear();
        bookIntroductionSelector.getItems().clear();

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearBookIntroduction(
                    "Study Bible EPUB not found. Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        Map<String, Set<Integer>> chapterMap =
                extractReadingChapterMap(referenceText);

        if (chapterMap.isEmpty()) {
            clearBookIntroduction(
                    "No matching Bible book was found for this passage."
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {

            for (String book : chapterMap.keySet()) {
                String code = studyBookCodes.get(book);
                if (code == null) continue;

                String entryName =
                        "BookIntros/" + code + "_BookIntro.xhtml";

                if (zipFile.getEntry(entryName) != null) {
                    bookIntroductionEntries.putIfAbsent(
                            book,
                            entryName
                    );
                }
            }

        } catch (IOException error) {
            clearBookIntroduction(
                    "Could not open the Study Bible EPUB: " + error.getMessage()
            );
            error.printStackTrace();
            return;
        }

        bookIntroductionSelector.getItems().addAll(
                bookIntroductionEntries.keySet()
        );

        if (bookIntroductionEntries.isEmpty()) {
            clearBookIntroduction(
                    "No book introduction is available for this passage."
            );
            return;
        }

        String firstBook =
                bookIntroductionEntries.keySet().iterator().next();

        bookIntroductionSelector.setValue(firstBook);
        loadSelectedBookIntroduction();
    }

    private void showIntroductionForNewBooks(
            int readingIndex,
            String referenceText
    ) {
        if (
                readingIndex < 0
                        || readingIndex >= readingDays.size()
                        || topInfoTabs == null
                        || bookIntroductionTab == null
                        || personalityProfileTab == null
                        || chartsTab == null
        ) {
            return;
        }

        /*
         * Book Introduction now remains available for every chapter/day.
         * It is never removed after the book's first appearance.
         */
        if (!topInfoTabs.getTabs().contains(bookIntroductionTab)) {
            topInfoTabs.getTabs().add(0, bookIntroductionTab);
        }

        if (!topInfoTabs.getTabs().contains(personalityProfileTab)) {
            topInfoTabs.getTabs().add(personalityProfileTab);
        }

        if (!topInfoTabs.getTabs().contains(chartsTab)) {
            topInfoTabs.getTabs().add(chartsTab);
        }

        Map<String, Set<Integer>> currentBooks =
                extractReadingChapterMap(referenceText);

        if (currentBooks.isEmpty()) {
            topInfoTabs.getSelectionModel().select(personalityProfileTab);
            return;
        }

        List<String> newBooks = new ArrayList<>();

        for (String book : currentBooks.keySet()) {
            if (isFirstAppearanceOfBook(book, readingIndex)) {
                newBooks.add(book);
            }
        }

        /*
         * The introduction remains available even when this is not the
         * first day of the book. In that case, leave the normal study tab
         * selected and let updateBookIntroductionsForReference(...) keep
         * the current book introduction loaded in the persistent tab.
         */
        if (newBooks.isEmpty()) {
            topInfoTabs.getSelectionModel().select(personalityProfileTab);
            return;
        }

        String bookToShow = null;

        for (String newBook : newBooks) {
            if (bookIntroductionEntries.containsKey(newBook)) {
                bookToShow = newBook;
                break;
            }
        }

        if (bookToShow == null) {
            topInfoTabs.getSelectionModel().select(personalityProfileTab);
            return;
        }

        /*
         * On the first appearance of a new Bible book, automatically open
         * its introduction. Afterwards the tab stays available so the
         * reader can return to it at any time.
         */
        bookIntroductionSelector.setValue(bookToShow);
        loadSelectedBookIntroduction();

        topInfoTabs.getSelectionModel().select(bookIntroductionTab);

        if (newBooks.size() == 1) {
            bookIntroductionStatusLabel.setText(
                    "Book introduction for " + bookToShow
                            + ". This tab remains available throughout the book."
            );
        } else {
            bookIntroductionStatusLabel.setText(
                    "New books begin in today's reading. "
                            + "Use the selector to review their introductions."
            );
        }
    }

    private boolean isFirstAppearanceOfBook(
            String book,
            int readingIndex
    ) {
        if (book == null || readingIndex < 0) return false;

        for (int i = 0; i < readingIndex; i++) {
            Map<String, Set<Integer>> earlierBooks =
                    extractReadingChapterMap(
                            readingDays.get(i).getReading()
                    );

            if (earlierBooks.containsKey(book)) {
                return false;
            }
        }

        Map<String, Set<Integer>> currentBooks =
                extractReadingChapterMap(
                        readingDays.get(readingIndex).getReading()
                );

        return currentBooks.containsKey(book);
    }

    private void loadSelectedBookIntroduction() {
        if (
                bookIntroductionSelector == null
                        || bookIntroductionWebEngine == null
        ) {
            return;
        }

        String book =
                bookIntroductionSelector.getValue();

        if (book == null || book.isBlank()) return;

        String entryName =
                bookIntroductionEntries.get(book);

        if (entryName == null) {
            clearBookIntroduction(
                    "Could not find the selected book introduction."
            );
            return;
        }

        loadBookIntroduction(book, entryName);
    }

    private void loadBookIntroduction(
            String book,
            String entryName
    ) {
        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearBookIntroduction(
                    "Study Bible EPUB not found. Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            ZipEntry entry =
                    zipFile.getEntry(entryName);

            if (entry == null) {
                clearBookIntroduction(
                        "The selected book introduction was not found in the EPUB."
                );
                return;
            }

            String html;

            try (InputStream input = zipFile.getInputStream(entry)) {
                html = new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            html = prepareBookIntroductionHtml(html);

            bookIntroductionTitleLabel.setText(
                    book + " — Book Introduction"
            );

            bookIntroductionStatusLabel.setText(
                    "Loaded locally from " + studyBibleEpubFile.getName()
            );

            bookIntroductionWebEngine.loadContent(
                    html,
                    "text/html"
            );

        } catch (IOException error) {
            clearBookIntroduction(
                    "Could not load the book introduction: "
                            + error.getMessage()
            );
            error.printStackTrace();
        }
    }

    private String prepareBookIntroductionHtml(String html) {
        String cleaned = html.replaceAll(
                "(?is)<link[^>]*rel=[\\\"']stylesheet[\\\"'][^>]*/?>",
                ""
        );

        /*
         * EPUB-relative links and images cannot be opened directly by
         * WebView.loadContent(), so keep the introduction self-contained.
         */
        cleaned = cleaned.replaceAll(
                "(?i)href=[\\\"'][^\\\"']*[\\\"']",
                "href=\"#\""
        );

        cleaned = cleaned.replaceAll(
                "(?is)<img[^>]*>",
                ""
        );

        String style =
                "<style>"
                        + "body{font-family:Georgia,'Times New Roman',serif;"
                        + "font-size:15px;line-height:1.55;margin:16px;"
                        + "color:#222;background:#fff;}"
                        + "h1,h2,h3,h4,.h1,.h2,.h3,.h4{"
                        + "font-family:Georgia,'Times New Roman',serif;"
                        + "color:#5d2815;margin-top:14px;}"
                        + "p{margin:0 0 10px 0;}"
                        + "ul,ol{padding-left:24px;}"
                        + "li{margin-bottom:7px;}"
                        + "table{width:100%;border-collapse:collapse;margin:10px 0;}"
                        + "td,th{vertical-align:top;padding:5px;"
                        + "border-bottom:1px solid #ddd;}"
                        + "a{color:#7a2c12;text-decoration:none;}"
                        + "</style>";

        if (cleaned.toLowerCase(Locale.ENGLISH).contains("</head>")) {
            cleaned = cleaned.replaceFirst(
                    "(?i)</head>",
                    Matcher.quoteReplacement(style + "</head>")
            );
        } else {
            cleaned = style + cleaned;
        }

        return cleaned;
    }

    private void clearBookIntroduction(String message) {
        bookIntroductionEntries.clear();

        if (bookIntroductionSelector != null) {
            bookIntroductionSelector.getItems().clear();
            bookIntroductionSelector.setValue(null);
        }

        if (bookIntroductionTitleLabel != null) {
            bookIntroductionTitleLabel.setText("Book Introduction");
        }

        if (bookIntroductionStatusLabel != null) {
            bookIntroductionStatusLabel.setText(message);
        }

        if (bookIntroductionWebEngine != null) {
            bookIntroductionWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;padding:16px;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    // ================================================================
    // Life Application Maps — matched to the current book/chapter
    // ================================================================

    private void updateMapsForReference(String referenceText) {
        if (mapsSelector == null || mapsWebEngine == null) return;

        mapEntries.clear();
        mapsSelector.getItems().clear();

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearMaps(
                    "Study Bible EPUB not found. Expected file: "
                            + STUDY_BIBLE_FILENAME
            );
            return;
        }

        Map<String, Set<Integer>> readingChapterMap =
                extractReadingChapterMap(referenceText);

        if (readingChapterMap.isEmpty()) {
            clearMaps(
                    "No matching Bible chapter was found for maps."
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            for (Map.Entry<String, Set<Integer>> entry
                    : readingChapterMap.entrySet()) {
                loadMapsForPassage(
                        zipFile,
                        entry.getKey(),
                        entry.getValue()
                );
            }
        } catch (IOException error) {
            clearMaps(
                    "Could not open the Study Bible EPUB: "
                            + error.getMessage()
            );
            error.printStackTrace();
            return;
        }

        mapsSelector.getItems().addAll(
                mapEntries.keySet()
        );

        if (mapEntries.isEmpty()) {
            clearMaps(
                    "No maps match this reading: "
                            + referenceText
            );
            return;
        }

        String first =
                mapEntries.keySet()
                        .iterator()
                        .next();

        mapsSelector.setValue(first);
        loadSelectedMap();
    }

    private void loadMapsForPassage(
            ZipFile zipFile,
            String book,
            Set<Integer> readingChapters
    ) throws IOException {

        if (
                book == null
                        || readingChapters == null
                        || readingChapters.isEmpty()
        ) {
            return;
        }

        ZipEntry indexEntry =
                zipFile.getEntry("Maps/00_Maps.xhtml");

        if (indexEntry == null) return;

        String indexHtml;

        try (InputStream input =
                     zipFile.getInputStream(indexEntry)) {
            indexHtml = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        String sectionId =
                mapIndexSectionId(book);

        if (sectionId == null) return;

        Pattern sectionPattern =
                Pattern.compile(
                        "(?is)<div\\s+class=[\\\"']Index[\\\"']\\s+"
                                + "id=[\\\"']"
                                + Pattern.quote(sectionId)
                                + "[\\\"']>(.*?)</div>"
                );

        Matcher sectionMatcher =
                sectionPattern.matcher(indexHtml);

        if (!sectionMatcher.find()) {
            return;
        }

        String sectionHtml =
                sectionMatcher.group(1);

        Pattern linkPattern =
                Pattern.compile(
                        "(?is)<a\\s+[^>]*href=[\\\"']"
                                + "([^\\\"']+_Map\\.xhtml)"
                                + "(?:#[^\\\"']*)?[\\\"'][^>]*>"
                                + "(.*?)</a>"
                );

        Matcher matcher =
                linkPattern.matcher(sectionHtml);

        boolean gospel =
                isGospelBook(book);

        while (matcher.find()) {
            String relativeEntry =
                    matcher.group(1).trim();

            String displayName =
                    stripHtmlTags(
                            matcher.group(2)
                    ).trim();

            if (displayName.isEmpty()) continue;

            String fullEntryName =
                    relativeEntry.startsWith("Maps/")
                            ? relativeEntry
                            : "Maps/" + relativeEntry;

            /*
             * Gospel maps share one chronological index. Only show maps
             * that actually reference the Gospel and chapter being read.
             *
             * Other books have their own map index section, so their
             * overview maps are useful throughout that book. Where a
             * chapter-specific reference exists, it is still matched.
             */
            boolean matches =
                    mapMatchesReading(
                            zipFile,
                            fullEntryName,
                            book,
                            readingChapters
                    );

            boolean overview =
                    displayName.toLowerCase(Locale.ENGLISH)
                            .startsWith("key places");

            if (gospel && !matches && !overview) {
                continue;
            }

            if (!gospel && !matches && !overview) {
                /*
                 * For the Pauline letters the EPUB map usually covers
                 * the entire letter (for example Romans 1:1-16:27), so
                 * chapter matching succeeds. For general book-level maps
                 * without a specific reference, retain them because they
                 * belong to the book's own map index section.
                 */
                if (!isPaulineLetter(book)) {
                    continue;
                }
            }

            String selectorName =
                    book + " — " + displayName;

            mapEntries.putIfAbsent(
                    selectorName,
                    fullEntryName
            );
        }

        /*
         * When reading Acts or one of Paul's letters, also make Paul's
         * four major travel maps available so the reader can follow his
         * movements geographically.
         */
        if ("Acts".equals(book) || isPaulineLetter(book)) {
            addPaulJourneyMapIfPresent(
                    zipFile,
                    "Paul’s First Missionary Journey",
                    "Maps/PaulsFirstMissionaryJourney_Map.xhtml"
            );
            addPaulJourneyMapIfPresent(
                    zipFile,
                    "Paul’s Second Missionary Journey",
                    "Maps/PaulsSecondMissionaryJourney_Map.xhtml"
            );
            addPaulJourneyMapIfPresent(
                    zipFile,
                    "Paul’s Third Missionary Journey",
                    "Maps/PaulsThirdMissionaryJourney_Map.xhtml"
            );
            addPaulJourneyMapIfPresent(
                    zipFile,
                    "Paul’s Journey to Rome",
                    "Maps/PaulsJourneyToRome_Map.xhtml"
            );
        }
    }

    private void addPaulJourneyMapIfPresent(
            ZipFile zipFile,
            String displayName,
            String entryName
    ) {
        if (zipFile.getEntry(entryName) != null) {
            mapEntries.putIfAbsent(
                    "Paul — " + displayName,
                    entryName
            );
        }
    }

    private boolean mapMatchesReading(
            ZipFile zipFile,
            String mapEntryName,
            String book,
            Set<Integer> readingChapters
    ) throws IOException {

        ZipEntry mapEntry =
                zipFile.getEntry(mapEntryName);

        if (mapEntry == null) return false;

        String html;

        try (InputStream input =
                     zipFile.getInputStream(mapEntry)) {
            html = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        String code =
                studyBookCodes.get(book);

        if (code == null) return false;

        /*
         * References inside the map link directly to chapter files such
         * as 40-Matt-013..., 44-Acts-016..., 45-Rom-001..., etc.
         */
        Pattern hrefPattern =
                Pattern.compile(
                        "(?i)href=[\\\"'][^\\\"']*"
                                + Pattern.quote(code)
                                + "-(\\d{3})"
                );

        Matcher matcher =
                hrefPattern.matcher(html);

        boolean foundBookReference = false;

        while (matcher.find()) {
            foundBookReference = true;

            try {
                int chapter =
                        Integer.parseInt(
                                matcher.group(1)
                        );

                if (readingChapters.contains(chapter)) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        /*
         * If a map's main visible reference spans the whole letter/book,
         * expand that range so a middle chapter also matches.
         */
        Pattern mapRefPattern =
                Pattern.compile(
                        "(?is)<(?:p|a)\\s+[^>]*class=[\\\"']"
                                + "[^\\\"']*map(?:key-)?ref[^\\\"']*"
                                + "[\\\"'][^>]*>(.*?)</(?:p|a)>"
                );

        Matcher refMatcher =
                mapRefPattern.matcher(html);

        while (refMatcher.find()) {
            String visible =
                    stripHtmlTags(
                            refMatcher.group(1)
                    )
                            .replace('\u00A0', ' ')
                            .trim();

            Set<Integer> chapters =
                    extractChaptersFromMapReference(
                            visible
                    );

            for (Integer chapter : chapters) {
                if (readingChapters.contains(chapter)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Set<Integer> extractChaptersFromMapReference(
            String reference
    ) {
        LinkedHashSet<Integer> chapters =
                new LinkedHashSet<>();

        if (reference == null) return chapters;

        Matcher first =
                Pattern.compile(
                        "(\\d{1,3}):\\d{1,3}"
                                + "(?:\\s*[–-]\\s*"
                                + "(\\d{1,3}):\\d{1,3})?"
                ).matcher(reference);

        if (first.find()) {
            int start =
                    Integer.parseInt(
                            first.group(1)
                    );
            int end = start;

            if (first.group(2) != null) {
                end = Integer.parseInt(
                        first.group(2)
                );
            }

            if (end >= start && end - start <= 100) {
                for (int c = start; c <= end; c++) {
                    chapters.add(c);
                }
            }
        }

        return chapters;
    }

    private String mapIndexSectionId(String book) {
        if (isGospelBook(book)) {
            return "Gospels-Maps_Index_LASB";
        }

        switch (book) {
            case "Genesis": return "Gen-Maps_Index_LASB";
            case "Exodus": return "Exod-Maps_Index_LASB";
            case "Leviticus": return "Lev-Maps_Index_LASB";
            case "Numbers": return "Num-Maps_Index_LASB";
            case "Deuteronomy": return "Deut-Maps_Index_LASB";
            case "Joshua": return "Josh-Maps_Index_LASB";
            case "Judges": return "Judg-Maps_Index_LASB";
            case "Ruth": return "Ruth-Maps_Index_LASB";
            case "1 Samuel": return "ISam-Maps_Index_LASB";
            case "2 Samuel": return "IISam-Maps_Index_LASB";
            case "1 Kings": return "IKgs-Maps_Index_LASB";
            case "2 Kings": return "IIKgs-Maps_Index_LASB";
            case "1 Chronicles": return "IChr-Maps_Index_LASB";
            case "2 Chronicles": return "IIChr-Maps_Index_LASB";
            case "Ezra": return "Ezra-Maps_Index_LASB";
            case "Nehemiah": return "Neh-Maps_Index_LASB";
            case "Esther": return "Esth-Maps_Index_LASB";
            case "Job": return "Job-Maps_Index_LASB";
            case "Song of Solomon":
            case "Song of Songs": return "Song-Maps_Index_LASB";
            case "Isaiah": return "Isa-Maps_Index_LASB";
            case "Jeremiah": return "Jer-Maps_Index_LASB";
            case "Ezekiel": return "Ezek-Maps_Index_LASB";
            case "Daniel": return "Dan-Maps_Index_LASB";
            case "Hosea": return "Hos-Maps_Index_LASB";
            case "Joel": return "Joel-Maps_Index_LASB";
            case "Amos": return "Amos-Maps_Index_LASB";
            case "Obadiah": return "Obad-Maps_Index_LASB";
            case "Jonah": return "Jon-Maps_Index_LASB";
            case "Micah": return "Mic-Maps_Index_LASB";
            case "Nahum": return "Nah-Maps_Index_LASB";
            case "Habakkuk": return "Hab-Maps_Index_LASB";
            case "Zephaniah": return "Zeph-Maps_Index_LASB";
            case "Haggai": return "Hagg-Maps_Index_LASB";
            case "Zechariah": return "Zech-Maps_Index_LASB";
            case "Malachi": return "Mal-Maps_Index_LASB";
            case "Acts": return "Acts-Maps_Index_LASB";
            case "Romans": return "Rom-Maps_Index_LASB";
            case "1 Corinthians": return "ICor-Maps_Index_LASB";
            case "2 Corinthians": return "IICor-Maps_Index_LASB";
            case "Galatians": return "Gal-Maps_Index_LASB";
            case "Ephesians": return "Eph-Maps_Index_LASB";
            case "Philippians": return "Phil-Maps_Index_LASB";
            case "Colossians": return "Col-Maps_Index_LASB";
            case "1 Thessalonians": return "IThes-Maps_Index_LASB";
            case "2 Thessalonians": return "IIThes-Maps_Index_LASB";
            case "Titus": return "Titus-Maps_Index_LASB";
            case "1 Peter": return "IPet-Maps_Index_LASB";
            case "Revelation": return "Rev-Maps_Index_LASB";
            default: return null;
        }
    }

    private boolean isGospelBook(String book) {
        return "Matthew".equals(book)
                || "Mark".equals(book)
                || "Luke".equals(book)
                || "John".equals(book);
    }

    private boolean isPaulineLetter(String book) {
        return "Romans".equals(book)
                || "1 Corinthians".equals(book)
                || "2 Corinthians".equals(book)
                || "Galatians".equals(book)
                || "Ephesians".equals(book)
                || "Philippians".equals(book)
                || "Colossians".equals(book)
                || "1 Thessalonians".equals(book)
                || "2 Thessalonians".equals(book)
                || "1 Timothy".equals(book)
                || "2 Timothy".equals(book)
                || "Titus".equals(book)
                || "Philemon".equals(book);
    }

    private void loadSelectedMap() {
        if (mapsSelector == null || mapsWebEngine == null) {
            return;
        }

        String selection =
                mapsSelector.getValue();

        if (selection == null || selection.isBlank()) {
            return;
        }

        String entryName =
                mapEntries.get(selection);

        if (entryName == null) {
            clearMaps(
                    "Could not find the selected map."
            );
            return;
        }

        loadMap(selection, entryName);
    }

    private void loadMap(
            String displayName,
            String entryName
    ) {
        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearMaps(
                    "Study Bible EPUB not found. Expected file: "
                            + STUDY_BIBLE_FILENAME
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            ZipEntry entry =
                    zipFile.getEntry(entryName);

            if (entry == null) {
                clearMaps(
                        "The selected map was not found in the EPUB."
                );
                return;
            }

            String html;

            try (InputStream input =
                     zipFile.getInputStream(entry)) {
                html = new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            html = prepareMapHtml(
                    zipFile,
                    html,
                    entryName
            );

            mapsTitleLabel.setText(
                    "Life Application Map — "
                            + displayName
            );

            mapsStatusLabel.setText(
                    "Loaded locally from the Life Application Study Bible."
            );

            mapsWebEngine.loadContent(
                    html,
                    "text/html"
            );

        } catch (IOException error) {
            clearMaps(
                    "Could not load the map: "
                            + error.getMessage()
            );
            error.printStackTrace();
        }
    }

    private String prepareMapHtml(
            ZipFile zipFile,
            String html,
            String entryName
    ) throws IOException {

        String cleaned =
                html.replaceAll(
                        "(?is)<link[^>]*rel=[\\\"']stylesheet[\\\"'][^>]*/?>",
                        ""
                );

        /*
         * Embed each EPUB image directly as a data URI. This is important
         * because WebView cannot resolve an image path inside a ZIP/EPUB
         * when loadContent(...) is used.
         */
        Pattern imagePattern =
                Pattern.compile(
                        "(?is)<img([^>]*?)src=[\\\"']"
                                + "([^\\\"']+)"
                                + "[\\\"']([^>]*)>"
                );

        Matcher imageMatcher =
                imagePattern.matcher(cleaned);

        StringBuffer imageBuffer =
                new StringBuffer();

        while (imageMatcher.find()) {
            String src =
                    imageMatcher.group(2);

            String imageEntryName =
                    resolveZipRelativePath(
                            entryName,
                            src
                    );

            ZipEntry imageEntry =
                    zipFile.getEntry(imageEntryName);

            String replacement;

            if (imageEntry == null) {
                replacement = "";
            } else {
                byte[] bytes;

                try (InputStream imageInput =
                             zipFile.getInputStream(imageEntry)) {
                    bytes =
                            imageInput.readAllBytes();
                }

                String mime =
                        imageEntryName
                                .toLowerCase(Locale.ENGLISH)
                                .endsWith(".png")
                                ? "image/png"
                                : "image/jpeg";

                String encoded =
                        Base64.getEncoder()
                                .encodeToString(bytes);

                replacement =
                        "<img"
                                + imageMatcher.group(1)
                                + "src=\"data:"
                                + mime
                                + ";base64,"
                                + encoded
                                + "\""
                                + imageMatcher.group(3)
                                + ">";
            }

            imageMatcher.appendReplacement(
                    imageBuffer,
                    Matcher.quoteReplacement(
                            replacement
                    )
            );
        }

        imageMatcher.appendTail(imageBuffer);
        cleaned = imageBuffer.toString();

        cleaned = cleaned.replaceAll(
                "(?i)href=[\\\"'][^\\\"']*[\\\"']",
                "href=\"#\""
        );

        String style =
                "<style>"
                        + "body{font-family:Georgia,'Times New Roman',serif;"
                        + "font-size:15px;line-height:1.45;margin:12px;"
                        + "color:#222;background:#fff;}"
                        + "img{display:block;max-width:100%;height:auto;"
                        + "margin:0 auto 12px auto;}"
                        + ".maphead,.mapkey-title{font-size:18px;"
                        + "font-weight:bold;color:#5d2815;}"
                        + ".map,.mapkey-first,.mapkey-ref{margin:8px 0;}"
                        + "a{color:#5d2815;text-decoration:none;}"
                        + "</style>";

        if (
                cleaned.toLowerCase(Locale.ENGLISH)
                        .contains("</head>")
        ) {
            cleaned = cleaned.replaceFirst(
                    "(?i)</head>",
                    Matcher.quoteReplacement(
                            style + "</head>"
                    )
            );
        } else {
            cleaned = style + cleaned;
        }

        return cleaned;
    }

    private String resolveZipRelativePath(
            String htmlEntryName,
            String relativePath
    ) {
        if (relativePath == null) return "";

        if (
                relativePath.startsWith("/")
                        || relativePath.startsWith("Maps/")
        ) {
            return relativePath.startsWith("/")
                    ? relativePath.substring(1)
                    : relativePath;
        }

        int slash =
                htmlEntryName.lastIndexOf('/');

        String base =
                slash >= 0
                        ? htmlEntryName.substring(
                                0,
                                slash + 1
                        )
                        : "";

        String combined =
                base + relativePath;

        List<String> parts =
                new ArrayList<>();

        for (String part : combined.split("/")) {
            if (
                    part.isEmpty()
                            || ".".equals(part)
            ) {
                continue;
            }

            if ("..".equals(part)) {
                if (!parts.isEmpty()) {
                    parts.remove(
                            parts.size() - 1
                    );
                }
            } else {
                parts.add(part);
            }
        }

        return String.join("/", parts);
    }

    private void clearMaps(String message) {
        mapEntries.clear();

        if (mapsSelector != null) {
            mapsSelector.getItems().clear();
            mapsSelector.setValue(null);
        }

        if (mapsTitleLabel != null) {
            mapsTitleLabel.setText("Maps");
        }

        if (mapsStatusLabel != null) {
            mapsStatusLabel.setText(message);
        }

        if (mapsWebEngine != null) {
            mapsWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;"
                            + "padding:16px;color:#222;background:#fff;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    // ================================================================
    // Life Application Charts — matched day-by-day to the reading
    // ================================================================

    private void updateChartsForReference(String referenceText) {
        if (chartsSelector == null || chartsWebEngine == null) return;

        chartEntries.clear();
        chartsSelector.getItems().clear();

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearCharts(
                    "Study Bible EPUB not found. Expected file: "
                            + STUDY_BIBLE_FILENAME
            );
            return;
        }

        Map<String, Set<Integer>> readingChapterMap =
                extractReadingChapterMap(referenceText);

        if (readingChapterMap.isEmpty()) {
            clearCharts(
                    "No matching Bible chapter was found for charts."
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {

            boolean multipleBooks = readingChapterMap.size() > 1;

            for (Map.Entry<String, Set<Integer>> readingEntry
                    : readingChapterMap.entrySet()) {

                loadChartsForPassage(
                        zipFile,
                        readingEntry.getKey(),
                        readingEntry.getValue(),
                        multipleBooks
                );
            }

        } catch (IOException error) {
            clearCharts(
                    "Could not open the Study Bible EPUB: "
                            + error.getMessage()
            );
            error.printStackTrace();
            return;
        }

        chartsSelector.getItems().addAll(chartEntries.keySet());

        if (chartEntries.isEmpty()) {
            clearCharts(
                    "No charts match this reading: " + referenceText
            );
            return;
        }

        String firstChart =
                chartEntries.keySet().iterator().next();

        chartsSelector.setValue(firstChart);
        loadSelectedChart();
    }

    private void loadChartsForPassage(
            ZipFile zipFile,
            String book,
            Set<Integer> readingChapters,
            boolean includeBookName
    ) throws IOException {

        String code = studyBookCodes.get(book);

        if (
                code == null
                        || readingChapters == null
                        || readingChapters.isEmpty()
        ) {
            return;
        }

        String indexEntryName =
                "Charts/" + code + "-Charts_Index.xhtml";

        ZipEntry indexEntry =
                zipFile.getEntry(indexEntryName);

        if (indexEntry == null) {
            return;
        }

        String indexHtml;

        try (InputStream input = zipFile.getInputStream(indexEntry)) {
            indexHtml = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        Pattern chartLinkPattern = Pattern.compile(
                "(?is)<a\\s+[^>]*href=[\\\"']"
                        + "([^\\\"']+_Chart\\.xhtml)"
                        + "(?:#[^\\\"']*)?[\\\"'][^>]*>(.*?)</a>"
        );

        Matcher matcher =
                chartLinkPattern.matcher(indexHtml);

        while (matcher.find()) {
            String relativeEntry =
                    matcher.group(1).trim();

            String displayName =
                    stripHtmlTags(matcher.group(2)).trim();

            if (displayName.isEmpty()) continue;

            String fullEntryName =
                    relativeEntry.startsWith("Charts/")
                            ? relativeEntry
                            : "Charts/" + relativeEntry;

            if (
                    !chartMatchesReading(
                            zipFile,
                            fullEntryName,
                            book,
                            code,
                            readingChapters
                    )
            ) {
                continue;
            }

            String selectorName =
                    includeBookName
                            ? book + " — " + displayName
                            : displayName;

            chartEntries.putIfAbsent(
                    selectorName,
                    fullEntryName
            );
        }
    }

    private boolean chartMatchesReading(
            ZipFile zipFile,
            String chartEntryName,
            String book,
            String bookCode,
            Set<Integer> readingChapters
    ) throws IOException {

        ZipEntry chartEntry =
                zipFile.getEntry(chartEntryName);

        if (chartEntry == null) {
            return false;
        }

        String chartHtml;

        try (InputStream input = zipFile.getInputStream(chartEntry)) {
            chartHtml = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        Pattern chartRefPattern = Pattern.compile(
                "(?is)<p\\s+[^>]*class=[\\\"']"
                        + "[^\\\"']*chart-ref[^\\\"']*"
                        + "[\\\"'][^>]*>(.*?)</p>"
        );

        Matcher refMatcher =
                chartRefPattern.matcher(chartHtml);

        if (!refMatcher.find()) {
            return false;
        }

        String chartRefHtml =
                refMatcher.group(1);

        Set<Integer> chartChapters =
                extractChartReferenceChapters(
                        chartRefHtml,
                        book,
                        bookCode
                );

        for (Integer chapter : chartChapters) {
            if (readingChapters.contains(chapter)) {
                return true;
            }
        }

        return false;
    }

    private Set<Integer> extractChartReferenceChapters(
            String chartRefHtml,
            String book,
            String bookCode
    ) {
        LinkedHashSet<Integer> chapters =
                new LinkedHashSet<>();

        if (chartRefHtml == null || chartRefHtml.isBlank()) {
            return chapters;
        }

        /*
         * The EPUB chart reference usually contains a link into the
         * Bible text.  Use that link to make sure the chart belongs to
         * the current Bible book.
         */
        Pattern hrefBookPattern = Pattern.compile(
                "(?i)href=[\\\"'][^\\\"']*"
                        + Pattern.quote(bookCode)
                        + "-(\\d{3})"
        );

        Matcher hrefMatcher =
                hrefBookPattern.matcher(chartRefHtml);

        boolean currentBookFound = false;

        while (hrefMatcher.find()) {
            currentBookFound = true;

            try {
                chapters.add(
                        Integer.parseInt(
                                hrefMatcher.group(1)
                        )
                );
            } catch (NumberFormatException ignored) {
                // Ignore malformed EPUB references.
            }
        }

        if (!currentBookFound) {
            return chapters;
        }

        /*
         * The visible reference gives us the complete chapter range.
         *
         * Examples:
         *   Genesis 1:1
         *   Genesis 1:3–2:4
         *   Ezekiel 34:1-31
         */
        String visibleReference =
                stripHtmlTags(chartRefHtml)
                        .replace('\u00A0', ' ')
                        .trim();

        Matcher firstNumber =
                Pattern.compile("(\\d{1,3})")
                        .matcher(visibleReference);

        if (firstNumber.find()) {
            String numericReference =
                    visibleReference.substring(
                            firstNumber.start()
                    );

            chapters.addAll(
                    extractChaptersFromPassagePart(
                            numericReference
                    )
            );
        }

        return chapters;
    }

    private void loadSelectedChart() {
        if (chartsSelector == null || chartsWebEngine == null) {
            return;
        }

        String selection =
                chartsSelector.getValue();

        if (selection == null || selection.isBlank()) {
            return;
        }

        String entryName =
                chartEntries.get(selection);

        if (entryName == null) {
            clearCharts(
                    "Could not find the selected chart."
            );
            return;
        }

        loadChart(selection, entryName);
    }

    private void loadChart(
            String displayName,
            String entryName
    ) {
        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearCharts(
                    "Study Bible EPUB not found. Expected file: "
                            + STUDY_BIBLE_FILENAME
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            ZipEntry entry =
                    zipFile.getEntry(entryName);

            if (entry == null) {
                clearCharts(
                        "The selected chart was not found in the EPUB."
                );
                return;
            }

            String html;

            try (InputStream input = zipFile.getInputStream(entry)) {
                html = new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            html = prepareChartHtml(html);

            chartsTitleLabel.setText(
                    "Life Application Chart — " + displayName
            );

            chartsStatusLabel.setText(
                    "Matched to the current chronological reading."
            );

            chartsWebEngine.loadContent(
                    html,
                    "text/html"
            );

        } catch (IOException error) {
            clearCharts(
                    "Could not load the chart: "
                            + error.getMessage()
            );
            error.printStackTrace();
        }
    }

    private String prepareChartHtml(String html) {
        String cleaned = html.replaceAll(
                "(?is)<link[^>]*rel=[\\\"']stylesheet[\\\"'][^>]*/?>",
                ""
        );

        /*
         * Keep the chart self-contained in JavaFX WebView.
         */
        cleaned = cleaned.replaceAll(
                "(?i)href=[\\\"'][^\\\"']*[\\\"']",
                "href=\"#\""
        );

        cleaned = cleaned.replaceAll(
                "(?is)<img[^>]*>",
                ""
        );

        String style =
                "<style>"
                        + "body{font-family:Georgia,'Times New Roman',serif;"
                        + "font-size:15px;line-height:1.5;margin:16px;"
                        + "color:#222;background:#fff;}"
                        + ".chart-title{font-size:20px;font-weight:bold;"
                        + "text-transform:uppercase;color:#5d2815;"
                        + "margin-bottom:4px;}"
                        + ".chart-ref{font-style:italic;color:#5d2815;"
                        + "margin-bottom:12px;}"
                        + ".chart-lead-in{font-weight:bold;}"
                        + "p{margin:0 0 10px 0;}"
                        + "ul,ol{padding-left:24px;}"
                        + "li{margin-bottom:7px;}"
                        + "table{width:100%;border-collapse:collapse;"
                        + "margin:10px 0;}"
                        + "td,th{vertical-align:top;padding:6px;"
                        + "border:1px solid #d8d8d8;}"
                        + "a{color:#5d2815;text-decoration:none;}"
                        + "</style>";

        if (
                cleaned.toLowerCase(Locale.ENGLISH)
                        .contains("</head>")
        ) {
            cleaned = cleaned.replaceFirst(
                    "(?i)</head>",
                    Matcher.quoteReplacement(
                            style + "</head>"
                    )
            );
        } else {
            cleaned = style + cleaned;
        }

        return cleaned;
    }

    private void clearCharts(String message) {
        chartEntries.clear();

        if (chartsSelector != null) {
            chartsSelector.getItems().clear();
            chartsSelector.setValue(null);
        }

        if (chartsTitleLabel != null) {
            chartsTitleLabel.setText("Charts");
        }

        if (chartsStatusLabel != null) {
            chartsStatusLabel.setText(message);
        }

        if (chartsWebEngine != null) {
            chartsWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;"
                            + "padding:16px;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    // ================================================================
    // Life Application Personality Profiles (local EPUB)
    // ================================================================

    private void updatePersonalityProfilesForReference(String referenceText) {
        if (personalityProfileSelector == null) return;

        personalityProfileEntries.clear();
        personalityProfileSelector.getItems().clear();

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearPersonalityProfile(
                    "Study Bible EPUB not found. Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        /*
         * Build the exact set of Bible chapters contained in the current
         * chronological reading.  This is what makes the personality
         * profiles day-by-day instead of book-by-book.
         *
         * Example:
         *     January 1 = Genesis 1-3
         *
         * becomes:
         *     Genesis -> 1, 2, 3
         */
        Map<String, Set<Integer>> readingChapterMap =
                extractReadingChapterMap(referenceText);

        if (readingChapterMap.isEmpty()) {
            clearPersonalityProfile(
                    "No matching Bible chapter was found for personality profiles."
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            boolean multipleBooks = readingChapterMap.size() > 1;

            for (Map.Entry<String, Set<Integer>> readingEntry
                    : readingChapterMap.entrySet()) {

                loadPersonalityProfilesForPassage(
                        zipFile,
                        readingEntry.getKey(),
                        readingEntry.getValue(),
                        multipleBooks
                );
            }

        } catch (IOException error) {
            clearPersonalityProfile(
                    "Could not open the Study Bible EPUB: " + error.getMessage()
            );
            error.printStackTrace();
            return;
        }

        personalityProfileSelector.getItems().addAll(
                personalityProfileEntries.keySet()
        );

        if (personalityProfileEntries.isEmpty()) {
            clearPersonalityProfile(
                    "No personality profiles match this reading: "
                            + referenceText
            );
            return;
        }

        String firstProfile =
                personalityProfileEntries.keySet().iterator().next();

        personalityProfileSelector.setValue(firstProfile);
        loadSelectedPersonalityProfile();
    }

    private void loadPersonalityProfilesForPassage(
            ZipFile zipFile,
            String book,
            Set<Integer> readingChapters,
            boolean includeBookName
    ) throws IOException {

        String code = studyBookCodes.get(book);
        if (code == null || readingChapters == null || readingChapters.isEmpty()) {
            return;
        }

        String indexEntryName =
                "Profiles/" + code + "-Profiles_Index.xhtml";

        ZipEntry indexEntry = zipFile.getEntry(indexEntryName);
        if (indexEntry == null) return;

        String indexHtml;

        try (InputStream input = zipFile.getInputStream(indexEntry)) {
            indexHtml =
                    new String(
                            input.readAllBytes(),
                            StandardCharsets.UTF_8
                    );
        }

        Pattern profilePattern = Pattern.compile(
                "(?is)<a\\s+[^>]*href=[\\\"']"
                        + "([^\\\"']+_Profile\\.xhtml)"
                        + "(?:#[^\\\"']*)?[\\\"'][^>]*>(.*?)</a>"
        );

        Matcher matcher = profilePattern.matcher(indexHtml);

        while (matcher.find()) {
            String relativeEntry = matcher.group(1).trim();
            String displayName =
                    stripHtmlTags(matcher.group(2)).trim();

            if (displayName.isEmpty()) continue;

            String fullEntryName =
                    relativeEntry.startsWith("Profiles/")
                            ? relativeEntry
                            : "Profiles/" + relativeEntry;

            /*
             * Do not add every profile in the Bible book.
             * First verify that the profile's own reference overlaps
             * one of the chapters in today's reading.
             */
            if (
                    !personalityProfileMatchesReading(
                            zipFile,
                            fullEntryName,
                            book,
                            readingChapters
                    )
            ) {
                continue;
            }

            String selectorName =
                    includeBookName
                            ? book + " — " + displayName
                            : displayName;

            personalityProfileEntries.putIfAbsent(
                    selectorName,
                    fullEntryName
            );
        }
    }

    private boolean personalityProfileMatchesReading(
            ZipFile zipFile,
            String profileEntryName,
            String book,
            Set<Integer> readingChapters
    ) throws IOException {

        ZipEntry profileEntry =
                zipFile.getEntry(profileEntryName);

        if (profileEntry == null) return false;

        String profileHtml;

        try (InputStream input = zipFile.getInputStream(profileEntry)) {
            profileHtml =
                    new String(
                            input.readAllBytes(),
                            StandardCharsets.UTF_8
                    );
        }

        Set<Integer> profileChapters =
                extractProfileReferenceChapters(
                        profileHtml,
                        book
                );

        for (Integer chapter : profileChapters) {
            if (readingChapters.contains(chapter)) {
                return true;
            }
        }

        return false;
    }

    private Set<Integer> extractProfileReferenceChapters(
            String profileHtml,
            String book
    ) {
        LinkedHashSet<Integer> chapters =
                new LinkedHashSet<>();

        if (profileHtml == null || profileHtml.isBlank()) {
            return chapters;
        }

        /*
         * Each Life Application personality profile contains a
         * <p class="pro-ref">...</p> element near the top.
         *
         * Examples from the EPUB:
         *
         * Adam:
         *     Genesis 2:15–5:5
         *
         * Eve:
         *     Genesis 3:20; 4:1
         *
         * Those references are used to decide whether the profile
         * belongs to the current day's reading.
         */
        Pattern proRefPattern = Pattern.compile(
                "(?is)<p\\s+[^>]*class=[\\\"']"
                        + "[^\\\"']*pro-ref[^\\\"']*"
                        + "[\\\"'][^>]*>(.*?)</p>"
        );

        Matcher proRefMatcher =
                proRefPattern.matcher(profileHtml);

        if (!proRefMatcher.find()) {
            return chapters;
        }

        String proRefHtml =
                proRefMatcher.group(1);

        /*
         * First use the readable reference text so ranges such as
         * Genesis 2:15–5:5 expand to chapters 2, 3, 4, and 5.
         */
        String referenceText =
                stripHtmlTags(proRefHtml)
                        .replace('\u00A0', ' ')
                        .trim();

        chapters.addAll(
                extractChaptersForKnownBook(
                        referenceText,
                        book
                )
        );

        /*
         * Also inspect the EPUB links.  This catches entries such as
         * Eve's "Genesis 3:20; 4:1", where the second reference omits
         * the book name in the visible text.
         */
        String code = studyBookCodes.get(book);

        if (code != null) {
            Pattern hrefChapterPattern = Pattern.compile(
                    "(?i)href=[\\\"'][^\\\"']*"
                            + Pattern.quote(code)
                            + "-(\\d{3})"
            );

            Matcher hrefMatcher =
                    hrefChapterPattern.matcher(proRefHtml);

            while (hrefMatcher.find()) {
                try {
                    chapters.add(
                            Integer.parseInt(
                                    hrefMatcher.group(1)
                            )
                    );
                } catch (NumberFormatException ignored) {
                    // Ignore malformed EPUB chapter references.
                }
            }
        }

        return chapters;
    }

    private Map<String, Set<Integer>> extractReadingChapterMap(
            String referenceText
    ) {
        LinkedHashMap<String, Set<Integer>> result =
                new LinkedHashMap<>();

        if (referenceText == null || referenceText.isBlank()) {
            return result;
        }

        List<String> bookNames =
                new ArrayList<>(studyBookCodes.keySet());

        bookNames.sort(
                (a, b) ->
                        Integer.compare(
                                b.length(),
                                a.length()
                        )
        );

        StringBuilder names =
                new StringBuilder();

        for (String name : bookNames) {
            if (names.length() > 0) {
                names.append("|");
            }

            names.append(
                    Pattern.quote(name)
            );
        }

        Pattern bookPattern = Pattern.compile(
                "(?i)\\b(" + names + ")\\b"
        );

        Matcher matcher =
                bookPattern.matcher(referenceText);

        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();
        List<String> books = new ArrayList<>();

        while (matcher.find()) {
            String book =
                    canonicalStudyBookName(
                            matcher.group(1)
                    );

            if (book == null) continue;

            starts.add(matcher.start());
            ends.add(matcher.end());
            books.add(book);
        }

        for (int i = 0; i < books.size(); i++) {
            int segmentStart = ends.get(i);

            int segmentEnd =
                    i + 1 < books.size()
                            ? starts.get(i + 1)
                            : referenceText.length();

            String segment =
                    referenceText.substring(
                            segmentStart,
                            segmentEnd
                    );

            Set<Integer> chapters =
                    extractChaptersFromPassageSegment(
                            segment
                    );

            if (chapters.isEmpty()) {
                continue;
            }

            result.computeIfAbsent(
                    books.get(i),
                    key -> new LinkedHashSet<>()
            ).addAll(chapters);
        }

        return result;
    }

    private Set<Integer> extractChaptersForKnownBook(
            String referenceText,
            String book
    ) {
        LinkedHashSet<Integer> chapters =
                new LinkedHashSet<>();

        if (referenceText == null || referenceText.isBlank()) {
            return chapters;
        }

        String cleaned =
                referenceText
                        .replace('\u00A0', ' ')
                        .trim();

        /*
         * Remove the book name only where it actually appears.
         * The remaining semicolon-separated references inherit the
         * same book.
         */
        cleaned = cleaned.replaceFirst(
                "(?i)^\\s*"
                        + Pattern.quote(book)
                        + "\\s*",
                ""
        );

        String[] pieces =
                cleaned.split("[;\\n]");

        for (String piece : pieces) {
            chapters.addAll(
                    extractChaptersFromPassagePart(
                            piece
                    )
            );
        }

        return chapters;
    }

    private Set<Integer> extractChaptersFromPassageSegment(
            String segment
    ) {
        LinkedHashSet<Integer> chapters =
                new LinkedHashSet<>();

        if (segment == null || segment.isBlank()) {
            return chapters;
        }

        /*
         * A semicolon or line break can begin another reference in the
         * same Bible book.  Commas are deliberately NOT used as
         * separators because they are often verse lists.
         */
        String[] pieces =
                segment.split("[;\\n]");

        for (String piece : pieces) {
            chapters.addAll(
                    extractChaptersFromPassagePart(
                            piece
                    )
            );
        }

        return chapters;
    }

    private Set<Integer> extractChaptersFromPassagePart(
            String passagePart
    ) {
        LinkedHashSet<Integer> chapters =
                new LinkedHashSet<>();

        if (passagePart == null) {
            return chapters;
        }

        String part =
                passagePart
                        .replace('\u00A0', ' ')
                        .trim();

        /*
         * Match only the first reference at the beginning of this
         * passage part.  This prevents verse lists such as
         * "22:54a,63-65" from being mistaken for chapters 63-65.
         *
         * Supported examples:
         *
         *     1-3
         *     2:15-5:5
         *     4:2-8
         *     40:1-37
         */
        Pattern pattern = Pattern.compile(
                "^\\s*(\\d{1,3})"
                        + "(?::(\\d{1,3}[a-zA-Z]?))?"
                        + "(?:\\s*[-\\u2013\\u2014]\\s*"
                        + "(\\d{1,3})"
                        + "(?::(\\d{1,3}[a-zA-Z]?))?"
                        + ")?"
        );

        Matcher matcher =
                pattern.matcher(part);

        if (!matcher.find()) {
            return chapters;
        }

        int startChapter;

        try {
            startChapter =
                    Integer.parseInt(
                            matcher.group(1)
                    );
        } catch (NumberFormatException error) {
            return chapters;
        }

        int endChapter = startChapter;

        String startVerse =
                matcher.group(2);

        String dashNumber =
                matcher.group(3);

        String dashVerse =
                matcher.group(4);

        if (dashNumber != null) {
            try {
                int secondNumber =
                        Integer.parseInt(
                                dashNumber
                        );

                if (startVerse == null) {
                    /*
                     * Genesis 1-3 = chapters 1 through 3.
                     */
                    endChapter = secondNumber;

                } else if (dashVerse != null) {
                    /*
                     * Genesis 2:15-5:5 = chapters 2 through 5.
                     */
                    endChapter = secondNumber;

                } else {
                    /*
                     * Genesis 4:2-8 = verses 2 through 8 in chapter 4,
                     * NOT chapters 4 through 8.
                     */
                    endChapter = startChapter;
                }

            } catch (NumberFormatException ignored) {
                endChapter = startChapter;
            }
        }

        if (endChapter < startChapter) {
            endChapter = startChapter;
        }

        /*
         * Safety guard against malformed references.
         */
        if (endChapter - startChapter > 150) {
            endChapter = startChapter;
        }

        for (
                int chapter = startChapter;
                chapter <= endChapter;
                chapter++
        ) {
            chapters.add(chapter);
        }

        return chapters;
    }

    private void loadSelectedPersonalityProfile() {
        if (
                personalityProfileSelector == null
                        || personalityProfileWebEngine == null
        ) {
            return;
        }

        String selection = personalityProfileSelector.getValue();
        if (selection == null || selection.isBlank()) return;

        String entryName = personalityProfileEntries.get(selection);
        if (entryName == null) {
            clearPersonalityProfile("Could not find the selected personality profile.");
            return;
        }

        loadPersonalityProfile(selection, entryName);
    }

    private void loadPersonalityProfile(
            String displayName,
            String entryName
    ) {
        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearPersonalityProfile(
                    "Study Bible EPUB not found. Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            ZipEntry entry = zipFile.getEntry(entryName);

            if (entry == null) {
                clearPersonalityProfile(
                        "The selected personality profile was not found in the EPUB."
                );
                return;
            }

            String html;
            try (InputStream input = zipFile.getInputStream(entry)) {
                html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }

            html = preparePersonalityProfileHtml(html);

            personalityProfileTitleLabel.setText(
                    "Personality Profile — " + displayName
            );
            personalityProfileStatusLabel.setText(
                    "Loaded locally from " + studyBibleEpubFile.getName()
            );
            personalityProfileWebEngine.loadContent(html, "text/html");

        } catch (IOException error) {
            clearPersonalityProfile(
                    "Could not load the personality profile: " + error.getMessage()
            );
            error.printStackTrace();
        }
    }

    private String preparePersonalityProfileHtml(String html) {
        String cleaned = html.replaceAll(
                "(?is)<link[^>]*rel=[\\\"']stylesheet[\\\"'][^>]*/?>",
                ""
        );

        cleaned = cleaned.replaceAll(
                "(?i)href=[\\\"'][^\\\"']*[\\\"']",
                "href=\"#\""
        );

        cleaned = cleaned.replaceAll(
                "(?is)<img[^>]*>",
                ""
        );

        String style =
                "<style>"
                        + "body{font-family:Georgia,'Times New Roman',serif;font-size:15px;"
                        + "line-height:1.5;margin:16px;color:#222;background:#fff;}"
                        + "h1,h2,h3,.h1,.h2,.h3{font-family:Georgia,'Times New Roman',serif;"
                        + "color:#5d2815;margin-top:10px;}"
                        + "p{margin:0 0 10px 0;}"
                        + "ul{padding-left:22px;}"
                        + "li{margin-bottom:7px;}"
                        + "table{width:100%;border-collapse:collapse;margin:10px 0;}"
                        + "td,th{vertical-align:top;padding:5px;border-bottom:1px solid #ddd;}"
                        + "a{color:#7a2c12;text-decoration:none;}"
                        + "</style>";

        if (cleaned.toLowerCase(Locale.ENGLISH).contains("</head>")) {
            cleaned = cleaned.replaceFirst(
                    "(?i)</head>",
                    Matcher.quoteReplacement(style + "</head>")
            );
        } else {
            cleaned = style + cleaned;
        }

        return cleaned;
    }

    private String stripHtmlTags(String html) {
        if (html == null) return "";

        return html
                .replaceAll("(?is)<[^>]+>", "")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    private void clearPersonalityProfile(String message) {
        personalityProfileEntries.clear();

        if (personalityProfileSelector != null) {
            personalityProfileSelector.getItems().clear();
            personalityProfileSelector.setValue(null);
        }

        if (personalityProfileTitleLabel != null) {
            personalityProfileTitleLabel.setText("Personality Profiles");
        }

        if (personalityProfileStatusLabel != null) {
            personalityProfileStatusLabel.setText(message);
        }

        if (personalityProfileWebEngine != null) {
            personalityProfileWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;padding:16px;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    // ================================================================
    // Life Application Study Notes (local EPUB)
    // ================================================================

    private File findStudyBibleEpub() {
        File studyFolder = new File(STUDY_BIBLE_FOLDER);

        if (!studyFolder.exists()) {
            studyFolder.mkdirs();
        }

        File preferred = new File(studyFolder, STUDY_BIBLE_FILENAME);
        if (preferred.isFile()) return preferred;

        File[] epubs = studyFolder.listFiles((dir, name) ->
                name.toLowerCase(Locale.ENGLISH).endsWith(".epub")
        );

        if (epubs == null || epubs.length == 0) return null;

        for (File epub : epubs) {
            String name = epub.getName().toLowerCase(Locale.ENGLISH);
            if (name.contains("life") && name.contains("application")) {
                return epub;
            }
        }

        return epubs[0];
    }

    private void updateStudyNotesForReference(String referenceText) {
        updatePersonalityProfilesForReference(referenceText);
        updateChartsForReference(referenceText);
        updateMapsForReference(referenceText);
        updateBookIntroductionsForReference(referenceText);
        updateOriginalLanguageForReference(referenceText);
        updateTimelineForReference(referenceText);

        if (studyReferenceSelector == null) return;

        List<String> references = extractStudyReferences(referenceText);

        studyReferenceSelector.getItems().clear();
        studyReferenceSelector.getItems().addAll(references);

        if (references.isEmpty()) {
            clearStudyNotes("No matching chapter reference was found for this reading.");
            return;
        }

        studyReferenceSelector.setValue(references.get(0));
        loadSelectedStudyNotes();
    }

    private List<String> extractStudyReferences(String referenceText) {
        LinkedHashSet<String> found = new LinkedHashSet<>();

        if (referenceText == null || referenceText.isBlank()) {
            return new ArrayList<>();
        }

        List<String> bookNames = new ArrayList<>(studyBookCodes.keySet());
        bookNames.sort((a, b) -> Integer.compare(b.length(), a.length()));

        StringBuilder names = new StringBuilder();
        for (String name : bookNames) {
            if (names.length() > 0) names.append("|");
            names.append(Pattern.quote(name));
        }

        /*
         * Match all of these forms:
         *
         *   Ezekiel 44
         *   Ezekiel 44-46
         *   Ezekiel 44:1-46:24
         *   John 3:16
         *   John 3:16-21
         *
         * The important distinction is:
         *
         *   Ezekiel 44:1-46:24  -> chapters 44, 45, 46
         *   Ezekiel 44:1-24     -> chapter 44 only
         *
         * A range ending with another chapter:verse pair means the
         * reading crosses chapters. A plain number after a verse is
         * treated as the ending VERSE, not another chapter.
         */
        Pattern pattern = Pattern.compile(
                "(?i)(" + names + ")\\s+"
                        + "(\\d{1,3})"              // 2 = start chapter
                        + "(?::(\\d{1,3}))?"        // 3 = start verse
                        + "(?:\\s*-\\s*"
                        + "(?:(\\d{1,3}):(\\d{1,3})" // 4/5 = end chapter/verse
                        + "|(\\d{1,3}))"            // 6 = plain end number
                        + ")?"
        );

        Matcher matcher = pattern.matcher(referenceText);

        while (matcher.find()) {
            String book = canonicalStudyBookName(matcher.group(1));
            if (book == null) continue;

            int startChapter = Integer.parseInt(matcher.group(2));
            int endChapter = startChapter;

            String startVerseText = matcher.group(3);
            String endChapterText = matcher.group(4);
            String plainEndText = matcher.group(6);

            if (endChapterText != null) {
                // Example: Ezekiel 44:1-46:24
                int candidate = Integer.parseInt(endChapterText);

                if (
                        candidate >= startChapter
                                && candidate - startChapter <= 50
                ) {
                    endChapter = candidate;
                }

            } else if (
                    startVerseText == null
                            && plainEndText != null
            ) {
                // Example: Genesis 1-3
                int candidate = Integer.parseInt(plainEndText);

                if (
                        candidate >= startChapter
                                && candidate - startChapter <= 50
                ) {
                    endChapter = candidate;
                }
            }

            for (
                    int chapter = startChapter;
                    chapter <= endChapter;
                    chapter++
            ) {
                found.add(book + " " + chapter);
            }
        }

        return new ArrayList<>(found);
    }

    private String canonicalStudyBookName(String matchedName) {
        for (String book : studyBookCodes.keySet()) {
            if (book.equalsIgnoreCase(matchedName)) {
                if (book.equals("Psalm")) return "Psalms";
                if (book.equals("Proverb")) return "Proverbs";
                if (book.equals("Song of Songs")) return "Song of Solomon";
                return book;
            }
        }
        return null;
    }

    private void loadSelectedStudyNotes() {
        String selection = studyReferenceSelector.getValue();
        if (selection == null || selection.isBlank()) return;

        int lastSpace = selection.lastIndexOf(' ');
        if (lastSpace <= 0) return;

        String book = selection.substring(0, lastSpace);
        int chapter;

        try {
            chapter = Integer.parseInt(selection.substring(lastSpace + 1));
        } catch (NumberFormatException error) {
            clearStudyNotes("Could not determine the study-note chapter.");
            return;
        }

        loadStudyNotes(book, chapter);
    }

    private void loadStudyNotes(String book, int chapter) {
        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            studyBibleEpubFile = findStudyBibleEpub();
        }

        if (studyBibleEpubFile == null || !studyBibleEpubFile.isFile()) {
            clearStudyNotes(
                    "Study Bible EPUB not found in the bibleyear project folder. "
                            + "Expected file: " + STUDY_BIBLE_FILENAME
            );
            return;
        }

        String code = studyBookCodes.get(book);
        if (code == null) {
            clearStudyNotes("No study-note mapping exists for " + book + ".");
            return;
        }

        String entryName = String.format(
                Locale.ENGLISH,
                "StudyNotes/%s-%03d_StudyNotes.xhtml",
                code,
                chapter
        );

        try (ZipFile zipFile = new ZipFile(studyBibleEpubFile)) {
            ZipEntry entry = zipFile.getEntry(entryName);

            if (entry == null) {
                clearStudyNotes("No study notes were found for " + book + " " + chapter + ".");
                return;
            }

            String html;
            try (InputStream input = zipFile.getInputStream(entry)) {
                html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }

            html = prepareStudyNotesHtml(html);

            studyNotesTitleLabel.setText("Life Application Study Notes — " + book + " " + chapter);
            studyNotesStatusLabel.setText("Loaded locally from " + studyBibleEpubFile.getName());
            studyNotesWebEngine.loadContent(html, "text/html");

        } catch (IOException error) {
            clearStudyNotes("Could not open the Study Bible EPUB: " + error.getMessage());
            error.printStackTrace();
        }
    }

    private String prepareStudyNotesHtml(String html) {
        String cleaned = html.replaceAll(
                "(?is)<link[^>]*rel=[\\\"']stylesheet[\\\"'][^>]*/?>",
                ""
        );

        cleaned = cleaned.replaceAll(
                "(?i)href=[\\\"'][^\\\"']*[\\\"']",
                "href=\"#\""
        );

        String style =
                "<style>"
                        + "body{font-family:Georgia,'Times New Roman',serif;font-size:16px;"
                        + "line-height:1.55;margin:18px;color:#222;background:#fff;}"
                        + ".h2,.digital{font-size:22px;font-weight:bold;margin-bottom:18px;}"
                        + ".StudyNote{margin:0 0 18px 0;padding-bottom:12px;"
                        + "border-bottom:1px solid #ddd;}"
                        + ".note-ref{font-weight:bold;text-decoration:none;color:#7a2c12;}"
                        + ".note,.note-para{margin:0 0 9px 0;}"
                        + "a{color:#7a2c12;text-decoration:none;}"
                        + "</style>";

        if (cleaned.toLowerCase(Locale.ENGLISH).contains("</head>")) {
            cleaned = cleaned.replaceFirst("(?i)</head>", Matcher.quoteReplacement(style + "</head>"));
        } else {
            cleaned = style + cleaned;
        }

        return cleaned;
    }

    private void clearStudyNotes(String message) {
        if (studyNotesTitleLabel != null) {
            studyNotesTitleLabel.setText("Life Application Study Notes");
        }
        if (studyNotesStatusLabel != null) {
            studyNotesStatusLabel.setText(message);
        }
        if (studyNotesWebEngine != null) {
            studyNotesWebEngine.loadContent(
                    "<html><body style='font-family:Georgia,serif;padding:18px;'>"
                            + escapeHtml(message)
                            + "</body></html>",
                    "text/html"
            );
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // ================================================================
    // Reading Completion Progress
    // ================================================================

    private void initializeReadingProgressForCurrentYear() {
        /*
         * Completion is user-driven.
         *
         * Do not automatically mark earlier dates complete. Each plan
         * keeps its own completion history and a day receives a check
         * only after the user marks that reading completed.
         */
    }

    private void loadCurrentReadingCompletion() {
        if (readingCompletedCheckBox == null) return;

        if (currentStudyReference != null || currentDayIndex < 0 || currentDayIndex >= readingDays.size()) {
            setCompletionCheckBoxForStudyMode();
            return;
        }

        readingCompletedCheckBox.setDisable(false);

        ReadingDay readingDay = readingDays.get(currentDayIndex);
        int year = LocalDate.now().getYear();
        boolean completed = false;

        if (databaseConfigLoaded) {
            String sql =
                    "SELECT completed FROM reading_progress "
                            + "WHERE plan_id = ? "
                            + "AND reading_year = ? "
                            + "AND month_name = ? "
                            + "AND day_number = ?";

            try (
                    Connection connection = getDatabaseConnection();
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                statement.setString(1, currentReadingPlanId);
                statement.setInt(2, year);
                statement.setString(3, readingDay.getMonth());
                statement.setInt(4, readingDay.getDay());

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) completed = result.getBoolean("completed");
                }
            } catch (SQLException error) {
                error.printStackTrace();
            }
        }

        updatingCompletionCheckBox = true;
        readingCompletedCheckBox.setSelected(completed);
        updatingCompletionCheckBox = false;
    }

    private void saveCurrentReadingCompletion() {
        if (!databaseConfigLoaded || currentStudyReference != null
                || currentDayIndex < 0 || currentDayIndex >= readingDays.size()) return;

        ReadingDay readingDay = readingDays.get(currentDayIndex);
        int year = LocalDate.now().getYear();
        boolean completed = readingCompletedCheckBox.isSelected();

        String sql =
                "INSERT INTO reading_progress "
                        + "(plan_id, reading_year, month_name, day_number, completed, completed_at) "
                        + "VALUES (?, ?, ?, ?, ?, "
                        + "CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE NULL END) "
                        + "ON CONFLICT(plan_id, reading_year, month_name, day_number) "
                        + "DO UPDATE SET "
                        + "completed = excluded.completed, "
                        + "completed_at = CASE "
                        + "WHEN excluded.completed = 1 "
                        + "THEN CURRENT_TIMESTAMP "
                        + "ELSE NULL END";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, currentReadingPlanId);
            statement.setInt(2, year);
            statement.setString(3, readingDay.getMonth());
            statement.setInt(4, readingDay.getDay());
            statement.setBoolean(5, completed);
            statement.setBoolean(6, completed);
            statement.executeUpdate();
            refreshReadingPlanCompletionMarks();
        } catch (SQLException error) {
            noteStatusLabel.setText("Could not save reading completion.");
            error.printStackTrace();
        }
    }

    private Set<String> loadCompletedReadingKeysForYear(
            int year
    ) {
        Set<String> completedDays = new HashSet<>();

        if (!databaseConfigLoaded) {
            return completedDays;
        }

        String sql =
                "SELECT month_name, day_number "
                        + "FROM reading_progress "
                        + "WHERE plan_id = ? "
                        + "AND reading_year = ? "
                        + "AND completed = TRUE";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, currentReadingPlanId);
            statement.setInt(2, year);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    completedDays.add(
                            progressKey(
                                    result.getString("month_name"),
                                    result.getInt("day_number")
                            )
                    );
                }
            }

        } catch (SQLException error) {
            error.printStackTrace();
        }

        return completedDays;
    }

    private void refreshReadingPlanCompletionMarks() {
        if (readingPlanTree == null) return;

        Set<String> completedDays =
                loadCompletedReadingKeysForYear(
                        LocalDate.now().getYear()
                );

        for (Map.Entry<TreeItem<String>, ReadingDay> entry : treeReadingMap.entrySet()) {
            ReadingDay readingDay = entry.getValue();
            String normalText = readingDay.getMonth() + " " + readingDay.getDay();
            if (completedDays.contains(progressKey(readingDay.getMonth(), readingDay.getDay()))) {
                entry.getKey().setValue("✓ " + normalText);
            } else {
                entry.getKey().setValue(normalText);
            }
        }

        readingPlanTree.refresh();
    }

    private String progressKey(String month, int day) {
        return month + "|" + day;
    }

    private void setCompletionCheckBoxForStudyMode() {
        if (readingCompletedCheckBox == null) return;
        updatingCompletionCheckBox = true;
        readingCompletedCheckBox.setSelected(false);
        readingCompletedCheckBox.setDisable(true);
        updatingCompletionCheckBox = false;
    }

    private void setCompletionCheckBoxForHome() {
        setCompletionCheckBoxForStudyMode();
    }

    private void loadDatabaseConfig() {
        /*
         * Portable SQLite edition.
         *
         * The database is stored relative to the application folder:
         *
         *     data/bible-reader.db
         *
         * That makes the database travel with the project/USB instead
         * of depending on a MySQL server.
         */
        try {
            Class.forName("org.sqlite.JDBC");

            File dataFolder = new File("data");
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                throw new IOException(
                        "Could not create the data folder."
                );
            }

            try (
                    Connection connection =
                            DriverManager.getConnection(
                                    SQLITE_DATABASE_URL
                            )
            ) {
                createSQLiteTables(connection);
            }

            databaseUrl = SQLITE_DATABASE_URL;
            databaseUser = "";
            databasePassword = "";
            databaseConfigLoaded = true;

        } catch (ClassNotFoundException error) {
            databaseConfigLoaded = false;
            System.err.println(
                    "SQLite JDBC driver not found. "
                            + "Make sure sqlite-jdbc is on the classpath."
            );
            error.printStackTrace();

        } catch (IOException | SQLException error) {
            databaseConfigLoaded = false;
            System.err.println(
                    "Could not initialize the SQLite database."
            );
            error.printStackTrace();
        }
    }

    private void createSQLiteTables(
            Connection connection
    ) throws SQLException {

        String createDailyNotes =
                "CREATE TABLE IF NOT EXISTS daily_notes ("
                        + "note_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "plan_id TEXT NOT NULL DEFAULT 'reading_plan_3', "
                        + "month_name TEXT NOT NULL, "
                        + "day_number INTEGER NOT NULL, "
                        + "note_text TEXT NOT NULL, "
                        + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";

        String createReadingProgress =
                "CREATE TABLE IF NOT EXISTS reading_progress ("
                        + "progress_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "plan_id TEXT NOT NULL DEFAULT 'reading_plan_3', "
                        + "reading_year INTEGER NOT NULL, "
                        + "month_name TEXT NOT NULL, "
                        + "day_number INTEGER NOT NULL, "
                        + "completed INTEGER NOT NULL DEFAULT 0, "
                        + "completed_at DATETIME, "
                        + "UNIQUE(plan_id, reading_year, month_name, day_number)"
                        + ")";

        try (
                java.sql.Statement statement =
                        connection.createStatement()
        ) {
            statement.execute(createDailyNotes);
            statement.execute(createReadingProgress);
        }

        migrateSQLiteMultiPlanSchema(connection);
    }

    private void migrateSQLiteMultiPlanSchema(
            Connection connection
    ) throws SQLException {

        if (!sqliteColumnExists(
                connection,
                "daily_notes",
                "plan_id"
        )) {
            try (java.sql.Statement statement =
                         connection.createStatement()) {
                statement.executeUpdate(
                        "ALTER TABLE daily_notes "
                                + "ADD COLUMN plan_id TEXT "
                                + "NOT NULL DEFAULT 'reading_plan_3'"
                );
            }
        }

        if (!sqliteColumnExists(
                connection,
                "reading_progress",
                "plan_id"
        )) {
            /*
             * SQLite cannot remove the old UNIQUE(year, month, day)
             * constraint in place, so rebuild the table once and copy
             * the existing completion history into Reading Plan 3.
             */
            try (java.sql.Statement statement =
                         connection.createStatement()) {

                statement.executeUpdate(
                        "CREATE TABLE reading_progress_new ("
                                + "progress_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "plan_id TEXT NOT NULL DEFAULT 'reading_plan_3', "
                                + "reading_year INTEGER NOT NULL, "
                                + "month_name TEXT NOT NULL, "
                                + "day_number INTEGER NOT NULL, "
                                + "completed INTEGER NOT NULL DEFAULT 0, "
                                + "completed_at DATETIME, "
                                + "UNIQUE(plan_id, reading_year, month_name, day_number)"
                                + ")"
                );

                statement.executeUpdate(
                        "INSERT INTO reading_progress_new "
                                + "(progress_id, plan_id, reading_year, "
                                + "month_name, day_number, completed, completed_at) "
                                + "SELECT progress_id, 'reading_plan_3', "
                                + "reading_year, month_name, day_number, "
                                + "completed, completed_at "
                                + "FROM reading_progress"
                );

                statement.executeUpdate(
                        "DROP TABLE reading_progress"
                );

                statement.executeUpdate(
                        "ALTER TABLE reading_progress_new "
                                + "RENAME TO reading_progress"
                );
            }
        }
    }

    private boolean sqliteColumnExists(
            Connection connection,
            String table,
            String column
    ) throws SQLException {

        try (
                java.sql.Statement statement =
                        connection.createStatement();
                ResultSet result =
                        statement.executeQuery(
                                "PRAGMA table_info(" + table + ")"
                        )
        ) {
            while (result.next()) {
                if (column.equalsIgnoreCase(
                        result.getString("name")
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    private Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(SQLITE_DATABASE_URL);
    }

    private void loadNoteHistory() {
        noteHistoryList.getItems().clear();

        if (currentDayIndex < 0) return;

        if (!databaseConfigLoaded) {
            noteStatusLabel.setText("Database configuration unavailable.");
            return;
        }

        ReadingDay readingDay = readingDays.get(currentDayIndex);

        String sql =
                "SELECT note_id, note_text, created_at "
                        + "FROM daily_notes "
                        + "WHERE plan_id = ? "
                        + "AND month_name = ? "
                        + "AND day_number = ? "
                        + "ORDER BY created_at DESC, note_id DESC";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, currentReadingPlanId);
            statement.setString(2, readingDay.getMonth());
            statement.setInt(3, readingDay.getDay());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    NoteEntry entry = new NoteEntry(
                            result.getInt("note_id"),
                            result.getString("note_text"),
                            result.getTimestamp("created_at")
                    );
                    noteHistoryList.getItems().add(entry);
                }
            }

        } catch (SQLException error) {
            noteStatusLabel.setText("Could not load journal entries.");
            error.printStackTrace();
        }
    }

    private void addCurrentNoteEntry() {
        if (currentStudyReference != null) {
            noteStatusLabel.setText("Bible Study journal entries are not enabled yet.");
            return;
        }

        if (currentDayIndex < 0) return;

        if (!databaseConfigLoaded) {
            noteStatusLabel.setText("Database configuration unavailable.");
            return;
        }

        String noteText = newNoteArea.getText().trim();

        if (noteText.isEmpty()) {
            noteStatusLabel.setText("Enter a note first.");
            return;
        }

        ReadingDay readingDay = readingDays.get(currentDayIndex);
        String sql =
                "INSERT INTO daily_notes "
                        + "(plan_id, month_name, day_number, note_text) "
                        + "VALUES (?, ?, ?, ?)";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, currentReadingPlanId);
            statement.setString(2, readingDay.getMonth());
            statement.setInt(3, readingDay.getDay());
            statement.setString(4, noteText);
            statement.executeUpdate();

            newNoteArea.clear();
            noteStatusLabel.setText("Entry added ✓");
            loadNoteHistory();

        } catch (SQLException error) {
            noteStatusLabel.setText("Could not add entry.");
            error.printStackTrace();
        }
    }

    private void removeSelectedNoteEntry() {
        if (currentStudyReference != null) return;

        NoteEntry selectedEntry = noteHistoryList.getSelectionModel().getSelectedItem();

        if (selectedEntry == null) {
            noteStatusLabel.setText("Select an entry to remove.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Journal Entry");
        confirmation.setHeaderText("Remove this journal entry?");
        confirmation.setContentText(
                selectedEntry.getFormattedDate()
                        + "\n\n"
                        + selectedEntry.getNoteText()
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        String sql = "DELETE FROM daily_notes WHERE note_id = ?";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, selectedEntry.getNoteId());
            int deleted = statement.executeUpdate();

            if (deleted > 0) {
                noteStatusLabel.setText("Entry removed.");
                loadNoteHistory();
            }

        } catch (SQLException error) {
            noteStatusLabel.setText("Could not remove entry.");
            error.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
