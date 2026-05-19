import org.sample.mavensample.App;
import processing.sound.SoundFile;
import processing.sound.*;
import controlP5.*;

ControlP5 c;
String erreur;

// Page pour entrer les parametres

// params par defaut pour guitare
ClasseGuitare guitare = new ClasseGuitare();

// params par defaut pour joueur
ClasseGuitariste player = new ClasseGuitariste();

// interactions
boolean pageParamsPending = false;
boolean pageParam = false;


// Page pour entrer les accords
Textfield inputField;
Textfield timelimitField;
Button addButton;
float yStart = 100;
float lineHeight = 35; 
float deleteW = 70;
ArrayList<int[]> tempAccords = new ArrayList<int[]>();
ArrayList<Button> deleteButtons = new ArrayList<Button>();
PImage imgGear;

// Transition entre page d'accords et page principale
boolean editMode = false;
boolean pendingPagePrincipal = false;
int timelimit = -1;


// Affichage des tablatures sur la page principale
boolean calculTermine = false;
boolean sonTermine = false;
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
float boutonXRatio = 0.10;
float boutonSpacingRatio1 = 0.285;
float boutonSpacingRatio2 = 0.415;
float boutonSpacingRatio3 = 0.57;
float boutonSpacingRatio4 = 0.7;

float offsetX = 0.17;
float offsetY = 0.15;
float txtOffsetXAccord = 2.2 * offsetX;
float txtOffsetYAccord = 5.2 * offsetY;
float margeProp = 0.13;

boolean defilement_auto = true;

int[][] mesAccords;
float sample_duration = 5.0;
double[][] samples;
SoundFile[] sons;

boolean buttonOff = false;


void setup() {
  size(300, 350);
  c = new ControlP5(this);
  imgGear = loadImage("gear.png");
  while (imgGear == null) {
    delay(1000);
  }
  imgGear.resize(height/10, height/10); 
  println ("image chargée");
  
  guitare.nf = 13; // nb de frettes
  guitare.nc = 6; // nb de cordes
  guitare.scordatura = new int[]{52, 57, 62, 67, 71, 76};
  
  player.distanceFirstFrette = 4;
  player.nbDoigts = 4;
  player.barre = 3;
   
  player.preferences = new ClassePreferences();
  player.preferences.fretteMoyenneFix = false;
  player.preferences.fretteMoyenne = 0;
  player.preferences.fretteMoyennePoids = 100;
   
  player.preferences.fondaBassFix = false;
  player.preferences.fondaBassPoids = 0;
   
  player.preferences.cordesNonJoueesFix = true;
  player.preferences.cordesNonJoueesMax = 1;
   
  player.preferences.cordesAVidesFix = false;
  player.preferences.cordesAVidesPoids = 0;
   
  player.preferences.distanceMaxSouhaitee = 3;
  player.preferences.distanceMaxSouhaiteePoids = 5;
   
  player.preferences.doigtsUtilisesFix = true;
  player.preferences.doigtsUtilisesMaxNb = 3;
   
  player.preferences.norme1Fix = true;
  player.preferences.norme1Poids = 5;
   
  player.preferences.pasRepetitionFix = false;
  player.preferences.pasRepetitionMax = 7;

  
  /* int[][] partition_chords = {
    {57, 0, 3, 7, 10},
    {62, 0, 4, 7, 10},                    
    {67, 0, 4, 7},
    {57, 0, 3, 7, 10},
    {64, 0, 3, 7}
  }; 
  samples = new double[partition_chords.length][]; */
 
  page_principal();
 
  
}

