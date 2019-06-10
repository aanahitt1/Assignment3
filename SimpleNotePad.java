import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;


public class SimpleNotePad extends JFrame implements ActionListener{
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");
    JMenu openRecent = new JMenu("Open Recent");
    JTextPane doc = new JTextPane();
    List<JMenuItem> fileMenuItems = new ArrayList<>();
    List<JMenuItem> editMenuItems = new ArrayList<>();
    List<JMenuItem> openRecentItems = new ArrayList<>();

    OpenRecent openRecentMenu = new OpenRecent(5);

    public SimpleNotePad() {
        setTitle("A Simple Notepad Tool");
        //Initialized the MenuItems in two lists based on which menu they will be in.
        fileMenuItems.add(new JMenuItem("New File"));
        fileMenuItems.add(new JMenuItem("Save File"));
        fileMenuItems.add(new JMenuItem("Print File"));
        fileMenuItems.add(new JMenuItem("Open"));

        editMenuItems.add(new JMenuItem("Copy"));
        editMenuItems.add(new JMenuItem("Paste"));
        editMenuItems.add(new JMenuItem("Replace"));

        addToMenu(fileMenuItems, fileMenu);
        addToMenu(editMenuItems, editMenu);

        //We add the openRecent separately
        fileMenu.add(openRecent);
        try {
            String[] recents = openRecentMenu.getRecents();
            if(recents.length > 0) {
                for (String item : recents) {
                    openRecentItems.add(new JMenuItem(item));
                }
            }
            //I add the open recent items separately because they do not use the default action command
            for (JMenuItem item : openRecentItems) {
                openRecent.add(item);
                item.addActionListener(this);
                item.setActionCommand("Open Recent" + item.getText());
            }
        } catch (BackingStoreException e) {
        }

        //This sets up the basic frame and adds the menus to the panel.
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
        add(new JScrollPane(doc));
        setPreferredSize(new Dimension(600,600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

    //This method quickly adds every item in the list to the needed menu and gives it a default action command
    public void addToMenu(List<JMenuItem> list, JMenu menu) {
        for(JMenuItem item : list) {
            menu.add(item);
            item.addActionListener(this);
            item.setActionCommand(item.getText());
        }
    }

    public static void main(String[] args) {
        SimpleNotePad app = new SimpleNotePad();
    }

    //This method performs the save file action by opening up a window for the user to choose a save location and name
    public void save() {
        File fileToWrite = null;
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            fileToWrite = fileChooser.getSelectedFile();
            String path = fileToWrite.getPath();
        try {
            openRecentMenu.addRecent(path);
            PrintWriter out = new PrintWriter(new FileWriter(fileToWrite));
            out.println(doc.getText());
            JOptionPane.showMessageDialog(null, "File is saved successfully...");
            out.close();
        } catch (Exception ex) {
        }
    }

    //This method opens a java print document window to allow the user to print the text they added to the SimpleNotePad.
    public void print() {
        try{
            //Basic setup of the print screen.
            PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Sample Command Pattern");
            pjob.setCopies(1);
            pjob.setPrintable((pg, pageFormat, pageNum) -> {
                if (pageNum>0)
                    return Printable.NO_SUCH_PAGE;
                pg.drawString(doc.getText(), 500, 500);
                paint(pg);
                return Printable.PAGE_EXISTS;
            });

            if (pjob.printDialog() == false)
                return;
            pjob.print();
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(null,
                    "Printer error" + pe, "Printing error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    //This method pastes whatever was earlier copied and sets the cursor at the end of the newly pasted text.
    public void paste() {
        StyledDocument sdoc = doc.getStyledDocument();
        Position position = sdoc.getEndPosition();
        doc.paste();
    }

    //This method opens a window with a textbox for input to replace the highlighted portion of the text
    public void replace() {
        String replacement = JOptionPane.showInputDialog("Replace or insert with: ");
        doc.replaceSelection(replacement);
    }

    //This method opens a file chooser, then opens the chosen file into the current screen.
    public void open() {
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(getParent());
        if(choice == fileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            String path = selected.getPath();

            openPath(path);
        }
    }

    //Given a String with the file path, this method opens that file into the current screen.
    public void openPath(String path) {
        try {
            openRecentMenu.addRecent(path);
            FileInputStream iStream = new FileInputStream(path);
            byte[] array = new byte[iStream.available()];
            iStream.read(array);
            String content = new String(array);
            doc.setText(content);
        } catch(Exception e) {

        }
    }

    @Override
    //This method checks which MenuItem was clicked, and performs the appropriate action given the command.
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("New File")) {
            doc.setText("");
        }else if(e.getActionCommand().equals("Save File")) {
            save();
        }else if(e.getActionCommand().equals("Print File")) {
            print();
        }else if(e.getActionCommand().equals("Copy")) {
            doc.copy();
        }else if(e.getActionCommand().equals("Paste")) {
            paste();
        }else if(e.getActionCommand().equals("Replace")) {
            replace();
        }else if(e.getActionCommand().equals("Open")) {
            open();
        }else if(e.getActionCommand().contains("Open Recent")) {
            String path = e.getActionCommand().substring(11);
            openPath(path);
        }
    }
}