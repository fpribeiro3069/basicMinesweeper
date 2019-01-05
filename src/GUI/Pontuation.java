package GUI;

import java.io.Serializable;

public class Pontuation implements Serializable {
    private static long PLAYER_ID = 1;
    private String nome;
    private float time;

    public static long getPLAYER_ID() {
        return PLAYER_ID;
    }

    public static void setPLAYER_ID(long player_id) {
        if(player_id > 0)
            PLAYER_ID = player_id;
        else
            System.out.println("*** setPLAYER_ID() - player_id <= 0 !!! Stting to 1");
    }

    public Pontuation(String nome, float time) {
        this.nome = nome;
        if (nome.length() > 50)
            this.nome = nome.substring(0, 50);
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