void draw() {
  if (pendingPagePrincipal) {
    pendingPagePrincipal = false;
    background(255);
    if (c.getController("setAccords") != null)  c.remove("setAccords");
    if (c.getController("validerAccord") != null)  c.remove("validerAccord");
    if (c.getController("ajoutChords") != null)  c.getController("ajoutChords").remove();
    if (c.getController("timelimitInput") != null)  c.getController("timelimitInput").remove();
    if (c.getController("accordInput") != null)  c.getController("accordInput").remove();
    for (Button b : deleteButtons) c.remove(b.getName());
    deleteButtons.clear();
    page_principal();
    return;
  }
  if (defilement_auto && mesAccords != null && !editMode && erreur == null) {
    if (millis() - lastChange > step) {
      changerAccordSuivant();
    }
  }
  if (editMode) {
  if (c.getController("nbFrettes") != null) c.remove("nbFrettes");
  if (c.getController("nbCordes") != null) c.remove("nbCordes");
  if (c.getController("scordatura") != null) c.remove("scordatura");
  if (c.getController("firstFretDistance") != null) c.remove("firstFretDistance");
  if (c.getController("nbDoigts") != null) c.remove("nbDoigts");
  if (c.getController("nbBarre") != null) c.remove("nbBarre");
  if (c.getController("paramOK") != null) c.remove("paramOK");
   background(255);
   fill(0);
   textSize(txtProp*height*0.8);
   text("Saisir un accord (ex: 57, 0, 3, 7, 10) :", 2*width/30, height/15); 
   text("Accords : ", 2*width/30, height/4);
   int y = int(yStart);
   for (int i = 0; i < tempAccords.size(); i++) {
     int[] accord = tempAccords.get(i);
     String str = join(nf(accord,0), ",");
     text(str, 2*width/30, y + lineHeight/2);
     y += lineHeight;
   }
   text("Limite de temps (optionnel):", width/30, 35*height/40);
   text("ms", 2.5*width/8, height);
   
   fill(250, 233, 135);
    stroke(0);
    rect(3*width/4, 9*height/10, height/10, height/10);
    image(imgGear, 3*width/4, 9*height/10);
  }
  if (pageParamsPending) {
    pageParamsPending = false;
    background(255);
    if (c.getController("setAccords") != null)  c.remove("setAccords");
    if (c.getController("validerAccord") != null)  c.remove("validerAccord");
    if (c.getController("ajoutChords") != null)  c.getController("ajoutChords").remove();
    if (c.getController("timelimitInput") != null)  c.getController("timelimitInput").remove();
    if (c.getController("accordInput") != null)  c.getController("accordInput").remove();
    for (Button b : deleteButtons) c.remove(b.getName());
    deleteButtons.clear();
    loadParamsPage();
    pageParam = true;
    return;
  }
  if (pageParam) {
    background(255);
    fill(0);
    text("Nombre de frettes", width/10, height/10);
    text("Nombre de cordes", width/10, height/5);
    text("Scordatura ", width/10, 3*height/10);
    text("Distance à la première frette", width/10, 2*height/5);
    text("Nombre de doigts utilisés", width/10, height/2);
    text("Barre", width/10, 3*height/5);
    return;
  }


}
 
void page_principal() {
  erreur = null;
  c.addButton("ajoutChords")
  .setPosition(9 * width / 10, 0)
  .setSize(width / 10, height / 15)
  .setLabel("...");
  if (tempAccords.isEmpty()) {
    background(255);
    fill(0);
     textSize(height*txtProp);
     text("Aucun accord saisi", width/3, height/2 ); 
     return;
  } else {
  mesAccords = tempAccords.toArray(new int[0][]);
  int [][] partition_chords = mesAccords;
  if (partition_chords == null || partition_chords.length == 0) {
    background(255);
     fill(0);
     textSize(height*txtProp);
     text("Aucun accord saisi", width/3, height/2 ); 
     return;
  } else {
    Thread t1 = new Thread(null, new Runnable() {
      public void run() {
        try {
        if (timelimit != -1) { mesAccords = App.getInfosWithTimeLimit(guitare, player, partition_chords, false, timelimit);} 
        else {mesAccords = App.getInfos(guitare, player, partition_chords, false);}
        println("Calcul terminé !");
        } catch (RuntimeException e) {
          erreur = e.getMessage();
          mesAccords = null;
        }
      }
    }, "calcul-thread", 64 * 1024 * 1024); // 64 Mo de stack
    samples = new double[partition_chords.length][];
    Thread t2 = new Thread(null, new Runnable() {
      public void run() {
        for (int i = 0; i < partition_chords.length; i++) {
          String filePath = sketchPath("assets/karplus_strong_chord" + i + ".wav");
          samples[i] = KarplusStrong.generateChord(partition_chords[i], sample_duration);
          try {
            KarplusStrong.saveToWav(samples[i], filePath);
          } catch (Exception e) {
            println("Impossible de générer les sons. Erreur : " + e);   
          }
        }
      }
    }, "sound-thread", 64 * 1024 *1024);
    fill(0, 50);
    rect(0, 0, width, height);
    fill(255);
    textSize(32);
    textAlign(CENTER, CENTER);
    text("loading...", width/2, height/2);
    textAlign(LEFT);
    ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).repaint(); // pour forcer le refresh de l'interface sans draw()
    buttonOff = true;
    t1.start();
    t2.start();
    try {
      t1.join(); // attend que le thread finisse avant de continuer
      t2.join();
    } catch (InterruptedException e) {
      println("Thread interrompu ! : "+ e);
    }
    buttonOff = false;
    
    if (erreur != null) {
      erreur = null;
      background(255);
      fill(0);
      textSize(height * txtProp);
      textAlign(CENTER, CENTER);
      text("Aucune solution trouvée", width/2, height/2);
      textAlign(LEFT);
      return;
      }
    sons = new SoundFile[partition_chords.length];
    for (int i = 0; i < partition_chords.length; i++) {
      String filePath = sketchPath("assets/karplus_strong_chord" + i + ".wav");
      File f = new File(filePath);
      if (f.exists()) {
        sons[i] = new SoundFile(this, filePath);
        println("Son chargé pour l'accord " + i);
      } else {
        println("Fichier manquant : " + filePath);
      }
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
    jouerSonAccord(0);
  }
  }
}  


