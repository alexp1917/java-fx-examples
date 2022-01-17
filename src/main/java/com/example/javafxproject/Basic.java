package com.example.javafxproject;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


/**
 * from https://docs.oracle.com/javase/tutorial/displayCode.html?code=
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/
 * components/ListDialogRunnerProject/src/components/ListDialogRunner.java
 */
public class Basic {
    JFrame frame;
    List<String> names = List.of("Arlo", "Cosmo", "Elmo", "Hugo",
            "Jethro", "Laszlo", "Milo", "Nemo",
            "Otto", "Ringo", "Rocco", "Rollo");
    private JTextField inputField;
    private JTextField positionField;
    private JList<MyEntity> modelsList;

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        Basic basic = new Basic();
        SwingUtilities.invokeLater(basic::createAndShowGUI);
    }

    public static Action makeListener(Runnable r) {
        return makeListener(e -> r.run());
    }

    public static Action makeListener(Consumer<ActionEvent> c) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.accept(e);
            }
        };
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Show a List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = createUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        // easy way to exit by pressing escape
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        frame.getRootPane().getInputMap().put(escape, "closeEscape");
        frame.getRootPane().getActionMap().put("closeEscape",
                makeListener(frame::dispose));
    }

    public JPanel createUI() {
        //Create the labels.
        JLabel intro = new JLabel("The list, as it stands:");
        final JLabel name = new JLabel("sample name");
        intro.setLabelFor(name);

        //Use a wacky font if it exists. If not, this falls
        //back to a font we know exists.
        name.setFont(getAFont());

        //Create the button.
        final JButton button = new JButton("Pick a new name...");
        button.addActionListener(e -> {
            String selectedName = ListDialog.showDialog(
                    frame,
                    button,
                    "Baby names ending in O:",
                    "Name Chooser",
                    names,
                    name.getText(),
                    "Cosmo  ");
            name.setText(selectedName);
        });

        //Create the panel we'll return and set up the layout.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        intro.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        name.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        //Add the labels to the content pane.
        panel.add(intro);

        // panel.add(Box.createVerticalStrut(5)); //extra space
        // panel.add(name);

        //Add a vertical spacer that also guarantees us a minimum width:
        // panel.add(Box.createRigidArea(new Dimension(150, 10)));
        // add button to launch picker
        // panel.add(button);

        {
            modelsList = new JList<>();

            modelsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            modelsList.setPrototypeCellValue(new MyEntity()); //get extra space
            modelsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            // list.setVisibleRowCount(minus one);
            modelsList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                }
            });

            panel.add(Box.createRigidArea(new Dimension(150, 10)));
            // panel.add(list);
            JScrollPane listScroller = new JScrollPane(modelsList);
            listScroller.setPreferredSize(new Dimension(500, 80));
            listScroller.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(listScroller);
        }

        inputField = new JTextField();
        inputField.setFont(new Font("Serif", Font.PLAIN, 12));
        inputField.setSize(100, 30);

        panel.add(Box.createRigidArea(new Dimension(150, 10)));
        panel.add(new JLabel("input name"));
        panel.add(inputField);

        positionField = new JTextField();
        positionField.setFont(new Font("Serif", Font.PLAIN, 12));
        positionField.setSize(100, 30);

        panel.add(Box.createRigidArea(new Dimension(150, 10)));
        panel.add(new JLabel("input position"));
        panel.add(positionField);


        for (String text : Arrays.asList("create", "update", "delete")) {
            JButton button1 = new JButton(text);
            button1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            button1.setSize(100, 100);
            button1.setLocation(0, 0);
            switch (text) {
                case "create":
                    button1.addMouseListener((MouseClickedListener) this::createClicked);
                    break;
                case "update":
                    throw new UnsupportedOperationException("not implemented");
                case "delete":
                    button1.addMouseListener((MouseClickedListener) this::deleteClicked);
                    break;
                default:
            }

            panel.add(Box.createRigidArea(new Dimension(150, 10)));
            panel.add(button1);
        }

        return panel;
    }

    private void createClicked(MouseEvent e) {
        List<MyEntity> modelsList = getModelsList();
        modelsList.add(getNewModel());
        ListModel<MyEntity> model = toModel(modelsList);
        this.modelsList.setModel(model);
    }

    private void deleteClicked(MouseEvent e) {
        MyEntity selectedValue = modelsList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(frame,
                    "hello, nothing there");
        } else {
            try {
                List<MyEntity> myEntities = getModelsList();
                myEntities.remove(modelsList.getSelectedIndex());
                modelsList.setModel(toModel(myEntities));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<MyEntity> getModelsList() {
        return this.toList(modelsList.getModel());
    }

    // convert listModel to list
    private <T> java.util.List<T> toList(ListModel<T> listModel) {
        int size = listModel.getSize();
        ArrayList<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            list.add(listModel.getElementAt(i));
        return list;
    }

    private <T> DefaultListModel<T> toModel(List<T> list) {
        DefaultListModel<T> dlm = new DefaultListModel<>();
        dlm.addAll(list);
        return dlm;
    }

    private String readTextField(JTextField field) {
        String text = field.getText();
        field.setText("");
        return text;
    }

    private MyEntity getNewModel() {
        return new MyEntity().setName(readTextField(inputField)).setPosition(readTextField(positionField));
    }

    /**
     * Finds a cursive font to use, or falls back to using
     * an italic serif font.
     */
    protected Font getAFont() {
        // more code here:
        // https://docs.oracle.com/javase/tutorial/uiswing
        // /examples/components/ListDialogRunnerProject
        // /src/components/ListDialogRunner.java
        return new Font("Serif", Font.ITALIC, 36);
    }

    @Accessors(chain = true)
    @Data
    public static class MyEntity {
        private String name;
        private String position;
    }

    /**
     * Interface which turns MouseListener into a functional interface
     */
    public interface MouseClickedListener extends MouseListener {
        @Override
        void mouseClicked(MouseEvent e);

        @Override
        default void mousePressed(MouseEvent e) {}

        @Override
        default void mouseReleased(MouseEvent e) {}

        @Override
        default void mouseEntered(MouseEvent e) {}

        @Override
        default void mouseExited(MouseEvent e) {}
    }

    /**
     * from
     * https://docs.oracle.com/javase/tutorial/displayCode.html?code=
     * https://docs.oracle.com/javase/tutorial/uiswing/examples/
     * components/ListDialogRunnerProject/src/components/ListDialog.java
     */
    public static class ListDialog<T> extends JDialog
            implements ActionListener {
        private static ListDialog<String> dialog;
        private static String value = "";
        private final JList<T> list;

        private ListDialog(Frame frame,
                           Component locationComp,
                           String labelText,
                           String title,
                           java.util.List<T> data,
                           T initialValue,
                           T longValue) {
            super(frame, title, true);

            //Create and initialize the buttons.
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            //
            final JButton setButton = new JButton("Set");
            setButton.setActionCommand("Set");
            setButton.addActionListener(this);
            getRootPane().setDefaultButton(setButton);

            //main part of the dialog
            list = new JList<>(getAbstractListModel(data)) {
                //Subclass JList to workaround bug 4832765, which can cause the
                //scroll pane to not let the user easily scroll up to the beginning
                //of the list.  An alternative would be to set the unitIncrement
                //of the JScrollBar to a fixed value. You wouldn't get the nice
                //aligned scrolling, but it should work.
                public int getScrollableUnitIncrement(Rectangle visibleRect,
                                                      int orientation,
                                                      int direction) {
                    int row;
                    if (orientation == SwingConstants.VERTICAL &&
                            direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                        Rectangle r = getCellBounds(row, row);
                        if ((r.y == visibleRect.y) && (row != 0)) {
                            Point loc = r.getLocation();
                            loc.y--;
                            int prevIndex = locationToIndex(loc);
                            Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                            if (prevR == null || prevR.y >= r.y) {
                                return 0;
                            }
                            return prevR.height;
                        }
                    }
                    return super.getScrollableUnitIncrement(
                            visibleRect, orientation, direction);
                }
            };

            list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            if (longValue != null) {
                list.setPrototypeCellValue(longValue); //get extra space
            }
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(-1);
            list.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setButton.doClick(); //emulate button click
                    }
                }
            });
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(250, 80));
            listScroller.setAlignmentX(LEFT_ALIGNMENT);

            //Create a container so that we can add a title around
            //the scroll pane.  Can't add a title directly to the
            //scroll pane because its background would be white.
            //Lay out the label and scroll pane from top to bottom.
            JPanel listPane = new JPanel();
            listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
            JLabel label = new JLabel(labelText);
            label.setLabelFor(list);
            listPane.add(label);
            listPane.add(Box.createRigidArea(new Dimension(0, 5)));
            listPane.add(listScroller);
            listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            //Lay out the buttons from left to right.
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(cancelButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(setButton);

            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add(listPane, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.PAGE_END);

            //Initialize values.
            setValue(String.valueOf(initialValue));
            pack();
            setLocationRelativeTo(locationComp);
        }

        static class Thing<T> extends AbstractListModel<T> {
            final List<T> data;

            Thing(List<T> data) {
                this.data = data;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public T getElementAt(int index) {
                return null;
            }
        }

        private AbstractListModel<T> getAbstractListModel(List<T> data) {
            return new Thing<>(data);
        }

        /**
         * Set up and show the dialog.  The first Component argument
         * determines which frame the dialog depends on; it should be
         * a component in the dialog's controlling frame. The second
         * Component argument should be null if you want the dialog
         * to come up with its left corner in the center of the screen;
         * otherwise, it should be the component on top of which the
         * dialog should appear.
         */
        public static String showDialog(Component frameComp,
                                        Component locationComp,
                                        String labelText,
                                        String title,
                                        java.util.List<String> possibleValues,
                                        String initialValue,
                                        String longValue) {
            Frame frame = JOptionPane.getFrameForComponent(frameComp);
            dialog = new ListDialog<>(frame,
                    locationComp,
                    labelText,
                    title,
                    possibleValues,
                    initialValue,
                    longValue);
            dialog.setVisible(true);
            return value;
        }

        private void setValue(String newValue) {
            value = newValue;
            list.setSelectedValue(value, true);
        }

        //Handle clicks on the Set and Cancel buttons.
        public void actionPerformed(ActionEvent e) {
            if ("Set".equals(e.getActionCommand())) {
                ListDialog.value = String.valueOf((list.getSelectedValue()));
            }
            ListDialog.dialog.setVisible(false);
        }
    }
}