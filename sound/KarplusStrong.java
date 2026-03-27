import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class KarplusStrong {
    
    private static final int SAMPLE_RATE = 44100;
    
    public static void main(String[] args) {
        try {
            int N = 100;   // Période -> determine la note (SAMPLE_RATE/N)
            int M = 1000;  // Nombre de répétitions
            
            System.out.println("Génération des échantillons...");
            double[] samples = karplusStrong(N, M);
            
            System.out.println("Sauvegarde du fichier...");
            saveToWav(samples, "karplus_strong.wav");
            
            System.out.println("Terminé !");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 
    public static double[] karplusStrong(int N, int M) {
        int totalSize = N * M;
        double[] t = new double[totalSize];
        Random random = new Random();
        
        for (int i = 0; i < N; i++) {
            t[i] = random.nextDouble();
        }
        
        for (int j = 1; j < M; j++) {
            for (int i = 0; i < N; i++) {
                int current = j * N + i;
                int prev = (j - 1) * N + i;
                int prevPrev;
                
                if (i == 0) {
                    // Pour le premier élément, on prend le dernier élément de la ligne précédente
                    prevPrev = (j - 1) * N + (N - 1);
                } else {
                    prevPrev = (j - 1) * N + i - 1;
                }
                
                t[current] = (t[prev] + t[prevPrev]) / 2.;
            }
        }
        
        
        return t;
    }
    
    public static void saveToWav(double[] samples, String filename) throws Exception {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        byte[] audioData = new byte[samples.length * 2];
        
        for (int i = 0; i < samples.length; i++) {
            short value = (short) (samples[i] * Short.MAX_VALUE);
            audioData[i * 2] = (byte) (value & 0xFF);
            audioData[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }
        
        AudioInputStream audioStream = new AudioInputStream(
            new java.io.ByteArrayInputStream(audioData),
            format,
            samples.length
        );
        
        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File(filename));
        audioStream.close();
    }
}