void jouerSonAccord(int index) {
  if (sons != null && index >= 0 && index < sons.length && sons[index] != null) {
    sons[index].stop();
    sons[index].play();
  }
}

void dessinerTabAccord(int[] cordes, float x, float y) {
  if (cordes == null || cordes.length == 0) return;
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
  if (mesAccords == null || mesAccords.length == 0) return;
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
  float boutonSpacing4 = width * boutonSpacingRatio4;
  
  
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
       
  fill(200,220,255);
  stroke(0);
  rect(boutonX + boutonSpacing4, boutonY, petitBoutonLargeur, boutonHauteur);
  fill(0);
  textFont(createFont("Segoe UI Symbol", 20));
  text("↻", boutonX+boutonSpacing4+petitBoutonLargeur/2, boutonY+boutonHauteur/2);
  textFont(createFont("SansSerif", 12));
  
  textAlign(LEFT);
  popStyle();
}


void mousePressed() {
  if (!buttonOff) {
  float boutonLargeur = width * boutonLargeurRatio;
  float boutonHauteur = height * boutonHauteurRatio;
  float petitBoutonLargeur = width * petitBoutonLargeurRatio;
  float boutonY = height * boutonYRatio;
  float boutonX = width * boutonXRatio;
  float boutonSpacing1 = width * boutonSpacingRatio1;
  float boutonSpacing2 = width * boutonSpacingRatio2;
  float boutonSpacing4 = width * boutonSpacingRatio4;
  
  // bouton mode auto/manuel
  if (mouseX > boutonX && mouseX < boutonX + boutonLargeur && 
      mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
    defilement_auto = !defilement_auto;
    lastChange = millis();
    redessinerTout();
  }
  
  if (mouseX > boutonX+boutonSpacing4 && mouseX< boutonX+boutonSpacing4+petitBoutonLargeur &&
      mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
        rechargerAccordCourant();
      }
  
  
  
  //  boutons de navig ( mode manuel)
  if (!defilement_auto) {
    // Bouton précédent
    if (mouseX > boutonX + boutonSpacing1 && mouseX < boutonX + boutonSpacing1 + petitBoutonLargeur && 
        mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
      indexAccord = (indexAccord - 1 + mesAccords.length) % mesAccords.length;
      redessinerTout();
      jouerSonAccord(indexAccord);
    }
    
    // Bouton suivant
    if (mouseX > boutonX + boutonSpacing2 && mouseX < boutonX + boutonSpacing2 + petitBoutonLargeur && 
        mouseY > boutonY && mouseY < boutonY + boutonHauteur) {
      indexAccord = (indexAccord + 1) % mesAccords.length;
      redessinerTout();
      jouerSonAccord(indexAccord);
    }
  }
  if (editMode) {
      float bx = 3*width/4;
      float by = 9*height/10;
      float bs = height/10;
      if (mouseX > bx && mouseX < bx + bs && mouseY > by && mouseY < by + bs) {
        doPageParams();
      }
    }
  }
}

