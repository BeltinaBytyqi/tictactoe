import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Loja Tic-Tac-Toe */

public class TicTacToe extends JFrame  {
   // Konstante t� em�rtuara p�r tabel�n e loj�s 3x3
   public static final int RRESHTA = 3;  
   public static final int KOLONA = 3;
 
   // Konstante t� em�rtuara t� dimensioneve t� ndryshme t� p�rdorura p�r vizatimin grafik
   public static final int MADHESIA_KATRORVE = 130;  // gjatesia dhe gjeresia katrorve
   public static final int CANVAS_GJERESIA = MADHESIA_KATRORVE * KOLONA;  // gjeresia e zones drejtkendore(canvas)
   public static final int CANVAS_GJATESIA = MADHESIA_KATRORVE * RRESHTA;  //gjatesia e zones drejtkendore(canvas) 
   public static final int GRID_GJERESIA = 8;  // Gjer�sia e vijes s� rrjetit(Grid)
   public static final int GRID_GJERESIA_HALF = GRID_GJERESIA / 2;  // Gjysm�-gjer�sia e vijes s� rrjetit(Grid)
   // Simbolet X / O shfaqen brenda nj� katrori, me mbushje(padding) nga tabela
   public static final int PADDING_KATRORVE = MADHESIA_KATRORVE / 6;
   public static final int MADHESIA_SIMBOLIT = MADHESIA_KATRORVE - PADDING_KATRORVE * 2;  // madhesia e simboleve X/O
   public static final int GJERESIA_VIJES_SIMBOLIT = 10;  // gjeresia vijes se lapsit
 
   // P�rdorimi i nj� enum (klas� t� brendshme) p�r t� p�rfaq�suar gjendjet e ndryshme t� loj�s
   public enum GameState {           // gjendja e lojes
      PLAYING, DRAW, X_WON, O_WON 
   }
   private GameState currentState;  //gjendja aktuale e loj�s
 
   // P�rdorimi i nje enum (klas� t� brendshme) p�r t� p�rfaq�suar Seed(vleren fillestare) dhe p�rmbajtjen e katrorve
   public enum Seed {
      EMPTY, X, O        
   }
   private Seed currentPlayer;  // lojtari aktual
 
   private Seed[][] tabela   ; // tabela lojes
   private DrawCanvas canvas;  // Vizatimi i zones drejtkendore (JPanel) p�r tabel�n e loj�s
   private JLabel statusBar;   // shfaqja e nje vargu te shkurt ose nje ikone te imazhit dhe zona ne te cilen shfaqet
 
   /**  Konstruktori p�r t� konfiguruar loj�n dhe komponentet GUI (Graphical User Interface) */
   public TicTacToe()  {
      canvas = new DrawCanvas();  // Nd�rtimi i nj� zbraz�tie vizatimi (nj� JPanel)
      canvas.setPreferredSize(new Dimension(CANVAS_GJERESIA, CANVAS_GJATESIA)); // shfaqja e madhesise se deshiruar te tabeles ne ekran
   
      // Zona drejtkendore (JPanel) hap nj� MouseEvent me klikim t� miut
      canvas.addMouseListener(
         new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {  // thirret menj�her� pasi p�rdoruesi t� klikoj� n� komponentin e d�gjuar
               int mouseX = e.getX();
               int mouseY = e.getY();  // pozicioni (x,y) i miut
             // klikimi n� rreshta dhe kolona       
               int rreshtaSelected = mouseY / MADHESIA_KATRORVE; // X permbane koordinaten horizontale
               int kolonaSelected = mouseX / MADHESIA_KATRORVE;  // Y permbane koordianten vertikale
            
               if (currentState == GameState.PLAYING) {
                  if (rreshtaSelected >= 0 && rreshtaSelected < RRESHTA && kolonaSelected >= 0
                     && kolonaSelected < KOLONA && tabela[rreshtaSelected][kolonaSelected] == Seed.EMPTY) {
                     tabela[rreshtaSelected][kolonaSelected] = currentPlayer; // Bene nje levizje lojtari aktual         
                     updateGame(currentPlayer, rreshtaSelected, kolonaSelected); // Freskohet gjendja
                  // Nderrohet lojtari 
                     currentPlayer = (currentPlayer == Seed.X) ? Seed.O : Seed.X;
                  }
               } else {       // Loja mbaroi
                  initGame(); // Ristartoni lojen
               }
            // Rifreskoni zonen drejtkendore te vizatimit
               repaint();  // thirrja e metod�s repaint kur k�rkohet.
            }
         });
   
