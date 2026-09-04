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

        bookIntroductionTab = new Tab("Book Introduction", bookIntroductionContent);
        bookIntroductionTab.setClosable(false);

        personalityProfileTab = new Tab("Personality Profiles", personalityProfileContent);
        personalityProfileTab.setClosable(false);

        chartsTab = new Tab("Charts", chartsContent);
        chartsTab.setClosable(false);

        /*
         * Book Introduction is shown only on the first chronological
         * reading day in which a Bible book appears.
         *
         * Personality Profiles and Charts remain available day-by-day.
         */
        topInfoTabs = new TabPane(personalityProfileTab, chartsTab);
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
         * The right-side Personality Profiles / Charts / Study Notes / Journal
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

        VBox readingPlanPanel = new VBox(8, readingPlanLabel, readingPlanTree);
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
        treeReadingMap.clear();

        TreeItem<String> rootItem = new TreeItem<>("Chronological Reading Plan");
        rootItem.setExpanded(true);

        addMonthToTree(rootItem, "January", January.getReadings());
        addMonthToTree(rootItem, "February", February.getReadings());
        addMonthToTree(rootItem, "March", March.getReadings());
        addMonthToTree(rootItem, "April", April.getReadings());
        addMonthToTree(rootItem, "May", May.getReadings());
        addMonthToTree(rootItem, "June", June.getReadings());
        addMonthToTree(rootItem, "July", July.getReadings());
        addMonthToTree(rootItem, "August", August.getReadings());
        addMonthToTree(rootItem, "September", September.getReadings());
        addMonthToTree(rootItem, "October", October.getReadings());
        addMonthToTree(rootItem, "November", November.getReadings());
        addMonthToTree(rootItem, "December", December.getReadings());

        TreeView<String> tree = new TreeView<>(rootItem);
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

    private void addMonthToTree(
            TreeItem<String> rootItem,
            String month,
            Map<Integer, String> readings
    ) {
        TreeItem<String> monthItem = new TreeItem<>(month);

        for (Map.Entry<Integer, String> entry : readings.entrySet()) {
            int day = entry.getKey();
            TreeItem<String> dayItem = new TreeItem<>(month + " " + day);
            ReadingDay readingDay = findReadingDay(month, day);
            if (readingDay != null) treeReadingMap.put(dayItem, readingDay);
            monthItem.getChildren().add(dayItem);
        }

        rootItem.getChildren().add(monthItem);
    }

    private void loadFullReadingPlan() {
        readingDays.clear();
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

    private void addMonth(String month, Map<Integer, String> readings) {
        for (Map.Entry<Integer, String> entry : readings.entrySet()) {
            readingDays.add(new ReadingDay(month, entry.getKey(), entry.getValue()));
        }
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

        if (
                topInfoTabs != null
                        && bookIntroductionTab != null
                        && personalityProfileTab != null
        ) {
            topInfoTabs.getTabs().remove(bookIntroductionTab);

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
                "Chronological Bible Reading Plan"
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
                "Select any date in the calendar to open that day's chronological Bible reading."
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
         * Default for an ordinary reading day:
         * hide the Introduction tab completely and show Personality
         * Profiles only.
         *
         * Example:
         *     September 3 — Ezekiel 40:1-37
         *
         * Ezekiel was introduced earlier in the Reading Plan, so the
         * Book Introduction tab is not shown on this day.
         */
        topInfoTabs.getTabs().remove(bookIntroductionTab);

        if (!topInfoTabs.getTabs().contains(personalityProfileTab)) {
            topInfoTabs.getTabs().add(personalityProfileTab);
        }

        if (!topInfoTabs.getTabs().contains(chartsTab)) {
            topInfoTabs.getTabs().add(chartsTab);
        }

        topInfoTabs.getSelectionModel().select(personalityProfileTab);

        Map<String, Set<Integer>> currentBooks =
                extractReadingChapterMap(referenceText);

        if (currentBooks.isEmpty()) {
            return;
        }

        List<String> newBooks = new ArrayList<>();

        for (String book : currentBooks.keySet()) {
            if (isFirstAppearanceOfBook(book, readingIndex)) {
                newBooks.add(book);
            }
        }

        /*
         * No new Bible book begins today, so there is intentionally no
         * Book Introduction tab.
         */
        if (newBooks.isEmpty()) {
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
            return;
        }

        /*
         * A new Bible book begins today.  Add the Introduction tab,
         * place it first, load the introduction, and select it
         * automatically.
         *
         * Example:
         *     January 1 — Genesis 1...
         *
         * Genesis is appearing for the first time, so the Genesis
         * introduction is shown.
         */
        if (!topInfoTabs.getTabs().contains(bookIntroductionTab)) {
            topInfoTabs.getTabs().add(0, bookIntroductionTab);
        }

        bookIntroductionSelector.setValue(bookToShow);
        loadSelectedBookIntroduction();

        topInfoTabs.getSelectionModel().select(bookIntroductionTab);

        if (newBooks.size() == 1) {
            bookIntroductionStatusLabel.setText(
                    "First appearance of " + bookToShow
                            + " in the chronological Reading Plan."
            );
        } else {
            bookIntroductionStatusLabel.setText(
                    "New books begin in today's chronological reading. "
                            + "Use the selector to view their introductions."
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
        updateBookIntroductionsForReference(referenceText);
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

        Pattern pattern = Pattern.compile(
                "(?i)(" + names + ")\\s+(\\d{1,3})(?:\\s*-\\s*(\\d{1,3}))?"
        );

        Matcher matcher = pattern.matcher(referenceText);

        while (matcher.find()) {
            String book = canonicalStudyBookName(matcher.group(1));
            if (book == null) continue;

            int startChapter = Integer.parseInt(matcher.group(2));
            int endChapter = startChapter;

            if (matcher.group(3) != null) {
                int candidate = Integer.parseInt(matcher.group(3));
                if (candidate >= startChapter && candidate - startChapter <= 20) {
                    endChapter = candidate;
                }
            }

            for (int chapter = startChapter; chapter <= endChapter; chapter++) {
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
        if (!databaseConfigLoaded || readingDays.isEmpty()) return;

        int year = LocalDate.now().getYear();
        String countSql = "SELECT COUNT(*) FROM reading_progress WHERE reading_year = ?";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement countStatement = connection.prepareStatement(countSql)
        ) {
            countStatement.setInt(1, year);

            try (ResultSet result = countStatement.executeQuery()) {
                if (result.next() && result.getInt(1) > 0) return;
            }

            LocalDate today = LocalDate.now();
            String todayMonth = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            int todayIndex = findReadingIndex(todayMonth, today.getDayOfMonth());
            if (todayIndex < 0) return;

            String insertSql =
                    "INSERT INTO reading_progress "
                            + "(reading_year, month_name, day_number, completed, completed_at) "
                            + "VALUES (?, ?, ?, TRUE, CURRENT_TIMESTAMP)";

            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                for (int i = 0; i <= todayIndex; i++) {
                    ReadingDay readingDay = readingDays.get(i);
                    insertStatement.setInt(1, year);
                    insertStatement.setString(2, readingDay.getMonth());
                    insertStatement.setInt(3, readingDay.getDay());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }

        } catch (SQLException error) {
            System.err.println("Could not initialize reading progress.");
            error.printStackTrace();
        }
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
                            + "WHERE reading_year = ? AND month_name = ? AND day_number = ?";

            try (
                    Connection connection = getDatabaseConnection();
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                statement.setInt(1, year);
                statement.setString(2, readingDay.getMonth());
                statement.setInt(3, readingDay.getDay());

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
                        + "(reading_year, month_name, day_number, completed, completed_at) "
                        + "VALUES (?, ?, ?, ?, "
                        + "CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE NULL END) "
                        + "ON CONFLICT(reading_year, month_name, day_number) "
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
            statement.setInt(1, year);
            statement.setString(2, readingDay.getMonth());
            statement.setInt(3, readingDay.getDay());
            statement.setBoolean(4, completed);
            statement.setBoolean(5, completed);
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
                        + "WHERE reading_year = ? "
                        + "AND completed = TRUE";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, year);

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
                        + "month_name TEXT NOT NULL, "
                        + "day_number INTEGER NOT NULL, "
                        + "note_text TEXT NOT NULL, "
                        + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";

        String createReadingProgress =
                "CREATE TABLE IF NOT EXISTS reading_progress ("
                        + "progress_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "reading_year INTEGER NOT NULL, "
                        + "month_name TEXT NOT NULL, "
                        + "day_number INTEGER NOT NULL, "
                        + "completed INTEGER NOT NULL DEFAULT 0, "
                        + "completed_at DATETIME, "
                        + "UNIQUE(reading_year, month_name, day_number)"
                        + ")";

        try (
                java.sql.Statement statement =
                        connection.createStatement()
        ) {
            statement.execute(createDailyNotes);
            statement.execute(createReadingProgress);
        }
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
                        + "WHERE month_name = ? "
                        + "AND day_number = ? "
                        + "ORDER BY created_at DESC, note_id DESC";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, readingDay.getMonth());
            statement.setInt(2, readingDay.getDay());

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
                "INSERT INTO daily_notes (month_name, day_number, note_text) VALUES (?, ?, ?)";

        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, readingDay.getMonth());
            statement.setInt(2, readingDay.getDay());
            statement.setString(3, noteText);
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