void rechargerAccordCourant() {
  if (mesAccords == null || tempAccords.isEmpty()) return;
  buttonOff = true;
  final int[][] partition_chords = tempAccords.toArray(new int[0][]);
  final int[][] previousSolution = mesAccords;
  final int indexARecharger = indexAccord;
  fill(0, 100);
  rect(0, 0, width, height);
  fill(255);
  textSize(32);
  textAlign(CENTER, CENTER);
  text("loading...", width/2, height/2);
  textAlign(LEFT);
  ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).repaint();
  try {
    mesAccords = App.findAlternativeChords(guitare, player, partition_chords, previousSolution, indexARecharger, timelimit);
    // Régénérer le son de l'accord rechargé
    String filePath = sketchPath("assets/karplus_strong_chord" + indexARecharger + ".wav");
    double[] sample = KarplusStrong.generateChord(partition_chords[indexARecharger], sample_duration);
    KarplusStrong.saveToWav(sample, filePath);
    sons[indexARecharger] = new SoundFile(pstl.this, filePath);
    println("Accord " + indexARecharger + " rechargé !");
    
  } catch (Exception e) {
    println("Erreur reload : " + e.getMessage());
  } 
  buttonOff = false;
  redessinerTout();
  ((processing.awt.PSurfaceAWT.SmoothCanvas) surface.getNative()).repaint();
  jouerSonAccord(indexARecharger);
}

void changerAccordSuivant() {
  if (mesAccords == null || mesAccords.length == 0 || erreur != null) return;
  indexAccord = (indexAccord + 1) % mesAccords.length;
  lastChange = millis();
  redessinerTout();
  jouerSonAccord(indexAccord);
}

void redessinerTout() {
  if (mesAccords == null || mesAccords.length == 0 || indexAccord >= mesAccords.length) return;
  background(255);
  dessinerTabAccord(mesAccords[indexAccord], width * offsetX, height * offsetY);
  fill(0);
  textSize(txtSize);
  text("Accord " + (indexAccord + 1) + "/" + mesAccords.length, 
       width * txtOffsetXAccord, height * txtOffsetYAccord);
  dessinerBoutons();
}

void ajoutChords() {
 editMode = true;
 if (c.getController("ajoutChords") != null) c.getController("ajoutChords").remove();
 if (c.getController("setAccords") != null) c.getController("setAccords").remove();
 if (c.getController("timelimitInput") != null) c.getController("timelimitInput").remove();
 if (c.getController("accordInput") != null) c.getController("accordInput").remove();
 if (c.getController("validerAccord") != null) c.getController("validerAccord").remove();
 if (c.getController("pageParams") != null) c.getController("pageParams").remove();
 background(255);
 inputField = c.addTextfield("accordInput")
               .setPosition(2*width/30, height/10)
               .setSize(2*width/3, height/10)
               .setColorBackground(color(250, 250, 250))
               .setLabelVisible(false)
               .setCaptionLabel("")
               .setColor(color(0, 0, 0))
               .setColorCursor(color(0, 0, 0))
               .setFont(createFont("Arial", 14));
 
 addButton = c.addButton("validerAccord")
              .setPosition(3*width/4, height/10)
              .setSize(width/5, height/10)
              .setLabel("Ajouter");
              
   c.addButton("setAccords")
    .setPosition(7*width/8, 9*height/10)
    .setSize(height/10, height/10)
    .setColorBackground(color(0,250,0))
    .setColorForeground(color(0,220,0))
    .setLabel("OK");

  
  timelimitField = c.addTextfield("timelimitInput")
                    .setPosition(width/30, 9*height/10)
                    .setSize(width/4, height/10)
                    .setColorBackground(color(250, 250, 250))
                    .setLabelVisible(false)
                    .setCaptionLabel("")
                    .setColor(color(0,0,0))
                    .setColorCursor(color(0,0,0))
                    .setFont(createFont("Arial", 14))
                    .setInputFilter(ControlP5.INTEGER);
  
  refreshButtons();
}

