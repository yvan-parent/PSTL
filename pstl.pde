void setup() {
  size(400, 300);
  background(255);
}

void dessinerTabAccord(int[] cordes, float x, float y, float marge) {
  pushStyle();
  
  // Paramètres de dessin
  float cordesWidth = 5 * marge;
  float rayon = marge * 0.3;
  float line = 2;
  
  stroke(0);
  strokeWeight(line);
  
  // les cordes
  for (int i = 0; i < 6; i++) {
    float cx = x + i * marge;
    line(cx, y, cx, y + cordesWidth);
  }
  
  // les frettes
  for (int f = 0; f <= 5; f++) {
    float fy = y + f * marge;
    line(x, fy, x + 5 * marge, fy);
  }
  
  // indicatiosn pour chaque corde
  for (int i = 0; i < 6; i++) {
    float cx = x + i * marge;
    float offY = y - marge * 0.6;
    
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
        float fy = y + (frette - 0.5) * marge;
        ellipse(cx, fy, rayon * 2, rayon * 2);
      }
    }
  }
  
  // trait plus épais en haut
  strokeWeight(8);
  line(x - line, y, x + 5 * marge + line, y);
  
  popStyle();
}

void draw () {
  int[] monAccord = {0, 0, 2, 0, 1, 0};
  dessinerTabAccord(monAccord, 50, 50, 40);
}
