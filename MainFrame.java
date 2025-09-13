import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private String username;

    public MainFrame(String username) {
        this.username = username;
        setTitle("Movie Ticket Booking System");

        // Movie list
        JList<String> movieList = new JList<>(new String[]{"Avengers", "The Dark Knight", "Harry Potter"});
        movieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane movieScrollPane = new JScrollPane(movieList);

        // Book Ticket button
        JButton bookTicketButton = new JButton("Book Ticket");
        bookTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMovie = movieList.getSelectedValue();
                if (selectedMovie != null) {
                    BookingFrame bookingFrame = new BookingFrame(username, selectedMovie);
                    bookingFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Please select a movie.");
                }
            }
        });

        // Layout setup
        setLayout(new BorderLayout());
        add(movieScrollPane, BorderLayout.CENTER);
        add(bookTicketButton, BorderLayout.SOUTH);

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame("User1");
            mainFrame.setVisible(true);
        });
    }
}
class BookingFrame extends JFrame {
    private String username;
    private String movie;
    private static final int TOTAL_SEATS = 100;
    private static final Map<String, Integer> seatsRemaining;

    public BookingFrame(String username, String movie) {
        this.username = username;
        this.movie = movie;
        setTitle("Booking - " + movie);

        // Show times and prices data
        Map<String, String[]> showTimes = new HashMap<>();
        showTimes.put("Avengers", new String[]{"10:00 AM - Rs400", "1:00 PM - Rs400", "4:00 PM - Rs400"});
        showTimes.put("The Dark Knight", new String[]{"11:00 AM - Rs300", "2:00 PM - Rs300", "5:00 PM - Rs300"});
        showTimes.put("Harry Potter", new String[]{"9:00 AM - Rs350", "12:00 PM - Rs350", "3:00 PM - Rs350"});

        // Show times list
        JList<String> showList = new JList<>(showTimes.get(movie));
        showList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane showScrollPane = new JScrollPane(showList);

        // Seats remaining label
        JLabel seatsRemainingLabel = new JLabel("Seats remaining: ");

        // Book Show button
        JButton bookShowButton = new JButton("Book Show");
        bookShowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedShow = showList.getSelectedValue();
                if (selectedShow != null) {
                    String showKey = movie + " " + selectedShow.split(" ")[0] + " " + selectedShow.split(" ")[1];
                    int remainingSeats = seatsRemaining.getOrDefault(showKey, 0);
                    seatsRemainingLabel.setText("Seats remaining: " + remainingSeats);

                    if (remainingSeats > 0) {
                        BookMultipleTicketsFrame bookMultipleTicketsFrame = new BookMultipleTicketsFrame(username, movie, selectedShow, remainingSeats, seatsRemaining, showKey);
                        bookMultipleTicketsFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(BookingFrame.this, "No seats available for " + selectedShow + "!");
                    }
                } else {
                    JOptionPane.showMessageDialog(BookingFrame.this, "Please select a show time.");
                }
            }
        });

        // Layout setup
        setLayout(new BorderLayout());
        add(showScrollPane, BorderLayout.CENTER);
        add(seatsRemainingLabel, BorderLayout.NORTH);
        add(bookShowButton, BorderLayout.SOUTH);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    static {
        seatsRemaining = new HashMap<>();
        seatsRemaining.put("Avengers 10:00 AM", TOTAL_SEATS);
        seatsRemaining.put("Avengers 1:00 PM", TOTAL_SEATS);
        seatsRemaining.put("Avengers 4:00 PM", TOTAL_SEATS);
        seatsRemaining.put("The Dark Knight 11:00 AM", TOTAL_SEATS);
        seatsRemaining.put("The Dark Knight 2:00 PM", TOTAL_SEATS);
        seatsRemaining.put("The Dark Knight 5:00 PM", TOTAL_SEATS);
        seatsRemaining.put("Harry Potter 9:00 AM", TOTAL_SEATS);
        seatsRemaining.put("Harry Potter 12:00 PM", TOTAL_SEATS);
        seatsRemaining.put("Harry Potter 3:00 PM", TOTAL_SEATS);
    }
}
class BookMultipleTicketsFrame extends JFrame {
    private String username;
    private String movie;
    private String selectedShow;
    private int remainingSeats;
    private Map<String, Integer> seatsRemaining;
    private JLabel totalPriceLabel;
    private JLabel remainingSeatsLabel;
    private String showKey;
    private int bookedTickets = 0;

