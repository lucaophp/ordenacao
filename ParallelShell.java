import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ParallelShell {
	//DECLARAÇÃO OS ATRIBUTOS
    static int vet[];
    static int tam;
    static Semaphore sinal1;
    static Semaphore sinal2;
    static Semaphore sinal3;
    static long tempoInicial;
    static long tempoFinal;
	
    public ParallelShell(int tam) {
        vet = new int[tam];
        ParallelShell.tam = tam;
    }
	//metodo responsavel para marcar o inicio do experimento. 
    public static void inicia() {
        tempoInicial = System.currentTimeMillis();
    }
	//metodo responsavel para marcar o termino do experimento.
    public static void termina() {
        tempoFinal = System.currentTimeMillis();
    }
	//metodo responsavel por preencher o vetor randomicamente.
    public void randomico() {
        for (int i = 0; i < tam; i++) {
            vet[i] = (int) ((Math.random() * 10001) % 10001);

        }

    }
	//metodo responsavel por ordenar o vetor usando shellsort. 
    public static void ordenaShell(int inicio, int fim) {
        int i, j, value;
        int gap = inicio + 1;
        while (gap < fim) {
            gap = 3 * gap + 1;
        }
        while (gap > inicio + 1) {
            gap /= 3;
            for (i = gap; i < fim; i++) {
                value = vet[i];
                j = i - gap;
                while (j >= 0 && value < vet[j]) {
                    vet[j + gap] = vet[j];
                    j -= gap;
                }
                vet[j + gap] = value;
            }
        }

    }
   
	//Classe estatica Responsavel pelo Processo 1.
    static class P1 implements Runnable {

        public void run() {
            try {
				inicia();
                ordenaShell(0, (tam / 2) - 1);
                sinal1.release();
            } catch (Exception e) {
            }
        }

    }

    static class P2 implements Runnable {

        public void run() {

            try {
                ordenaShell((int) tam / 2, tam);
                sinal2.release();

            } catch (Exception e) {
            }

        }

    }

    static class P3 implements Runnable {

        public void run() {

            try {
				sinal1.acquire();
                sinal2.acquire();
                ordenaShell(0, tam);
                termina();
                sinal3.release();

            } catch (Exception e) {
            }

        }

    }

    public static void main(String[] args) {
        ParallelShell p = new ParallelShell(100000);

        
        double tempo[] = new double[10];
        double soma = 0;
        try {
            boolean status = true;
            int k = 0;
            BufferedWriter out = new BufferedWriter(new FileWriter("Alg3.txt"));
            out.write("--------------------------------\n");
            out.write("Algoritmo <3>\n");
            out.write("--------------------------------\n");

            while (status) {
                sinal1 = new Semaphore(1);
                sinal2 = new Semaphore(0);
                sinal3 = new Semaphore(0);
                //primeiro aloca-se uma instancia de tarefa 
                P1 tarefa1 = new P1();
				//e depois cria-se a nova thread, passando a tarefa como argumento

                Thread thread1 = new Thread(tarefa1);
                P2 tarefa2 = new P2();
				//e depois cria-se a nova thread, passando a tarefa como argumento

                Thread thread2 = new Thread(tarefa2);
                P3 tarefa3 = new P3();
                Thread thread3 = new Thread(tarefa3);
                p.randomico();
                
                
                thread1.start();
                thread2.start();
                thread3.start();
                try {
                    sinal3.acquire();
                } catch (Exception e) {
                }
                long time=(((tempoFinal - tempoInicial)));
				String x=String.format("%d",(time));
				Double h=Double.parseDouble(x)/1000;
				
                
				out.write((k) + "-" + h + " seg.\n");
				
				tempo[k] = h;
                soma += h;
                k++;
                
                
                if (k == 10) {
                    status = false;
                }
            }
            

            out.write("--------------------------------\n");
            double media = soma / 10;
            out.write("Media: " + String.format("%.3f",media) + " seg.\n");
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (tempo[i] - media) * (tempo[i] - media);
            }
            double desvio = Math.sqrt(soma / 5);
            
            out.write("Desvio padrao: " + String.format("%.3f",desvio) + " seg.\n");
            out.write("--------------------------------\n");

            //out.write("O tempo e "+(tempoFinal-tempoInicial));
            out.close();
        } catch (IOException e) {

        }

    }

}