void setAccords() {
 indexAccord = 0; 
 editMode = false;
 String input = timelimitField.getText().trim();
 try {
   timelimit = Integer.parseInt(input);
   if (timelimit < 1) timelimit = -1;
 } catch (NumberFormatException e) {
   println("Format invalide (accords): " + input);
   timelimit = -1;
 }
 pendingPagePrincipal = true;
}

void validerAccord() {
 String saisie = inputField.getText().trim();
 if (saisie.length() == 0) return;
 
 String[] parsed = saisie.split(",");
 int[] accord = new int[parsed.length];
 try{
   for (int i = 0; i < parsed.length; i++) {
     accord[i] = Integer.parseInt(parsed[i].trim());
   }
   tempAccords.add(accord);
   inputField.clear();
   refreshButtons();
 } catch (NumberFormatException e) {
   println("Format invalide : " + saisie);
}
}

void refreshButtons() {
  for (Button b : deleteButtons) {
    c.remove(b.getName());
  }
  deleteButtons.clear();
  
  for (int i = 0; i < tempAccords.size(); i++) {
    float x = width - deleteW - 20;
    float y = yStart + i * lineHeight;
    Button btn = c.addButton("del_"+i)
                  .setPosition(x, y)
                  .setSize(int(deleteW), int(lineHeight - 5))
                  .setLabel("X")
                  .setColorBackground(color(200, 0, 0));
    deleteButtons.add(btn);
  }
}

void supprimerAccord(int index) {
  if (index >= 0 && index < tempAccords.size()) {
    tempAccords.remove(index);
    refreshButtons();
  }
}

public void controlEvent(ControlEvent event) {
  String name = event.getController().getName();
  
  if (name.equals("ajoutChords")) {
    ajoutChords();
  } 
  else if (name.equals("validerAccord")) {
    validerAccord();
  }
  else if (name.equals("setAccords")) {
    setAccords();
  }
  else if (name.startsWith("del_")) {
    int index = Integer.parseInt(name.substring(4));
    supprimerAccord(index);
  }
}

void doPageParams() {
  println("Bouton pageParams cliqué !");
  pageParamsPending = true;
  editMode = false;
}


void loadParamsPage() {
   c.addTextfield("nbFrettes")
    .setPosition(2*width/3, height/20)
    .setSize(width/4, height/15)
    .setColorBackground(color(250, 250, 250))
    .setLabelVisible(false)
    .setCaptionLabel("")
    .setColor(color(0,0,0))
    .setColorCursor(color(0,0,0))
    .setFont(createFont("Arial", 14))
    .setInputFilter(ControlP5.INTEGER);
    
   c.addTextfield("nbCordes")
    .setPosition(2*width/3, height/6.6)
    .setSize(width/4, height/15)
    .setColorBackground(color(250, 250, 250))
    .setLabelVisible(false)
    .setCaptionLabel("")
    .setColor(color(0,0,0))
    .setColorCursor(color(0,0,0))
    .setFont(createFont("Arial", 14))
    .setInputFilter(ControlP5.INTEGER);
    
   c.addTextfield("scordatura")
    .setPosition(2*width/3, height/3.97)
    .setSize(width/4, height/15)
    .setColorBackground(color(250, 250, 250))
    .setLabelVisible(false)
    .setCaptionLabel("")
    .setColor(color(0,0,0))
    .setColorCursor(color(0,0,0))
    .setFont(createFont("Arial", 14));
    
   c.addTextfield("firstFretDistance")
    .setPosition(2*width/3, height/2.79)
    .setSize(width/4, height/15)
    .setColorBackground(color(250, 250, 250))
    .setLabelVisible(false)
    .setCaptionLabel("")
    .setColor(color(0,0,0))
    .setColorCursor(color(0,0,0))
    .setFont(createFont("Arial", 14))
    .setInputFilter(ControlP5.INTEGER);
   
  c.addTextfield("nbDoigts")
    .setPosition(2*width/3, height/2.2)
    .setSize(width/4, height/15)
    .setColorBackground(color(250, 250, 250))
    .setLabelVisible(false)
    .setCaptionLabel("")
    .setColor(color(0,0,0))
    .setColorCursor(color(0,0,0))
    .setFont(createFont("Arial", 14))
    .setInputFilter(ControlP5.INTEGER);

  c.addTextfield("nbBarre")
    .setPosition(2*width/3, height/1.8)
    .setSize(width/4, height/15)
    .setColorBackground(color(250, 250, 250))
    .setLabelVisible(false)
    .setCaptionLabel("")
    .setColor(color(0,0,0))
    .setColorCursor(color(0,0,0))
    .setFont(createFont("Arial", 14))
    .setInputFilter(ControlP5.INTEGER);
    
  c.addButton("paramOK")
   .setPosition(7*width/8, 9*height/10)
    .setSize(height/10, height/10)
    .setColorBackground(color(0,250,0))
    .setColorForeground(color(0,220,0))
    .setLabel("OK");
  
}