    public BookMultipleTicketsFrame(String username, String movie, String selectedShow, int remainingSeats, Map<String, Integer> seatsRemaining, String showKey) {
        this.username = username;
        this.movie = movie;
        this.selectedShow = selectedShow;
        this.remainingSeats = remainingSeats;
        this.seatsRemaining = seatsRemaining;
        this.showKey = showKey;

        setTitle("Book Multiple Tickets - " + movie + " - " + selectedShow);

        // Extract ticket price from show details
        String[] showDetails = selectedShow.split(" ");
        int ticketPrice = Integer.parseInt(showDetails[showDetails.length - 1].substring(2));

        // Number of tickets input
        JLabel numberOfTicketsLabel = new JLabel("Number of tickets:");
        JTextField numberOfTicketsField = new JTextField(5);

        // Total price label
        totalPriceLabel = new JLabel("Total price: Rs0");

        // Remaining seats label
        remainingSeatsLabel = new JLabel("Seats remaining: " + remainingSeats);

        // Book Tickets button
        JButton bookTicketsButton = new JButton("Book Tickets");
        bookTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (seatsRemaining) {
                    int numberOfTickets;
                    try {
                        numberOfTickets = Integer.parseInt(numberOfTicketsField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "Please enter a valid number of tickets.");
                        return;
                    }

                    if (numberOfTickets <= 0 || numberOfTickets > remainingSeats) {
                        JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "Invalid number of tickets. Please enter a number between 1 and " + remainingSeats + ".");
                        return;
                    }

                    // Update remaining seats
                    bookTickets(showKey, numberOfTickets);
                    int newRemainingSeats = seatsRemaining.get(showKey);
                    remainingSeatsLabel.setText("Seats remaining: " + newRemainingSeats);

                    // Update total price
                    int totalPrice = numberOfTickets * ticketPrice;
                    totalPriceLabel.setText("Total price: Rs" + totalPrice);
                    bookedTickets = numberOfTickets;

                    JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "Booking confirmed for " + numberOfTickets + " tickets!\nTotal price: Rs" + totalPrice + "\nSeats remaining: " + newRemainingSeats);
                }
            }
        });

        // My Ticket button
        JButton showMyTicket = new JButton("My Ticket");
        showMyTicket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookedTickets == 0) {
                    JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "No ticket is booked!");
                } else {
                    JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "******************************\n" +
                            "Name: " + username + "\n" + "Movie: " + movie + "\n" +
                            "Show Timing: " + selectedShow + "\n" + "Number of Seats: " + bookedTickets +
                            "\n******************************");
                }
            }
        });

        // Cancel Tickets button
        JButton cancelTicketsButton = new JButton("Cancel Tickets");
        cancelTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (seatsRemaining) {
                    String input = JOptionPane.showInputDialog(BookMultipleTicketsFrame.this, "Enter number of tickets to cancel:");
                    if (input == null || input.isEmpty()) {
                        return; // User canceled or entered nothing
                    }

                    int ticketsToCancel;
                    try {
                        ticketsToCancel = Integer.parseInt(input);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "Please enter a valid number.");
                        return;
                    }

                    if (ticketsToCancel <= 0 || ticketsToCancel > bookedTickets) {
                        JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, "Invalid number of tickets to cancel.");
                        return;
                    }

                    // Update remaining seats
                    cancelTickets(showKey, ticketsToCancel);
                    int newRemainingSeats = seatsRemaining.get(showKey);
                    remainingSeatsLabel.setText("Seats remaining: " + newRemainingSeats);
                    bookedTickets -= ticketsToCancel;

                    JOptionPane.showMessageDialog(BookMultipleTicketsFrame.this, ticketsToCancel + " tickets canceled.\nSeats remaining: " + newRemainingSeats);
                }
            }
        });

        // Layout setup
        setLayout(null);

        numberOfTicketsLabel.setBounds(20, 20, 150, 25);
        numberOfTicketsField.setBounds(180, 20, 100, 25);
        totalPriceLabel.setBounds(20, 60, 200, 25);
        remainingSeatsLabel.setBounds(20, 100, 200, 25);
        bookTicketsButton.setBounds(20, 140, 120, 30);
        cancelTicketsButton.setBounds(160, 140, 120, 30);
        showMyTicket.setBounds(80, 180, 120, 30);

        add(numberOfTicketsLabel);
        add(numberOfTicketsField);
        add(totalPriceLabel);
        add(remainingSeatsLabel);
        add(bookTicketsButton);
        add(cancelTicketsButton);
        add(showMyTicket);

        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private synchronized void bookTickets(String showKey, int numberOfTickets) {
        int remainingSeats = seatsRemaining.get(showKey);
        seatsRemaining.put(showKey, remainingSeats - numberOfTickets);
    }

    private synchronized void cancelTickets(String showKey, int numberOfTickets) {
        int remainingSeats = seatsRemaining.get(showKey);
        seatsRemaining.put(showKey, remainingSeats + numberOfTickets);
    }
}
