import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ImageComparisonApp extends JFrame {

    private JLabel imageLabel1, imageLabel2, resultLabel;
    private File file1, file2;

    public ImageComparisonApp() {
        setTitle("Deteksi Wajah");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel(new GridLayout(1, 2));
        imageLabel1 = new JLabel("Gambar 1", SwingConstants.CENTER);
        imageLabel2 = new JLabel("Gambar 2", SwingConstants.CENTER);
        imagePanel.add(imageLabel1);
        imagePanel.add(imageLabel2);

        JButton uploadButton1 = new JButton("Pilih Gambar 1");
        JButton uploadButton2 = new JButton("Pilih Gambar 2");
        JButton analyzeButton = new JButton("Analisis");

        resultLabel = new JLabel("Silakan pilih dua gambar.", SwingConstants.CENTER);

        uploadButton1.addActionListener(e -> chooseImage(1));
        uploadButton2.addActionListener(e -> chooseImage(2));
        analyzeButton.addActionListener(e -> runPythonScript());

        JPanel controlPanel = new JPanel();
        controlPanel.add(uploadButton1);
        controlPanel.add(uploadButton2);
        controlPanel.add(analyzeButton);

        add(imagePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.SOUTH);
    }

    private void chooseImage(int imgNumber) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (imgNumber == 1) {
                file1 = selectedFile;
                imageLabel1.setIcon(new ImageIcon(new ImageIcon(selectedFile.getAbsolutePath()).getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
            } else {
                file2 = selectedFile;
                imageLabel2.setIcon(new ImageIcon(new ImageIcon(selectedFile.getAbsolutePath()).getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
            }
        }
    }

    private void runPythonScript() {
        if (file1 == null || file2 == null) {
            resultLabel.setText("Kedua gambar harus dipilih.");
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("/home/anonim/.pyenv/versions/facerecog39/bin/python", "/home/anonim/NetBeansProjects/facerecognitionmasr/src/facerecognitionmasr/compare.py",
                    file1.getAbsolutePath(), file2.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine;
            String resultLine = null;

            while ((outputLine = reader.readLine()) != null) {
                System.out.println(outputLine); // untuk debug di console
                if (outputLine.contains("|")) {
                    resultLine = outputLine;
                    break;
                }
            }

            process.waitFor();

            if (resultLine != null) {
                String[] parts = resultLine.split("\\|");
                if (parts.length == 3) {
                    String status = parts[0];
                    String distance = parts[1];
                    String similarity = parts[2];
                    resultLabel.setText("Hasil: " + status + " | Kemiripan: " + similarity + "% | Distance: " + distance);
                } else {
                    resultLabel.setText("Format hasil tidak dikenali.");
                }
            } else {
                resultLabel.setText("Gagal membaca hasil analisis.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Terjadi kesalahan saat menjalankan script.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageComparisonApp().setVisible(true));
    }
}