void paramOK() {
 editMode = true;
 String nf = ((Textfield) c.getController("nbFrettes")).getText().trim();
 String nc = ((Textfield) c.getController("nbCordes")).getText().trim();
 String sc = ((Textfield) c.getController("scordatura")).getText().trim();
 String dpf = ((Textfield) c.getController("firstFretDistance")).getText().trim();
 String nd = ((Textfield) c.getController("nbDoigts")).getText().trim();
 String br = ((Textfield) c.getController("nbBarre")).getText().trim();
 
 try {
   if (nf.length() == 0) {guitare.nf = 13;}
   else {
   Integer res = Integer.parseInt(nf);
   if (res > 0) {guitare.nf = res;}
   else {guitare.nf = 13;}// on preserve les params par defauts les vals ne sont pas correctes
 }} catch (NumberFormatException e) {
   println("Format invalide (nf) : " + nf);
   guitare.nf = 13;
 }
 
 try {
   if (nc.length() == 0) {guitare.nc = 6;} else {
   Integer res = Integer.parseInt(nc);
   if (res > 0) {guitare.nc = res;}
   else {guitare.nc = 6;}// on preserve les params par defauts les vals ne sont pas correctes
 } 
 }catch (NumberFormatException e) {
   println("Format invalide (nc) : " + nc);
   guitare.nc = 6;
 }
  
 
 try{
   if (sc.length() == 0) {guitare.scordatura = new int[]{52, 57, 62, 67, 71, 76};} 
   else {
     String[] parsed = sc.split(",");
     if (parsed.length != guitare.nc) {
         guitare.scordatura = new int[]{52, 57, 62, 67, 71, 76};
         guitare.nc = 6;
     } else {
       int[] scord = new int[parsed.length];
       for (int i = 0; i < parsed.length; i++) {
         scord[i] = Integer.parseInt(parsed[i].trim());
       }
       guitare.scordatura = scord;
     }
   }
   } catch (NumberFormatException e) {
   println("Format invalide (sc): " + sc);
   guitare.scordatura = new int[]{52, 57, 62, 67, 71, 76};
}
 
 try {
   if (dpf.length() == 0) {player.distanceFirstFrette = 4;} else {
   Integer res = Integer.parseInt(dpf);
   if (res > 0) {player.distanceFirstFrette = res;}
   else {player.distanceFirstFrette = 4;}// on preserve les params par defauts les vals ne sont pas correctes
 }} catch (NumberFormatException e) {
   println("Format invalide (dpf) : " + dpf);
   player.distanceFirstFrette = 4;
 }
 
 try {
   if (nd.length() == 0) {player.nbDoigts = 4;} else {
   Integer res = Integer.parseInt(nd);
   if (res > 0) {player.nbDoigts = res;}
   else {player.nbDoigts = 4;}// on preserve les params par defauts les vals ne sont pas correctes
 }} catch (NumberFormatException e) {
   println("Format invalide (nd) : " + nd);
   player.nbDoigts = 4;
 }
 
 try {
   if (br.length() == 0) {player.barre = 3;} else {
   Integer res = Integer.parseInt(br);
   if (res > 0) {player.barre = res;}
   else {player.barre = 3;}// on preserve les params par defauts les vals ne sont pas correctes
 }} catch (NumberFormatException e) {
   println("Format invalide (br): " + br);
   player.barre = 3;
 }
 
 NB_CORDES = guitare.nc;
  pageParam = false;
  ajoutChords();
 
}
