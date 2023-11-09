import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

public class DictionaryDisplay extends JPanel {
    private static Dictionary d;
    private JList<String> searchResults;
    private JList<String> historyEntries;
    private ArrayList<String> keyList;
    private static Deque<String> history;
    private String mode;

    // main constructor
    public DictionaryDisplay() {
        super(new GridLayout(1, 1));

        // load data from files
        d = new Dictionary();
        d.importSlangList("data/user_slang.txt");

        history = new ArrayDeque<>();
        importHistory("data/history.txt");

        // retrieve all the slang entries to display on the search results pane
        keyList = d.getKeyList();

        // display the imported search history on the history tab
        historyEntries = new JList<>();
        historyEntries.setListData(history.toArray(new String[history.size()]));

        // initialize the default search mode
        mode = "key";

        // create a tabbed pane as main component
        JTabbedPane tabs = new JTabbedPane();

        // create tabs for each group of functions
        JComponent dict = dictPanel();
        tabs.addTab("Dictionary", null, dict,
                "View slang entries from dictionary");
        tabs.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent daily = makeTextPanel("Random slang");
        tabs.addTab("Random Slang", null, daily,
                "View a random slang from the dictionary");
        tabs.setMnemonicAt(0, KeyEvent.VK_2);

        JComponent games = gamesPanel();
        tabs.addTab("Minigames", null, games,
                "Play slang minigames");
        tabs.setMnemonicAt(0, KeyEvent.VK_3);

        JComponent history = historyPanel();
        tabs.addTab("History", null, history,
                "View search history");
        tabs.setMnemonicAt(0, KeyEvent.VK_4);

        add(tabs);

        // enable tab scrolling when window width is too small
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    // export the search history to a text file
    protected static boolean exportHistory(String fname) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(fname));

            for (String entry : history) {
                buffer.write(entry);
                buffer.newLine();
            }

