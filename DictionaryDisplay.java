import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class DictionaryDisplay extends JPanel {
    private static Dictionary d;
    private JList<String> searchResults;
    private String[] keyList;

    public DictionaryDisplay() {
        super(new GridLayout(1, 1));

        d = new Dictionary();
        d.importFromFile("data/user_slang.txt");
        keyList = d.getKeyArray();

        JTabbedPane tabs = new JTabbedPane();

        JComponent dict = dictPanel();
        tabs.addTab("Dictionary", null, dict,
                "View slang entries from dictionary");
        tabs.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent daily = makeTextPanel("Slang of the Day");
        tabs.addTab("Daily Slang", null, daily,
                "View the Slang of the Day");
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

        // enable tab scrolling
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    protected JComponent dictPanel() {
        // create a JTextPane to display the selected slang's definition
        JTextPane definition = new JTextPane();
        definition.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        definition.setEditable(false);
        definition.setBorder(BorderFactory.createCompoundBorder(definition.getBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // create a button panel with a button to delete the selected slang and another
        // to edit the selected slang
        JButton editButton = new JButton("Edit slang");
        JButton deleteButton = new JButton("Delete slang");

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
        slangInfoPane.add(definition, BorderLayout.CENTER);

        // create a search field with a search button
        SearchField searchField = new SearchField("Enter search terms...", "key");
        searchField.setPreferredSize(new Dimension(225, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // create a JComboBox to switch between search modes
        String[] searchModes = { "Search by slang", "Search by slang definition" };
        JComboBox<String> searchModeSelector = new JComboBox<>(searchModes);

        ActionListener cbListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>) e.getSource();
                String searchMode = (String) cb.getSelectedItem();

                if (searchMode.equals("Search by slang")) {
                    searchField.setMode("key");
                } else if (searchMode.equals("Search by slang definition")) {
                    searchField.setMode("value");
                }
            }
        };

        searchModeSelector.addActionListener(cbListener);

        // create a pane to contain the search field and the mode selector
        JPanel searchFieldPane = new JPanel(new BorderLayout(5, 5));
        searchFieldPane.add(searchModeSelector, BorderLayout.NORTH);
        searchFieldPane.add(searchField, BorderLayout.CENTER);

        searchResults = new JList<>(keyList);
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

                        doc.insertString(0, key + "\n", sas);
                        doc.insertString(doc.getLength(), "_______________________\n\n\n", null);

                        ArrayList<String> definitions = d.get(key);

                        for (int i = 0; i < definitions.size(); i++) {
                            doc.insertString(doc.getLength(), "\n" + (i + 1) + ". " + definitions.get(i) + "\n", null);
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

    protected JComponent historyPanel() {
        JList<String> entries = new JList<>();
        entries.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton clearButton = new JButton("Clear history");

        JPanel rightPane = new JPanel(new BorderLayout());
        rightPane.add(clearButton, BorderLayout.SOUTH);

        JPanel mainPane = new JPanel(new BorderLayout(10, 10));
        mainPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPane.add(entries, BorderLayout.CENTER);
        mainPane.add(rightPane, BorderLayout.EAST);

        return mainPane;
    }

    protected JComponent gamesPanel() {
        // create a JComboBox to switch between game modes
        String[] gameModes = { "Guess the slang", "Guess the definition" };
        JComboBox<String> gameModeSelector = new JComboBox<>(gameModes);
        gameModeSelector.setMaximumSize(new Dimension(225, 30));

        ActionListener cbListener = new ActionListener() {
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

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Slang Dictionary");
        frame.setSize(new Dimension(900, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                d.exportToFile("data/user_slang.txt");
            }
        });

        frame.add(new DictionaryDisplay(), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    class SearchField extends JTextField implements FocusListener, DocumentListener {
        private final String placeholder;
        private boolean showPlaceholder;
        private String mode; // search mode

        public SearchField(final String placeholder, String mode) {
            super(placeholder);

            this.setForeground(Color.GRAY);
            this.placeholder = placeholder;
            this.showPlaceholder = true;
            this.mode = mode;

            super.addFocusListener(this);
            super.getDocument().addDocumentListener(this);
        }

        public void setMode(String mode) {
            this.mode = mode;
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

        // override methods for DocumentListener
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateResults(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateResults(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateResults(e);
        }

        void updateResults(DocumentEvent e) {
            String searchTerm = this.getText();

            if (searchTerm.isEmpty() || searchTerm == null || searchTerm.equals(placeholder)) {
                searchResults.setListData(keyList);
                return;
            }

            String[] results = null;

            if (mode.equals("key")) {
                results = d.searchSubstringByKey(searchTerm);
            } else if (mode.equals("value")) {
                results = d.searchSubstringByDefinition(searchTerm);
            }

            searchResults.setListData(results);
        }
    }
}