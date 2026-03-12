import org.sample.mavensample.App;
int NB_CORDES = 6;
int NB_FRETTES = 6;
float MARGE = 40;

int indexAccord = 0;
int step = 2000;
long lastChange = 0;
int txtSize = 16;

int[][] infos;

int[][] mesAccords = {{0, -1, 2, 0, 1, 0},
                      {-1, 0, 2, 2, 2, 0},
                      {3 , 2, 0, 0, 0, 3},
                      {0 , 0, 2, 2, 2, 0}};

void setup() {
  size(300, 300);
  background(255);
  lastChange = millis();
  dessinerTabAccord(mesAccords[indexAccord], 50, 50);
  fill(0);
  textSize(txtSize);
  text("Accord " + (indexAccord + 1) + "/" + mesAccords.length, 120 , 280);
  // infos = App.getInfos();
}

void dessinerTabAccord(int[] cordes, float x, float y) {
  pushStyle();
  
  // Paramètres de dessin
  float cordesWidth = 5 * MARGE;
  float rayon = MARGE * 0.3;
  float line = 2;
  
  stroke(0);
  strokeWeight(line);
  
  // les cordes
  for (int i = 0; i < NB_CORDES; i++) {
    float cx = x + i * MARGE;
    line(cx, y, cx, y + cordesWidth);
  }
  
  // les frettes
  for (int f = 0; f < NB_FRETTES ; f++) {
    float fy = y + f * MARGE;
    line(x, fy, x + 5 * MARGE, fy);
  }
  
  // indications pour chaque corde
  for (int i = 0; i < NB_CORDES; i++) {
    float cx = x + i * MARGE;
    float offY = y - MARGE * 0.6;
    
    // corde non jouée (-1) : croix rouge en hauy
    if (cordes[i] == -1) {
      strokeWeight(2);
      stroke(255, 0, 0); 
     
      line(cx - rayon, offY - rayon, 
           cx + rayon, offY + rayon);
      line(cx - rayon, offY + rayon, 
           cx + rayon, offY - rayon);
      
      
    // Corde à vide (0) : rond vide en haut
    } else if (cordes[i] == 0) {
      strokeWeight(1.5);
      stroke(0);
      fill(255);
      ellipse(cx, offY, rayon * 2, rayon * 2);
      
      
    // corde jouée avec frette i (i) : rond plein dans la frette i
    } else {
      int frette = cordes[i];
      if (frette >= 1 && frette <= 5) {
        strokeWeight(1);
        stroke(0);
        fill(0);
        float fy = y + (frette - 0.5) * MARGE;
        ellipse(cx, fy, rayon * 2, rayon * 2);
      }
    }
  }
  
  // trait plus épais en haut
  strokeWeight(8);
  line(x - line, y, x + 5 * MARGE + line, y);
  
  popStyle();
}

void draw () {
  if (millis() - lastChange > step) {
    indexAccord = (indexAccord + 1) % mesAccords.length;
    lastChange = millis();
    background(255);
    dessinerTabAccord(mesAccords[indexAccord], 50, 50);
    fill(0);
    textSize(txtSize);
    text("Accord " + (indexAccord + 1) + "/" + mesAccords.length, 120 , 280);
  }
}
