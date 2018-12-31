package GUI;

import java.io.Serializable;

public class Pontuacao implements Serializable {
    private static long PLAYER_ID = 1;
    private String nome;
    private float time;

    public Pontuacao(String nome, float time) {
        this.nome = nome;
        if (nome.length() > 15)
            this.nome = nome.substring(0, 15);
        if(nome.isEmpty())
            this.nome = "Player " + PLAYER_ID++;
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    @Override
    public String toString() {
        return nome + " - " + time + "seconds";
    }
}
