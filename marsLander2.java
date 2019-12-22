import java.util.Scanner;

class Player {

    //On défini les constantes qui sont utilisées
    //Pour les calculs dans les méthodes de la classe
    private static final int epsilon = 5;
    private static final int maxVVert = 40;
    private static final int maxVHoriz = 20;
    private static final double gravite = 3.711;
    private static final int distanceDeSecurite = 50;
    
    public static void main(String args[]) {
    
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the number of points used to draw the surface of Mars.
        //On défini les coordonnées du début et de la fin  de la piste
        int debutPiste = 0;
        int finDePiste = 0;
        //Alttitude de la piste (Terrain plat)
        int pisteY = -1;

        //Point de recherche
        int previousPointX = -1;
        int previousPointY = -1;

        for (int i = 0; i < N; i++) {
            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            if(previousPointY == landY) {
                debutPiste = previousPointX;
                finDePiste = landX;
                pisteY = landY;
            }else {
                previousPointX = landX;
                previousPointY = landY;
            }
        }
        
        // game loop
        while (true) {
            int X = in.nextInt();
            int Y = in.nextInt();
            int HS = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            int VS = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int F = in.nextInt(); // the quantity of remaining fuel in liters.
            int R = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int P = in.nextInt(); // the thrust power (0 to 4).

            // On vérifie si la navette est au dessus d'un sol plat (Piste d'atterrissage)
            if (auDessusSolPlat(X, debutPiste, finDePiste)) {
                //On vérifie si la navette est prêt à atterir 
                //C'est à dire que
                if (pretAtterir(Y, pisteY)) {
                    //On ajuste les variables en conséquence
                    R = 0;
                    P = 3;
                //Si non si la navette n'est pas sur au dessus d'un sol plat, 
                //on vérifie si la vitesse est dans les normes
                //Si la 
                } else if (vitesseOK(HS, VS)) {
                    //On ajuste les variables en conséquence
                    R = 0;
                    P = 2;
                //Si non, cela voudra dire que la rotation n'est pas normale.
                } else {
                    //On calcule donc la nouvelle rotation en fonction de la vitesse
                    //On ajuste les variables en conséquence
                    R = rotationDeRalentissement(HS, VS);
                    P = 4;
                }
            } 
            // Ce cas correspond au cas ou la navette n'est pas au dessus d'un sol plat
            else {
                // On verifie sa direction et la vitesse.
                //Si on va dans une mauvaise direction et que notre vitesse horizontale est superieur aux normes
                if (mauvaiseDirection(X, HS, debutPiste, finDePiste) || excesVitesseHorizontale(HS)) {
                    //on calcule une nouvelle rotation pour ralentir la navette  et l'orienter vers la piste 
                    //Puis on ajuste les variables
                    R = rotationDeRalentissement(HS, VS);
                    P = 4;
                } 
                //Ce cas correspond au cas ou la navette a une vitesse basse
                else if (vitesseBasse(HS)) {
                    //on calcule une nouvelle rotation pour ralentir la navette et l'orienter vers la piste
                    //Puis on ajuste les variables
                    R = coorrigerAngleVitesseSol(X, debutPiste, finDePiste);
                    P = 4;
                } 
                //Dans tous les autres cas, on corrige juste la pussance verticale
                //Puisque la vitesse Horizontale n'est pas forte
                //Et on est dans la bonne direction
                //Donc pas besoin de changer la rotation
                else {
                    R = 0;
                    P = coorrigerPuissance(VS);
                }
            }
            System.out.println(R + " " + P); // R P. R is the desired rotation angle. P is the desired thrust power.
        }
    }

    //Fonction qui détermine si la navette est au dessus d'un sol plat
    private static boolean auDessusSolPlat(int marsLanderX, int debutPiste, int finDePiste) {
        //la navette est au dessus d'un sol plat (La piste), si et seulement si 
        //x est compris entre le début et la fin de la piste
        return marsLanderX >= debutPiste && marsLanderX <= finDePiste;
    }

    //Fonction qui détermine si la navette est prete à atterrir
    private static boolean pretAtterir(int marsLanderY, int pisteY) {
        // La navette est prête à atterir si et seulement si 
        // Y est inférieur à l'altitude de la piste + la distance de sécurité 
        return marsLanderY < pisteY + distanceDeSecurite;
    }

    //Fonction qui détermine si la vitesse est normale 
    private static boolean vitesseOK(int marsLanderHorizontalSpeed, int marsLanderVerticalSpeed) {
        return Math.abs(marsLanderHorizontalSpeed) <= (maxVHoriz - epsilon) && Math.abs(marsLanderVerticalSpeed) <= (maxVVert - epsilon);
    }

    private static int rotationDeRalentissement(int horizontalSpeed, int verticalSpeed) {
        double speed = Math.sqrt(Math.pow(horizontalSpeed, 2) + Math.pow(verticalSpeed, 2));
        double rotationAsRadians = Math.asin((double) horizontalSpeed / speed);
        return (int) Math.toDegrees(rotationAsRadians);
    }

    private static boolean mauvaiseDirection(int marsLanderX, int marsLanderHorizontalSpeed, int debutPiste, int finDePiste) {
        if (marsLanderX < debutPiste && marsLanderHorizontalSpeed < 0) {
            return true;
        }

        if (marsLanderX > finDePiste && marsLanderHorizontalSpeed > 0) {
            return true;
        }
        return false;
    }

    private static boolean excesVitesseHorizontale(int marsLanderHorizontalSpeed) {
        return Math.abs(marsLanderHorizontalSpeed) > (maxVHoriz * 4);
    }

    private static boolean vitesseBasse(int marsLanderHorizontalSpeed) {
        return Math.abs(marsLanderHorizontalSpeed) < (maxVHoriz * 2);
    }

    private static int coorrigerAngleVitesseSol(int marsLanderX, int debutPiste, int finDePiste) {
        if (marsLanderX < debutPiste) {
        return - (int) Math.toDegrees(Math.acos(gravite / 4.0));
        }
        if (marsLanderX > finDePiste) {
        return + (int) Math.toDegrees(Math.acos(gravite / 4.0));
        }
        return 0;
    }

    private static int coorrigerPuissance(int marsLanderVerticalSpeed) {
        return (marsLanderVerticalSpeed >= 0) ? 3 : 4;
    }

}

