import org.sample.mavensample.App;

int NB_CORDES = 6;
int NB_FRETTES = 6;
float MARGE;
float lineRatio = 0.005;

int indexAccord = 0;
int step = 2000;
long lastChange = 0;

int txtSize;
float txtProp = 0.05;

float boutonLargeurRatio = 0.25;
float boutonHauteurRatio = 0.08;
float petitBoutonLargeurRatio = 0.12;
float boutonYRatio = 0.85;
float boutonTextSizeRatio = 0.035;
float boutonXRatio = 0.13;
float boutonSpacingRatio1 = 0.285;
float boutonSpacingRatio2 = 0.415;
float boutonSpacingRatio3 = 0.57;

float offsetX = 0.17;
float offsetY = 0.15;
float txtOffsetXAccord = 2.2 * offsetX;
float txtOffsetYAccord = 5.2 * offsetY;
float margeProp = 0.13;

boolean defilement_auto = true;

int[][] mesAccords;


void setup() {
  size(300, 350);
  
  int[][] partition_chords = {
     {57, 0, 3, 7, 10},
     {62, 0, 4, 7, 10},
     {67, 0, 4, 7},
     {57, 0, 3, 7, 10},
     {59, 0, 4, 7, 10},
     {64, 0, 3, 7},
  };
 
 Thread t = new Thread(null, new Runnable() {
    public void run() {
      try {
        mesAccords = App.getInfos(partition_chords); // ton appel
        println("Calcul terminé !");
      } catch (StackOverflowError e) {
        println("Stack overflow confirmé !");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }, "calcul-thread", 64 * 1024 * 1024); // 64 Mo de stack
  
  t.start();
  
  try {
    t.join(); // attend que le thread finisse avant de continuer
  } catch (InterruptedException e) {
    println("Thread interrompu !");
  }
  
  MARGE = min(width * margeProp, height * margeProp);
  txtSize = int(height * txtProp);
  
  background(255);
  lastChange = millis();
  dessinerTabAccord(mesAccords[indexAccord], width * offsetX, height * offsetY);
  fill(0);
  textSize(txtSize);
  text("Accord " + (indexAccord + 1) + "/" + mesAccords.length, 
       width * txtOffsetXAccord, height * txtOffsetYAccord);
  dessinerBoutons();
  
}

void draw() {
  if (defilement_auto) {
    if (millis() - lastChange > step) {
      changerAccordSuivant();
    }
  }
}

void dessinerTabAccord(int[] cordes, float x, float y) {
  pushStyle();
  
  float cordesWidth = (NB_CORDES - 1) * MARGE;
  float rayon = MARGE * 0.3;
  float line = max(1, height * lineRatio); 
  
  stroke(0);
  strokeWeight(line);
  
  // les cordes (lignes verticales)
  for (int i = 0; i < NB_CORDES; i++) {
    float cx = x + i * MARGE;
    line(cx, y, cx, y + cordesWidth);
  }
  
  // les frettes (lignes horizontales)
  for (int f = 0; f < NB_FRETTES; f++) {
    float fy = y + f * MARGE;
    line(x, fy, x + (NB_CORDES - 1) * MARGE, fy);
  }
  
  // indications pour chaque corde
  for (int i = 0; i < NB_CORDES; i++) {
    float cx = x + i * MARGE;
    float offY = y - MARGE * 0.6;
    
    // corde non jouée (-1) : croix rouge en haut
    if (cordes[i] == -1) {
      strokeWeight(line * 1.5);
      stroke(255, 0, 0); 
     
      line(cx - rayon, offY - rayon, 
           cx + rayon, offY + rayon);
      line(cx - rayon, offY + rayon, 
           cx + rayon, offY - rayon);
      
    // Corde à vide (0) : rond vide en haut
    } else if (cordes[i] == 0) {
      strokeWeight(line);
      stroke(0);
      fill(255);
      ellipse(cx, offY, rayon * 2, rayon * 2);
      
    // corde jouée avec frette i : rond plein dans la frette i
    } else {
      int frette = cordes[i];
      if (frette >= 1 && frette <= NB_FRETTES) {
        strokeWeight(line);
        stroke(0);
        fill(0);
        float fy = y + (frette - 0.5) * MARGE;
        ellipse(cx, fy, rayon * 2, rayon * 2);
      }
    }
  }
  
  // trait plus épais en haut (sillet)
  strokeWeight(line * 3);
  line(x - line, y, x + (NB_CORDES - 1) * MARGE + line, y);
  
  popStyle();
}

void dessinerBoutons() {
  pushStyle();
  
  float boutonLargeur = width * boutonLargeurRatio;
  float boutonHauteur = height * boutonHauteurRatio;
  float petitBoutonLargeur = width * petitBoutonLargeurRatio;
  float boutonY = height * boutonYRatio;
  float texteSize = height * boutonTextSizeRatio;
  float boutonX = width * boutonXRatio;
  float boutonSpacing1 = width * boutonSpacingRatio1;
  float boutonSpacing2 = width * boutonSpacingRatio2;
  float boutonSpacing3 = width * boutonSpacingRatio3;
  
  
  // Bouton mode auto/manuel
  fill(defilement_auto ? color(200, 255, 200) : color(255, 200, 200));
  stroke(0);
  rect(boutonX, boutonY, boutonLargeur, boutonHauteur);
  fill(0);
  textSize(texteSize);
  textAlign(CENTER, CENTER);
  text(defilement_auto ? "Mode Auto" : "Mode Manuel", 
       boutonX + boutonLargeur/2, 
       boutonY + boutonHauteur/2);
  
  // Boutons précédent et suivant
  if (!defilement_auto) {
    // Bouton précédent
    fill(200);
    rect(boutonX + boutonSpacing1, boutonY, petitBoutonLargeur, boutonHauteur);
    fill(0);
    text("◀", boutonX + boutonSpacing1 + petitBoutonLargeur/2, boutonY + boutonHauteur/2);
    
    // Bouton suivant
    fill(200);
    rect(boutonX + boutonSpacing2, boutonY, petitBoutonLargeur, boutonHauteur);
    fill(0);
    text("▶", boutonX + boutonSpacing2 + petitBoutonLargeur/2, boutonY + boutonHauteur/2);
  }
  
  // Affichage de l'index de l'accord
  fill(255);
  rect(boutonX + boutonSpacing3, boutonY, petitBoutonLargeur * 1.2, boutonHauteur);
  fill(0);
  text((indexAccord + 1) + "/" + mesAccords.length, 
       boutonX + boutonSpacing3 + petitBoutonLargeur * 0.6, 
       boutonY + boutonHauteur/2);
  
  textAlign(LEFT);
  popStyle();
}


void mousePressed() {
  float boutonLargeur = width * boutonLargeurRatio;
  float boutonHauteur = height * boutonHauteurRatio;
  float petitBoutonLargeur = width * petitBoutonLargeurRatio;
  float boutonY = height * boutonYRatio;
  float boutonX = width * boutonXRatio;
  float boutonSpacing1 = width * boutonSpacingRatio1;
  float boutonSpacing2 = width * boutonSpacingRatio2;
  
  // bouton mode auto/manuel
  if (mouseX > boutonX && mouseX < boutonX + boutonLargeur && 
      mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
    defilement_auto = !defilement_auto;
    lastChange = millis();
    redessinerTout();
  }
  
  //  boutons de navig ( mode manuel)
  if (!defilement_auto) {
    // Bouton précédent
    if (mouseX > boutonX + boutonSpacing1 && mouseX < boutonX + boutonSpacing1 + petitBoutonLargeur && 
        mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
      indexAccord = (indexAccord - 1 + mesAccords.length) % mesAccords.length;
      redessinerTout();
    }
    
    // Bouton suivant
    if (mouseX > boutonX + boutonSpacing2 && mouseX < boutonX + boutonSpacing2 + petitBoutonLargeur && 
        mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
      indexAccord = (indexAccord + 1) % mesAccords.length;
      redessinerTout();
    }
  }
}

void changerAccordSuivant() {
  indexAccord = (indexAccord + 1) % mesAccords.length;
  lastChange = millis();
  redessinerTout();
}

void redessinerTout() {
  background(255);
  dessinerTabAccord(mesAccords[indexAccord], width * offsetX, height * offsetY);
  fill(0);
  textSize(txtSize);
  text("Accord " + (indexAccord + 1) + "/" + mesAccords.length, 
       width * txtOffsetXAccord, height * txtOffsetYAccord);
  dessinerBoutons();
}
