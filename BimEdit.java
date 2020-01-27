import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class BimEdit {

    public static final Scanner reader = new Scanner(System.in);

    public static char[][] loadPictureToArray(String fileName, char[] backCharFrontChar) {

        /* Metodi luo 2d char[][]-taulukon, joka ladataan erillisestä tekstitiedostosta. Lataus toimii
        niin, että kuvan ensimmäinen merkki on kohdassa (0, 0) ja viimeinen merkki (r - 1, c - 1). R on lyhenne
        sanasta row, joka tarkoittaa rivien lukumäärää ja c on lyhenne sanasta column, joka tarkoittaa sarakkeiden
        lukumäärää. Metodi käy läpi useamman error checkin ja palauttaa arvon null, jos error check toteutuu.
        */

        Scanner fileReader = null;

        // Error, jos merkkejä ei ole tai on annettu vain yksi tausta- tai edustamerkki
        if(backCharFrontChar == null || backCharFrontChar.length != 2) {
            return null;
        }
        
        File file = new File(fileName);

        try {
            fileReader = new Scanner(file);
        } catch(FileNotFoundException error) { // Error, jos tiedostonimeä ei löydy
            return null;
        }

        /* Käydään läpi tiedoston ensimmäiset neljä riviä, jossa käy ilmi rivien lukumäärä,
        sarakkeiden lukumäärä, taustamerkki ja edustamerkki
        */

        String rows = fileReader.nextLine();
        int r = Integer.parseInt(rows);
        String columns = fileReader.nextLine();
        int c = Integer.parseInt(columns);
        String backChar = fileReader.nextLine();
        char bChar = backChar.charAt(0);
        String frontChar = fileReader.nextLine();
        char fChar = frontChar.charAt(0);

        backCharFrontChar[0] = bChar;
        backCharFrontChar[1] = fChar;

        char[][] picture = new char[r][c]; // Luodaan kaksiulotteinen taulukko

        if (r < 3 || c < 3) { // Tarkistetaan, että rivien ja sarakkeiden pituus on vähintään kolme
            return null;
        }

        // Ladataan taulukko tekstitiedostosta
        try {
            // For-luuppi, joka käy kuvan rivi riviltä läpi
            for (int i = 0; i < r; i++) {
                // Otetaan rivi talteen ja siirrtyään seuraavalle riville
                String lines = fileReader.nextLine();
                // Error-check, jos rivien pituus ei ole sama kuvassa, kuin annetussa rivien pituudessa
                if (lines.length() != c) {
                    throw new Exception(); // Siirrytään catchiin
                }
                for (int j = 0; j < c; j++) {
                    picture[i][j] = lines.charAt(j);
                }
            }
            // Error-check, joka tarkistaa jos luupin jälkeen kuvaan on jäänyt vielä rivejä
            if (fileReader.hasNextLine()) {
                throw new Exception(); // Siirrytään catchiin
            }

        }
        catch (Exception fail) { // Suljetaan tiedostonlukija ja palautetaan null, jos joku error check on toteutunut
            if (fileReader != null) {
                fileReader.close();
            }
            return null;
        }
        // Error-check, joka tarkistaa ettei kuvassa ole ylimääräisiä merkkejä
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                if (picture[i][j] != fChar && picture[i][j] != bChar) {
                    return null;
                }
            }
        }
        return picture; // Palautetaan ladattu kuva
    }

    public static void print(char[][] picture) {

        /* Metodi, joka printtaa kuvan, jos käyttäjä antaa komennon "print".
        */

        if (picture != null) {
            int rowNum = picture.length;
            for (int row = 0; row < rowNum; row++) {
                for (int column = 0; column < picture[row].length; column++) {
                    System.out.print(picture[row][column]);
                }
                System.out.println();
            }
        }
    }

    public static boolean changeCharacters(char[][] array, char character1, char character2) {

        /* Boolean tyyppinen metodi, joka vaihtaa kuvan merkit päikseen.
        */

        // Tarkistetaan, että kuva on validi, jos ei ole, palautetaan false
        if(array == null || array[0].length == 0 || array.length == 0) {
            return false;
        }
        for(int i = 0; i < array.length; i++) {
            for(int j = 0; j < array[i].length; j++) {
                if(array[i][j] == character1) {
                    array[i][j] = character2;
                }
                else if(array[i][j] == character2) {
                    array[i][j] = character1;
                }
            }
        }
        // Palautetaan true, kun merkit on vaihdettu
        return true;
    }

    public static char[][] copy2dArray(char[][] copyArray) {

        /* Metodi kopioi pääohjelmasta saadut taulukot. Metodin paluuarvo on null jos, listan arvo on null tai pituus
        nolla tai listan ensimmäisen alkion pituus on nolla, muutoin paluuarvona on kopioidut listat.
        */

        if(copyArray == null || copyArray.length == 0 || copyArray[0].length == 0){
            return null;
        }
        else {
            char[][] newArray;
            newArray = new char[copyArray.length][copyArray[0].length];
            for (int row = 0; row < copyArray.length; row++){
                for (int column = 0; column < copyArray[row].length; column++){
                    newArray[row][column] = copyArray[row][column];
                }

            }
            return newArray;
        }    
    }

    public static char[][] dilate(String input, char[][] picture, char[] backCharFrontChar) {

        /* Dilaatio metodi lisää ikkunan keskikohtaan edustamerkin, jos sen naapurustoon osuu
        edustamerkki.
        */

        // Splitataan käyttäjän antama syöte
        String splitter = " ";
        String[] parts = input.split("[" + splitter + "]");

        // Jos käyttäjä on antanut syötteen, jossa on liikaa välilyönnillä eroteltuja merkkejä palautetaan null
        if (parts.length != 2) {
            return null;
        }
        else {
            int dilateNum = Integer.parseInt(parts[1]);
            char[][] dilatedPicture = copy2dArray(picture);
            char[][] origPicture = picture;
            int center = (dilateNum-1) / 2;
            // Käydään kuva läpi ikkunan avulla, jonka koon käyttäjä on määritellyt.
            // Ikkunan täytyy olla pariton, suurempi tai yhtäsuuri kuin kolme ja ikkuna ei saa olla isompi kuin kuva
            if (dilateNum >= 3 && dilateNum % 2 == 1 && dilateNum <= picture.length) {
                for (int i = 0 + center; i < origPicture.length - center; i++) {
                    for (int j = 0 + center; j < origPicture[0].length - center; j++) {
                        for (int windowRow = 0 - center; windowRow < center + 1; windowRow++) {
                            for (int windowColumn = 0 - center; windowColumn < center + 1; windowColumn++) {
                                /* Jos naapurustoon on osunut edustamerkki vaihdetaan ikkunan keskipisteen merkki 
                                edustamerkiksi.
                                */
                                if (picture[i+windowRow][j+windowColumn] == backCharFrontChar[1]) {
                                    dilatedPicture[i][j] = backCharFrontChar[1];
                                }
                            }
                        }
                    }
                }
                // Palautetaan dilatoitu kuva
                return dilatedPicture;
            }
            else {
                return null;
            }
        }
    }

    public static void turnArray(char[] backCharFrontChar) {

        /* Metodi kääntää taulukon merkit ympäri, jos invert on ajettu, jotta
        info näyttää merkit oikein.
        */

        char helper = backCharFrontChar[0];
        backCharFrontChar[0] = backCharFrontChar[1];
        backCharFrontChar[1] = helper;
    }

    public static boolean checkParameters(String[] args) {

        /* Käynnistysparametrien tarkistus metodi, joka tarkistaa, että käyttäjä
        on antanut oikeanlaiset parametrit.
        */

        if (args.length == 0 || args.length > 2 || (args.length == 2 && !args[1].equals("echo"))) {
            return false;
        }
        else {
            return true;
        }
    }

    public static void main(String[] args) {

        // Ohjelman nimi kehystettynä
        System.out.println("-----------------------");
        System.out.println("| Binary image editor |");
        System.out.println("-----------------------");

        String fileName = ""; // Komentorivi parametri

        // Lippumuuttujat while-luupille ja echolle (jos on ajettu)
        boolean looping = true;
        boolean echoing = false;

        // Kutsutaan käynnistysparametrien tarkistus metodia
        if (!checkParameters(args)) {
            System.out.println("Invalid command-line argument!");
            System.out.println("Bye, see you soon.");
            looping = false;
            return;
        }
        /* Jos käynnistysparametrit ovat oikein sijoitetaan käyttäjän antama tiedosto
        muuttujaan ja tarkistetaan onko käyttäjä antanut echo komennon.
        */
        else if (args.length == 2 && args[1].equals("echo")) {
            echoing = true;
            fileName = args[0];
        }
        else {
            fileName = args[0];
        }

        char[] backCharFrontChar = {' ',' '};
        // Ladataan kuva
        char[][] picture = loadPictureToArray(fileName, backCharFrontChar);

        // Tarkistetaan onko kuvan lataus metodi palauttanut arvon null, eli tiedosto on ollut virheellinen
        if (picture == null) {
            System.out.println("Invalid image file!");
            System.out.println("Bye, see you soon.");
            looping = false;
        }

        // While-luuppi, jossa käyttäjän kanssa käydään vuorovaikutusta
        while (looping) {
            System.out.println("print/info/invert/dilate/erode/load/quit?"); // Komento vaihtoehdot
            String command = reader.nextLine(); // Käyttäjän syöte
            char[][] backup = copy2dArray(picture); // Varmuuskopioi edellisestä kuvasta komennon jälkeen

            // Jos echo on toteutunut eli lippumuuttujan arvo on true, toistetaan käyttäjän antamat syötteet
            if (echoing) {
                System.out.println(command);
            }

            if (command.equals("quit")) { // Lopetetaan ohjelma, jos käyttäjä antaa syötteeksi "quit"
                System.out.println("Bye, see you soon.");
                looping = false; // Keskeytetään while-looppi
            }
            else if (command.equals("print")) { // Tulostetaan ladattu kuva
                print(picture);
            }
            /* Käyttäjä on antanut info syötteen.
            Tulostetaan näytölle kuvan tiedot eli, kuinka suuri kuva on ja 
            kuinka monta edusta- ja taustamerkkiä siinä on
            */
            else if (command.equals("info")) {
                int rowNum = picture.length;
                int columnNum = picture[0].length;
                System.out.println(rowNum + " x " + columnNum);
                int characters[] = new int[backCharFrontChar.length];
                // For-luupin avulla käydään koko kuva läpi ja otetaan talteen montako kutakin merkkiä siinä on
                for (int i = 0; i < rowNum; i++) {
                    for (int j = 0; j < columnNum; j++) {
                        for (int index = 0; index < backCharFrontChar.length; index++) {
                            if (picture[i][j] == backCharFrontChar[index]) {
                                characters[index] = characters[index] + 1;
                            }
                        }
                    }
                }
                System.out.println(backCharFrontChar[0] + " " + characters[0]);
                System.out.println(backCharFrontChar[1] + " " + characters[1]);
            }

            else if (command.equals("invert")) { // Vaihdetaan edusta- ja taustamerkit päikseen
                changeCharacters(picture, backCharFrontChar[0], backCharFrontChar[1]);
                turnArray(backCharFrontChar);
            }

            else if (command.contains("dilate")) {
                // Kutsutaan dilate metodia
                picture = dilate(command, picture, backCharFrontChar);
                /* Jos käyttäjä on antanut vääränlaisen dilate-komennon, metodi palauttaa arvon null,
                jolloin ohjelma tulostaa näytölle virheilmoituksen
                ja lataa kuvasta varmuuskopion, koska muutoin seuraavan komennon kohdalla kuva olisi tyhjä
                */
                if (picture == null) {
                    System.out.println("Invalid command!");
                    picture = backup;
                }
            }

            else if (command.contains("erode")) {
                /* Erode komentoo ajaa saman kuin dilate, mutta ennen dilaten kutsua ajetaan invert,
                jonka jälkeen invert ajetaan uudestaan, jotta saadaan oikeanlainen kuva
                */
                changeCharacters(picture, backCharFrontChar[0], backCharFrontChar[1]);
                picture = dilate(command, picture, backCharFrontChar);
                changeCharacters(picture, backCharFrontChar[0], backCharFrontChar[1]);
                // Sama kuin dilatessa, eli käyttäjä on antanut vääränlaisen erode-komennon
                if (picture == null) {
                    System.out.println("Invalid command!");
                    picture = backup;
                }
            }
            else if (command.equals("load")) { // Ladataan kuva uudestaan, sellaisena kun se oli alunperin
                picture = loadPictureToArray(fileName, backCharFrontChar);
            }
            else { // Jos komento ei ole mikään edellä määritelty sana, niin tulostetaan error
                System.out.println("Invalid command!");
            }
        }
    }
}