      // Konfigurimi i shiritit te statusit (JLabel) p�r t� shfaqur mesazhin e statusit
      statusBar = new JLabel("  ");
      statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));  // madhesia dhe modeli i fontit te mesazhit ne shirit
      statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));  // pozicioni i mesazhit ne shirit
   
      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(canvas, BorderLayout.CENTER); // vendosja e tabeles se lojes ne qender
      cp.add(statusBar, BorderLayout.PAGE_END); //vendosja e statusBar ne fund te faqes
   
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();  // paketoni t� gjith� komponentet n� k�t� JFrame
      setTitle("TIC TAC TOE");  // titulli i lojes
      setVisible(true);  // paraqitni kete JFrame
   
      tabela = new Seed[RRESHTA][KOLONA];
      initGame(); // inicializimi i p�rmbajtjes se tabeles s� loj�s dhe ndryshoret e loj�s
   }
 
   /** Inicializimi i p�rmbajtjes se tabeles s� loj�s dhe statusit */
   public void initGame() {
      for (int rreshta = 0; rreshta < RRESHTA; ++rreshta) {
         for (int kolona = 0; kolona < KOLONA; ++kolona) {
            tabela[rreshta][kolona] = Seed.EMPTY; // te gjithe katoret e zbrazte
         }
      }
      currentState = GameState.PLAYING; // gati per te luajtur
      currentPlayer = Seed.X;       // X luan i pari
   }
 
  /** P�rdit�simi i currentState pasi t� jet� vendosur lojtari me "theSeed"
        (rreshtaSelected, kolonaSelected). */
   public void updateGame(Seed theSeed, int rreshtaSelected, int kolonaSelected) {
      if (hasWon(theSeed, rreshtaSelected, kolonaSelected)) {  // shiko fituesin
         currentState = (theSeed == Seed.X) ? GameState.X_WON : GameState.O_WON;
      } else if (isDraw()) {  // shiko per barazim
         currentState = GameState.DRAW;
      }
   // P�rndryshe, asnj� ndryshim n� gjendjen aktuale (ende GameState.PLAYING).
   }
 
   /** Kthe true nese eshte barazim (dmth nuk ka me katror bosh) */
   public boolean isDraw() {
      for (int rreshta = 0; rreshta < RRESHTA; ++rreshta) {
         for (int kolona = 0; kolona < KOLONA; ++kolona) {
            if (tabela[rreshta][kolona] == Seed.EMPTY) {
               return false; // nj� kator bosh �sht� gjetur,jo barazim, dalje
            }
         }
      }
      return true;  // jo me katror bosh, eshte barazim
   }
 
   /** Kthe true nese lojtari me "theSeed" ka fituar pas vendosjes ne
       (rreshtaSelected, kolonaSelected) */
   public boolean hasWon(Seed theSeed, int rreshtaSelected, int kolonaSelected) {
      return (tabela[rreshtaSelected][0] == theSeed  // 3 ne rreshte
            && tabela[rreshtaSelected][1] == theSeed
            && tabela[rreshtaSelected][2] == theSeed
         || tabela[0][kolonaSelected] == theSeed      // 3 ne kolone
            && tabela[1][kolonaSelected] == theSeed
            && tabela[2][kolonaSelected] == theSeed
         || rreshtaSelected == kolonaSelected            // 3 ne diagonale
            && tabela[0][0] == theSeed
            && tabela[1][1] == theSeed
            && tabela[2][2] == theSeed
         || rreshtaSelected + kolonaSelected == 2     // 3 ne diagonalen e kundert
            && tabela[0][2] == theSeed
            && tabela[1][1] == theSeed
            && tabela[2][0] == theSeed);
   }
 
   /**
    *  Klasa e brendshme DrawCanvas (extends JPanel) e p�rdorur p�r vizatim grafik.
    */
   class DrawCanvas extends JPanel {
      public void paintComponent(Graphics g) {  // therrasim permes repaint()
         super.paintComponent(g);    // mbush sfondin(background)
         setBackground(Color.BLACK); // vendosja e ngjyr�s se sfondit t� saj
      
         // Vizatimi i linjave te rrjet�s(grid-lines)         
         g.setColor(Color.LIGHT_GRAY);
         for (int rreshta = 1; rreshta < RRESHTA; ++rreshta) {
            g.fillRoundRect(0, MADHESIA_KATRORVE * rreshta - GRID_GJERESIA_HALF,
                  CANVAS_GJERESIA-1, GRID_GJERESIA, GRID_GJERESIA, GRID_GJERESIA);
         }
         for (int kolona = 1; kolona < KOLONA; ++kolona) {
            g.fillRoundRect(MADHESIA_KATRORVE * kolona - GRID_GJERESIA_HALF, 0,
                  GRID_GJERESIA, CANVAS_GJATESIA-1, GRID_GJERESIA, GRID_GJERESIA);
         }
      
        // Vizatoni Seeds e t� gjithe katrorve n�se nuk jan� bosh
        // P�rdorni Graphics2D e cila na lejon t� vendosim vijen e lapsit          
         Graphics2D g2d = (Graphics2D)g;
         g2d.setStroke(new BasicStroke(GJERESIA_VIJES_SIMBOLIT, BasicStroke.CAP_ROUND,
               BasicStroke.JOIN_ROUND));  // vetem Graphics2D 
         for (int rreshta = 0; rreshta < RRESHTA; ++rreshta) {
            for (int kolona = 0; kolona < KOLONA; ++kolona) {
               int x1 = kolona * MADHESIA_KATRORVE + PADDING_KATRORVE;
               int y1 = rreshta * MADHESIA_KATRORVE + PADDING_KATRORVE;
               if (tabela[rreshta][kolona] == Seed.X) {
                  g2d.setColor(Color.YELLOW);
                  int x2 = (kolona + 1) * MADHESIA_KATRORVE - PADDING_KATRORVE;
                  int y2 = (rreshta + 1) * MADHESIA_KATRORVE - PADDING_KATRORVE;
                  g2d.drawLine(x1, y1, x2, y2);
                  g2d.drawLine(x2, y1, x1, y2);
               } else if (tabela[rreshta][kolona] == Seed.O) {
                  g2d.setColor(Color.GREEN);
                  g2d.drawOval(x1, y1, MADHESIA_SIMBOLIT, MADHESIA_SIMBOLIT);
               }
            }
         }
      
         // Printo mesazhin status-bar
         if (currentState == GameState.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            if (currentPlayer == Seed.X) {
               statusBar.setText("Radha e X");
            } else {
               statusBar.setText("Radha e O");
            }
         } else if (currentState == GameState.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Eshte BARAZIM! Kliko per te luajtur perseri.");
         } else if (currentState == GameState.X_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Fitoi 'X' ! Kliko per te luajtur perseri.");
         } else if (currentState == GameState.O_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Fitoi 'O' ! Kliko per te luajtur perseri.");         
         }
      }
   }
 
   /** Metoda hyrese main() */
   public static void main(String[] args) {
      // Ekzekutimi i kodeve GUI n� thread Event-Dispatching p�r sigurin� e thread
      SwingUtilities.invokeLater(
         new Runnable() {
            public void run() {
               new TicTacToe(); // Leme konstruktorin ta beje punen
            }
         });
   }
}