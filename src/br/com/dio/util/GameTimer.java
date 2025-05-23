package br.com.dio.util;

import javax.swing.Timer;
import java.awt.event.ActionListener;

/**
 * Classe responsável pelo cronômetro do jogo
 */
public class GameTimer {
    private Timer timer;
    private long startTime;
    private ActionListener updateListener;

    public GameTimer(ActionListener updateListener) {
        this.updateListener = updateListener;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, updateListener);
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    public String getFormattedTime() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;
        return String.format("Tempo: %02d:%02d", minutes, seconds);
    }

    public String getElapsedTime() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}