            buffer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // import the search history from a text file
    protected boolean importHistory(String fname) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fname));
            String line = "";

            while ((line = buffer.readLine()) != null) {
                history.push(line);
            }

            buffer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // UI panel for the dictionary view
    protected JComponent dictPanel() {
        // create a button to edit the selected slang
        JButton editButton = new JButton("Edit slang");
        editButton.setEnabled(false);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSlangDialog();
            }
        });

        // create a button to delete the selected slang
        JButton deleteButton = new JButton("Delete slang");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show confirm dialog
                int input = JOptionPane.showConfirmDialog(null, "Do you want to delete this slang?",
                        "Confirm slang delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (input == 0) {
                    // OK
                    String key = (String) searchResults.getSelectedValue();
                    d.delete(key);
                    keyList.remove(key);
                    searchResults.setListData(keyList.toArray(new String[keyList.size()]));

                    // show complete dialog
                    JOptionPane.showConfirmDialog(null,
                            "Slang deleted successfully.", "Delete complete", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // create a JTextPane to display the selected slang's definition
        JTextPane definition = new JTextPane();
        definition.setEditable(false);
        definition.setBorder(BorderFactory.createCompoundBorder(definition.getBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JScrollPane definitionScrollPane = new JScrollPane(definition);

        // disable the edit and delete buttons above if the definition pane is empty
        definition.getDocument().addDocumentListener(new DocumentListener() {
            // override methods for DocumentListener
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonStatus(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonStatus(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonStatus(e);
            }

            void updateButtonStatus(DocumentEvent e) {
                String content = definition.getText();

                if (content.isEmpty() || content == null) {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        JPanel slangOptionPane = new JPanel();
        slangOptionPane.setLayout(new BoxLayout(slangOptionPane, BoxLayout.X_AXIS));
        slangOptionPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
        slangOptionPane.setPreferredSize(new Dimension(630, 30));
        slangOptionPane.add(editButton);
        slangOptionPane.add(Box.createRigidArea(new Dimension(5, 0)));
        slangOptionPane.add(deleteButton);

        // create a panel to contain the components above
        JPanel slangInfoPane = new JPanel(new BorderLayout(10, 10));
        slangInfoPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        slangInfoPane.add(slangOptionPane, BorderLayout.NORTH);
        slangInfoPane.add(definitionScrollPane, BorderLayout.CENTER);

        // create a search field with a search button
        PlaceholderField searchField = new PlaceholderField("Enter search terms...", true);
        searchField.setPreferredSize(new Dimension(225, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        ActionListener searchFieldListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                definition.setText("");

                StyledDocument doc = definition.getStyledDocument();
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setForeground(sas, Color.BLUE);
                StyleConstants.setBold(sas, true);
                StyleConstants.setFontSize(sas, 24);

                try {
                    if (mode == "key") {
                        searchResults.setSelectedIndex(0);
                        String key = searchResults.getSelectedValue();
                        displayDefinition(doc, sas, key, false);
                    } else if (mode == "value") {
                        int n = searchResults.getModel().getSize();

                        for (int i = 0; i < n - 1; i++) {
                            String entry = searchResults.getModel().getElementAt(i);
                            displayDefinition(doc, sas, entry, true);
                        }

                        String entry = searchResults.getModel().getElementAt(n - 1);
                        displayDefinition(doc, sas, entry, false);
                    }
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        };

        searchField.addActionListener(searchFieldListener);

        // create a JComboBox to switch between search modes
        String[] searchModes = { "Search by slang", "Search by slang definition" };
        JComboBox<String> searchModeSelector = new JComboBox<>(searchModes);

        // create a JPanel to hold the search field, the mode selector and a search
        // button if searching by definition
        JPanel searchFieldPane = new JPanel(new BorderLayout(5, 5));
        searchFieldPane.add(searchModeSelector, BorderLayout.NORTH);
        searchFieldPane.add(searchField, BorderLayout.CENTER);

        ActionListener cbListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                String searchMode = (String) cb.getSelectedItem();

                if (searchMode.equals("Search by slang")) {
                    mode = "key";
                } else if (searchMode.equals("Search by slang definition")) {
                    mode = "value";

                    // add a search button
                    JButton searchButton = new JButton("Go");
                    searchButton.setPreferredSize(new Dimension(30, 30));
                    searchButton.setMargin(new Insets(0, 0, 0, 0));

                    searchButton.addActionListener(searchFieldListener);

                    searchFieldPane.add(searchButton, BorderLayout.EAST);
                    searchFieldPane.revalidate();
                }
            }
        };

        searchModeSelector.addActionListener(cbListener);

        searchResults = new JList<>(keyList.toArray(new String[keyList.size()]));
        JScrollPane resultScrollPane = new JScrollPane(searchResults);

        searchResults.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    definition.setText("");

                    StyledDocument doc = definition.getStyledDocument();
                    SimpleAttributeSet sas = new SimpleAttributeSet();
                    StyleConstants.setForeground(sas, Color.BLUE);
                    StyleConstants.setBold(sas, true);
                    StyleConstants.setFontSize(sas, 24);

                    try {
                        String key = searchResults.getSelectedValue();
                        if (key == null)
                            return;

                        if (mode == "key") {
                            displayDefinition(doc, sas, key, false);

                            // enable edit and delete buttons
                            editButton.setEnabled(true);
                            deleteButton.setEnabled(true);
                        } else if (mode == "value") {

                        }
                    } catch (BadLocationException ble) {
                        ble.printStackTrace();
                    }
                }
            }
        });

        // create a button panel with a button to add new slangs and another to reset
        // the slang list
        JButton addButton = new JButton("Add slang");
        JButton resetButton = new JButton("Reset");

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show confirm dialog
                int input = JOptionPane.showConfirmDialog(null, "Do you want to restore the original slang list?",
                        "Confirm slang list reset", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (input == 0) {
                    // OK
                    // clear dictionary, key list and search result list
                    d.clear();
                    keyList.clear();
                    searchResults.setListData(keyList.toArray(new String[keyList.size()]));

                    // clear search field and definition pane
                    searchField.clear();
                    definition.setText("");

                    // import from the original slang list
                    d.importSlangList("data/slang.txt");
                    keyList = d.getKeyList();
                    searchResults.setListData(keyList.toArray(new String[keyList.size()]));

                    // show complete dialog
                    JOptionPane.showConfirmDialog(null,
                            "Original slang list restored successfully.", "Reset complete", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSlangDialog();
            }
        });

        JPanel searchOptionPane = new JPanel(new BorderLayout(5, 5));
        searchOptionPane.add(addButton, BorderLayout.EAST);
        searchOptionPane.add(resetButton, BorderLayout.WEST);

        // create a panel to contain the components above
        JPanel searchPane = new JPanel(new BorderLayout(10, 10));
        searchPane.setPreferredSize(new Dimension(250, 700));
        searchPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPane.add(searchFieldPane, BorderLayout.NORTH);
        searchPane.add(resultScrollPane, BorderLayout.CENTER);
        searchPane.add(searchOptionPane, BorderLayout.SOUTH);

        // create a main pane to contain the two panels
        JPanel mainPane = new JPanel(new BorderLayout());
        mainPane.add(searchPane, BorderLayout.WEST);
        mainPane.add(slangInfoPane, BorderLayout.CENTER);

        return mainPane;
    }

    // display slang definitions
    protected void displayDefinition(Document doc, SimpleAttributeSet sas, String key, boolean multiple)
            throws BadLocationException {
        doc.insertString(doc.getLength(), key + "\n", sas);
        doc.insertString(doc.getLength(), "_________________________\n", null);

        ArrayList<String> definitions = d.get(key);

        for (int i = 0; i < definitions.size(); i++) {
            doc.insertString(doc.getLength(), "\n" + (i + 1) + ". " + definitions.get(i) + "\n", null);
        }

        if (multiple) {
            doc.insertString(doc.getLength(), "\n\n\n=========================\n\n\n", null);
        }

        // log the selected slang to history list
        // if the entry was already in history, remove it and re-add the entry on top
        if (history.contains(key)) {
            history.remove(key);
        }

        history.push(key);
        historyEntries.setListData(history.toArray(new String[history.size()]));
    }

    // UI panel for the search history view
    protected JComponent historyPanel() {
        JButton clearButton = new JButton("Clear history");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show confirm dialog
                int input = JOptionPane.showConfirmDialog(null, "Do you want to clear search history?",
                        "Confirm clear history", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (input == 0) {
                    // OK
                    history.clear();
                    historyEntries.setListData(history.toArray(new String[history.size()]));

                    // show complete dialog
                    JOptionPane.showConfirmDialog(null,
                            "Search history cleared successfully.", "History cleared", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        JPanel rightPane = new JPanel(new BorderLayout());
        rightPane.add(clearButton, BorderLayout.SOUTH);

        // create a scroll pane for the history pane
        JScrollPane historyScrollPane = new JScrollPane(historyEntries);

        JPanel mainPane = new JPanel(new BorderLayout(10, 10));
        mainPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPane.add(historyScrollPane, BorderLayout.CENTER);
        mainPane.add(rightPane, BorderLayout.EAST);

        return mainPane;
    }

    public void addSlangDialog() {
        // set up the dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Add a new slang");

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JButton button = new JButton("OK");
        button.setEnabled(false);

        // create text fields
        PlaceholderField keyText = new PlaceholderField("Slang entry", false);
        keyText.setPreferredSize(new Dimension(200, 30));
        PlaceholderArea valueText = new PlaceholderArea("Separate multiple definitions by ;", 3, 30);
        valueText.setLineWrap(true);
        valueText.setWrapStyleWord(true);

        JScrollPane valueTextScrollPane = new JScrollPane(valueText);

        /**
         * Adapted from @bobasti on StackOverflow:
         * https://stackoverflow.com/a/35973147
         * 
         * Add a DocumentListener to JTextFields to enable the OK button if all fields
         * are filled.
         */
        DocumentListener fieldListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                boolean emptyKeyField = (keyText.getText().isEmpty()
                        || keyText.getText().equals(keyText.getPlaceholder())
                        || keyText.getText() == null);
                boolean emptyValueField = (valueText.getText().isEmpty()
                        || valueText.getText().equals(valueText.getPlaceholder())
                        || valueText.getText() == null);

                if (!emptyKeyField && !emptyValueField) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        };

        keyText.getDocument().addDocumentListener(fieldListener);
        valueText.getDocument().addDocumentListener(fieldListener);
        /* end of adapted snippet */

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // get the text input from text fields
                String key = keyText.getText().trim();
                String valueLine = valueText.getText().trim();
                String[] values = valueLine.split("\\;");

                dialog.setVisible(false);
                dialog.dispose();

                // slang already exists
                if (d.contains(key)) {
                    int input = JOptionPane.showConfirmDialog(null,
                            "This slang already exists. Do you want to update its definition?",
                            "Confirm slang update", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (input == 0) {
                        // OK
                        ArrayList<String> defList = d.get(key);
                        System.out.println(defList.size());

                        for (String value : values) {
                            defList.add(value.trim());
                        }

                        d.replace(key, defList);

                        JOptionPane.showConfirmDialog(null,
                                "Slang updated successfully.", "Update slang complete",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } else {
                    d.add(key.toUpperCase(), new ArrayList<String>(Arrays.asList(values)));
                    keyList = d.getKeyList();
                    searchResults.setListData(keyList.toArray(new String[keyList.size()]));
                }

                // show complete dialog
                JOptionPane.showConfirmDialog(null,
                        "Slang added successfully.", "Add slang complete", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        // create labels for the text fields
        JLabel keyLabel = new JLabel("Slang", JLabel.LEFT);
        JLabel valueLabel = new JLabel("Definition", JLabel.LEFT);

        JPanel keyPane = new JPanel(new BorderLayout(0, 5));
        keyPane.add(keyLabel, BorderLayout.NORTH);
        keyPane.add(keyText, BorderLayout.CENTER);

        JPanel valuePane = new JPanel(new BorderLayout(0, 5));
        valuePane.add(valueLabel, BorderLayout.NORTH);
        valuePane.add(valueTextScrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(keyPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(valuePane);

        // add the panel to the dialog window
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(button, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void editSlangDialog() {
        // set up the dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit slang");

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JButton button = new JButton("OK");

        // retrieve the slang and its definition
        String key = searchResults.getSelectedValue();
        ArrayList<String> values = d.get(key);
        String valueLine = "";

        for (int i = 0; i < values.size() - 1; i++) {
            valueLine += values.get(i) + ";";
        }

        valueLine += values.get(values.size() - 1);

        // create text field
        JTextArea valueText = new JTextArea(valueLine, 3, 30);
        valueText.setLineWrap(true);
        valueText.setWrapStyleWord(true);

        JScrollPane valueTextScrollPane = new JScrollPane(valueText);

        valueText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (valueText.getText().isEmpty() || valueText.getText() == null) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // get the text input from text field
                String valueLine = valueText.getText().trim();
                String[] values = valueLine.split("\\;");

                dialog.setVisible(false);
                dialog.dispose();

                ArrayList<String> defList = new ArrayList<>();
                for (String value : values) {
                    defList.add(value.trim());
                }

                d.replace(key, defList);
                searchResults.clearSelection();
                searchResults.setSelectedValue(key, true);

                // show complete dialog
                JOptionPane.showConfirmDialog(null,
                        "Slang edited successfully.", "Edit slang complete", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
            }
        });

        // create a label for the text field
        JLabel valueLabel = new JLabel("Edit definitions (separate defitions with ;)", JLabel.LEFT);

        JPanel valuePane = new JPanel(new BorderLayout(0, 5));
        valuePane.add(valueLabel, BorderLayout.NORTH);
        valuePane.add(valueTextScrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(valuePane);

        // add the panel to the dialog window
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(button, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // UI panel for the games view
    protected JComponent gamesPanel() {
        // create a JComboBox to switch between game modes
        String[] gameModes = { "Guess the slang", "Guess the definition" };
        JComboBox<String> gameModeSelector = new JComboBox<>(gameModes);
        gameModeSelector.setMaximumSize(new Dimension(225, 30));

        ActionListener cbListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                String gameMode = (String) cb.getSelectedItem();
                // TODO: handle game mode switching
            }
        };

        gameModeSelector.addActionListener(cbListener);

        JButton newButton = new JButton("New question");
        newButton.setMaximumSize(new Dimension(225, 30));

        // create a game control pane to contain the components above
        JPanel gameControlPane = new JPanel();
        gameControlPane.add(newButton);

        // create a JTextPane to display the game's question
        JTextPane question = new JTextPane();
        question.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        question.setEditable(false);

        // create a panel to contain the JTextPane and the game control pane
        JPanel topPane = new JPanel(new BorderLayout(10, 10));
        topPane.add(gameModeSelector, BorderLayout.NORTH);
        topPane.add(question, BorderLayout.CENTER);
        topPane.add(gameControlPane, BorderLayout.EAST);

        // create a button panel to contain buttons for answers
        JButton option1 = new JButton("Option 1");
        JButton option2 = new JButton("Option 2");
        JButton option3 = new JButton("Option 3");
        JButton option4 = new JButton("Option 4");

        GridLayout answersPaneLayout = new GridLayout(2, 2);
        answersPaneLayout.setHgap(10);
        answersPaneLayout.setVgap(10);

        JPanel answersPane = new JPanel(answersPaneLayout);
        answersPane.setPreferredSize(new Dimension(900, 150));
        answersPane.add(option1);
        answersPane.add(option2);
        answersPane.add(option3);
        answersPane.add(option4);

        // create a main panel to contain the two panels
        JPanel mainPane = new JPanel(new BorderLayout(10, 10));
        mainPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPane.add(topPane, BorderLayout.CENTER);
        mainPane.add(answersPane, BorderLayout.SOUTH);

        return mainPane;
    }

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    // create a JFrame to host components
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Slang Dictionary");
        frame.setSize(new Dimension(900, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // export the slang list and search history to text files upon closing the
        // window
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                d.exportSlangList("data/user_slang.txt");
                exportHistory("data/history.txt");
            }
        });

        frame.add(new DictionaryDisplay(), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // set system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    setUIFont(new FontUIResource("Segoe UI", Font.PLAIN, 13));
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                createAndShowGUI();
            }
        });
    }

    class PlaceholderField extends JTextField implements FocusListener, DocumentListener {
        private final String placeholder;
        private boolean showPlaceholder;

        public PlaceholderField(final String placeholder, boolean isSearchField) {
            super(placeholder);

            this.setForeground(Color.GRAY);
            this.placeholder = placeholder;
            this.showPlaceholder = true;

            super.addFocusListener(this);

            if (isSearchField) {
                super.getDocument().addDocumentListener(this);
            }
        }

        public String getPlaceholder() {
            return this.placeholder;
        }

        // override methods for FocusListener
        @Override
        public void focusGained(FocusEvent e) {
            if (this.getText().isEmpty()) {
                this.setForeground(Color.BLACK);
                super.setText("");
                showPlaceholder = false;
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (this.getText().isEmpty()) {
                this.setForeground(Color.GRAY);
                super.setText(placeholder);
                showPlaceholder = true;
            }
        }

        public void clear() {
            if (this.getText() != placeholder) {
                this.setForeground(Color.GRAY);
                super.setText(placeholder);
                showPlaceholder = true;
            }
        }

        // override getText method for JTextComponent
        @Override
        public String getText() {
            return showPlaceholder ? "" : super.getText();
        }

        // override methods for DocumentListener
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateSearchResults(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateSearchResults(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateSearchResults(e);
        }

        void updateSearchResults(DocumentEvent e) {
            String searchTerm = this.getText();

            if (searchTerm.isEmpty() || searchTerm == null || searchTerm.equals(placeholder)) {
                searchResults.setListData(keyList.toArray(new String[keyList.size()]));
                return;
            }

            ArrayList<String> results = null;

            if (mode.equals("key")) {
                results = d.searchSubstringByKey(searchTerm);
            } else if (mode.equals("value")) {
                results = d.searchSubstringByDefinition(searchTerm);
            }

            searchResults.setListData(results.toArray(new String[results.size()]));
        }
    }

    /**
     * Adapted from @Bart Kiers on StackOverflow:
     * https://stackoverflow.com/a/1739037
     * 
     * Create a JTextField with a placeholder. If the field is marked as a search
     * field, add a DocumentLister to it to update the search results JList when the
     * field's value changes.
     */
    class PlaceholderArea extends JTextArea implements FocusListener {
        private final String placeholder;
        private boolean showPlaceholder;

        public PlaceholderArea(final String placeholder, int rows, int cols) {
            super(placeholder, rows, cols);

            this.setForeground(Color.GRAY);
            this.placeholder = placeholder;
            this.showPlaceholder = true;

            super.addFocusListener(this);
        }

        public String getPlaceholder() {
            return this.placeholder;
        }

        // override methods for FocusListener
        @Override
        public void focusGained(FocusEvent e) {
            if (this.getText().isEmpty()) {
                this.setForeground(Color.BLACK);
                super.setText("");
                showPlaceholder = false;
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (this.getText().isEmpty()) {
                this.setForeground(Color.GRAY);
                super.setText(placeholder);
                showPlaceholder = true;
            }
        }

        // override getText method for JTextComponent
        @Override
        public String getText() {
            return showPlaceholder ? "" : super.getText();
        }
    }
    /* end of adapted snippet */
}