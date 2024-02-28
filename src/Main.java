import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class StudentDatabaseApp extends JFrame {
    private JTextField nameField, ageField, majorField;
    private JButton addButton, viewButton, updateButton, deleteButton;
    private JTextArea outputArea;
    private JComboBox<String> comboBox;
    private Connection connection;

    public StudentDatabaseApp() {
        setTitle("Student Database App");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Major:"));
        majorField = new JTextField();
        inputPanel.add(majorField);

        addButton = new JButton("Add");
        viewButton = new JButton("View");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        connectToDatabase();

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewStudents();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String URL = "jdbc:mysql://localhost:3306/university";
            String USER = "root";
            String PASSWORD = "кщще";
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
        }
    }

    private void addStudent() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String major = majorField.getText();

        try {
            String insertQuery = "INSERT INTO students (name, age, major) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, major);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                outputArea.append("Student added successfully.\n");
                nameField.setText("");
                ageField.setText("");
                majorField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add student.");
        }
    }

    private void viewStudents() {
        outputArea.setText("");
        try {

            String selectQuery = "SELECT * FROM students";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                outputArea.append("ID: " + resultSet.getInt("id") +
                        ", Name: " + resultSet.getString("name") +
                        ", Age: " + resultSet.getInt("age") +
                        ", Major: " + resultSet.getString("major") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve students.");
        }
    }

    private void updateStudent() {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String major = majorField.getText();

        try {
            String updateQuery = "UPDATE students SET age = ?, major = ? WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setInt(1, age);
            statement.setString(2, major);
            statement.setString(3, name);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                outputArea.append("Student information updated successfully.\n");
                nameField.setText("");
                ageField.setText("");
                majorField.setText("");
            } else {
                outputArea.append("No student found with the name: " + name + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update student information.");
        }
    }

    private void deleteStudent() {
        String name = nameField.getText();

        try {
            String deleteQuery = "DELETE FROM students WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, name);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                outputArea.append("Student deleted successfully.\n");
                nameField.setText("");
                ageField.setText("");
                majorField.setText("");
            } else {
                outputArea.append("No student found with the name: " + name + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete student.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentDatabaseApp();
            }
        });
    }
}
