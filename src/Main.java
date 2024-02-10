import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connect4Model model = new Connect4Model();
            Connect4View view = new Connect4View();
            Connect4Controller controller = new Connect4Controller(model, view);
        });
    }
}
