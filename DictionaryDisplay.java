
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

class TextFieldWithPlaceholder extends JTextField implements FocusListener {
    private final String placeholder;
    private boolean showPlaceholder;

    public TextFieldWithPlaceholder(final String placeholder) {
        super(placeholder);
        this.setForeground(Color.GRAY);
        this.placeholder = placeholder;
        this.showPlaceholder = true;
        super.addFocusListener(this);
    }

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

    @Override
    public String getText() {
        return showPlaceholder ? "" : super.getText();
    }
}

public class DictionaryDisplay extends JPanel {
    public DictionaryDisplay() {
        super(new GridLayout(1, 1));

        JTabbedPane tabs = new JTabbedPane();

        JComponent dict = dictPanel();
        tabs.addTab("Dictionary", null, dict,
                "View slang entries from dictionary");
        tabs.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent daily = makeTextPanel("Slang of the Day");
        tabs.addTab("SotD", null, daily,
                "View the Slang of the Day");
        tabs.setMnemonicAt(0, KeyEvent.VK_2);

        JComponent games = makeTextPanel("Minigames");
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
        JPanel mainPane = new JPanel(new BorderLayout());

        TextFieldWithPlaceholder searchField = new TextFieldWithPlaceholder("Enter a slang or definition...");
        searchField.setPreferredSize(new Dimension(225, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton searchButton = new JButton("🔎︎");
        searchButton.setMargin(new Insets(0, 0, 0, 0));
        searchButton.setPreferredSize(new Dimension(30, 30));

        JPanel searchFieldPane = new JPanel(new BorderLayout(5, 5));
        searchFieldPane.add(searchField, BorderLayout.CENTER);
        searchFieldPane.add(searchButton, BorderLayout.EAST);

        JList<String> results = new JList<>();
        results.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton addButton = new JButton("Add slang");
        JButton resetButton = new JButton("Reset");

        JPanel searchOptionPane = new JPanel(new BorderLayout(5, 5));
        searchOptionPane.add(addButton, BorderLayout.EAST);
        searchOptionPane.add(resetButton, BorderLayout.WEST);

        JPanel searchPane = new JPanel(new BorderLayout(10, 10));
        searchPane.setPreferredSize(new Dimension(250, 700));
        searchPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPane.add(searchFieldPane, BorderLayout.NORTH);
        searchPane.add(results, BorderLayout.CENTER);
        searchPane.add(searchOptionPane, BorderLayout.SOUTH);

        JTextPane definition = new JTextPane();
        definition.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        definition.setEditable(false);

        JButton editButton = new JButton("Edit slang");
        JButton deleteButton = new JButton("Delete slang");

        JPanel slangOptionPane = new JPanel();
        slangOptionPane.setLayout(new BoxLayout(slangOptionPane, BoxLayout.X_AXIS));
        slangOptionPane.setAlignmentX(Component.RIGHT_ALIGNMENT);
        slangOptionPane.setPreferredSize(new Dimension(630, 30));
        slangOptionPane.add(editButton);
        slangOptionPane.add(Box.createRigidArea(new Dimension(5, 0)));
        slangOptionPane.add(deleteButton);

        JPanel slangInfoPane = new JPanel(new BorderLayout(10, 10));
        slangInfoPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        slangInfoPane.add(slangOptionPane, BorderLayout.NORTH);
        slangInfoPane.add(definition, BorderLayout.CENTER);

        mainPane.add(searchPane, BorderLayout.WEST);
        mainPane.add(slangInfoPane, BorderLayout.CENTER);

        return mainPane;
    }

    protected JComponent historyPanel() {
        JPanel mainPane = new JPanel(new BorderLayout(10, 10));
        mainPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextPane entries = new JTextPane();
        entries.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        entries.setEditable(false);

        JButton clearButton = new JButton("Clear history");

        JPanel rightPane = new JPanel(new BorderLayout());
        rightPane.add(clearButton, BorderLayout.SOUTH);

        mainPane.add(entries, BorderLayout.CENTER);
        mainPane.add(rightPane, BorderLayout.EAST);

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
}