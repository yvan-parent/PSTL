import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class KarplusStrong {
    
    private static final int SAMPLE_RATE = 44100;
    private static final double DAMPING = 0.499; // 0.5 = original, < 0.5 = plus d'atténuation
    
    public static double[] generateChord(int[] chordSpec, double durationSeconds) {
        int root = chordSpec[0];
        int[] intervals = new int[chordSpec.length - 1];
        for (int i = 1; i < chordSpec.length; i++) {
            intervals[i - 1] = chordSpec[i];
        }
        
        int totalSamples = (int) Math.round(durationSeconds * SAMPLE_RATE);
        double[] mix = new double[totalSamples];

        for (int interval : intervals) {
            int midi = root + interval;
            int N = midiToPeriod(midi);
            int M = (totalSamples + N - 1) / N;
            double[] noteSamples = karplusStrong(N, M);
            int length = Math.min(totalSamples, noteSamples.length);

            for (int i = 0; i < length; i++) {
                mix[i] += noteSamples[i];
            }
        }

        // Évite la saturation en moyenneant les notes et en normalisant si nécessaire
        double gain = 1.0 / intervals.length;
        double max = 0.0;

        for (int i = 0; i < totalSamples; i++) {
            mix[i] *= gain;
            max = Math.max(max, Math.abs(mix[i]));
        }

        if (max > 1.0) {
            for (int i = 0; i < totalSamples; i++) {
                mix[i] /= max;
            }
        }

        return mix;
    }

    public static int midiToPeriod(int midiNote) {
        double frequency = midiToFrequency(midiNote);
        int period = (int) Math.round(SAMPLE_RATE / frequency);
        return Math.max(period, 2);
    }

    public static double midiToFrequency(int midiNote) {
        return 440.0 * Math.pow(2.0, (midiNote - 69) / 12.0);
    }

    public static double[] karplusStrong(int N, int M) {
        int totalSize = N * M;
        double[] t = new double[totalSize];
        Random random = new Random();
        
        for (int i = 0; i < N; i++) {
            t[i] = random.nextDouble() ;
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
                
                t[current] = DAMPING * (t[prev] + t[prevPrev]);
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

    public static void main(String[] args) {
        try {
            int[] chordSpec = {64, 0, 3, 7}; // Racine + intervalles en demi-tons (E + unison, tierce majeure, quinte)
            double durationSeconds = 5.;

            System.out.println("Génération de l'accord...");
            double[] samples = generateChord(chordSpec, durationSeconds);

            System.out.println("Sauvegarde du fichier...");
            saveToWav(samples, "karplus_strong_chord.wav");

            System.out.println("Terminé !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}